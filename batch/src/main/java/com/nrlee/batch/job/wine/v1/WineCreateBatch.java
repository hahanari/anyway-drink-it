package com.nrlee.batch.job.wine.v1;

import javax.annotation.Resource;

import com.nrlee.batch.util.JobCompletionNotificationListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "job.name", havingValue = WineCreateBatch.JOB_NAME)
@RequiredArgsConstructor
public class WineCreateBatch {
    static final String JOB_NAME = "WineCreateBatch";
    private int chunkSize = 10;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    @Resource(name = "wineItemReaderImpl")
    private ItemReader itemReader;
    @Resource(name = "wineItemWriterImpl")
    private ItemWriter itemWriter;
    @Resource(name = "transactionManager")
    private final PlatformTransactionManager transactionManager;
    private final JobCompletionNotificationListener jobCompletionNotificationListener;


    @Bean
    public Job WineCreateBatchJob() {
        log.info("WineCreateBatchJob");
        return this.jobBuilderFactory.get(JOB_NAME)
                .start(createIndex())
                .next(bulkWineForCreateStep())
                .listener(jobCompletionNotificationListener)
                .build();
    }

    @Bean
    public Step createIndex() {
        log.info("createIndex");
        return stepBuilderFactory.get("createIndex")
                .tasklet((contribution, chunkContext) -> {
                    // TODO: ES 호출 - 인덱스 생성
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step bulkWineForCreateStep() {
        log.info("bulkWineForCreateStep");
        return stepBuilderFactory
                .get("bulkWineForCreateStep")
                .transactionManager(transactionManager)
                .<Long, Long>chunk(chunkSize)
                .reader(itemReader)
                .writer(itemWriter)
                .build();
    }
}
