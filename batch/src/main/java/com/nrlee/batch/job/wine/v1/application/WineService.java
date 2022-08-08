package com.nrlee.batch.job.wine.v1.application;

import java.util.List;

import com.nrlee.batch.job.wine.v1.domain.Wine;
import com.nrlee.batch.job.wine.v1.repository.WineRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WineService {

    private final WineRepository wineRepository;

    public List<Wine> getAllWine() {
        return wineRepository.findAll();
    }
}
