package org.apache.fineract.infrastructure.documentmanagement.data;

public class DocumentTagData {

    private final String reportName;
    private final String reportType;
    private final String reportCategory;
    private final Long tagId;
    private final String outputType ;
    
    public DocumentTagData(String reportName, String reportType, String reportCategory, Long tagId, final String outputType) {
        super();
        this.reportName = reportName;
        this.reportType = reportType;
        this.reportCategory = reportCategory;
        this.tagId = tagId;
        this.outputType = outputType ;
    }

    public String getReportName() {
        return this.reportName;
    }

    public String getReportType() {
        return this.reportType;
    }

    public String getReportCategory() {
        return this.reportCategory;
    }

    public Long getTagId() {
        return this.tagId;
    }

    public String getOutputType() {
        return this.outputType ;
    }
}
