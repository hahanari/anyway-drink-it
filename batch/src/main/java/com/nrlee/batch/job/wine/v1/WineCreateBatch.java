package com.nrlee.batch.job.wine.v1;

import java.util.Collections;

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
    private static final int chunkSize = 10;


    @Bean
    public Job WineCreateBatchJob() throws Exception {
        log.info("WineCreateBatchJob");
        return this.jobBuilderFactory.get(JOB_NAME)
                .start(createIndex())
                .next(bulkWine())
                .build();
    }

    @Bean
    public Step createIndex() {
        return stepBuilderFactory.get("createIndex")
                .tasklet((contribution, chunkContext) -> {
                    // TODO: ES 호출 - 인덱스 생성
                    log.info("createIndex");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step bulkWine() throws Exception {
        return stepBuilderFactory.get("bulkWine")
                .<Wine, Wine>chunk(chunkSize)
                .reader(repositoryItemReader())
                .writer(jpaPagingItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Wine> repositoryItemReader() {
        return new RepositoryItemReaderBuilder<Wine>()
                .repository(wineRepository)
                .methodName("findAll")
                .pageSize(chunkSize)
                .name("repositoryItemReader")
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    private ItemWriter<Wine> jpaPagingItemWriter() {
        return list -> {
            for (Wine wine: list) {
                log.info("Current Wine={}", wine);
            }
        };
    }
}
