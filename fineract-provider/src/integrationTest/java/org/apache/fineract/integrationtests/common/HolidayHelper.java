/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.integrationtests.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSender;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings({ "unused", "rawtypes" })
public class HolidayHelper {

    private static final String HOLIDAYS_URL = "/fineract-provider/api/v1/holidays";
    private static final String CREATE_HOLIDAY_URL = HOLIDAYS_URL + "?" + Utils.TENANT_IDENTIFIER;

    private static final String OFFICE_ID = "1";

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    public HolidayHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

	public static String getCreateHolidayDataAsJSON(final boolean extendRepaymentReschedule) {
		final HashMap<String, Object> map = new HashMap<>();
		List<HashMap<String, String>> offices = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> officeMap = new HashMap<>();
		officeMap.put("officeId", OFFICE_ID);
		offices.add(officeMap);

		map.put("offices", offices);
		map.put("locale", "en");
		map.put("dateFormat", "dd MMMM yyyy");
		map.put("name", Utils.randomNameGenerator("HOLIDAY_", 5));
		map.put("fromDate", "01 April 2013");
		map.put("toDate", "01 April 2013");
		if (!extendRepaymentReschedule) {
			map.put("repaymentsRescheduledTo", "08 April 2013");
		}
		if (!extendRepaymentReschedule) {
			map.put("reshedulingType", 2);
		} else {
			map.put("reshedulingType", 1);
		}
		map.put("extendRepaymentReschedule", extendRepaymentReschedule);

		String HolidayCreateJson = new Gson().toJson(map);
		System.out.println(HolidayCreateJson);
		return HolidayCreateJson;
	}
    
    public static String getCreateChristmasHolidayDataAsJSON(final boolean extendRepaymentReschedule) {
        final HashMap<String, Object> map = new HashMap<>();
        List<HashMap<String, String>> offices = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> officeMap = new HashMap<>();
        officeMap.put("officeId", OFFICE_ID);
        offices.add(officeMap);

        map.put("offices", offices);
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("name", Utils.randomNameGenerator("HOLIDAY_", 5));
        map.put("fromDate", "19 December 2013");
        map.put("toDate", "30 December 2013");
        if(!extendRepaymentReschedule){
            map.put("repaymentsRescheduledTo", "31 December 2013");
        }
        if(!extendRepaymentReschedule){
        map.put("reshedulingType", 2);
        }else{
        	map.put("reshedulingType", 1);
        }
        map.put("extendRepaymentReschedule", extendRepaymentReschedule);

        String HolidayCreateJson = new Gson().toJson(map);
        System.out.println(HolidayCreateJson);
        return HolidayCreateJson;
    }
    
	public static String getCreateDusseraHolidayDataAsJSON(final boolean extendRepaymentReschedule) {
		final HashMap<String, Object> map = new HashMap<>();
		List<HashMap<String, String>> offices = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> officeMap = new HashMap<>();
		officeMap.put("officeId", OFFICE_ID);
		offices.add(officeMap);

		map.put("offices", offices);
		map.put("locale", "en");
		map.put("dateFormat", "dd MMMM yyyy");
		map.put("name", Utils.randomNameGenerator("HOLIDAY_", 5));
		map.put("fromDate", "19 November 2013");
		map.put("toDate", "30 November 2013");
		if (!extendRepaymentReschedule) {
			map.put("repaymentsRescheduledTo", "31 November 2013");
		}
		if (!extendRepaymentReschedule) {
			map.put("reshedulingType", 2);
		} else {
			map.put("reshedulingType", 1);
		}
		map.put("extendRepaymentReschedule", extendRepaymentReschedule);
		String HolidayCreateJson = new Gson().toJson(map);
		System.out.println(HolidayCreateJson);
		return HolidayCreateJson;
	}
    
    public String build(final String fromDate, final String toDate, final String repaymentsRescheduledTo, final boolean extendRepaymentReschedule) {
		final HashMap<String, Object> map = new HashMap<>();
		final ArrayList<HashMap<String, Object>> offices = new ArrayList<>();
		final HashMap<String, Object> officesMap = new HashMap<>();
		map.put("locale", "en");
		map.put("dateFormat", "dd MMMM yyyy");
		map.put("name", Utils.randomNameGenerator("HOLIDAYS_", 9));
		map.put("fromDate", fromDate);
		map.put("toDate", toDate);
		map.put("repaymentsRescheduledTo", repaymentsRescheduledTo);
		map.put("description", Utils.randomNameGenerator("HOLIDAYS_", 9));
		officesMap.put("officeId", 1);
		offices.add(officesMap);
		map.put("offices", offices);
		map.put("extendRepaymentReschedule", extendRepaymentReschedule);
		map.put("reshedulingType", 2);
		return new Gson().toJson(map);
	}
    
    public static String getActivateHolidayDataAsJSON() {
        final HashMap<String, String> map = new HashMap<>();
        String activateHoliday = new Gson().toJson(map);
        System.out.println(activateHoliday);
        return activateHoliday;
    }

    public static Integer createHolidays(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        boolean extendRepaymentReschedule = false;
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_HOLIDAY_URL, getCreateHolidayDataAsJSON(extendRepaymentReschedule), "resourceId");
    }
    
    public static Integer createHolidays(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String json) {
        boolean extendRepaymentReschedule = false;
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_HOLIDAY_URL, json, "resourceId");
    }
    
    public static Integer createChristmasHolidaysWithExtendingRepaymentSchedule(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec, boolean extendRapaymentReschedule) {
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_HOLIDAY_URL, getCreateChristmasHolidayDataAsJSON(extendRapaymentReschedule), "resourceId");
    }
    
    public static Integer createDusseraHolidaysWithExtendingRepaymentSchedule(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec, boolean extendRapaymentReschedule) {
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_HOLIDAY_URL, getCreateDusseraHolidayDataAsJSON(extendRapaymentReschedule), "resourceId");
    }
    
    public static Integer activateHolidays(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String holidayID) {
        final String ACTIVATE_HOLIDAY_URL = HOLIDAYS_URL + "/" + holidayID + "?command=activate&" + Utils.TENANT_IDENTIFIER; 
        return Utils.performServerPost(requestSpec, responseSpec, ACTIVATE_HOLIDAY_URL, getActivateHolidayDataAsJSON(), "resourceId");
    }
    
    public static Integer deleteHolidays(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String holidayID) {
        final String ACTIVATE_HOLIDAY_URL = HOLIDAYS_URL + "/" + holidayID + "?" + Utils.TENANT_IDENTIFIER; 
        return Utils.performServerDelete(requestSpec, responseSpec, ACTIVATE_HOLIDAY_URL, "resourceId");
    }
    
    public static HashMap getHolidayById(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String holidayID) {
        final String GET_HOLIDAY_BY_ID_URL = HOLIDAYS_URL + "/" + holidayID + "?" + Utils.TENANT_IDENTIFIER;
        System.out.println("------------------------ RETRIEVING HOLIDAY BY ID -------------------------");
        final HashMap response = Utils.performServerGet(requestSpec, responseSpec, GET_HOLIDAY_BY_ID_URL, "");
        return response;
    }

}