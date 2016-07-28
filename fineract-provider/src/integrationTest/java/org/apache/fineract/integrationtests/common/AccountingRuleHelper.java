package org.apache.fineract.integrationtests.common;

import java.util.HashMap;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class AccountingRuleHelper {
	
	private static final String AccountingRules = "/fineract-provider/api/v1/accountingrules";
	private static final String OfficeId = "officeId=";
	private static final String IncludeInheritedRules  = "includeInheritedRules=";
	private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    
    public AccountingRuleHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec){
    	this.responseSpec = responseSpec;
    	this.requestSpec = requestSpec;
    }
    
    public Integer createAccountingRules(final String ruleName, final Integer officeId,final boolean isInheritedToChildOffices) {
		String json = getAsJSON(ruleName,officeId,isInheritedToChildOffices);
		return Utils.performServerPost(this.requestSpec, this.responseSpec,
				AccountingRules + "?" + Utils.TENANT_IDENTIFIER, json,
				CommonConstants.RESPONSE_RESOURCE_ID);
	}
    
    public String getRules(final Integer officeId,final boolean isInheritedToChildOffices){
    	
    	return Utils.performServerGet(this.requestSpec, this.responseSpec, AccountingRules+"?"+OfficeId+officeId+"&"+IncludeInheritedRules+isInheritedToChildOffices+"&"+Utils.TENANT_IDENTIFIER, null);
    }
    
    public String getAsJSON(final String ruleName, final Integer officeId,final boolean inInheritedTochildOffices){
    	final HashMap<String, String> map = new HashMap<>();
		map.put("officeId", String.valueOf(officeId));
		map.put("name", ruleName);
		map.put("description", "description :"+ruleName);
		map.put("isInheritedToChildOffices", inInheritedTochildOffices?"true":"false");
		map.put("accountToCredit", "1");
		map.put("accountToDebit", "2");
    	return new Gson().toJson(map);
    }
}
