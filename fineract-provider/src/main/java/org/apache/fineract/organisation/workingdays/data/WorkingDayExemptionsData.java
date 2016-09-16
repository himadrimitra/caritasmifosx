/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.organisation.workingdays.data;

public class WorkingDayExemptionsData {

	private int protfolio_type;

	private int applicableProperty;

	private String expression;

	private int actionToBePerformed;
	
	private int updateType;

	public WorkingDayExemptionsData(int protfolio_type, int applicableProperty, String expression,
			int actionToBePerformed, int updateType) {
		this.protfolio_type = protfolio_type;
		this.applicableProperty = applicableProperty;
		this.expression = expression;
		this.actionToBePerformed = actionToBePerformed;
		this.updateType = updateType;
	}

	public int getProtfolio_type() {
		return this.protfolio_type;
	}

	public int getApplicableProperty() {
		return this.applicableProperty;
	}

	public String getExpression() {
		return this.expression;
	}

	public int getActionToBePerformed() {
		return this.actionToBePerformed;
	}

	public int getupdateType() {
		return this.updateType;
	}
	
}
