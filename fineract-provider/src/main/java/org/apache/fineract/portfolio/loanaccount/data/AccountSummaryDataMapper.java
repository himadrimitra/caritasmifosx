package org.apache.fineract.portfolio.loanaccount.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.group.data.CenterData;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class AccountSummaryDataMapper {

    public static final class StaffClientMapper implements ResultSetExtractor<Collection<ClientData>> {

        public String schemaForreschedule() {
            return "select distinct loan.id as loanID, loan.account_no as AccountNumber, lp.name as ProductName, client.id as ClientId, client.display_name as Client_Name"
                    + " from m_loan loan inner join m_loan_repayment_schedule shedule on loan.id = shedule.loan_id join m_product_loan as lp on lp.id = loan.product_id "
                    + " inner join m_client client on loan.client_id = client.id and  client.id not in (select DISTINCT client_id from m_group_client) "
                    + " where loan.loan_officer_id = ? and shedule.duedate = ? and client.status_enum = ? order by client.id";
        }

        public String schemaForReassign() {
            return "select distinct loan.id as loanID, loan.account_no as AccountNumber, lp.name as ProductName, client.id as ClientId, client.display_name as Client_Name"
                    + " from m_loan loan join m_product_loan as lp on lp.id = loan.product_id "
                    + " inner join m_client client on loan.client_id = client.id and  client.id not in (select DISTINCT client_id from m_group_client) "
                    + " where loan.loan_officer_id = ? and client.status_enum = ? order by client.id";

        }

        @Override
        public Collection<ClientData> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<ClientData> clientDataList = new ArrayList<>();
            ClientData tempclientData = null;
            Long clientidTemp = null;
            while (rs.next()) {
                Long clientId = JdbcSupport.getLong(rs, "ClientId");
                if (clientidTemp == null || !clientidTemp.equals(clientId)) {
                    clientidTemp = clientId;
                    ClientData clientData = ClientData.formClientData(clientId, rs.getString("Client_Name"));
                    tempclientData = clientData;
                    clientDataList.add(tempclientData);

                }

                LoanAccountSummaryData loanAccountSummaryData = LoanAccountSummaryData.formLoanAccountSummaryData(
                        JdbcSupport.getLong(rs, "loanID"), rs.getString("AccountNumber"), rs.getString("ProductName"));
                if (rs.getObject("ClientId") != null) {
                    if (tempclientData != null) tempclientData.addLoanAccountSummaryData(loanAccountSummaryData);
                }

            }

            return clientDataList;
        }
    }

    public static final class StaffGroupMapper implements ResultSetExtractor<Collection<GroupGeneralData>> {

        public String schemaForreschedule() {

            return "select * from ( select distinct loan.id as loanid,if(grup.id is not null, grup.id,group_client.id) as group_id,if(grup.display_name is not null, grup.display_name,group_client.display_name) as groupdisplayname, group_client.display_name as cientgroupname,"
                    + "customer.id as clientid, "
                    + "customer.display_name as client_name, loan.account_no as AccountNumber, lp.name as ProductName "
                    + "from m_loan as loan " + "join m_loan_repayment_schedule shedule on loan.id = shedule.loan_id "
                    + "join  m_product_loan as lp on lp.id = loan.product_id "
                    + "left join m_group grup on loan.group_id = grup.id and grup.level_id = 2 and grup.parent_id is null and grup.status_enum = ? "
                    + "left join m_client customer on loan.client_id = customer.id "
                    + "left join m_group_client group_client_mapping on  customer.id = group_client_mapping.client_id "
                    + "left join m_group group_client on group_client.id = group_client_mapping.group_id and group_client.level_id = 2 and group_client.parent_id is null "
                    + "where loan.loan_officer_id = ?  and shedule.duedate = ? " + "  order by if(grup.id is not null, grup.id,group_client.id), customer.id) allloans  "
                    + "where allloans.groupdisplayname is not null";
        }

        public String schemaForReassign() {
            return "select * from ( select distinct loan.id as loanid,if(grup.id is not null, grup.id,group_client.id) as group_id,if(grup.display_name is not null, grup.display_name,group_client.display_name) as groupdisplayname, group_client.display_name as cientgroupname,"
                    + "customer.id as clientid, "
                    + "customer.display_name as client_name, loan.account_no as AccountNumber, lp.name as ProductName "
                    + "from m_loan as loan " + "join  m_product_loan as lp on lp.id = loan.product_id "
                    + "left join m_group grup on loan.group_id = grup.id and grup.level_id = 2 and grup.parent_id is null and grup.status_enum = ? "
                    + "left join m_client customer on loan.client_id = customer.id "
                    + "left join m_group_client group_client_mapping on  customer.id = group_client_mapping.client_id "
                    + "left join m_group group_client on group_client.id = group_client_mapping.group_id and group_client.level_id = 2 and group_client.parent_id is null "
                    + "where loan.loan_officer_id = ?  " + " order by if(grup.id is not null, grup.id,group_client.id), customer.id ) allloans  " + "where allloans.groupdisplayname is not null";
        }

        @Override
        public Collection<GroupGeneralData> extractData(ResultSet rs) throws SQLException, DataAccessException {

            List<GroupGeneralData> groupDataList = new ArrayList<>();
            GroupGeneralData tempgroupGeneralData = null;
            ClientData tempclientData = null;
            Long groupIdTemp = null;
            Long clientidTemp = null;
            while (rs.next()) {
                Long groupId = JdbcSupport.getLong(rs, "GROUP_ID");
                if (groupIdTemp == null || !groupIdTemp.equals(groupId)) {
                    groupIdTemp = groupId;
                    GroupGeneralData groupGeneralData = GroupGeneralData.formGroupData(groupId, rs.getString("groupDisplayName"));
                    tempgroupGeneralData = groupGeneralData;
                    groupDataList.add(tempgroupGeneralData);
                }

                if (rs.getObject("ClientId") != null) {
                    Long clientId = JdbcSupport.getLong(rs, "ClientId");
                    if (clientidTemp == null || !clientidTemp.equals(clientId)) {
                        clientidTemp = clientId;
                        ClientData clientData = ClientData.formClientData(clientId, rs.getString("Client_Name"));
                        tempclientData = clientData;
                        if (tempgroupGeneralData != null) tempgroupGeneralData.addClients(tempclientData);

                    }
                }
                LoanAccountSummaryData loanAccountSummaryData = LoanAccountSummaryData.formLoanAccountSummaryData(
                        JdbcSupport.getLong(rs, "loanID"), rs.getString("AccountNumber"), rs.getString("ProductName"));
                if (rs.getObject("clientId") != null) {
                    if (tempclientData != null) tempclientData.addLoanAccountSummaryData(loanAccountSummaryData);
                } else {
                    if (tempgroupGeneralData != null) tempgroupGeneralData.addLoanAccountSummaryData(loanAccountSummaryData);
                }

            }

            return groupDataList;
        }
    }

    public static final class StaffAccountSummaryCollectionDataMapper implements ResultSetExtractor<Collection<CenterData>> {

        public String schemaForReschedule() {
            return " select * from (select  lp.name as productName, lp.short_name as shortProductName, loan.account_no as loanAcountNo, loan.id as id,cl.id as clientId, cl.display_name as clientName,"
                    + " if(g.id is not null, g.id,parentGroup.id )as groupId, if( g.display_name is not null,g.display_name,parentGroup.display_name ) as groupName, "
                    + "if(cn.id is not null, cn.id,parentcenter.id)  as centerId, if( cn.display_name is not null, cn.display_name, parentcenter.display_name ) as Centername  "
                    + "from m_loan loan " + " join  m_product_loan AS lp ON lp.id = loan.product_id "
                    + "left join m_client cl on cl.id = loan.client_id and cl.status_enum = ? "
                    + "left join m_group g on g.id = loan.group_id and g.status_enum = ? "
                    + "left join m_group_client groupClient on groupClient.client_id = cl.id "
                    + "left join m_group parentGroup on groupClient.group_id =  parentGroup.id "
                    + " left join m_group parentCenter on parentGroup.parent_id =  parentCenter.id "
                    + "left join m_group cn on cn.id = g.parent_id "
                    + " join m_loan_repayment_schedule shedule on loan.id = shedule.loan_id "
                    + " where loan.loan_officer_id = ? and loan.loan_status_id = ? and shedule.duedate = ? " + " group by loan.id "
                    + " order by if(g.id is not null, g.id,parentGroup.id ), cl.id) loandetails " + " where loandetails.Centername is not null";

        }

        public String schemaForReassign() {
            return "  select * from (select  lp.name as productName, lp.short_name as shortProductName, loan.account_no as loanAcountNo, loan.id as id,cl.id as clientId, cl.display_name as clientName,"
                    + " if(g.id is not null, g.id,parentGroup.id )as groupId, if( g.display_name is not null,g.display_name,parentGroup.display_name ) as groupName, "
                    + "if(cn.id is not null, cn.id,parentcenter.id)  as centerId, if( cn.display_name is not null, cn.display_name, parentcenter.display_name ) as Centername  "
                    + "from m_loan loan " + " join  m_product_loan AS lp ON lp.id = loan.product_id "
                    + "left join m_client cl on cl.id = loan.client_id and cl.status_enum = ? "
                    + "left join m_group g on g.id = loan.group_id and g.status_enum = ? "
                    + "left join m_group_client groupClient on groupClient.client_id = cl.id "
                    + "left join m_group parentGroup on groupClient.group_id =  parentGroup.id "
                    + " left join m_group parentCenter on parentGroup.parent_id =  parentCenter.id "
                    + "left join m_group cn on cn.id = g.parent_id " + " where loan.loan_officer_id = ? and loan.loan_status_id = ? "
                    + " group by loan.id " + " order by if(g.id is not null, g.id,parentGroup.id ), cl.id) loandetails " + " where loandetails.Centername is not null";
        }

        @Override
        public Collection<CenterData> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<CenterData> centerDataList = new ArrayList<>();

            CenterData tempcenterData = null;
            GroupGeneralData tempgroupGeneralData = null;
            ClientData tempclientData = null;
            Long centerIdTemp = null;
            Long groupIdTemp = null;
            Long clientidTemp = null;
            while (rs.next()) {
                Long centerId = JdbcSupport.getLong(rs, "centerId");
                if (centerIdTemp == null || !centerId.equals(centerIdTemp)) {
                    centerIdTemp = centerId;
                    CenterData centerData = CenterData.formCenterData(centerId, rs.getString("Centername"));
                    tempcenterData = centerData;
                    centerDataList.add(tempcenterData);
                }

                Long groupId = JdbcSupport.getLong(rs, "groupId");
                if (groupIdTemp == null || !groupIdTemp.equals(groupId)) {
                    groupIdTemp = groupId;
                    GroupGeneralData groupGeneralData = GroupGeneralData.formGroupData(groupId, rs.getString("groupName"));
                    tempgroupGeneralData = groupGeneralData;
                    if (tempcenterData != null) tempcenterData.addGroups(tempgroupGeneralData);
                }
                if (rs.getObject("clientId") != null) {
                    Long clientId = JdbcSupport.getLong(rs, "clientId");
                    if (clientidTemp == null || !clientidTemp.equals(clientId)) {
                        clientidTemp = clientId;
                        ClientData clientData = ClientData.formClientData(clientId, rs.getString("clientName"));
                        tempclientData = clientData;
                        if (tempgroupGeneralData != null) tempgroupGeneralData.addClients(tempclientData);
                    }
                }

                LoanAccountSummaryData loanAccountSummaryData = LoanAccountSummaryData.formLoanAccountSummaryData(
                        JdbcSupport.getLong(rs, "id"), rs.getString("loanAcountNo"), rs.getString("productName"));
                if (rs.getObject("clientId") != null) {
                    if (tempclientData != null) tempclientData.addLoanAccountSummaryData(loanAccountSummaryData);
                } else {
                    if (tempgroupGeneralData != null) tempgroupGeneralData.addLoanAccountSummaryData(loanAccountSummaryData);
                }
            }

            return centerDataList;
        }
    }

}
