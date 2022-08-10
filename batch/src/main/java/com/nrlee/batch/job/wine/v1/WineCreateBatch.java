package com.nrlee.batch.job.wine.v1;

import java.util.Collections;

import javax.annotation.Resource;

import com.nrlee.batch.config.UniqueRunIdIncrementer;
import com.nrlee.batch.constant.IndexEnum;
import com.nrlee.batch.helper.IndexHelper;
import com.nrlee.batch.job.wine.v1.domain.Wine;
import com.nrlee.batch.job.wine.v1.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "job.name", havingValue = WineCreateBatch.JOB_NAME)
@RequiredArgsConstructor
public class WineCreateBatch {
    static final String JOB_NAME = "WineCreateBatch";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final WineRepository wineRepository;
    private final IndexHelper indexHelper;
    private final IndexEnum indexEnum = IndexEnum.WINE;

    @Resource(name = "wineItemWriterImpl")
    private ItemWriter itemWriter;
    private static final int chunkSize = 10;


    @Bean
    public Job wineCreateBatchJob() throws Exception {
        log.info("wineCreateBatchJob");
        return this.jobBuilderFactory.get(JOB_NAME)
                .start(createIndex())
                .incrementer(new UniqueRunIdIncrementer())
                .next(setRefreshInterval("3s"))
                .next(bulkWine())
                .next(setRefreshInterval("30s"))
                .build();
    }

    public Step createIndex() {
        log.info("createIndex");
        return stepBuilderFactory.get("createIndex")
                .tasklet((contribution, chunkContext) -> {
                    indexHelper.createIndex(indexEnum);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    public Step setRefreshInterval(String interval) {
        log.info("setRefreshInterval");
        return stepBuilderFactory.get("setRefreshInterval")
                .tasklet((contribution, chunkContext) -> {
                    indexHelper.setRefreshInterval(indexEnum, interval);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step bulkWine() throws Exception {
        log.info("bulkWine");
        return stepBuilderFactory.get("bulkWine")
                .<Wine, Wine>chunk(chunkSize)
                .reader(repositoryItemReader())
                .writer(itemWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Wine> repositoryItemReader() {
        log.info("repositoryItemReader");
        return new RepositoryItemReaderBuilder<Wine>()
                .repository(wineRepository)
                .methodName("findAll")
                .pageSize(chunkSize)
                .name("repositoryItemReader")
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }
}
