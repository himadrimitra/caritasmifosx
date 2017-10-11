package com.finflux.risk.creditbureau.provider.data;

/**
 * Created by dhirendra on 17/09/16.
 */
public class CreditBureauReportFile {

    final private String fileName;

    final private byte[] fileContent;

    final private ReportFileType fileType;

    public CreditBureauReportFile(String filename, byte[] content, ReportFileType fileType) {
        this.fileContent = content;
        this.fileName = filename;
        this.fileType = fileType;
    }

    public ReportFileType getFileType() {
        return fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileContent() {
        return fileContent;
    }
}
