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
package org.apache.fineract.infrastructure.configuration.service;

import java.util.HashSet;
import java.util.Set;

public class ExternalServicesConstants {

    public static final String S3_SERVICE_NAME = "S3";
    public static final String S3_BUCKET_NAME = "s3_bucket_name";
    public static final String S3_ACCESS_KEY = "s3_access_key";
    public static final String S3_SECRET_KEY = "s3_secret_key";

    public static final String SMTP_SERVICE_NAME = "SMTP_Email_Account";
    public static final String SMTP_USERNAME = "username";
    public static final String SMTP_PASSWORD = "password";
    public static final String SMTP_HOST = "host";
    public static final String SMTP_PORT = "port";
    public static final String SMTP_USE_TLS = "useTLS";

    public static final String AADHAAR_SERVICE_NAME = "Aadhaar_Service";
    public static final String AADHAAR_HOST = "host";
    public static final String PORT = "port";
    public static final String CERTIFICATE = "certificate_type";
    public static final String SACODE = "saCode";
    public static final String SALTKEY = "saltKey";
    /*
     * Highmark related constants -begin
     */

    public static final String HIGHMARK_SERVICE_NAME = "HIGHMARK";
    public static final String HIGHMARK_PRODUCTTYP = "PRODUCTTYP";
    public static final String HIGHMARK_PRODUCTVER = "PRODUCTVER";
    public static final String HIGHMARK_REQMBR = "REQMBR";
    public static final String HIGHMARK_SUBMBRID = "SUBMBRID";
    public static final String HIGHMARK_REQVOLTYP = "REQVOLTYP";
    public static final String HIGHMARK_TESTFLG = "TESTFLG";
    public static final String HIGHMARK_USERID = "USERID";
    public static final String HIGHMARK_PWD = "PWD";
    public static final String HIGHMARK_AUTHFLG = "AUTHFLG";
    public static final String HIGHMARK_AUTHTITLE = "AUTHTITLE";
    public static final String HIGHMARK_RESFRMT = "RESFRMT";
    public static final String HIGHMARK_MEMBERPREOVERRIDE = "MEMBERPREOVERRIDE";
    public static final String HIGHMARK_RESFRMTEMBD = "RESFRMTEMBD";
    public static final String HIGHMARK_LOSNAME = "LOSNAME";
    public static final String HIGHMARK_URL = "URL";
    public static final String HIGHMARK_CREDTRPTID = "CREDTRPTID";
    public static final String HIGHMARK_CREDTREQTYP = "CREDTREQTYP";
    public static final String HIGHMARK_CREDTINQPURPSTYP = "CREDTINQPURPSTYP";
    public static final String HIGHMARK_CREDTINQPURPSTYPDESC = "CREDTINQPURPSTYPDESC";
    public static final String HIGHMARK_CREDITINQUIRYSTAGE = "CREDITINQUIRYSTAGE";
    public static final String HIGHMARK_CREDTRPTTRNDTTM = "CREDTRPTTRNDTTM";
    public static final String HIGHMARK_ORDEROFREQUEST = "ORDEROFREQUEST";
    public static final String HIGHMARK_HIGHMARKQUERY = "HIGHMARKQUERY";

    public static final String DOCUMENT_TYPE_ID01_PASSPORT = "DOCUMENT_TYPE_ID01_PASSPORT";
    public static final String DOCUMENT_TYPE_ID02_VOTER_ID = "DOCUMENT_TYPE_ID02_VOTER_ID";
    public static final String DOCUMENT_TYPE_ID03_UID = "DOCUMENT_TYPE_ID03_UID";
    public static final String DOCUMENT_TYPE_ID04_OTHER = "DOCUMENT_TYPE_ID04_OTHER";
    public static final String DOCUMENT_TYPE_ID05_RATION_CARD = "DOCUMENT_TYPE_ID05_RATION_CARD";
    public static final String DOCUMENT_TYPE_ID06_DRIVING_CARD = "DOCUMENT_TYPE_ID06_DRIVING_CARD";
    public static final String DOCUMENT_TYPE_ID07_PAN = "DOCUMENT_TYPE_ID07_PAN";

    public static final String ADDRESS_TYPE_D01_RESIDENCE = "ADDRESS_TYPE_D01_RESIDENCE";
    public static final String ADDRESS_TYPE_D02_COMPANY = "ADDRESS_TYPE_D02_COMPANY";
    public static final String ADDRESS_TYPE_D03_RESCUMOFF = "ADDRESS_TYPE_D03_RESCUMOFF";
    public static final String ADDRESS_TYPE_D04_PERMANENT = "ADDRESS_TYPE_D04_PERMANENT";
    public static final String ADDRESS_TYPE_D05_CURRENT = "ADDRESS_TYPE_D06_FOREIGN";
    public static final String ADDRESS_TYPE_D06_FOREIGN = "ADDRESS_TYPE_D06_FOREIGN";
    public static final String ADDRESS_TYPE_D07_MILITARY = "ADDRESS_TYPE_D07_MILITARY";
    public static final String ADDRESS_TYPE_D08_OTHER = "ADDRESS_TYPE_D08_OTHER";

    public static final String GENDER_TYPE_MALE = "GENDER_TYPE_MALE";
    public static final String GENDER_TYPE_FEMALE = "GENDER_TYPE_FEMALE";
    public static final String RELATIONSHIP_TYPE_SPOUSE = "RELATIONSHIP_TYPE_SPOUSE";

    public static final String RELATIONSHIP_TYPE_K01_FATHER = "RELATIONSHIP_TYPE_K01_FATHER";
    public static final String RELATIONSHIP_TYPE_K02_HUSBAND = "RELATIONSHIP_TYPE_K02_HUSBAND";
    public static final String RELATIONSHIP_TYPE_K03_MOTHER = "RELATIONSHIP_TYPE_K03_MOTHER";
    public static final String RELATIONSHIP_TYPE_K04_SON = "RELATIONSHIP_TYPE_K04_SON";
    public static final String RELATIONSHIP_TYPE_K05_DAUGHTER = "RELATIONSHIP_TYPE_K05_DAUGHTER";
    public static final String RELATIONSHIP_TYPE_K06_WIFE = "RELATIONSHIP_TYPE_K06_WIFE";
    public static final String RELATIONSHIP_TYPE_K07_BROTHER = "RELATIONSHIP_TYPE_K07_BROTHER";
    public static final String RELATIONSHIP_TYPE_K08_MOTHER_IN_LAW = "RELATIONSHIP_TYPE_K08_MOTHER_IN_LAW";
    public static final String RELATIONSHIP_TYPE_K09_FATHER_IN_LAW = "RELATIONSHIP_TYPE_K09_FATHER_IN_LAW";
    public static final String RELATIONSHIP_TYPE_K10_DAUGHTER_IN_LAW = "RELATIONSHIP_TYPE_K10_DAUGHTER_IN_LAW";
    public static final String RELATIONSHIP_TYPE_K11_SISTER_IN_LAW = "RELATIONSHIP_TYPE_K11_SISTER_IN_LAW";
    public static final String RELATIONSHIP_TYPE_K12_SON_IN_LAW = "RELATIONSHIP_TYPE_K12_SON_IN_LAW";
    public static final String RELATIONSHIP_TYPE_K13_BROTHER_IN_LAW = "RELATIONSHIP_TYPE_K13_BROTHER_IN_LAW";
    public static final String RELATIONSHIP_TYPE_K15_OTHER = "RELATIONSHIP_TYPE_K15_OTHER";

    //Equifax related constants 
    
    public static final String EQUIFAX_SERVICE_NAME = "EQUIFAX";
    
    public final static String CUSTOMER_ID = "CUSTOMER_ID" ;
    
    public final static String USER_ID = "USER_ID" ;
    
    public final static String PASSWORD = "PASSWORD" ;
    
    public final static String MEMBER_NUMBER = "MEMBER_NUMBER" ;
    
    public final static String SECURITY_CODE = "SECURITY_CODE" ;
    
    public final static String PRODUCT_CODE = "PRODUCT_CODE" ;
    
    public final static String PRODUCT_VERSION = "PRODUCT_VERSION" ;
    
    public final static String CUSTOMER_REFERENCE_NO = "CUSTOMER_REFERENCE_NO" ;
    
    public final static String EQUIFAX_URL = "URL" ;
    
    public final static String QNAME = "QNAME" ;
    
    public final static String QNAME_VERSION = "QNAME_VERSION" ;
    
    public static final String DOCUMENT_TYPE_PASSPORT = "DOCUMENT_TYPE_PASSPORT";
    public static final String DOCUMENT_TYPE_VOTER_ID = "DOCUMENT_TYPE_VOTER_ID";
    public static final String DOCUMENT_TYPE_UID = "DOCUMENT_TYPE_UID";
    public static final String DOCUMENT_TYPE_RATION_CARD = "DOCUMENT_TYPE_RATION_CARD";
    public static final String DOCUMENT_TYPE_DRIVING_CARD = "DOCUMENT_TYPE_DRIVING_CARD";
    public static final String DOCUMENT_TYPE_PAN = "DOCUMENT_TYPE_PAN";
    public static final String DOCUMENT_TYPE_OTHER = "DOCUMENT_TYPE_OTHER";
    
    
    public static final String RELATIONSHIP_TYPE_FATHER = "RELATIONSHIP_TYPE_FATHER";
    public static final String RELATIONSHIP_TYPE_HUSBAND = "RELATIONSHIP_TYPE_HUSBAND";
    public static final String RELATIONSHIP_TYPE_SON = "RELATIONSHIP_TYPE_SON";
    public static final String RELATIONSHIP_TYPE_BROTHER = "RELATIONSHIP_TYPE_BROTHER";
    public static final String RELATIONSHIP_TYPE_FATHER_IN_LAW = "RELATIONSHIP_TYPE_FATHER_IN_LAW";
    public static final String RELATIONSHIP_TYPE_SON_IN_LAW = "RELATIONSHIP_TYPE_SON_IN_LAW";
    public static final String RELATIONSHIP_TYPE_BROTHER_IN_LAW = "RELATIONSHIP_TYPE_BROTHER_IN_LAW";
    public static final String RELATIONSHIP_TYPE_MOTHER = "RELATIONSHIP_TYPE_MOTHER";
    public static final String RELATIONSHIP_TYPE_WIFE = "RELATIONSHIP_TYPE_WIFE";
    public static final String RELATIONSHIP_TYPE_SISTER = "RELATIONSHIP_TYPE_SISTER";
    public static final String RELATIONSHIP_TYPE_DAUGHTER = "RELATIONSHIP_TYPE_DAUGHTER";
    public static final String RELATIONSHIP_TYPE_MOTHER_IN_LAW = "RELATIONSHIP_TYPE_MOTHER_IN_LAW";
    public static final String RELATIONSHIP_TYPE_DAUGHTER_IN_LAW = "RELATIONSHIP_TYPE_DAUGHTER_IN_LAW";
    public static final String RELATIONSHIP_TYPE_SISTER_IN_LAW = "RELATIONSHIP_TYPE_SISTER_IN_LAW";
    public static final String RELATIONSHIP_TYPE_OTHER = "RELATIONSHIP_TYPE_OTHER";
    
    //End Equifax
    
    public static final String NACH_SERVICE_NAME = "NACH";
    public static final String PROCESSOR_QUALIFIER = "PROCESSOR_QUALIFIER";
    public static final String CORPORATE_UTILITY_CODE = "CORPORATE_UTILITY_CODE";
    public static final String CORPORATE_UTILITY_NAME = "CORPORATE_UTILITY_NAME";
    public static final String SPONSOR_BANK = "SPONSOR_BANK";
    public static final String SPONSOR_BANK_CODE = "SPONSOR_BANK_CODE";

    public static enum EXTERNALSERVICEPROPERTIES_JSON_INPUT_PARAMS {
        EXTERNAL_SERVICE_ID("external_service_id"), NAME("name"), VALUE("value");

        private final String value;

        private EXTERNALSERVICEPROPERTIES_JSON_INPUT_PARAMS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<>();

        static {
            for (final EXTERNALSERVICEPROPERTIES_JSON_INPUT_PARAMS type : EXTERNALSERVICEPROPERTIES_JSON_INPUT_PARAMS.values()) {
                values.add(type.value);
            }
        }

        public static Set<String> getAllValues() {
            return values;
        }

        @Override
        public String toString() {
            return name().toString().replaceAll("_", " ");
        }

        public String getValue() {
            return this.value;
        }
    }

    public static enum SMTP_JSON_INPUT_PARAMS {
        USERNAME("username"), PASSWORD("password"), HOST("host"), PORT("port"), USETLS("useTLS");

        private final String value;

        private SMTP_JSON_INPUT_PARAMS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<>();

        static {
            for (final SMTP_JSON_INPUT_PARAMS type : SMTP_JSON_INPUT_PARAMS.values()) {
                values.add(type.value);
            }
        }

        public static Set<String> getAllValues() {
            return values;
        }

        @Override
        public String toString() {
            return name().toString().replaceAll("_", " ");
        }

        public String getValue() {
            return this.value;
        }
    }

    public static enum S3_JSON_INPUT_PARAMS {
        S3_ACCESS_KEY("s3_access_key"), S3_BUCKET_NAME("s3_bucket_name"), S3_SECRET_KEY("s3_secret_key");

        private final String value;

        private S3_JSON_INPUT_PARAMS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<>();

        static {
            for (final S3_JSON_INPUT_PARAMS type : S3_JSON_INPUT_PARAMS.values()) {
                values.add(type.value);
            }
        }

        public static Set<String> getAllValues() {
            return values;
        }

        @Override
        public String toString() {
            return name().toString().replaceAll("_", " ");
        }

        public String getValue() {
            return this.value;
        }
    }

    public static enum AADHAAR_JSON_INPUT_PARAMS {
		HOST("host"), PORT("port"), CERTIFICATE("certificate_type"), SACODE("saCode"), SALTKEY("saltKey"), OTPURL(
				"otpUrl"), KYCURL("kycUrl");

        private final String value;

        private AADHAAR_JSON_INPUT_PARAMS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<>();

        static {
            for (final AADHAAR_JSON_INPUT_PARAMS type : AADHAAR_JSON_INPUT_PARAMS.values()) {
                values.add(type.value);
            }
        }

        public static Set<String> getAllValues() {
            return values;
        }

        @Override
        public String toString() {
            return name().toString().replaceAll("_", " ");
        }

        public String getValue() {
            return this.value;
        }
    }

    public static enum NACH_JSON_INPUT_PARAMS {
        PROCESSOR_QUALIFIER("PROCESSOR_QUALIFIER"),
        CORPORATE_UTILITY_CODE("CORPORATE_UTILITY_CODE"),
        CORPORATE_UTILITY_NAME("CORPORATE_UTILITY_NAME"),
        SPONSOR_BANK("SPONSOR_BANK"),
        SPONSOR_BANK_CODE("SPONSOR_BANK_CODE");

        private final String value;

        private NACH_JSON_INPUT_PARAMS(final String value) {
            this.value = value;
        }

        private static final Set<String> values = new HashSet<>();

        static {
            for (final NACH_JSON_INPUT_PARAMS type : NACH_JSON_INPUT_PARAMS.values()) {
                values.add(type.value);
            }
        }

        public static Set<String> getAllValues() {
            return values;
        }

        @Override
        public String toString() {
            return name().toString().replaceAll("_", " ");
        }

        public String getValue() {
            return this.value;
        }
    }
}
