package com.finflux.fingerprint.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class FingerPrintDataForAuthentication {
	
	private final Collection<EnumOptionData> fingerOptions;
	private final Collection<FingerPrintData> fingerPrintData;
	
	private FingerPrintDataForAuthentication(final Collection<EnumOptionData> fingerOptions,final Collection<FingerPrintData> fingerPrintData){
		this.fingerOptions = fingerOptions;
		this.fingerPrintData = fingerPrintData;
	}
	
	public static FingerPrintDataForAuthentication instance(final Collection<EnumOptionData> fingerOptions,final Collection<FingerPrintData> fingerPrintData){
		return new FingerPrintDataForAuthentication(fingerOptions,fingerPrintData);
	}

	public Collection<EnumOptionData> getFingerOptions() {
		return fingerOptions;
	}

	public Collection<FingerPrintData> getFingerPrintData() {
		return fingerPrintData;
	}
}
