package com.nrlee.batch.job.wine.v1;

import javax.annotation.Resource;

import com.nrlee.batch.util.JobCompletionNotificationListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@ConditionalOnProperty(name = "job.name", havingValue = WineCreateBatch.JOB_NAME)
@RequiredArgsConstructor
public class WineCreateBatch {
    static final String JOB_NAME = "WineCreateBatch";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    @Resource(name = "WineItemReaderImpl")
    private ItemReader<Long> itemReader;
    @Resource(name = "WineItemWriterImpl")
    private ItemWriter<Long> itemWriter;
    private final JobCompletionNotificationListener jobCompletionNotificationListener;


    @Bean
    private Job WineCreateBatchJob() {
        log.info("WineCreateBatchJob");
        return jobBuilderFactory.get(JOB_NAME)
                .start(createIndex())
                .next(bulkWineForCreateStep())
                .listener(jobCompletionNotificationListener)
                .build();
    }

    private Step createIndex() {
        log.info("createIndex");
        return stepBuilderFactory.get("createIndex")
                .tasklet((contribution, chunkContext) -> {
                    // TODO: ES 호출 - 인덱스 생성
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    private Step bulkWineForCreateStep() {
        log.info("bulkWineForCreateStep");
        int batchSize = 10;
        return stepBuilderFactory
                .get("bulkWineForCreateStep")
                .<Long, Long>chunk(batchSize)
                .reader(itemReader)
                .writer(itemWriter)
                .taskExecutor(executor(0))
                .throttleLimit(0)
                .build();
    }

    public TaskExecutor executor(int poolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setThreadNamePrefix("multi-thread-");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }
}
