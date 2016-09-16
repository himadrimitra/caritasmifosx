/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.organisation.workingdays.service;

import org.joda.time.LocalDate;

public class EpressionUtilsServiceImpl{

	public static boolean isGivenDateisWorkingDayExemption(LocalDate repaymentDate, String expression) {
		expression = expression.replace("in(", "");
		expression = expression.replace(")","");
		String[] DayList = expression.split(",");
		for(String Day : DayList){
			if (Integer.parseInt(Day)==(repaymentDate.getDayOfMonth())){
				return true;
			}
		}
		return false;
	}

}
