package com.finflux.pdcm.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PDCEventListenerServiceImpl implements PDCEventListenerService {

    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(PDCEventListenerServiceImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PDCEventListenerServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void adjustLoanTransaction(final LoanTransaction loanTransaction) {
        final Long loanTransactionId = loanTransaction.getId();
        final String sql = "select COUNT(pdcm.id) from f_pdc_cheque_detail_mapping pdcm where pdcm.is_deleted = 0 and pdcm.transaction_id = ? ";
        final List<Object> params = new ArrayList<>();
        params.add(loanTransactionId);
        final Long count = queryExecuteAndReturnLongValue(sql, params);
        if (count != null && count > 0) {
            final String globalisationMessageCode = "error.msg.can.not.adjust.loan.transaction.was.linked.with.pdc";
            final String defaultUserMessage = "Can not undo / adjust loan transaction was linked with PDC";
            throwGeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, loanTransactionId);
        }
    }

    @Override
    public void undoLoanDisbursal(final Loan loan) {
        final Long loanId = loan.getId();
        final StringBuilder sb = new StringBuilder(200);
        sb.append("select COUNT(lt.id) FROM m_loan_transaction lt ");
        sb.append("join f_pdc_cheque_detail_mapping pdcm ON pdcm.transaction_id = lt.id and pdcm.is_deleted = 0 ");
        sb.append("where lt.loan_id = ? ");
        final String sql = sb.toString();
        final List<Object> params = new ArrayList<>();
        params.add(loanId);
        final Long count = queryExecuteAndReturnLongValue(sql, params);
        if (count != null && count > 0) {
            final String globalisationMessageCode = "error.msg.can.not.undo.disbursed.loan.transactions.are.linked.with.pdc";
            final String defaultUserMessage = "Can not undo disbursed loan, transactions are linked with PDC";
            throwGeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, loanId);
        }
    }

    private void throwGeneralPlatformDomainRuleException(final String globalisationMessageCode, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        throw new GeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, defaultUserMessageArgs);
    }

    private Long queryExecuteAndReturnLongValue(final String sql, final List<Object> params) {
        try {
            return this.jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

}