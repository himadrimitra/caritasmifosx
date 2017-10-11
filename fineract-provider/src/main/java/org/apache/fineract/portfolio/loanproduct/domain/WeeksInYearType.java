/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanproduct.domain;

public enum WeeksInYearType {
	Week_52(1, "52"), //
	Week_48(2, "48"); //

	private final Integer value;
	private final String code;

	private WeeksInYearType(final Integer value, final String code) {
		this.value = value;
		this.code = code;
	}

	public Integer getValue() {
		return this.value;
	}

	public String getCode() {
		return this.code;
	}

	public static WeeksInYearType fromInt(final Integer weeks) {
		WeeksInYearType weeksInYearType = WeeksInYearType.Week_52;
		if (weeks != null) {
			switch (weeks) {
			case 1:
				weeksInYearType = Week_52;
				break;
			case 2:
				weeksInYearType = Week_48;
				break;
			}
		}
		return weeksInYearType;
	}

	public static Integer getWeeksInYear(final Integer code) {
		return Integer.parseInt(fromInt(code).getCode());
	}
}
