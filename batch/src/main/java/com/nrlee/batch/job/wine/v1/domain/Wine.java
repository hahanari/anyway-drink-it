package com.nrlee.batch.job.wine.v1.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.nrlee.batch.vo.IndexBulk;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "wine")
@Data
public class Wine implements IndexBulk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 20, nullable = false)
    private String name;

    @Override
    public String getIndexBulkKey() {
        return String.valueOf(this.id);
    }

}
