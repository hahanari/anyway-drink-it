package com.nrlee.batch.job.wine.v1.repository;

import com.nrlee.batch.job.wine.v1.domain.Wine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WineRepository extends JpaRepository<Wine, Integer> {

    Page<Wine> findAll(Pageable pageable);
}
