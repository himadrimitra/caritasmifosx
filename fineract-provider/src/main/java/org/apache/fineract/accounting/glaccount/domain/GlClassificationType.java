/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.accounting.glaccount.domain;

public enum GlClassificationType {

	CashGL(1, "glClassificationType.CashGL"), BankGL(2, "glClassificationType.BankGL"), OtherJVGL(3,
			"glClassificationType.OtherJVGL");

	private final Integer value;
	private final String code;

	private GlClassificationType(Integer value, String code) {
		this.value = value;
		this.code = code;
	}

	public Integer getValue() {
		return this.value;
	}

	public String getCode() {
		return this.code;
	}

	public static GlClassificationType fromInt(final Integer frequency) {
		GlClassificationType glClassificationType = GlClassificationType.CashGL;
		if (frequency != null) {
			switch (frequency) {
			case 1:
				glClassificationType = GlClassificationType.CashGL;
				break;
			case 2:
				glClassificationType = GlClassificationType.BankGL;
				break;
			case 3:
				glClassificationType = GlClassificationType.OtherJVGL;
			}
		}
		return glClassificationType;
	}
}
