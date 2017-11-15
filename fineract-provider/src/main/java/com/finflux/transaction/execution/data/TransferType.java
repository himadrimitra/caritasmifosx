package com.finflux.transaction.execution.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 23/11/16.
 */
public enum TransferType {
	FT(1, "transactionType.ft"),
    IMPS(2, "transactionType.imps"),
    NEFT(3, "transactionType.neft"),
    RTGS(4, "transactionType.rtgs");

	private static final Map<Integer, TransferType> intToEnumMap = new HashMap<>();
	private static final Map<String, TransferType> stringToEnumMap = new HashMap<>();

	static {
		for (final TransferType type : TransferType.values()) {
			intToEnumMap.put(type.value, type);
			stringToEnumMap.put(type.name().toLowerCase(),type);
		}
	}

	private final Integer value;
	private final String code;

	TransferType(final Integer value, final String code) {
		this.value = value;
		this.code = code;
	}

	public static TransferType fromInt(final int i) {
		final TransferType type = intToEnumMap.get(Integer.valueOf(i));
		return type;
	}

	public static TransferType fromString(final String str) {
		if(str == null){
			return  null;
		}
		final TransferType type = stringToEnumMap.get(str.toLowerCase());
		return type;
	}

	public EnumOptionData getEnumOptionData() {
		return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.name().toLowerCase());
	}


	public Integer getValue() {
		return this.value;
	}

	public String getCode() {
		return this.code;
	}

}