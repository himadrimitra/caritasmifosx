package com.finflux.sms.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.sms.data.SmsData;
import org.apache.fineract.infrastructure.sms.data.TenantSmsConfiguration;
import org.apache.fineract.infrastructure.sms.domain.SmsMessage;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageEnumerations;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageRepository;
import org.apache.fineract.infrastructure.sms.scheduler.SmsMessageScheduledJobService;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.sms.domain.SMSSupportedProductType;
import com.finflux.sms.domain.SMSSupportedTransactionType;

@Service
public class SmsEventListenerServiceImpl implements SmsEventListenerService {

    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(SmsEventListenerServiceImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final SmsMessageRepository smsMessageRepository;
    private final SmsMessageScheduledJobService smsMessageScheduledJobService;

    @Autowired
    public SmsEventListenerServiceImpl(final RoutingDataSource dataSource, final BusinessEventNotifierService businessEventNotifierService,
            final SmsMessageRepository smsMessageRepository, final SmsMessageScheduledJobService smsMessageScheduledJobService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.businessEventNotifierService = businessEventNotifierService;
        this.smsMessageRepository = smsMessageRepository;
        this.smsMessageScheduledJobService = smsMessageScheduledJobService;
    }

    @PostConstruct
    public void registerForNotification() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_DISBURSAL,
                new SmsSchedulaEventListnerForLoanDisbursal());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT,
                new SmsSchedulaEventListnerForRepayment());
    }

    private class SmsSchedulaEventListnerForLoanDisbursal implements BusinessEventListner {

        @SuppressWarnings("unused")
        @Override
        public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // TODO Auto-generated method stub
        }

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object loanTransactionEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN_TRANSACTION);
            if (loanTransactionEntity != null) {
                final LoanTransaction loanTransaction = (LoanTransaction) loanTransactionEntity;
                final Integer transactionType = SMSSupportedTransactionType.DISBURSEMENT.getValue();
                final Integer productType = SMSSupportedProductType.LOANPRODUCT.getValue();
                final Long productId = loanTransaction.getLoan().productId();
                String messageTemplate = getSmsMessageTemplate(transactionType, productType, productId);
                sendMessgeProcessForLoanTransaction(loanTransaction, messageTemplate);
            }
        }
    }

    private String getSmsMessageTemplate(final Integer transactionType, final Integer productType, final Long productId) {
        try {
            final StringBuilder sqlBuilder = new StringBuilder(10);
            sqlBuilder.append("SELECT pstyc.message_template AS messageTemplate ");
            sqlBuilder.append("FROM f_product_sms_configuration psmc ");
            sqlBuilder.append("JOIN f_product_sms_transaction_type_config pstyc ON pstyc.type = ? ");
            sqlBuilder.append("AND pstyc.product_sms_config_id = psmc.id AND pstyc.is_active = 1 ");
            sqlBuilder.append("WHERE psmc.is_active = 1 AND psmc.is_enabled = 1 AND psmc.product_type = ? AND psmc.product_id = ? ");
            return this.jdbcTemplate.queryForObject(sqlBuilder.toString(), new Object[] { transactionType, productType, productId },
                    String.class);
        } catch (EmptyResultDataAccessException e) {

        }
        return null;
    }

    private class SmsSchedulaEventListnerForRepayment implements BusinessEventListner {

        @SuppressWarnings("unused")
        @Override
        public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // TODO Auto-generated method stub
        }

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object loanTransactionEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN_TRANSACTION);
            if (loanTransactionEntity != null) {
                final LoanTransaction loanTransaction = (LoanTransaction) loanTransactionEntity;
                final Integer transactionType = SMSSupportedTransactionType.REPAYMENT.getValue();
                final Integer productType = SMSSupportedProductType.LOANPRODUCT.getValue();
                final Long productId = loanTransaction.getLoan().productId();
                String messageTemplate = getSmsMessageTemplate(transactionType, productType, productId);
                sendMessgeProcessForLoanTransaction(loanTransaction, messageTemplate);
            }
        }

    }

    @Override
    public void sendMessgeProcessForLoanTransaction(final LoanTransaction loanTransaction, String messageTemplate) {
        final Loan loan = loanTransaction.getLoan();
        if (loan.client().getMobileNo() != null) {
            if (messageTemplate != null && !messageTemplate.equalsIgnoreCase("null") && messageTemplate.length() > 0) {
                /*
                 * Dear {{clientName}}, Your EMI amount {{transactionAmount}}
                 * was paid successfully on {{transactionDate}}, Your loan
                 * account number is {{accountNumber}}.
                 * 
                 * Regards Chaitanya Microfinance.
                 */
                final String clientName = loan.client().getDisplayName();
                final Money transactionAmount = loanTransaction.getAmount(loan.getCurrency());
                final LocalDate transactionDate = loanTransaction.getTransactionDate();
                final String accountNumber = loan.getAccountNumber();
                messageTemplate = messageTemplate.replace("{{clientName}}", clientName);
                messageTemplate = messageTemplate.replace("{{transactionAmount}}", transactionAmount.toString());
                messageTemplate = messageTemplate.replace("{{transactionDate}}", transactionDate.toString());
                messageTemplate = messageTemplate.replace("{{accountNumber}}", accountNumber.toString());
                sendMessge(messageTemplate, loan);
            }
        }
    }

    @SuppressWarnings("unused")
    private void sendMessge(final String message, final Loan loan) {
        try {
            final SmsMessage smsMessage = SmsMessage.pendingSms(null, null, null, loan.group(), loan.client(), loan.loanOfficer(), message,
                    null, loan.client().getMobileNo(), null);
            this.smsMessageRepository.save(smsMessage);
            Long groupId = null;
            if (loan.group() != null) {
                groupId = loan.group().getId();
            }
            Long clientId = null;
            if (loan.client() != null) {
                clientId = loan.client().getId();
            }
            Long staffId = null;
            if (loan.loanOfficer() != null) {
                staffId = loan.loanOfficer().getId();
            }
            final EnumOptionData status = SmsMessageEnumerations.status(smsMessage.getStatusType());
            final SmsData smsData = SmsData.instance(smsMessage.getId(), smsMessage.getExternalId(), groupId, clientId, staffId, status,
                    smsMessage.getSourceAddress(), smsMessage.getMobileNo(), message, smsMessage.getCampaignName(), new LocalDate(
                            smsMessage.getSubmittedOnDate()));
            final Collection<SmsData> pendingMessages = new ArrayList<SmsData>();
            pendingMessages.add(smsData);
            TenantSmsConfiguration tenantSmsConfiguration = null;
            this.smsMessageScheduledJobService.sendMessagesProcess(tenantSmsConfiguration, pendingMessages);
        } catch (Exception e) {

        }
    }
}