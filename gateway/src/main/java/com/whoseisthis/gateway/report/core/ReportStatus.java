package com.whoseisthis.gateway.report.core;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReportStatus {
    OPEN, RESOLVED;

    @JsonCreator
    public static ReportStatus fromString(String value){
        return ReportStatus.valueOf(value.toUpperCase());
    }
}
