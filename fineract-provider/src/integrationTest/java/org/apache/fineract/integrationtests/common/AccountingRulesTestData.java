package org.apache.fineract.integrationtests.common;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import edu.emory.mathcs.backport.java.util.Arrays;

public class AccountingRulesTestData {
	
	static String[] ruleFor10 = new String[]{"Rule for office_1_2_6_10","Rule for Head Office"};
	public static String[] ruleForHeadOffice = new String[]{"Rule for Head Office"};
	public static String[] ruleForOffice1_9 = new String[]{"Rule for Head Office","Rule 1 for office_1_9"};
	public static String[] ruleNotInheritedForOffice1_9 = new String[]{"Rule 1 for office_1_9"};
	@SuppressWarnings("unchecked")
	public static List<String> rulesForOFFICE_1_2_6_10 = Arrays.asList(ruleFor10);
	public static List<String> rulesForHeadOffice1 = Arrays.asList(ruleForHeadOffice);
	public static List<String> rulesForOffice1_9 = Arrays.asList(ruleForOffice1_9);
	public static List<String> rulesNotInheritedForOffice1_9 = Arrays.asList(ruleNotInheritedForOffice1_9);


	public static <T> List<T> stringToArray(String s, Class<T> clazz) {
	   Gson gson = new Gson();
	    Type listType = new TypeToken<ArrayList<T>>(){}.getType();
	    ArrayList<T> list = gson.fromJson(s, listType);
	    return list; //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
	}
	
	public static List<AccountingRuleDataHelper> getData(String json){
	    Gson gson = new Gson();
	    Type type = new TypeToken<List<AccountingRuleDataHelper>>(){}.getType();
	    return gson.fromJson(json, type);     
	}
	
}
