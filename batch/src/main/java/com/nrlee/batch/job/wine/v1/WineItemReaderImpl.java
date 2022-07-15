package com.nrlee.batch.job.wine.v1;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class WineItemReaderImpl implements ItemWriter<Long> {
    @Override
    public void write(List<? extends Long> items) throws Exception {

    }
}
