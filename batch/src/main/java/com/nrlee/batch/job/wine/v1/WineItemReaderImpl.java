package com.nrlee.batch.job.wine.v1;

import com.nrlee.batch.application.WineService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WineItemReaderImpl implements ItemReader<Long> {

    WineService wineService;

    @Override
    public Long read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        return null;
    }
}
