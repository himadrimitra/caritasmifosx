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
package org.mifosplatform.integrationtests.common;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class HookHelper {
	
	private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    private static final String CREATE_HOOK_URL = "/mifosng-provider/api/v1/hooks?" + Utils.TENANT_IDENTIFIER;

    public HookHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }
    
    public Integer createHook(final String payloadURL) {
        System.out.println("---------------------------------CREATING A HOOK---------------------------------------------");
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_HOOK_URL, getTestHookAsJson(payloadURL),
                "resourceId");
    }
    
    public String getTestHookAsJson(final String payloadURL) {
    	final HashMap<String, Object> map = new HashMap<>();
    	map.put("name", "Web");
    	map.put("displayName", Utils.randomNameGenerator("Hook_DisplayName_", 5));
        map.put("isActive", "true");
        final HashMap<String, String> config = new HashMap<>();
        config.put("Content Type", "json");
        config.put("Payload URL", payloadURL);
        map.put("config", config);
        final ArrayList<HashMap<String, String>> events = new ArrayList<>();
        final HashMap<String, String> createOfficeEvent = new HashMap<>();
        createOfficeEvent.put("actionName", "CREATE");
        createOfficeEvent.put("entityName", "OFFICE");
        events.add(createOfficeEvent);
        map.put("events", events);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

}
