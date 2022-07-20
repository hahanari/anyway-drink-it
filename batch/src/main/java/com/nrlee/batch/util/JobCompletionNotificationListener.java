package com.nrlee.batch.util;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    @Override
    public void afterJob(JobExecution jobExecution) {
        BatchStatus batchStatus = jobExecution.getStatus();

        if (!BatchStatus.COMPLETED.equals(batchStatus)) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }

}
