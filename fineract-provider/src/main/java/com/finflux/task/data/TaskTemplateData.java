package com.finflux.task.data;

import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.staff.data.StaffData;

import java.util.Collection;

/**
 * Created by dhirendra on 14/12/16.
 */
public class TaskTemplateData {

	private final Long defaultOfficeId;
	private final Collection<OfficeData> officeOptions;
	private final Collection<StaffData> staffOptions;

	private TaskTemplateData(Long defaultOfficeId, Collection<OfficeData> officeOptions, Collection<StaffData> staffOptions) {
		this.defaultOfficeId = defaultOfficeId;
		this.officeOptions = officeOptions;
		this.staffOptions = staffOptions;
	}

	public static TaskTemplateData template(Long defaultOfficeId,Collection<OfficeData> officeOptions, Collection<StaffData> staffOptions){
		return new TaskTemplateData(defaultOfficeId, officeOptions,staffOptions);
	}
}
