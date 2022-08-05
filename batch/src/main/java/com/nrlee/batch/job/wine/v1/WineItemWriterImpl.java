package com.nrlee.batch.job.wine.v1;

import java.util.List;

import com.nrlee.batch.constant.IndexEnum;
import com.nrlee.batch.helper.IndexHelper;
import com.nrlee.batch.job.wine.v1.domain.Wine;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WineItemWriterImpl implements ItemWriter<Wine> {

    private final IndexHelper indexHelper;

    @Override
    public void write(List<? extends Wine> items) throws Exception {
        indexHelper.bulk(items, IndexEnum.WINE.getWriteAlias());
    }
}