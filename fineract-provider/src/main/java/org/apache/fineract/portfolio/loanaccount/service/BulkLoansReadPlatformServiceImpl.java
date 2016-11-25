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
package org.apache.fineract.portfolio.loanaccount.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.data.StaffAccountSummaryCollectionData;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.accountdetails.service.AccountDetailsReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.ClientStatus;
import org.apache.fineract.portfolio.group.data.CenterData;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.domain.GroupingTypeStatus;
import org.apache.fineract.portfolio.interestratechart.data.InterestRateChartSlabData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class BulkLoansReadPlatformServiceImpl implements BulkLoansReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final AccountDetailsReadPlatformService accountDetailsReadPlatformService;

    @Autowired
    public BulkLoansReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final AccountDetailsReadPlatformService accountDetailsReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.accountDetailsReadPlatformService = accountDetailsReadPlatformService;
    }

    @Override
    public StaffAccountSummaryCollectionData retrieveLoanOfficerAccountSummary(final Long loanOfficerId) {

        this.context.authenticatedUser();

        final StaffClientMapper staffClientMapper = new StaffClientMapper();
        final String clientSql = "select distinct " + staffClientMapper.schema() + " and c.status_enum=?";

        final StaffGroupMapper staffGroupMapper = new StaffGroupMapper();
        final String groupSql = "select distinct " + staffGroupMapper.schema() + " and g.status_enum=? and g.parent_id is null";

        final List<StaffAccountSummaryCollectionData.LoanAccountSummary> clientSummaryList = this.jdbcTemplate.query(clientSql,
                staffClientMapper, new Object[] { loanOfficerId, ClientStatus.ACTIVE.getValue() });

        for (final StaffAccountSummaryCollectionData.LoanAccountSummary clientSummary : clientSummaryList) {
            final Collection<LoanAccountSummaryData> clientLoanAccounts = this.accountDetailsReadPlatformService
                    .retrieveClientLoanAccountsByLoanOfficerId(clientSummary.getId(), loanOfficerId);

            clientSummary.setLoans(clientLoanAccounts);
        }

        final List<StaffAccountSummaryCollectionData.LoanAccountSummary> groupSummaryList = this.jdbcTemplate.query(groupSql,
                staffGroupMapper, new Object[] { loanOfficerId, GroupingTypeStatus.ACTIVE.getValue() });

        for (final StaffAccountSummaryCollectionData.LoanAccountSummary groupSummary : groupSummaryList) {

            final Collection<LoanAccountSummaryData> groupLoanAccounts = this.accountDetailsReadPlatformService
                    .retrieveGroupLoanAccountsByLoanOfficerId(groupSummary.getId(), loanOfficerId);

            groupSummary.setLoans(groupLoanAccounts);
        }
        
        final StaffAccountSummaryCollectionDataMapper staffAccountSummaryCollectionDataMapper = new StaffAccountSummaryCollectionDataMapper();
        final String dataSql = "select "+staffAccountSummaryCollectionDataMapper.schema()+" order by cn.id,g.id,cl.id";
        Collection <CenterData> staffAccountSummaryCollectionData = this.jdbcTemplate.query(dataSql, staffAccountSummaryCollectionDataMapper, new Object[] { 
        		ClientStatus.ACTIVE.getValue(), GroupingTypeStatus.ACTIVE.getValue(), loanOfficerId, LoanStatus.ACTIVE.getValue(),
        		});
        
      
        return  new StaffAccountSummaryCollectionData( clientSummaryList, groupSummaryList,staffAccountSummaryCollectionData);
    }

    private static final class StaffClientMapper implements RowMapper<StaffAccountSummaryCollectionData.LoanAccountSummary> {

        public String schema() {
            return " c.id as id, c.display_name as displayName from m_client c "
                    + " join m_loan l on c.id = l.client_id "
            		+ " left join m_group_client gc on gc.client_id = c.id "
                    + " where l.loan_officer_id = ?"
                    + " and gc.client_id is null";
        }

        @Override
        public StaffAccountSummaryCollectionData.LoanAccountSummary mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String displayName = rs.getString("displayName");

            return new StaffAccountSummaryCollectionData.LoanAccountSummary(id, displayName);
        }
    }

    private static final class StaffGroupMapper implements RowMapper<StaffAccountSummaryCollectionData.LoanAccountSummary> {

        public String schema() {
            return " g.id as id, g.display_name as name from m_group g"
                    + " join m_loan l on g.id = l.group_id where l.loan_officer_id = ? ";
        }

        @Override
        public StaffAccountSummaryCollectionData.LoanAccountSummary mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");

            return new StaffAccountSummaryCollectionData.LoanAccountSummary(id, name);
        }
    }
    
    private static final class StaffAccountSummaryCollectionDataMapper implements ResultSetExtractor<Collection<CenterData>>{
		public String schema() {
			return " lp.name as productName, lp.short_name as shortProductName, "
					+ "loan.account_no as loanAcountNo, "
					+ "loan.id as id,cl.id as clientId, cl.display_name as clientName, "
					+ "g.id as groupId, g.display_name as groupName, "
					+ "cn.id as centerId, cn.display_name as Cetername " + "from m_loan loan "
					+ "join  m_product_loan AS lp ON lp.id = loan.product_id "
					+ "inner join m_client cl on cl.id = loan.client_id and cl.status_enum = ? "
					+ "inner join m_group g on g.id = loan.group_id and g.status_enum = ? "
					+ "inner join m_group cn on cn.id = g.parent_id "
					+ "where loan.loan_officer_id = ? and loan.loan_status_id = ? ";
		}
    	@Override
        public Collection <CenterData> extractData(ResultSet rs)  throws SQLException,DataAccessException {
    		 List<CenterData> centerDataList = new ArrayList<>();
    	
    		 CenterData tempcenterData = null;
    		 GroupGeneralData tempgroupGeneralData = null;
    		 ClientData tempclientData = null;
    		 Long centerIdTemp = null;
    		 Long groupIdTemp = null;
    		 Long clientidTemp = null;
    		 while (rs.next()) {
    			 Long centerId = JdbcSupport.getLong(rs, "centerId");
    			 if(centerIdTemp == null || centerId.longValue() != centerIdTemp.longValue()){
    				 centerIdTemp = centerId;
    				 CenterData centerData = CenterData.formCenterData(centerId, rs.getString("Cetername"));
    				 tempcenterData = centerData;
    				 centerDataList.add(tempcenterData);
    			 }
    			 
    			 Long groupId = JdbcSupport.getLong(rs, "groupId");
    			 if(groupIdTemp == null || groupIdTemp.longValue() != groupId.longValue()){
    				 groupIdTemp = groupId;
    				 GroupGeneralData groupGeneralData = GroupGeneralData.formGroupData(groupId, rs.getString("groupName"));
    				 tempgroupGeneralData = groupGeneralData;
    				 tempcenterData.addGroups(tempgroupGeneralData);
    			 }
    			 
    			 Long  clientId = JdbcSupport.getLong(rs, "clientId");
    			 if(clientidTemp == null || clientidTemp.longValue() != clientId.longValue()){
    				 clientidTemp = clientId;
    				 ClientData clientData = ClientData.formClientData(clientId,  rs.getString("clientName"));
    				 tempclientData = clientData;
    				 tempgroupGeneralData.addClients(tempclientData);
    			 }
    			 
    			 LoanAccountSummaryData loanAccountSummaryData = LoanAccountSummaryData.formLoanAccountSummaryData(JdbcSupport.getLong(rs, "id"),
    					 rs.getString("loanAcountNo"), rs.getString("productName"));
    			 tempclientData.addLoanAccountSummaryData(loanAccountSummaryData);
    		 }
            
            return centerDataList;
        }
    }
}