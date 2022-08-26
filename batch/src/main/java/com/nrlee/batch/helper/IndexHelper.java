package com.nrlee.batch.helper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrlee.batch.constant.IndexEnum;
import com.nrlee.batch.util.AsyncFileUtil;
import com.nrlee.batch.vo.IndexBulk;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class IndexHelper {

    public static final String DATETIME_FORMAT_YYYYMMDDHHMM = "yyyyMMddHHmm";
    private final AsyncFileUtil asyncFileUtil;

    public void createIndex(IndexEnum indexEnum) throws Exception {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        );

        final String indexNameSuffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_YYYYMMDDHHMM));
        final String indexName = indexEnum.getReadAlias() + "-" + indexNameSuffix;

        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.alias(new Alias(indexEnum.getWriteAlias()));

        asyncFileUtil.getFileContent(indexEnum.getSettingJsonPath(), (settings) -> {
            Map<String, Object> settingsMap = getStringJsonToMap(settings, "settings");
            request.settings(settingsMap);

            try {
                asyncFileUtil.getFileContent(indexEnum.getMappingJsonPath(), (mappings) -> {
                    Map<String, Object> mappingsMap = getStringJsonToMap(mappings, "mappings");
                    request.mapping(mappingsMap);
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        client.indices().create(request, RequestOptions.DEFAULT);
        client.close();
    }

    public void setRefreshInterval(IndexEnum indexEnum, String interval) throws IOException {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        );

        UpdateSettingsRequest request = new UpdateSettingsRequest(indexEnum.getWriteAlias());
        String settingKey = "index.refresh_interval";
        Settings settings = Settings.builder()
                .put(settingKey, interval)
                .build();
        request.settings(settings);

        client.indices().putSettings(request, RequestOptions.DEFAULT);
    }

    public void bulk(List<? extends IndexBulk> bulkList, String writeIndex) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        ObjectMapper mapper = new ObjectMapper();

        bulkList.forEach(bulkVO -> {
            try {
                byte[] bytes = mapper.writeValueAsBytes(bulkVO);
                bulkRequest.add(
                        new IndexRequest(writeIndex)
                                .id(bulkVO.getIndexBulkKey())
                                .source(bytes, XContentType.JSON)
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        client.bulk(bulkRequest, RequestOptions.DEFAULT);
        client.close();
    }

    public void rebindReadAlias(IndexEnum indexEnum) throws IOException {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        );

        List<String> indexNameListWithReadAlias = getIndexListByAliasName(indexEnum.getReadAlias());

        IndicesAliasesRequest request = new IndicesAliasesRequest();

        if (indexNameListWithReadAlias.size() > 0) {
            IndicesAliasesRequest.AliasActions deleteReadAliasAction = new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.REMOVE)
                    .index(indexNameListWithReadAlias.get(0))
                    .alias(indexEnum.getReadAlias());
            request.addAliasAction(deleteReadAliasAction);
        }

        List<String> indexNameListWithWriteAlias = getIndexListByAliasName(indexEnum.getWriteAlias());

        IndicesAliasesRequest.AliasActions addReadAliasAction = new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                .index(indexNameListWithWriteAlias.get(0))
                .alias(indexEnum.getReadAlias());
        request.addAliasAction(addReadAliasAction);

        IndicesAliasesRequest.AliasActions deleteWriteAliasAction = new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.REMOVE)
                .index(indexNameListWithWriteAlias.get(0))
                .alias(indexEnum.getWriteAlias());
        request.addAliasAction(deleteWriteAliasAction);

        client.indices().updateAliases(request, RequestOptions.DEFAULT);
    }

    private List<String> getIndexListByAliasName(String aliasName) throws IOException {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        );

        GetAliasesRequest requestWithAlias = new GetAliasesRequest(aliasName);

        GetAliasesResponse response = client.indices().getAlias(requestWithAlias, RequestOptions.DEFAULT);

        final Map<String, Set<AliasMetaData>> aliases = response.getAliases();
        final Set<String> keySet = aliases.keySet();

        List<String> indexNameList = new ArrayList<>(keySet);
        indexNameList.sort(Collections.reverseOrder());

        return indexNameList;
    }

    private Map<String, Object> getStringJsonToMap(String stringJson, String typeKey) {
        try {
            return (Map<String, Object>) new ObjectMapper().readValue(stringJson, new TypeReference<Map<String, Object>>() {
            }).get(typeKey);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

}