package com.nrlee.batch.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IndexBulk {

    @JsonIgnore
    String getIndexBulkKey();
}
