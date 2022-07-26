package com.nrlee.batch.job.wine.v1;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WineItemWriterImpl implements ItemWriter<Long> {
    @Override
    public void write(List<? extends Long> items) throws Exception {

    }
}