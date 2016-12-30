package org.apache.fineract.integrationtests;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.integrationtests.common.CenterDomain;
import org.apache.fineract.integrationtests.common.CenterHelper;
import org.apache.fineract.integrationtests.common.CgtHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.GlobalConfigurationHelper;
import org.apache.fineract.integrationtests.common.OfficeHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.organisation.StaffHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class CgtIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private CgtHelper cgtHelper;
    private GlobalConfigurationHelper globalConfigurationHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

    }

    @Test
    public void testCgtTest() {

        int officeId = new OfficeHelper(requestSpec, responseSpec).createOffice("01 July 2007");
        String name = "TestFullCreation" + new Timestamp(new java.util.Date().getTime());
        String externalId = Utils.randomStringGenerator("ID_", 7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        int staffId = StaffHelper.createStaff(requestSpec, responseSpec);
        String activationDate = "01 July 2007";

        final List<String> clientIds = new ArrayList<>();
        final HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        for (int clientCount = 0; clientCount < 2; clientCount++) {
            final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec, activationDate,
                    String.valueOf(officeId));
            clientIds.add(clientID.toString());
        }
        map.put("clientMembers", clientIds);
        int[] groupMembers = generateGroupMembers(1, officeId, map);

        int resourceId = CenterHelper.createCenter(name, officeId, externalId, staffId, groupMembers, requestSpec, responseSpec);
        CenterDomain center = CenterHelper.retrieveByID(resourceId, requestSpec, responseSpec);
        String entityId = center.getId().toString();

        Assert.assertNotNull(center);
        this.globalConfigurationHelper = new GlobalConfigurationHelper(this.requestSpec, this.responseSpec);
        
        // Retrieving All Global Configuration details
        final ArrayList<HashMap> globalConfig = this.globalConfigurationHelper.getAllGlobalConfigurations(this.requestSpec,
                this.responseSpec);
        Assert.assertNotNull(globalConfig);
        String []configNames = {"enable-cgt", "min-cgt-days", "max-cgt-days"}; 
        
        for (int i = 0; i < configNames.length; i++) {
            for (Integer configIndex = 0; configIndex < globalConfig.size(); configIndex++) {
                if (globalConfig.get(configIndex).get("name").equals(configNames[i])) {
                    Integer configId = (Integer) globalConfig.get(configIndex).get("id");
                    Assert.assertNotNull(configId);
                    Boolean enabled = (Boolean) globalConfig.get(configIndex).get("enabled");
                    if (!enabled) {
                        enabled = true;
                    }
                    configId = this.globalConfigurationHelper.updateEnabledFlagForGlobalConfiguration(this.requestSpec, this.responseSpec,
                            configId.toString(), enabled);
                    break;
                }
            }
        }
       
        
        
        this.cgtHelper = new CgtHelper(this.requestSpec, this.responseSpec);
        String cgtId = CgtHelper.createCgt(requestSpec, responseSpec, entityId, String.valueOf(staffId), clientIds);
        Assert.assertNotNull(cgtId);

        HashMap<String, String> status = (HashMap<String, String>) CgtHelper.getCgt(this.requestSpec, this.responseSpec, cgtId, "status");
        Assert.assertNotNull(status);
        assertEquals(status.get("id"), 1);

        HashMap<String, List<Integer>> response = CgtHelper.createCgtDay(requestSpec, responseSpec, cgtId, String.valueOf(staffId));
        Assert.assertNotNull(response);
        assertEquals(response.get("numberOfEntitiesCreated"), 3);
        List<Integer> cgtDayIds = response.get("resourceIds");

        HashMap<String, String> inProgressStatus = (HashMap<String, String>) CgtHelper.getCgt(this.requestSpec, this.responseSpec, cgtId,
                "status");
        Assert.assertNotNull(status);
        assertEquals(inProgressStatus.get("id"), 2);

        Map<String, List<Map<String, Object>>> clientAttendances = new HashMap<String, List<Map<String, Object>>>(clientIds.size());
        List<Map<String, Object>> clatt = new ArrayList<>();
        for (String clientId : clientIds) {
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("id", clientId);
            map2.put("attendanceType", "1");
            clatt.add(map2);
        }
        clientAttendances.put("clientIds", clatt);
        for (Integer cgtDayId : cgtDayIds) {
            HashMap<String, String> cgtDayComplete = CgtHelper
                    .completeCgtDay(requestSpec, responseSpec, cgtId, cgtDayId, clientAttendances);
            Assert.assertNotNull(cgtDayComplete);
        }

        HashMap<String, String> cgtComplete = CgtHelper.completeCgt(this.requestSpec, this.responseSpec, cgtId);
        Assert.assertNotNull(cgtComplete);
        HashMap<String, String> completeStatus = (HashMap<String, String>) CgtHelper.getCgt(this.requestSpec, this.responseSpec, cgtId,
                "status");
        Assert.assertNotNull(status);
        assertEquals(completeStatus.get("id"), 3);

    }

    private int[] generateGroupMembers(int size, int officeId, HashMap<String, List<String>> ClientMembers) {
        int[] groupMembers = new int[size];
        for (int i = 0; i < groupMembers.length; i++) {
            final HashMap<String, Object> map = new HashMap<>();
            map.put("officeId", "" + officeId);
            map.put("name", Utils.randomStringGenerator("Group_Name_", 5));
            map.put("externalId", Utils.randomStringGenerator("ID_", 7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
            map.put("dateFormat", "dd MMMM yyyy");
            map.put("locale", "en");
            map.put("active", "true");
            map.put("activationDate", "04 March 2011");
            map.putAll(ClientMembers);

            groupMembers[i] = Utils.performServerPost(requestSpec, responseSpec, "/fineract-provider/api/v1/groups?"
                    + Utils.TENANT_IDENTIFIER, new Gson().toJson(map), "groupId");
        }
        return groupMembers;
    }

}
