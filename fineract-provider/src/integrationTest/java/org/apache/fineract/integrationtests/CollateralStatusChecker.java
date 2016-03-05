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
package org.apache.fineract.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;


public class CollateralStatusChecker {
    
    public static void verifyCollateralUpdated(final HashMap<String, Object> collateralStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING COLLATERAL IS UPDATED ------------------------------------");
        assertEquals((int) collateralStatusHashMap.get("baseUnitPrice"), 2000);
        assertEquals((int) collateralStatusHashMap.get("typeClassifier"), 3);
        System.out.println("Collateral Status:" + collateralStatusHashMap + "\n");
    }
    
    public static void verifyModifiedQualitystandard(final HashMap<String, Object> modifiedQualityStandardMap, final Integer qualityStandardId) {
        System.out.println("\n-------------------------------------- VERIFYING UPDATED QUALITY STANDARD ------------------------------------");
        assertEquals( modifiedQualityStandardMap.get("name"), "Modifed_Quality_Standards_Name_"+qualityStandardId);
        assertEquals((int) modifiedQualityStandardMap.get("absolutePrice"), 500);
        System.out.println("quality standard Status:" + modifiedQualityStandardMap + "\n");
    }
    
    public static void verifyPledgeUpdated(final HashMap<String, Object> modifiedUpdatedMap, final Integer sealNum) {
        System.out.println("\n-------------------------------------- VERIFYING UPDATED PLEDGE ------------------------------------");
        assertEquals((modifiedUpdatedMap.get("sealNumber"))+"",(sealNum)+"");
        System.out.println("Pledge Status:" + modifiedUpdatedMap + "\n");
    }
}
