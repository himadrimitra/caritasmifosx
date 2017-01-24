package com.finflux.infrastructure.external.requestreponse.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 23/11/16.
 */
public enum ThirdPartyRequestEntityType {
	BANKTRANSACTION(1, "thirdPartyRequestEntityType.banktransaction");

	private static final Map<Integer, ThirdPartyRequestEntityType> intToEnumMap = new HashMap<>();
	private static final Map<String, ThirdPartyRequestEntityType> stringToEnumMap = new HashMap<>();

	static {
		for (final ThirdPartyRequestEntityType type : ThirdPartyRequestEntityType.values()) {
			intToEnumMap.put(type.value, type);
			stringToEnumMap.put(type.name().toLowerCase(),type);
		}
	}

	private final Integer value;
	private final String code;

	ThirdPartyRequestEntityType(final Integer value, final String code) {
		this.value = value;
		this.code = code;
	}

	public static ThirdPartyRequestEntityType fromInt(final int i) {
		final ThirdPartyRequestEntityType type = intToEnumMap.get(Integer.valueOf(i));
		return type;
	}

	public static ThirdPartyRequestEntityType fromString(final String str) {
		if(str == null){
			return  null;
		}
		final ThirdPartyRequestEntityType type = stringToEnumMap.get(str.toLowerCase());
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
