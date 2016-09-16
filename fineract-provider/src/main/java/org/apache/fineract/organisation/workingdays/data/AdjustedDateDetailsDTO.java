/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.organisation.workingdays.data;

import org.joda.time.LocalDate;

public class AdjustedDateDetailsDTO {

	LocalDate changedScheduleDate;
	LocalDate changedActualRepaymentDate;
	Boolean changeactualMeetingday;

	public AdjustedDateDetailsDTO(LocalDate changedScheduleDate, Boolean changeactualMeetingday) {
		this.changedScheduleDate = changedScheduleDate;
		this.changeactualMeetingday = changeactualMeetingday;
		this.changedActualRepaymentDate = changedScheduleDate;
	}

	public AdjustedDateDetailsDTO(LocalDate changedScheduleDate, LocalDate changedActualRepaymentDate,
			Boolean changeactualMeetingday) {
		this.changedScheduleDate = changedScheduleDate;
		this.changedActualRepaymentDate = changedActualRepaymentDate;
		this.changeactualMeetingday = changeactualMeetingday;
	}
	
	public AdjustedDateDetailsDTO(LocalDate changedScheduleDate, LocalDate changedActualRepaymentDate) {
		this.changedScheduleDate = changedScheduleDate;
		this.changedActualRepaymentDate = changedActualRepaymentDate;
		this.changeactualMeetingday = false;
	}

	public LocalDate getChangedScheduleDate() {
		return this.changedScheduleDate;
	}

	public Boolean getChangeactualMeetingday() {
		return this.changeactualMeetingday;
	}

	public LocalDate getChangedActualRepaymentDate() {
		return this.changedActualRepaymentDate;
	}

}
