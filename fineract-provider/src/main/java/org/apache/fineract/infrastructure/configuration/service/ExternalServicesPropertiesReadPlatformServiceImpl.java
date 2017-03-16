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

import org.apache.fineract.infrastructure.configuration.data.EquifaxCredentialsData;
import org.apache.fineract.infrastructure.configuration.data.EquifaxDocumentTypes;
import org.apache.fineract.infrastructure.configuration.data.EquifaxRelationTypes;
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
            String DOCUMENT_TYPE_ID01_PASSPORT = null;
            String DOCUMENT_TYPE_ID02_VOTER_ID = null;
            String DOCUMENT_TYPE_ID03_UID = null;
            String DOCUMENT_TYPE_ID04_OTHER = null;
            String DOCUMENT_TYPE_ID05_RATION_CARD = null;
            String DOCUMENT_TYPE_ID06_DRIVING_CARD = null;
            String DOCUMENT_TYPE_ID07_PAN = null;

            String ADDRESS_TYPE_D01_RESIDENCE = null;
            String ADDRESS_TYPE_D02_COMPANY = null;
            String ADDRESS_TYPE_D03_RESCUMOFF = null;
            String ADDRESS_TYPE_D04_PERMANENT = null;
            String ADDRESS_TYPE_D05_CURRENT = null;
            String ADDRESS_TYPE_D06_FOREIGN = null;
            String ADDRESS_TYPE_D07_MILITARY = null;
            String ADDRESS_TYPE_D08_OTHER = null;

            String GENDER_TYPE_MALE = null;
            String GENDER_TYPE_FEMALE = null;
            String RELATIONSHIP_TYPE_SPOUSE = null;

            String RELATIONSHIP_TYPE_K01_FATHER = null;
            String RELATIONSHIP_TYPE_K02_HUSBAND = null;
            String RELATIONSHIP_TYPE_K03_MOTHER = null;
            String RELATIONSHIP_TYPE_K04_SON = null;
            String RELATIONSHIP_TYPE_K05_DAUGHTER = null;
            String RELATIONSHIP_TYPE_K06_WIFE = null;
            String RELATIONSHIP_TYPE_K07_BROTHER = null;
            String RELATIONSHIP_TYPE_K08_MOTHER_IN_LAW = null;
            String RELATIONSHIP_TYPE_K09_FATHER_IN_LAW = null;
            String RELATIONSHIP_TYPE_K10_DAUGHTER_IN_LAW = null;
            String RELATIONSHIP_TYPE_K11_SISTER_IN_LAW = null;
            String RELATIONSHIP_TYPE_K12_SON_IN_LAW = null;
            String RELATIONSHIP_TYPE_K13_BROTHER_IN_LAW = null;
            String RELATIONSHIP_TYPE_K15_OTHER = null;

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
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.HIGHMARK_HIGHMARKQUERY)) {
                    HIGHMARKQUERY = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_ID01_PASSPORT)) {
                    DOCUMENT_TYPE_ID01_PASSPORT = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_ID02_VOTER_ID)) {
                    DOCUMENT_TYPE_ID02_VOTER_ID = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_ID03_UID)) {
                    DOCUMENT_TYPE_ID03_UID = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_ID04_OTHER)) {
                    DOCUMENT_TYPE_ID04_OTHER = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_ID05_RATION_CARD)) {
                    DOCUMENT_TYPE_ID05_RATION_CARD = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_ID06_DRIVING_CARD)) {
                    DOCUMENT_TYPE_ID06_DRIVING_CARD = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_ID07_PAN)) {
                    DOCUMENT_TYPE_ID07_PAN = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.ADDRESS_TYPE_D01_RESIDENCE)) {
                    ADDRESS_TYPE_D01_RESIDENCE = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.ADDRESS_TYPE_D02_COMPANY)) {
                    ADDRESS_TYPE_D02_COMPANY = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.ADDRESS_TYPE_D03_RESCUMOFF)) {
                    ADDRESS_TYPE_D03_RESCUMOFF = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.ADDRESS_TYPE_D04_PERMANENT)) {
                    ADDRESS_TYPE_D04_PERMANENT = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.ADDRESS_TYPE_D05_CURRENT)) {
                    ADDRESS_TYPE_D05_CURRENT = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.ADDRESS_TYPE_D06_FOREIGN)) {
                    ADDRESS_TYPE_D06_FOREIGN = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.ADDRESS_TYPE_D07_MILITARY)) {
                    ADDRESS_TYPE_D07_MILITARY = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.ADDRESS_TYPE_D08_OTHER)) {
                    ADDRESS_TYPE_D08_OTHER = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.GENDER_TYPE_MALE)) {
                    GENDER_TYPE_MALE = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.GENDER_TYPE_FEMALE)) {
                    GENDER_TYPE_FEMALE = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_SPOUSE)) {
                    RELATIONSHIP_TYPE_SPOUSE = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K01_FATHER)) {
                    RELATIONSHIP_TYPE_K01_FATHER = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K02_HUSBAND)) {
                    RELATIONSHIP_TYPE_K02_HUSBAND = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K03_MOTHER)) {
                    RELATIONSHIP_TYPE_K03_MOTHER = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K04_SON)) {
                    RELATIONSHIP_TYPE_K04_SON = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K05_DAUGHTER)) {
                    RELATIONSHIP_TYPE_K05_DAUGHTER = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K06_WIFE)) {
                    RELATIONSHIP_TYPE_K06_WIFE = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K07_BROTHER)) {
                    RELATIONSHIP_TYPE_K07_BROTHER = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K08_MOTHER_IN_LAW)) {
                    RELATIONSHIP_TYPE_K08_MOTHER_IN_LAW = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K09_FATHER_IN_LAW)) {
                    RELATIONSHIP_TYPE_K09_FATHER_IN_LAW = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K10_DAUGHTER_IN_LAW)) {
                    RELATIONSHIP_TYPE_K10_DAUGHTER_IN_LAW = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K11_SISTER_IN_LAW)) {
                    RELATIONSHIP_TYPE_K11_SISTER_IN_LAW = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K12_SON_IN_LAW)) {
                    RELATIONSHIP_TYPE_K12_SON_IN_LAW = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K13_BROTHER_IN_LAW)) {
                    RELATIONSHIP_TYPE_K13_BROTHER_IN_LAW = rs.getString("value");
                }

                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_K15_OTHER)) {
                    RELATIONSHIP_TYPE_K15_OTHER = rs.getString("value");
                }

            }
            return new HighmarkCredentialsData(PRODUCTTYP, PRODUCTVER, REQMBR, SUBMBRID, REQVOLTYP, TESTFLG, USERID, PWD, AUTHFLG,
                    AUTHTITLE, RESFRMT, MEMBERPREOVERRIDE, RESFRMTEMBD, LOSNAME, URL, CREDTRPTID, CREDTREQTYP, CREDTINQPURPSTYP,
                    CREDTINQPURPSTYPDESC, CREDITINQUIRYSTAGE, CREDTRPTTRNDTTM, ORDEROFREQUEST, HIGHMARKQUERY, DOCUMENT_TYPE_ID01_PASSPORT,
                    DOCUMENT_TYPE_ID02_VOTER_ID, DOCUMENT_TYPE_ID03_UID, DOCUMENT_TYPE_ID04_OTHER, DOCUMENT_TYPE_ID05_RATION_CARD,
                    DOCUMENT_TYPE_ID06_DRIVING_CARD, DOCUMENT_TYPE_ID07_PAN, ADDRESS_TYPE_D01_RESIDENCE, ADDRESS_TYPE_D02_COMPANY,
                    ADDRESS_TYPE_D03_RESCUMOFF, ADDRESS_TYPE_D04_PERMANENT, ADDRESS_TYPE_D05_CURRENT, ADDRESS_TYPE_D06_FOREIGN,
                    ADDRESS_TYPE_D07_MILITARY, ADDRESS_TYPE_D08_OTHER, GENDER_TYPE_MALE, GENDER_TYPE_FEMALE, RELATIONSHIP_TYPE_SPOUSE,
                    RELATIONSHIP_TYPE_K01_FATHER, RELATIONSHIP_TYPE_K02_HUSBAND, RELATIONSHIP_TYPE_K03_MOTHER, RELATIONSHIP_TYPE_K04_SON,
                    RELATIONSHIP_TYPE_K05_DAUGHTER, RELATIONSHIP_TYPE_K06_WIFE, RELATIONSHIP_TYPE_K07_BROTHER,
                    RELATIONSHIP_TYPE_K08_MOTHER_IN_LAW, RELATIONSHIP_TYPE_K09_FATHER_IN_LAW, RELATIONSHIP_TYPE_K10_DAUGHTER_IN_LAW,
                    RELATIONSHIP_TYPE_K11_SISTER_IN_LAW, RELATIONSHIP_TYPE_K12_SON_IN_LAW, RELATIONSHIP_TYPE_K13_BROTHER_IN_LAW,
                    RELATIONSHIP_TYPE_K15_OTHER);
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

	@Override
	public EquifaxCredentialsData getEquifaxCredentials() {
		final ResultSetExtractor<EquifaxCredentialsData> resultSetExtractor = new EquifaxCredentialsDataExtractor();
		final String sql = "SELECT esp.name, esp.value FROM c_external_service_properties esp inner join c_external_service es on esp.external_service_id = es.id where es.name = '"
				+ ExternalServicesConstants.EQUIFAX_SERVICE_NAME + "'";
		final EquifaxCredentialsData equifaxCredentialsData = this.jdbcTemplate.query(sql, resultSetExtractor,
				new Object[] {});
		return equifaxCredentialsData;
	}

	private final static class EquifaxCredentialsDataExtractor implements ResultSetExtractor<EquifaxCredentialsData> {

		@Override
		public EquifaxCredentialsData extractData(ResultSet rs) throws SQLException, DataAccessException {
			Integer customerId = null;
			String userId = null;
			String password = null;
			String memberNumber = null;
			String securityCode = null;
			String productCode = null;
			String productVersion = null;
			String customerReferenceNo = null;
			String qName = null;
			String qNameVersion = null;
			String equifaxUrl = null;
			String documentTypePassport = null;
			String documentTypePan = null;
			String documentTypeVoterId = null;
			String documentTypeAadhar = null;
			String documentTypeRation = null;
			String documentTypeDrivingLicense = null;
			String documentTypeOther = null;
			String relationTypeOther = null;
			String relationTypeSisterInLaw = null;
			String relationTypeDaughterInLaw = null;
			String relationTypeMotherInLaw = null;
			String relationTypeDaughter = null;
			String relationTypeSister = null;
			String relationTypeWife = null;
			String relationTypeMother = null;
			String relationTypeBrotherInLaw = null;
			String relationTypeSonInLaw = null;
			String relationTypeFatherInLaw = null;
			String relationTypeBrother = null;
			String relationTypeSon = null;
			String relationTypeHusband = null;
			String relationTypeFather = null;
			String relationTypeSpouse = null ;
			String genderTypeFemale = null ;
			String genderTypeMale = null ;
			
			while (rs.next()) {
				if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.CUSTOMER_ID)) {
					customerId = rs.getInt("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.USER_ID)) {
					userId = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.PASSWORD)) {
					password = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.MEMBER_NUMBER)) {
					memberNumber = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SECURITY_CODE)) {
					securityCode = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.PRODUCT_CODE)) {
					productCode = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.PRODUCT_VERSION)) {
					productVersion = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.CUSTOMER_REFERENCE_NO)) {
					customerReferenceNo = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.QNAME)) {
					qName = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.QNAME_VERSION)) {
					qNameVersion = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.EQUIFAX_URL)) {
					equifaxUrl = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_PASSPORT)) {
					documentTypePassport = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_VOTER_ID)) {
					documentTypeVoterId = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_UID)) {
					documentTypeAadhar = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_RATION_CARD)) {
					documentTypeRation = rs.getString("value");
				} else if (rs.getString("name")
						.equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_DRIVING_CARD)) {
					documentTypeDrivingLicense = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_PAN)) {
					documentTypePan = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.DOCUMENT_TYPE_OTHER)) {
					documentTypeOther = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_FATHER)) {
					relationTypeFather = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_HUSBAND)) {
					relationTypeHusband = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_SON)) {
					relationTypeSon = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_BROTHER)) {
					relationTypeBrother = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_FATHER_IN_LAW)) {
					relationTypeFatherInLaw = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_SON_IN_LAW)) {
					relationTypeSonInLaw = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_BROTHER_IN_LAW)) {
					relationTypeBrotherInLaw = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_MOTHER)) {
					relationTypeMother = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_WIFE)) {
					relationTypeWife = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_SISTER)) {
					relationTypeSister = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_DAUGHTER)) {
					relationTypeDaughter = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_MOTHER_IN_LAW)) {
					relationTypeMotherInLaw = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_DAUGHTER_IN_LAW)) {
					relationTypeDaughterInLaw = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_SISTER_IN_LAW)) {
					relationTypeSisterInLaw = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_OTHER)) {
					relationTypeOther = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.GENDER_TYPE_FEMALE)) {
					genderTypeFemale = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.GENDER_TYPE_MALE)) {
					genderTypeMale = rs.getString("value");
				}else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.RELATIONSHIP_TYPE_SPOUSE)) {
					relationTypeSpouse = rs.getString("value");
				}
				
			}
			
			
			EquifaxDocumentTypes documentTypes = new EquifaxDocumentTypes(documentTypePassport, documentTypePan,
					documentTypeAadhar, documentTypeVoterId, documentTypeDrivingLicense, documentTypeRation,
					documentTypeOther);
			EquifaxRelationTypes relationTypes = new EquifaxRelationTypes(relationTypeFather, relationTypeHusband,
					relationTypeBrother, relationTypeSon, relationTypeSonInLaw, relationTypeFatherInLaw,
					relationTypeBrotherInLaw, relationTypeMother, relationTypeWife, relationTypeSister,
					relationTypeDaughter, relationTypeDaughterInLaw, relationTypeMotherInLaw, relationTypeSisterInLaw,
					relationTypeSpouse, relationTypeOther);
			return new EquifaxCredentialsData(customerId, userId, password, memberNumber, securityCode, productCode,
					productVersion, customerReferenceNo, equifaxUrl, qName, qNameVersion, documentTypes, relationTypes, genderTypeFemale, genderTypeMale);
		}
    	
    }
}
