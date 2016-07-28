package org.apache.fineract.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.fineract.integrationtests.common.AccountingRuleDataHelper;
import org.apache.fineract.integrationtests.common.AccountingRuleHelper;
import org.apache.fineract.integrationtests.common.AccountingRulesTestData;
import org.apache.fineract.integrationtests.common.OfficeHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class AccountingRuleTest {

	private ResponseSpecification responseSpec;
	private RequestSpecification requestSpec;
	private static HashMap<String, Integer> officeIdMapping = new HashMap<>();
	private final String HeadOffice = "Head Office";
	private final Integer HeadOfficeID = 1;
	private final String OFFICE_1_2 = "Office_1_2";
	private final String OFFICE_1_2_3 = "Office_1_2_3";
	private final String OFFICE_1_2_3_4 = "Office_1_2_3_4";
	private final String OFFICE_1_2_3_4_7 = "Office_1_2_3_4_7";
	private final String OFFICE_1_2_3_8 = "Office_1_2_3_8";
	private final String OFFICE_1_2_6 = "Office_1_2_6";
	private final String OFFICE_1_2_6_10 = "Office_1_2_6_10";
	private final String OFFICE_1_5 = "Office_1_5";
	private final String OFFICE_1_9 = "Office_1_9";

	@Before
	public void setup() {
		Utils.initializeRESTAssured();
		this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
		this.requestSpec.header("Authorization",
				"Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

	}

	public void prepareOffices() {
		officeIdMapping.put(HeadOffice, HeadOfficeID);
		OfficeHelper officeHelper = new OfficeHelper(requestSpec, responseSpec);
		Integer officeId;
		System.out.println("Creating Offices");
		officeId = officeHelper.createOfficeWithOfficeNameAndParentId(OFFICE_1_2, officeIdMapping.get(HeadOffice));
		officeIdMapping.put(OFFICE_1_2, officeId);
		Assert.assertNotNull(OFFICE_1_2, officeId);
		officeId = officeHelper.createOfficeWithOfficeNameAndParentId(OFFICE_1_2_3, officeIdMapping.get(OFFICE_1_2));
		officeIdMapping.put(OFFICE_1_2_3, officeId);
		Assert.assertNotNull(OFFICE_1_2_3, officeId);
		officeId = officeHelper.createOfficeWithOfficeNameAndParentId(OFFICE_1_2_3_4,
				officeIdMapping.get(OFFICE_1_2_3));
		officeIdMapping.put(OFFICE_1_2_3_4, officeId);
		Assert.assertNotNull(OFFICE_1_2_3_4, officeId);
		officeId = officeHelper.createOfficeWithOfficeNameAndParentId(OFFICE_1_2_3_4_7,
				officeIdMapping.get(OFFICE_1_2_3_4));
		officeIdMapping.put(OFFICE_1_2_3_4_7, officeId);
		Assert.assertNotNull(OFFICE_1_2_3_4_7, officeId);
		officeId = officeHelper.createOfficeWithOfficeNameAndParentId(OFFICE_1_2_3_8,
				officeIdMapping.get(OFFICE_1_2_3));
		officeIdMapping.put(OFFICE_1_2_3_8, officeId);
		Assert.assertNotNull(OFFICE_1_2_3_8, officeId);
		officeId = officeHelper.createOfficeWithOfficeNameAndParentId(OFFICE_1_2_6, officeIdMapping.get(OFFICE_1_2));
		officeIdMapping.put(OFFICE_1_2_6, officeId);
		Assert.assertNotNull(OFFICE_1_2_6, officeId);
		officeId = officeHelper.createOfficeWithOfficeNameAndParentId(OFFICE_1_2_6_10,
				officeIdMapping.get(OFFICE_1_2_6));
		officeIdMapping.put(OFFICE_1_2_6_10, officeId);
		Assert.assertNotNull(OFFICE_1_2_6_10, officeId);
		officeId = officeHelper.createOfficeWithOfficeNameAndParentId(OFFICE_1_5, officeIdMapping.get(HeadOffice));
		officeIdMapping.put(OFFICE_1_5, officeId);
		Assert.assertNotNull(OFFICE_1_5, officeId);
		officeId = officeHelper.createOfficeWithOfficeNameAndParentId(OFFICE_1_9, officeIdMapping.get(HeadOffice));
		officeIdMapping.put(OFFICE_1_9, officeId);
		Assert.assertNotNull(OFFICE_1_9, officeId);
		System.out.println("the map is " + new Gson().toJson(officeIdMapping));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void TestCreateOffices() {
		prepareOffices();
		createAccountingRules();

		String json = getRulesForOfficeId(officeIdMapping.get(OFFICE_1_2_6_10), true);
		List<AccountingRuleDataHelper> accountingRules = new ArrayList<>();
		accountingRules = AccountingRulesTestData.getData(json);
		List<String> rules = getRulesFromList(accountingRules);
		if (rules.containsAll(AccountingRulesTestData.rulesForOFFICE_1_2_6_10)) {
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void TestCenter() {
		String json = getRulesForOfficeId(officeIdMapping.get(HeadOffice), true);
		List<AccountingRuleDataHelper> accountingRules = new ArrayList<>();
		accountingRules = AccountingRulesTestData.getData(json);
		List<String> rules = getRulesFromList(accountingRules);
		if (rules.containsAll(AccountingRulesTestData.rulesForHeadOffice1)) {
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void TestCenter1_9() {
		String json = getRulesForOfficeId(officeIdMapping.get(OFFICE_1_9), true);
		List<AccountingRuleDataHelper> accountingRules = new ArrayList<>();
		accountingRules = AccountingRulesTestData.getData(json);
		List<String> rules = getRulesFromList(accountingRules);
		if (rules.containsAll(AccountingRulesTestData.rulesForOffice1_9)) {
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void TestCenter1_9RuleNotInherited() {
		String json = getRulesForOfficeId(officeIdMapping.get(OFFICE_1_9), false);
		List<AccountingRuleDataHelper> accountingRules = new ArrayList<>();
		accountingRules = AccountingRulesTestData.getData(json);
		List<String> rules = getRulesFromList(accountingRules);
		if (rules.containsAll(AccountingRulesTestData.rulesNotInheritedForOffice1_9)) {
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void TestCenter1_2_3_4_7RuleNotInherited() {
		String json = getRulesForOfficeId(officeIdMapping.get(OFFICE_1_2_3_4_7), false);
		List<AccountingRuleDataHelper> accountingRules = new ArrayList<>();
		accountingRules = AccountingRulesTestData.getData(json);
		List<String> rules = getRulesFromList(accountingRules);
		Assert.assertTrue(rules.isEmpty());
	}

	public List<String> getRulesFromList(List<AccountingRuleDataHelper> acccountingRule) {
		List<String> rules = new ArrayList<>();
		if (acccountingRule.size() > 0) {
			Iterator<AccountingRuleDataHelper> iteratory = acccountingRule.iterator();

			while (iteratory.hasNext()) {
				AccountingRuleDataHelper rule = iteratory.next();
				rules.add(rule.getName());
			}
		}
		System.out.println("the rules are "+rules);
		return rules;
	}

	public String getRulesForOfficeId(final Integer officeId, final boolean isInheritedToChildOffices) {
		AccountingRuleHelper accountingRuleHelper = new AccountingRuleHelper(requestSpec, responseSpec);
		return accountingRuleHelper.getRules(officeId, isInheritedToChildOffices);
	}

	public void createAccountingRules() {
		AccountingRuleHelper accountingRuleHelper = new AccountingRuleHelper(requestSpec, responseSpec);
		accountingRuleHelper.createAccountingRules("Rule for Head Office", officeIdMapping.get(HeadOffice), true);
		accountingRuleHelper.createAccountingRules("Loan Disbersal rule", officeIdMapping.get(OFFICE_1_2), false);
		accountingRuleHelper.createAccountingRules("Accounting Rule 1", officeIdMapping.get(OFFICE_1_2_3), true);
		accountingRuleHelper.createAccountingRules("Rule for office_1_2_3_8", officeIdMapping.get(OFFICE_1_2_3_8),
				true);
		accountingRuleHelper.createAccountingRules("Rule 1 for office_1_2_6", officeIdMapping.get(OFFICE_1_2_6), false);
		accountingRuleHelper.createAccountingRules("Rule 2 for office_1_2_6", officeIdMapping.get(OFFICE_1_2_6), false);
		accountingRuleHelper.createAccountingRules("Rule for office_1_2_6_10", officeIdMapping.get(OFFICE_1_2_6_10),
				true);
		accountingRuleHelper.createAccountingRules("Rule 1 for office_1_9", officeIdMapping.get(OFFICE_1_9), true);
	}
}
