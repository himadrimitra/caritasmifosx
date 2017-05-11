package com.finflux.mandates.service;

import com.finflux.mandates.data.MandateTransactionsData;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class TransactionsProcessingWritePlatformServiceImpl implements TransactionsProcessingWritePlatformService{

        private final JdbcTemplate jdbcTemplate;
        final SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd");

        @Autowired
        public TransactionsProcessingWritePlatformServiceImpl(final RoutingDataSource dataSource){
                this.jdbcTemplate = new JdbcTemplate(dataSource);
        }

        @Override
        public int addTransactionsWithRequestStatus(MandatesProcessData processData) {
                Date currentDate = DateUtils.getDateOfTenant();
                Calendar cal = Calendar.getInstance() ;
                cal.setTime(currentDate);
                cal.add(Calendar.DATE, 1);
                Date nextDay = cal.getTime() ;
                
                StringBuilder sql = new StringBuilder(" insert into f_mandate_transactions ")
                        .append(" (mandate_id, loan_id, payment_due_amount, payment_due_date, request_date, `status`) ")
                        .append(" select * from ( select lm.id as mandateId, l.id as loanId, ")
                        .append(" (ifnull(lrs.principal_amount,0) - ifnull(lrs.principal_completed_derived,0) ")
                        .append(" + ifnull(lrs.interest_amount,0) - ifnull(lrs.interest_completed_derived,0) - ifnull(lrs.interest_waived_derived,0) ")
                        .append(" + ifnull(lrs.fee_charges_amount,0) - ifnull(lrs.fee_charges_completed_derived,0) - ifnull(lrs.fee_charges_waived_derived,0) ")
                        .append(" + ifnull(lrs.penalty_charges_amount,0) - ifnull(lrs.penalty_charges_completed_derived,0) ")
                        .append(" - ifnull(lrs.penalty_charges_waived_derived,0)) as dueAmount, ")
                        .append(" min(lrs.duedate) as dueDate,").append(" ? as requestDate, ")
                        .append(" 1 as status from f_loan_mandates as lm ")
                        .append(" left join m_loan as l on lm.loan_id = l.id ")
                        .append(" left join m_client as cl on l.client_id = cl.id ")
                        .append(" left join m_office as o on cl.office_id = o.id ")
                        .append(" left join m_loan_repayment_schedule as lrs on lrs.loan_id = l.id ")
                        .append(" where lm.mandate_status_enum = 400 AND ")
                        .append("(lm.period_until_cancelled=true OR (lm.period_to_date > dueDate and lm.period_from_date < dueDate)) ")
                        .append(" and l.loan_status_id = 300 ")
                        .append(" and lrs.completed_derived = 0 ")
                        .append(" and l.id not in (select fmt.loan_id from f_mandate_transactions as fmt where fmt.`status` in (1,2) ")
                        .append(" and fmt.payment_due_date between '").append(yyyyMMddFormat.format(processData.getPaymentDueStartDate())).append("' ")
                        .append(" and '").append(yyyyMMddFormat.format(processData.getPaymentDueEndDate())).append("') ")
                        .append(" and lrs.duedate between '").append(yyyyMMddFormat.format(processData.getPaymentDueStartDate())).append("' ")
                        .append(" and '").append(yyyyMMddFormat.format(processData.getPaymentDueEndDate())).append("' ");
                        if(processData.includeChildOffices()){
                                sql.append(" and o.hierarchy like concat((select off.hierarchy from m_office as off where off.id = ")
                                        .append(processData.getOfficeId()).append("),'%') ");
                        }else{
                                sql.append(" and o.id = ").append(processData.getOfficeId()).append(" ");
                        }
                        sql.append(" group by l.id ")
                        .append(" union ")
                        .append(" select mt.mandate_id as mandateId, mt.loan_id as loanId, mt.payment_due_amount as dueAmount, ")
                        .append(" '").append(yyyyMMddFormat.format(nextDay)).append("' as dueDate, ") 
                        .append(" ? as requestDate, 1 as status ")
                        .append(" from f_mandate_transactions as mt ")
                        .append(" where mt.id in (").append(processData.getIncludeFailedTransactions()).append(")) as temp ");
                return this.jdbcTemplate.update(sql.toString(), new Object[] {currentDate, currentDate});

        }

        @Override
        public void updateTransactionAsFailed(final Long transactionId, final String failureReason, final String processReferenceId) {
                String failureReasonDesc = null;
                Date currentDate = DateUtils.getDateOfTenant();
                if(null == failureReason){
                        failureReasonDesc = "";
                }else{
                        failureReasonDesc = failureReason.replaceAll("'","#");
                }
                if(failureReasonDesc.length() > 99){
                        failureReasonDesc = failureReasonDesc.substring(0,99);
                }

                String sql = "update f_mandate_transactions as t "
                        + " set t.`status` = 4, t.return_process_date = ? ,"
                        + " t.return_process_reference_id = '" + processReferenceId + "', t.return_reason = '"+failureReasonDesc+"' "
                        + " where t.id = " + transactionId;
                this.jdbcTemplate.update(sql, new Object[] {currentDate});
        }

        @Override
        public void updateTransactionAsSuccess(final Long transactionId, final Long repaymentTransactionId, final String processReferenceId) {
            Date currentDate = DateUtils.getDateOfTenant();
                String sql = "update f_mandate_transactions as t "
                        + " set t.`status` = 3, t.return_process_date = ? , "
                        + " t.return_process_reference_id = '" + processReferenceId + "', "
                        + " t.repayment_transaction_id = "+repaymentTransactionId+" "
                        + " where t.id = " + transactionId;
                this.jdbcTemplate.update(sql, new Object[] {currentDate});
        }

        @Override
        public void updateTransactionsStatusAsInProcess(Collection<MandateTransactionsData> transactionsToProcess) {
                String sql = "update f_mandate_transactions as f set f.status = ? where f.id = ?";
                List<Object[]> params = new ArrayList<>();
                for(MandateTransactionsData data : transactionsToProcess){
                        params.add(new Object[]{ MandateProcessStatusEnum.INPROCESS.getValue(), data.getId()});
                }
                this.jdbcTemplate.batchUpdate(sql, params);
        }


}
