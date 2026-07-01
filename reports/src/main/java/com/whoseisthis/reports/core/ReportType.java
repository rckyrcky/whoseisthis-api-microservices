package com.whoseisthis.reports.core;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReportType {
    LOST, FOUND;

    @JsonCreator
    public static ReportType fromString(String value)
    {
        return ReportType.valueOf(value.toUpperCase());
    }
}