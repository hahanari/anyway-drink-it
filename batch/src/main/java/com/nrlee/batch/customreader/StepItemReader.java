package com.nrlee.batch.customreader;

import org.springframework.batch.item.ItemReader;

public interface StepItemReader {
    ItemReader getItemReader(Integer batchSize) throws Exception;
}