package com.nrlee.batch.application;

import java.util.List;

import com.nrlee.batch.domain.Wine;
import com.nrlee.batch.domain.WineRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WineService {

    WineRepository wineRepository;

    public List<Wine> getAllWine() {
        return wineRepository.findAll();
    }
}
