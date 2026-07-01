package com.whoseisthis.gateway.report;

import com.whoseisthis.gateway.report.core.ReportType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReportTypeConverter implements Converter<String, ReportType> {

    @Override
    public ReportType convert(String source)
    {
        try {
            return ReportType.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}