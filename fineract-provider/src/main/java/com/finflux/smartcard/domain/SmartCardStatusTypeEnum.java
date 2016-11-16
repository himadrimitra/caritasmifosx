package com.finflux.smartcard.domain;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.smartcard.api.SmartCardApiConstants;

public enum SmartCardStatusTypeEnum {

	INVALID(0, "smartCardStatusType.invalid"),
	PENDING(1, "smartCardStatusType.pending"),
	ACTIVE(2, "smartCardStatusType.active"),
	INACTIVE(3, "smartCardStatusType.inactive"),
	CLIENTS(4,"smartCardStatusType.clients"),
	LOANS(5,"smartCardStatusType.loans"),
	SAVINGS(6,"smartCardStatusType.savings");

	/**
	 * Smartcard status Types Enumerations
	 */
	public static final String enumTypePending = "PENDING";
	public static final String enumTypeActive = "ACTIVE";
	public static final String enumTypeInactive = "INACTIVE";

	private final Integer value;
	private final String code;

	private SmartCardStatusTypeEnum(final Integer value, final String code) {
		this.value = value;
		this.code = code;
	}

	public Integer getValue() {
		return this.value;
	}

	public String getCode() {
		return this.code;
	}
	
	public boolean hasStateOf(final SmartCardStatusTypeEnum state) {
        return this.value.equals(state.getValue());
    }

	public static SmartCardStatusTypeEnum fromInt(final Integer frequency) {
		SmartCardStatusTypeEnum smartCardStatusTypeEnums = SmartCardStatusTypeEnum.INVALID;
		if (frequency != null) {
			switch (frequency) {
			case 1:
				smartCardStatusTypeEnums = SmartCardStatusTypeEnum.PENDING;
				break;
			case 2:
				smartCardStatusTypeEnums = SmartCardStatusTypeEnum.ACTIVE;
				break;
			case 3:
				smartCardStatusTypeEnums = SmartCardStatusTypeEnum.INACTIVE;
				break;
			case 4:
				smartCardStatusTypeEnums = SmartCardStatusTypeEnum.CLIENTS;
				break;
			case 5:
				smartCardStatusTypeEnums = SmartCardStatusTypeEnum.LOANS;
				break;
			case 6:
				smartCardStatusTypeEnums = SmartCardStatusTypeEnum.SAVINGS;
				break;
			}
		}
		return smartCardStatusTypeEnums;
	}

	public static Object[] integerValues() {
		final List<Integer> values = new ArrayList<>();
		for (final SmartCardStatusTypeEnum enumType : values()) {
			values.add(enumType.getValue());
		}
		return values.toArray();
	}

	public static Object[] codeValues() {
		final List<String> codes = new ArrayList<>();
		for (final SmartCardStatusTypeEnum enumType : values()) {
			codes.add(enumType.getCode());
		}
		return codes.toArray();
	}
	
	public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> smartCardTypeOptions = new ArrayList<>();
        for (final SmartCardStatusTypeEnum enumType : values()) {
            final EnumOptionData enumOptionData = smartCardEntity(enumType.getValue());
            if (enumOptionData != null) {
            	smartCardTypeOptions.add(enumOptionData);
            }
        }
        return smartCardTypeOptions;
    }
	public static EnumOptionData smartCardEntity(final int id) {
        return smartCardEntity(SmartCardStatusTypeEnum.fromInt(id));
    }

	
	 public static EnumOptionData smartCardEntity(final SmartCardStatusTypeEnum type) {
	        EnumOptionData optionData = null;
	        switch (type) {
	            case PENDING:
	                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SmartCardStatusTypeEnum.enumTypePending);
	            break;
	            case ACTIVE:
	                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SmartCardStatusTypeEnum.enumTypeActive);
	            break;
	            case INACTIVE:
	                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SmartCardStatusTypeEnum.enumTypeInactive);
	            break;
	            case CLIENTS:
	                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SmartCardApiConstants.enumTypeClients);
	            break;
	            case LOANS:
	                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SmartCardApiConstants.enumTypeLoans);
	            break;
	            case SAVINGS:
	                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SmartCardApiConstants.enumTypeSavings);
	            break;
	          
	            default:
	            break;

	        }
	        return optionData;
	    }
	 
	 public boolean isPending() {
	        return this.value.equals(SmartCardStatusTypeEnum.PENDING.getValue());
	    }
	 public boolean isActive() {
	        return this.value.equals(SmartCardStatusTypeEnum.ACTIVE.getValue());
	    }
	 public boolean isInactive() {
	        return this.value.equals(SmartCardStatusTypeEnum.INACTIVE.getValue());
	    }
	 public boolean isClients() {
	        return this.value.equals(SmartCardStatusTypeEnum.CLIENTS.getValue());
	    }
	 public boolean isLoans() {
	        return this.value.equals(SmartCardStatusTypeEnum.LOANS.getValue());
	    }
	 public boolean isSavings() {
	        return this.value.equals(SmartCardStatusTypeEnum.SAVINGS.getValue());
	    }
	 
	 private static final Map<String, SmartCardStatusTypeEnum> entityTypeNameToEnumMap = new HashMap<>();

	    static {
	        for (final SmartCardStatusTypeEnum entityType : SmartCardStatusTypeEnum.values()) {
	            entityTypeNameToEnumMap.put(entityType.name().toLowerCase(), entityType);
	        }
	    }

	    public static SmartCardStatusTypeEnum getEntityType(String entityType) {
	        return entityTypeNameToEnumMap.get(entityType.toLowerCase());
	    }
}
