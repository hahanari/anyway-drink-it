package com.nrlee.batch.job.wine.v1;

import com.nrlee.batch.customreader.StepItemReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WineItemReaderImpl implements StepItemReader {

    @Override
    public ItemReader getItemReader(Integer batchSize) throws Exception {
        return null;
    }
}