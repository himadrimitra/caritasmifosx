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
package org.apache.fineract.infrastructure.sms.data;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/** 
 * immutable object representing a tenant's SMS configuration
 **/
public class TenantSmsConfiguration {
    private final String apiAuthUsername;
    private final String apiAuthPassword;
    private final String apiBaseUrl;
    private final String sourceAddress;
    private final Integer smsCredits;
    private final String countryCallingCode;
    private final static String API_AUTH_USERNAME = "API_AUTH_USERNAME";
    private final static String API_AUTH_PASSWORD = "API_AUTH_PASSWORD";
    private final static String API_BASE_URL = "API_BASE_URL";
    private final static String SMS_SOURCE_ADDRESS = "SMS_SOURCE_ADDRESS";
    private final static String SMS_CREDITS = "SMS_CREDITS";
    private final static String COUNTRY_CALLING_CODE = "COUNTRY_CALLING_CODE";
    
    /**
     * @param apiAuthUsername
     * @param apiAuthPassword
     * @param apiBaseUrl
     * @param sourceAddress
     * @param smsCredits
     * @param countryCallingCode
     */
    private TenantSmsConfiguration(String apiAuthUsername, String apiAuthPassword, String apiBaseUrl,
            String sourceAddress, Integer smsCredits, String countryCallingCode) {
        this.apiAuthUsername = apiAuthUsername;
        this.apiAuthPassword = apiAuthPassword;
        this.apiBaseUrl = apiBaseUrl;
        this.sourceAddress = sourceAddress;
        this.smsCredits = smsCredits;
        this.countryCallingCode = countryCallingCode;
    }
    
    /**
     * @param smsConfigurationDataCollection
     * @return {@link TenantSmsConfiguration}
     */
    public static TenantSmsConfiguration instance(final Collection<SmsConfigurationData> smsConfigurationDataCollection) {
        final String apiAuthUsername = getConfigurationValue(smsConfigurationDataCollection, API_AUTH_USERNAME);
        final String apiAuthPassword = getConfigurationValue(smsConfigurationDataCollection, API_AUTH_PASSWORD);
        final String apiBaseUrl = getConfigurationValue(smsConfigurationDataCollection, API_BASE_URL);
        final String sourceAddress = getConfigurationValue(smsConfigurationDataCollection, SMS_SOURCE_ADDRESS);
        final String smsCreditsString = getConfigurationValue(smsConfigurationDataCollection, SMS_CREDITS);
        Integer smsCredits = null;
        
        if (smsCreditsString != null) {
            smsCredits = Integer.parseInt(smsCreditsString);
        }
        
        final String countryCallingCode = getConfigurationValue(smsConfigurationDataCollection, COUNTRY_CALLING_CODE);
        
        return new TenantSmsConfiguration(apiAuthUsername, apiAuthPassword, apiBaseUrl, sourceAddress, smsCredits, 
                countryCallingCode);
    }
    
    /**
     * @param smsConfigurationDataCollection
     * @param configurationName
     * @return {@link SmsConfigurationData} value
     */
    private static String getConfigurationValue(final Collection<SmsConfigurationData> configurationDataCollection, 
            final String configurationName) {
        String configuration = null;
        
        for (SmsConfigurationData configurationData : configurationDataCollection) {
            if (StringUtils.equals(configurationName, configurationData.getName())) {
                
            	configuration = configurationData.getValue();
                break;
            }
        }
        
        return configuration;
    }

    /**
     * @return the apiAuthUsername
     */
    public String getApiAuthUsername() {
        return apiAuthUsername;
    }

    /**
     * @return the apiAuthPassword
     */
    public String getApiAuthPassword() {
        return apiAuthPassword;
    }

    /**
     * @return the apiBaseUrl
     */
    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    /**
     * @return the sourceAddress
     */
    public String getSourceAddress() {
        return sourceAddress;
    }

    /**
     * @return the smsCredits
     */
    public Integer getSmsCredits() {
        return smsCredits;
    }

    /**
     * @return the countryCallingCode
     */
    public String getCountryCallingCode() {
        return countryCallingCode;
    }
}
