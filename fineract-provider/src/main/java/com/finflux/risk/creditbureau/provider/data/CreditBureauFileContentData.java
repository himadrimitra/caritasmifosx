package com.finflux.risk.creditbureau.provider.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;


public class CreditBureauFileContentData {

    private EnumOptionData reportFileType;
    private byte[] fileContent;

    public CreditBureauFileContentData(final EnumOptionData reportFileType, final byte[] fileContent) {
        this.reportFileType = reportFileType;
        this.fileContent = fileContent;
    }

	public CreditBureauFileContentData() {
		this.reportFileType = ReportFileType.reportFileType(ReportFileType.HTML);
		this.fileContent = "Data not present".getBytes();
	}
    
}