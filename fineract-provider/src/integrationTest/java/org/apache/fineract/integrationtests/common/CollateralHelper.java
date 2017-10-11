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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;







import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;


public class CollateralHelper {
        private final RequestSpecification requestSpec;
        private final ResponseSpecification responseSpec;
        private static final String BASE_COLLATERAL_URL = "/fineract-provider/api/v1/collaterals";
        private static final String MAPPING_URL = "/fineract-provider/api/v1/loanproducts/";
        private static final String IDENTIFIER_URL = "?" + Utils.TENANT_IDENTIFIER;
        private static final String CREATE_COLLATERAL_URL = BASE_COLLATERAL_URL+IDENTIFIER_URL;
        private static final String BASE_PLEDGE_URL = "/fineract-provider/api/v1/pledges";
        
        
        public CollateralHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
            this.requestSpec = requestSpec;
            this.responseSpec = responseSpec;
        }
        
        
        
        public static String getTestCollateralAsJSON() {
            final HashMap<String, String> map = new HashMap<>();
            map.put("name", Utils.randomNameGenerator("Collateral_Name_", 5));
            map.put("description", Utils.randomNameGenerator("Description_", 4));
            map.put("typeClassifier", randomIdForRange(1, 2)+"");
            map.put("locale", "en");
            map.put("baseUnitPrice", randomIdForRange(1000, 2222222)+"");
            System.out.println("map : " + map);
            return new Gson().toJson(map);
        }
        
        public static String getTestCollateralQualityStandardsAsJSON(String collateralId) {
            final HashMap<String, String> map = new HashMap<>();
            map.put("collateralId", collateralId);
            map.put("name", Utils.randomNameGenerator("Quality_Standards_Name_", 5));
            map.put("description", Utils.randomNameGenerator("Description_", 4));
            map.put("absolutePrice", randomIdForRange(10000, 2222222)+"");
            map.put("locale", "en");
            return new Gson().toJson(map);
        }
        
        public static String updateCollateralAsJSON() {
            final HashMap<String, String> map = new HashMap<>();
            map.put("description", Utils.randomNameGenerator("Description_modified", 4));
            map.put("typeClassifier", "3");
            map.put("locale", "en");
            map.put("baseUnitPrice","2000");
            System.out.println("modified collateral map : " + map);
            return new Gson().toJson(map);
        }
        
        public static String updateQualityStandardAsJSON(final Integer qualityStandardId) {
            final HashMap<String, String> map = new HashMap<>();
            map.put("description", Utils.randomNameGenerator("Description_modified", 4));
            map.put("name", "Modifed_Quality_Standards_Name_"+qualityStandardId);
            map.put("absolutePrice", "500");
            map.put("locale", "en");
            System.out.println("modified quality standard map : " + map);
            return new Gson().toJson(map);
        }
        
        public static Integer createCollateral(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
            System.out.println("---------------------------------CREATING A COLLATERAL---------------------------------------------");
            return Utils.performServerPost(requestSpec, responseSpec, CREATE_COLLATERAL_URL, getTestCollateralAsJSON(),
                    "resourceId");
        }
        
        public static Integer createProductCollateralMapping(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String collateralId, final String loanProductId) {
            System.out.println("---------------------------------CREATING A PRODUCT COLLATERAL MAPPING ---------------------------------------------");
            final String url = MAPPING_URL+loanProductId+"/collaterals"+IDENTIFIER_URL;
            return Utils.performServerPost(requestSpec, responseSpec, url, getTestProductCollateralMappingAsJSON(collateralId, loanProductId),
                    "subResourceId");
        }
        
        public static String getTestProductCollateralMappingAsJSON(final String collateralId, final String loanProductId) {
            final HashMap<String, String> map = new HashMap<>();
            map.put("collateralId", collateralId);
            map.put("loanProductId", loanProductId);
            return new Gson().toJson(map);
        }
        
        public static String updateProductCollateralMappingAsJson(final String collateralId, final String loanProductId) {
            final HashMap<String, String> map = new HashMap<>();
            map.put("collateralId", collateralId);
            map.put("loanProductId", loanProductId);
            return new Gson().toJson(map);
        }
        
        public static Integer createCollateralQualityStandards(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String collateralId) {
            System.out.println("---------------------------------CREATING A COLLATERAL QUALITY STANDARDS---------------------------------------------");
            return Utils.performServerPost(requestSpec, responseSpec, BASE_COLLATERAL_URL+"/"+collateralId+"/qualitystandards"+IDENTIFIER_URL, getTestCollateralQualityStandardsAsJSON(collateralId),
                    "subResourceId");
        }
        
        public static HashMap<String, Object> updateCollateral(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer collateralId) {
            System.out.println("------------------------------UPDATE A COLLATERAL---------------------------------------------");
            return Utils.performServerPut(requestSpec, responseSpec, (BASE_COLLATERAL_URL+"/"+collateralId+IDENTIFIER_URL), updateCollateralAsJSON(),
                    "changes");
        }
        
        public static HashMap<String, Object> updateProductCollateralMapping(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String productCollateralMappingId, final String collateralId, final String loanProductId) {
            System.out.println("------------------------------UPDATE A Product Collateral Mapping---------------------------------------------");
            final String url = MAPPING_URL+loanProductId+"/collaterals/"+productCollateralMappingId+IDENTIFIER_URL; 
            return Utils.performServerPut(requestSpec, responseSpec, url, updateProductCollateralMappingAsJson(collateralId, loanProductId),
                    "changes");
        }
        
        public HashMap<String, Object> updateCollateralQualityStandard(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer collateralId, final Integer qualityStandardId) {
            System.out.println("------------------------------UPDATE A QUALITY STANDARD---------------------------------------------");
            return Utils.performServerPut(requestSpec, responseSpec, (BASE_COLLATERAL_URL+"/"+collateralId+"/qualitystandards/"+qualityStandardId+IDENTIFIER_URL), updateQualityStandardAsJSON(qualityStandardId),
                    "changes");
        }
        
        @SuppressWarnings("unchecked")
        public static HashMap<String, Object> getCollateral(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
                final String collateralId) {
            List<String> attributeList = new ArrayList<String>();
            attributeList.add("id");
            attributeList.add("name");
            attributeList.add("description");
            attributeList.add("baseUnitPrice");
            attributeList.add("typeClassifier");
            return (HashMap<String, Object>) getCollateral(requestSpec, responseSpec, collateralId, attributeList);
        }
        
        @SuppressWarnings("unchecked")
        public static HashMap<String, Object> getProductCollateralMapping(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
                final String loanProductId, final String productCollateralMappingId) {
            List<String> attributeList = new ArrayList<String>();
            attributeList.add("id");
            attributeList.add("product");
            attributeList.add("collateral");
            final String url = MAPPING_URL+loanProductId+"/collaterals/"+productCollateralMappingId+IDENTIFIER_URL;
            return (HashMap<String, Object>) getProductCollateralMapping(requestSpec, responseSpec, url, attributeList);
        }
        
        public static Object getProductCollateralMapping(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String url,
                final List<String> attributeList) {            
            System.out.println("---------------------------------GET A Product Collateral Mapping---------------------------------------------");
            HashMap<String, Object> productCollateralMapping = new HashMap<String, Object>();
            for (int i = 0; i < attributeList.size(); i++) {                
                productCollateralMapping.put(attributeList.get(i), Utils.performServerGet(requestSpec, responseSpec, url, attributeList.get(i)));
            }
            return productCollateralMapping;
        }
        
        public static Integer deleteProductCollateralMapping(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer loanProductId,
                final Integer productCollateralMappingId) {
            final String url = MAPPING_URL+loanProductId+"/collaterals/"+productCollateralMappingId+IDENTIFIER_URL;
            System.out.println("---------------------------------DELETE PRODUCT COLLATERAL MAPPING ---------------------------------------------");
            return (int)Utils.performServerDelete(requestSpec, responseSpec, url, "subResourceId");
        }
        
        @SuppressWarnings("unchecked")
        public static HashMap<String, Object> getCollateralQualityStandard(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
                final String collateralId,final String qualitystandardId) {
            List<String> attributeList = new ArrayList<String>();
            attributeList.add("id");
            attributeList.add("name");
            attributeList.add("description");
            attributeList.add("absolutePrice");
            attributeList.add("collateralId");
            attributeList.add("createdBy");
            attributeList.add("createdDate");
            final String GET_Quality_standard_URL = BASE_COLLATERAL_URL + "/"+collateralId+"/qualitystandards/"+ qualitystandardId + IDENTIFIER_URL;
            return (HashMap<String, Object>) getCollateralQualityStandard(requestSpec, responseSpec, GET_Quality_standard_URL, attributeList);
        }
        
        public static Object getCollateralQualityStandard(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String url,
                final List<String> attributeList) {
            
            System.out.println("---------------------------------GET A Quality Standard---------------------------------------------");
            HashMap<String, Object> qualityStandard = new HashMap<String, Object>();
            for (int i = 0; i < attributeList.size(); i++) {                
                qualityStandard.put(attributeList.get(i), Utils.performServerGet(requestSpec, responseSpec, url, attributeList.get(i)));
            }
            return qualityStandard;
        }
        
        @SuppressWarnings("unchecked")
        public static List<HashMap<String, Object>> getCollateralWithQualityStandards(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
                final String collateralId) {
            return (List<HashMap<String, Object>>) getCollateralWithQualityStandards(requestSpec, responseSpec, collateralId, "qualityStandards");
        }
        
        public static Object getCollateralWithQualityStandards(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String collateralId,
                final String attributeToBererturned) {
            final String url = BASE_COLLATERAL_URL+"/"+collateralId+IDENTIFIER_URL+"&associations=qualityStandards";
            System.out.println("---------------------------------GET A COLLATERAL WITH QUALITY STANDARDS---------------------------------------------");
            
            return Utils.performServerGet(requestSpec, responseSpec, url, attributeToBererturned);
        }
        
        public static Integer deleteQualityStandards(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer collateralId,
                final Integer qualitystandardId) {
            final String DELETE_Quality_standard_URL = BASE_COLLATERAL_URL + "/"+collateralId+"/qualitystandards/"+ qualitystandardId + IDENTIFIER_URL;
            System.out.println("---------------------------------DELETE QUALITY STANDARDS---------------------------------------------");
            return (int)Utils.performServerDelete(requestSpec, responseSpec, DELETE_Quality_standard_URL, "subResourceId");
        }
        
        public static Object getCollateral(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String collateralId,
                final List<String> attributeList) {
            final String GET_COLLATERAL_URL = BASE_COLLATERAL_URL + "/"+collateralId + IDENTIFIER_URL;
            System.out.println("---------------------------------GET A COLLATERAL---------------------------------------------");
            HashMap<String, Object> collateral = new HashMap<String, Object>();
            for (int i = 0; i < attributeList.size(); i++) {                
                collateral.put(attributeList.get(i), Utils.performServerGet(requestSpec, responseSpec, GET_COLLATERAL_URL, attributeList.get(i)));
            }
            return collateral;
        }
        
        public static Integer createPledge(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer clientId, final Integer loanId,final Integer collateralId, final Integer qualityStandardId, final boolean isAttachToLoan) {
            System.out.println("---------------------------------CREATING A PLEDGE---------------------------------------------");
            return Utils.performServerPost(requestSpec, responseSpec, BASE_PLEDGE_URL+IDENTIFIER_URL, createPledge( clientId, loanId, collateralId, qualityStandardId, isAttachToLoan),
                    "resourceId");
        }
        
        public static String createPledge( final Integer clientId, final Integer loanId,final Integer collateralId, final Integer qualityStandardId, final boolean isAttachToLoan) {
            Map<String, String> [] pledgeDetails = new HashMap[3];
            for(int i=0;i<3;i++){
                pledgeDetails[i] = createPledgeDetails(collateralId, qualityStandardId);
            }
            int systemValue = 0;
            int userValue = 0;
            for(int i=0;i<3;i++){
                systemValue += Integer.valueOf(pledgeDetails[i].get("systemPrice"));
                userValue += Integer.valueOf(pledgeDetails[i].get("userPrice"));
            }             
            //System.out.println(pledgedetailsList.toString());
            final HashMap<String, Object> pledgeMap = new HashMap<>();
            pledgeMap.put("clientId", clientId+"");
            if(isAttachToLoan){
                pledgeMap.put("loanId", loanId+"");
            }
            pledgeMap.put("status", "1");
            pledgeMap.put("systemValue", systemValue+"");
            pledgeMap.put("userValue", userValue+"");
            pledgeMap.put("locale", "en");
            pledgeMap.put("collateralDetails",pledgeDetails);
            System.out.println(new Gson().toJson(pledgeMap));
            return new Gson().toJson(pledgeMap);
        }
        
        public static HashMap<String, String> createPledgeDetails(final Integer collateralId, final Integer qualityStandardId){
            final HashMap<String, String> pledgeDetailsmap = new HashMap<String, String>();
            int randomValue = randomIdForRange(10000, 2222222);
            pledgeDetailsmap.put("collateralId", collateralId+"");
            pledgeDetailsmap.put("qualityStandardId", qualityStandardId+"");
            pledgeDetailsmap.put("description", Utils.randomNameGenerator("Description_", 4));
            pledgeDetailsmap.put("systemPrice", randomValue+"");
            pledgeDetailsmap.put("userPrice", (randomValue-5000)+"");
            return pledgeDetailsmap;
        }
        
        @SuppressWarnings("unchecked")
        public static HashMap<String, Object> getPledge(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer pledgeId) {
            final String url = BASE_PLEDGE_URL+"/"+pledgeId+IDENTIFIER_URL;
            List<String> attributeList = new ArrayList<String>();
            attributeList.add("id");
            attributeList.add("clientId");
            attributeList.add("loanId");
            attributeList.add("sealNumber");
            attributeList.add("pledgeNumber");
            attributeList.add("userValue");
            attributeList.add("systemValue");
            return (HashMap<String, Object>) getPledge(requestSpec, responseSpec, url, attributeList);
        }
        
        public static Object getPledge(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String url,
                final List<String> attributeList) {
            
            System.out.println("---------------------------------GET A PLEDGE---------------------------------------------");
            HashMap<String, Object> pledge = new HashMap<String, Object>();
            for (int i = 0; i < attributeList.size(); i++) {                
                pledge.put(attributeList.get(i), Utils.performServerGet(requestSpec, responseSpec, url, attributeList.get(i)));
            }
            return pledge;
        }
        
        @SuppressWarnings("unchecked")
        public static HashMap<String, Object> updatePledge(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer pledgeId, final int sealNum) {
            final String url = BASE_PLEDGE_URL+"/"+pledgeId+IDENTIFIER_URL;
            return (HashMap<String, Object>)Utils.performServerPut(requestSpec, responseSpec, url, updatePledge(sealNum),
                    "changes");
        }
        
        public static String updatePledge(int sealNum) {
            final HashMap<String, String> map = new HashMap<>();
            map.put("sealNumber", sealNum+"");
            map.put("status", "2");
            map.put("locale", "en");
            map.put("userValue", "2000");
            return new Gson().toJson(map);
        }
        
        @SuppressWarnings("unchecked")
        public static Integer closePledge(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer pledgeId) {
            final String url = BASE_PLEDGE_URL+"/"+pledgeId+IDENTIFIER_URL+"&command=close";
            System.out.println("---------------------------------CLOSE A PLEDGE---------------------------------------------");
            return Utils.performServerPost(requestSpec, responseSpec, url, getClosePledgeAsJson(pledgeId),
                    "resourceId");
        }
        
        public static String getClosePledgeAsJson(final Integer pledgeId) {
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy");
            String date = DATE_FORMAT.format(new Date());
            final HashMap<String, String> map = new HashMap<>();
            map.put("dateFormat", "dd MMMM yyyy");
            map.put("locale", "en");
            map.put("id", ""+pledgeId);
            map.put("closureDate", date);
            System.out.println(new Gson().toJson(map));
            return new Gson().toJson(map);
        }
        
        public static Integer deletePledge(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer pledgeId) {
            final String url = BASE_PLEDGE_URL+"/"+pledgeId+IDENTIFIER_URL;
            System.out.println("---------------------------------DELETE A PLEDGE---------------------------------------------");
            return (int)Utils.performServerDelete(requestSpec, responseSpec, url, "resourceId");
        }
        
        @SuppressWarnings("unchecked")
        public static HashMap<String, Object> getPledgeWithCollateral(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer pledgeId) {
            final String url = BASE_PLEDGE_URL+"/"+pledgeId+IDENTIFIER_URL+"&association=collateralDetails";
            List<String> attributeList = new ArrayList<String>();
            attributeList.add("id");
            attributeList.add("collateralDetailsData");
            return (HashMap<String, Object>) getPledgeWithCollateral(requestSpec, responseSpec, url, attributeList);
        }
        
        public static Object getPledgeWithCollateral(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String url,
                final List<String> attributeList) {
            
            System.out.println("-------------------------------- GET A PLEDGE WITH COLLATERAL---------------------------------------------");
            HashMap<String, Object> pledge = new HashMap<String, Object>();
            for (int i = 0; i < attributeList.size(); i++) {                
                pledge.put(attributeList.get(i), Utils.performServerGet(requestSpec, responseSpec, url, attributeList.get(i)));
            }
            return pledge;
        }
        
        public static int randomIdForRange(final int min, final int max) {
            return min+(int)(Math.random()*max);
        }
}