package com.nrlee.batch.helper;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrlee.batch.constant.IndexEnum;
import com.nrlee.batch.util.AsyncFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class IndexHelper {

    private final AsyncFileUtil asyncFileUtil;

    public void createIndex(IndexEnum indexEnum) throws Exception {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        CreateIndexRequest request = new CreateIndexRequest(indexEnum.getReadAlias());
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
    }

    private Map<String, Object> getStringJsonToMap(String stringJson, String typeKey) {
        try {
            return (Map<String, Object>) new ObjectMapper().readValue(stringJson, new TypeReference<Map<String,Object>>(){}).get(typeKey);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

}
