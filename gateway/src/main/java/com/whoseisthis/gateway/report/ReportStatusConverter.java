package com.whoseisthis.gateway.report;

import com.whoseisthis.gateway.report.core.ReportStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReportStatusConverter implements Converter<String, ReportStatus> {

    @Override
    public ReportStatus convert(String source)
    {
        try {
            return ReportStatus.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}