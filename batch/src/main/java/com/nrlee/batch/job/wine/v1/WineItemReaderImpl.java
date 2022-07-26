package com.nrlee.batch.job.wine.v1;

import java.util.List;

import com.nrlee.batch.application.WineService;
import com.nrlee.batch.domain.Wine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WineItemReaderImpl implements ItemReader<List<Wine>> {

    WineService wineService;

    @Override
    public List<Wine> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        List<Wine> wineList = wineService.getAllWine();

        log.info("wineList: {}", wineList);
        return wineList;
    }
}