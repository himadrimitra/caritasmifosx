package com.finflux.risk.creditbureau.provider.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 17/09/16.
 */
public enum ReportFileType {

    INVALID(0, "report.invalid"), HTML(1, "report.html"), PDF(2, "report.pdf");

    final private Integer value;
    final private String code;

    ReportFileType(Integer value, String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public static ReportFileType fromInt(final Integer statusValue) {

        ReportFileType type = ReportFileType.INVALID;
        switch (statusValue) {
            case 1:
                type = ReportFileType.HTML;
            break;
            case 2:
                type = ReportFileType.PDF;
            break;
        }
        return type;
    }

    public static EnumOptionData reportFileType(final int id) {
        return reportFileType(ReportFileType.fromInt(id));
    }

    public static EnumOptionData reportFileType(final ReportFileType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case HTML:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "HTML");
            break;
            case PDF:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "PDF");
            break;
            default:
            break;
        }
        return optionData;
    }
}
