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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.configuration.data.ExternalServicesPropertiesData;
import org.apache.fineract.infrastructure.configuration.data.HighmarkCredentialsData;
import org.apache.fineract.infrastructure.configuration.data.S3CredentialsData;
import org.apache.fineract.infrastructure.configuration.data.SMTPCredentialsData;
import org.apache.fineract.infrastructure.configuration.exception.ExternalServiceConfigurationNotFoundException;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ExternalServicesPropertiesReadPlatformServiceImpl implements ExternalServicesPropertiesReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ExternalServicesPropertiesReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class S3CredentialsDataExtractor implements ResultSetExtractor<S3CredentialsData> {

        @Override
        public S3CredentialsData extractData(final ResultSet rs) throws SQLException, DataAccessException {
            String accessKey = null;
            String bucketName = null;
            String secretKey = null;
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.S3_ACCESS_KEY)) {
                    accessKey = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.S3_BUCKET_NAME)) {
                    bucketName = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.S3_SECRET_KEY)) {
                    secretKey = rs.getString("value");
                }
            }
            return new S3CredentialsData(bucketName, accessKey, secretKey);
        }
    }

    private static final class SMTPCredentialsDataExtractor implements ResultSetExtractor<SMTPCredentialsData> {

        @Override
        public SMTPCredentialsData extractData(final ResultSet rs) throws SQLException, DataAccessException {
            String username = null;
            String password = null;
            String host = null;
            String port = "25";
            boolean useTLS = false;

            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMTP_USERNAME)) {
                    username = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMTP_PASSWORD)) {
                    password = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMTP_HOST)) {
                    host = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMTP_PORT)) {
                    port = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMTP_USE_TLS)) {
                    useTLS = Boolean.parseBoolean(rs.getString("value"));
                }
            }
            return new SMTPCredentialsData(username, password, host, port, useTLS);
        }
    }
    
    private static final class HighmarkCredentialsDataExtractor implements ResultSetExtractor<HighmarkCredentialsData> {

        @Override
        public HighmarkCredentialsData extractData(ResultSet rs) throws SQLException, DataAccessException {
            String PRODUCTTYP = null;
            String PRODUCTVER = null;
            String REQMBR = null;
            String SUBMBRID = null;
            String REQVOLTYP = null;
            String TESTFLG = null;
            String USERID = null;
            String PWD = null;
            String AUTHFLG = null;
            String AUTHTITLE = null;
            String RESFRMT = null;
            String MEMBERPREOVERRIDE = null;
            String RESFRMTEMBD = null;
            String LOSNAME = null;
            String URL = null;
            String CREDTRPTID = null;
            String CREDTREQTYP = null;
            String CREDTINQPURPSTYP = null;
            String CREDTINQPURPSTYPDESC = null;
            String CREDITINQUIRYSTAGE = null;
            String CREDTRPTTRNDTTM = null;
            String ORDEROFREQUEST = null;
            String HIGHMARKQUERY = null;
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_AUTHFLG)) {
                    AUTHFLG = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_AUTHTITLE)) {
                    AUTHTITLE = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_LOSNAME)) {
                    LOSNAME = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_MEMBERPREOVERRIDE)) {
                    MEMBERPREOVERRIDE = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_PRODUCTTYP)) {
                    PRODUCTTYP = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_PRODUCTVER)) {
                    PRODUCTVER = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_PWD)) {
                    PWD = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_REQMBR)) {
                    REQMBR = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_REQVOLTYP)) {
                    REQVOLTYP = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_RESFRMT)) {
                    RESFRMT = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_RESFRMTEMBD)) {
                    RESFRMTEMBD = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_SUBMBRID)) {
                    SUBMBRID = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_TESTFLG)) {
                    TESTFLG = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_USERID)) {
                    USERID = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_URL)) {
                    URL = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_CREDTRPTID)) {
                    CREDTRPTID = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_CREDTREQTYP)) {
                    CREDTREQTYP = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_CREDTINQPURPSTYP)) {
                    CREDTINQPURPSTYP = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_CREDTINQPURPSTYPDESC)) {
                    CREDTINQPURPSTYPDESC = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_CREDITINQUIRYSTAGE)) {
                    CREDITINQUIRYSTAGE = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_CREDTRPTTRNDTTM)) {
                    CREDTRPTTRNDTTM = rs.getString("value");
                }
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_ORDEROFREQUEST)) {
                    CREDTRPTTRNDTTM = rs.getString("value");
                }
                if(rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_HIGHMARKQUERY)){
                    HIGHMARKQUERY = rs.getString("value");
                }

            }
            return new HighmarkCredentialsData(PRODUCTTYP, PRODUCTVER, REQMBR, SUBMBRID, REQVOLTYP, TESTFLG, USERID, PWD, AUTHFLG,
                    AUTHTITLE, RESFRMT, MEMBERPREOVERRIDE, RESFRMTEMBD, LOSNAME, URL, CREDTRPTID, CREDTREQTYP, CREDTINQPURPSTYP,
                    CREDTINQPURPSTYPDESC, CREDITINQUIRYSTAGE, CREDTRPTTRNDTTM, ORDEROFREQUEST,HIGHMARKQUERY);
        }

    }


    private static final class ExternalServiceMapper implements RowMapper<ExternalServicesPropertiesData> {

        @Override
        public ExternalServicesPropertiesData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            // TODO Auto-generated method stub
            final String name = rs.getString("name");
            String value = rs.getString("value");
            // Masking the password as we should not send the password back
            if (name != null && "password".equalsIgnoreCase(name)) {
                value = "XXXX";
            }
            return new ExternalServicesPropertiesData(name, value);
        }

    }

    @Override
    public S3CredentialsData getS3Credentials() {
        final ResultSetExtractor<S3CredentialsData> resultSetExtractor = new S3CredentialsDataExtractor();
        final String sql = "SELECT esp.name, esp.value FROM c_external_service_properties esp inner join c_external_service es on esp.external_service_id = es.id where es.name = '"
                + ExternalServicesConstants.S3_SERVICE_NAME + "'";
        final S3CredentialsData s3CredentialsData = this.jdbcTemplate.query(sql, resultSetExtractor, new Object[] {});
        return s3CredentialsData;
    }

    @Override
    public SMTPCredentialsData getSMTPCredentials() {
        // TODO Auto-generated method stub
        final ResultSetExtractor<SMTPCredentialsData> resultSetExtractor = new SMTPCredentialsDataExtractor();
        final String sql = "SELECT esp.name, esp.value FROM c_external_service_properties esp inner join c_external_service es on esp.external_service_id = es.id where es.name = '"
                + ExternalServicesConstants.SMTP_SERVICE_NAME + "'";
        final SMTPCredentialsData smtpCredentialsData = this.jdbcTemplate.query(sql, resultSetExtractor, new Object[] {});
        return smtpCredentialsData;
    }

    @Override
    public Collection<ExternalServicesPropertiesData> retrieveOne(String serviceName) {
        String serviceNameToUse = null;
        switch (serviceName) {
            case "S3":
                serviceNameToUse = ExternalServicesConstants.S3_SERVICE_NAME;
            break;

            case "SMTP":
                serviceNameToUse = ExternalServicesConstants.SMTP_SERVICE_NAME;
            break;
            
            case "Aadhaar":
            	serviceNameToUse = ExternalServicesConstants.AADHAAR_SERVICE_NAME;
            break;

            default:
                throw new ExternalServiceConfigurationNotFoundException(serviceName);
        }
        final ExternalServiceMapper mapper = new ExternalServiceMapper();
        final String sql = "SELECT esp.name, esp.value FROM c_external_service_properties esp inner join c_external_service es on esp.external_service_id = es.id where es.name = '"
                + serviceNameToUse + "'";
        return this.jdbcTemplate.query(sql, mapper, new Object[] {});

    }
    
    @Override
    public HighmarkCredentialsData getHighmarkCredentials() {
        final ResultSetExtractor<HighmarkCredentialsData> resultSetExtractor = new HighmarkCredentialsDataExtractor();
        final String sql = "SELECT esp.name, esp.value FROM c_external_service_properties esp inner join c_external_service es on esp.external_service_id = es.id where es.name = '"
                + ExternalServicesConstants.HIGHMARK_SERVICE_NAME + "'";
        final HighmarkCredentialsData highmarkCredentialsData = this.jdbcTemplate.query(sql, resultSetExtractor, new Object[] {});
        return highmarkCredentialsData;
    }
    
}
