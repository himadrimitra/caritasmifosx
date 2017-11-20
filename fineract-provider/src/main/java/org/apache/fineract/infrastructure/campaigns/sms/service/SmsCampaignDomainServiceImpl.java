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

package org.apache.fineract.infrastructure.campaigns.sms.service;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignTriggerType;
import org.apache.fineract.infrastructure.campaigns.sms.data.SmsProviderData;
import org.apache.fineract.infrastructure.campaigns.sms.domain.SmsCampaign;
import org.apache.fineract.infrastructure.campaigns.sms.domain.SmsCampaignRepository;
import org.apache.fineract.infrastructure.campaigns.sms.exception.SmsRuntimeException;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.sms.domain.InboundMessage;
import org.apache.fineract.infrastructure.sms.domain.SmsMessage;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageRepository;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageStatusType;
import org.apache.fineract.infrastructure.sms.scheduler.SmsMessageScheduledJobService;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepository;
import org.apache.fineract.portfolio.accountdetails.PaymentDetailCollectionData;
import org.apache.fineract.portfolio.accountdetails.SharesAccountBalanceCollectionData;
import org.apache.fineract.portfolio.accountdetails.data.AccountSummaryCollectionData;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.accountdetails.service.AccountDetailsReadPlatformService;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepository;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupRepository;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidLoanTypeException;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class SmsCampaignDomainServiceImpl implements SmsCampaignDomainService {

	private static final Logger logger = LoggerFactory.getLogger(SmsCampaignDomainServiceImpl.class);
	
	//private final static int POOL_SIZE = 5 ;
	
    private final SmsCampaignRepository smsCampaignRepository;
    private final SmsMessageRepository smsMessageRepository;
    private final OfficeRepository officeRepository;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final SmsCampaignWritePlatformService smsCampaignWritePlatformCommandHandler;
    private final GroupRepository groupRepository;

    private final SmsMessageScheduledJobService smsMessageScheduledJobService;
    private final JdbcTemplate jdbcTemplate;
    private final ClientRepository clientRepository;
    private final AccountDetailsReadPlatformService accountDetailsReadPlatformService;
    private final SmsCampaignDropdownReadPlatformService smsCampaignDropdownReadPlatformService;
    
    @Autowired
    public SmsCampaignDomainServiceImpl(final SmsCampaignRepository smsCampaignRepository, final SmsMessageRepository smsMessageRepository,
                                        final BusinessEventNotifierService businessEventNotifierService, final OfficeRepository officeRepository,
                                        final SmsCampaignWritePlatformService smsCampaignWritePlatformCommandHandler,
                                        final GroupRepository groupRepository,
                                        final SmsMessageScheduledJobService smsMessageScheduledJobService, final RoutingDataSource dataSource,
                                        final ClientRepository clientRepository,
                                        final AccountDetailsReadPlatformService accountDetailsReadPlatformService,
                                        final SmsCampaignDropdownReadPlatformService smsCampaignDropdownReadPlatformService){
        this.smsCampaignRepository = smsCampaignRepository;
        this.smsMessageRepository = smsMessageRepository;
        this.businessEventNotifierService = businessEventNotifierService;
        this.officeRepository = officeRepository;
        this.smsCampaignWritePlatformCommandHandler = smsCampaignWritePlatformCommandHandler;
        this.groupRepository = groupRepository;
        this.smsMessageScheduledJobService = smsMessageScheduledJobService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.clientRepository = clientRepository;
        this.accountDetailsReadPlatformService = accountDetailsReadPlatformService;
        this.smsCampaignDropdownReadPlatformService = smsCampaignDropdownReadPlatformService;
    }

    @PostConstruct
    public void addListners() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_APPROVED, new SendSmsOnLoanApproved());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REJECTED, new SendSmsOnLoanRejected());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT, new SendSmsOnLoanRepayment());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_ACTIVATE, new ClientActivatedListener());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_REJECT, new ClientRejectedListener());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_ACTIVATE, new SavingsAccountActivatedListener());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_REJECT, new SavingsAccountRejectedListener());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_DEPOSIT, new SavingsAccountTransactionListener(true));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVING_WITHDRAWAL, new SavingsAccountTransactionListener(false));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_DISBURSAL, new SendSmsOnLoanDisbursed());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENT_CREATE, new ClientCreatedListener());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_CLOSED, new SavingsAccountClosedListener());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENT_PAYMENTS, new ClientPaymentsListener());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.INBOUND_MESSAGE, new InboundSMSListener());
    }

	private void notifyRejectedLoanOwner(Loan loan) {
		List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("Loan Rejected");
		if (smsCampaigns.size() > 0) {
			for (SmsCampaign campaign : smsCampaigns) {
				if (campaign.isActive()) {
					SmsCampaignDomainServiceImpl.this.smsCampaignWritePlatformCommandHandler
							.insertDirectCampaignIntoSmsOutboundTable(loan, campaign);
				}
			}
		}
	}

    private void notifyAcceptedLoanOwner(Loan loan) {
        List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("Loan Approved");
        if(smsCampaigns.size()>0){
            for (SmsCampaign campaign:smsCampaigns){
            	this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(loan, campaign);
            }
        }
    }
    
    private void notifyDisbursedLoanOwner(Loan loan) {
        List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("Loan Disbursed");
        if(smsCampaigns.size()>0){
            for (SmsCampaign campaign:smsCampaigns){
                this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(loan, campaign);
            }
        }
    }
    
    private void notifyClientCreated(final Client client) {
        List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("Client Created");
        if(smsCampaigns.size()>0){
            for (SmsCampaign campaign:smsCampaigns){
                this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(client, campaign, null);
            }
        }
        
   }

    private void notifyClientPaymentDone(final Client client, final String receiptNo) {
        List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("Client Payments");
        final Map<String, String> smsParams = new HashMap<>();
        if (StringUtils.isNotBlank(receiptNo)) {
            smsParams.put("reciptNo", receiptNo);
        }
        if (smsCampaigns.size() > 0) {
            for (SmsCampaign campaign : smsCampaigns) {
                this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(client, campaign, smsParams);
            }
        }

    }

    private void notifyClientActivated(final Client client) {
    	 List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("Client Activated");
    	 if(smsCampaigns.size()>0){
             for (SmsCampaign campaign:smsCampaigns){
            	 this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(client, campaign, null);
             }
         }
    	 
    }
    
    private void notifyClientRejected(final Client client) {
   	 List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("Client Rejected");
   	 if(smsCampaigns.size()>0){
            for (SmsCampaign campaign:smsCampaigns){
            	this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(client, campaign, null);
            }
        }
   	 
   }

    private void notifySavingsAccountClosed(final SavingsAccount savingsAccount) {
        List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("Savings Closed");
        if (smsCampaigns.size() > 0) {
            for (SmsCampaign campaign : smsCampaigns) {
                this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(savingsAccount, campaign);
            }
        }
    }

	private void notifySavingsAccountActivated(final SavingsAccount savingsAccount) {
		List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("Savings Activated");
		if (smsCampaigns.size() > 0) {
			for (SmsCampaign campaign : smsCampaigns) {
				this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(savingsAccount,
							campaign);
			}
		}

	}

	private void notifySavingsAccountRejected(final SavingsAccount savingsAccount) {
		List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("Savings Rejected");
		if (smsCampaigns.size() > 0) {
			for (SmsCampaign campaign : smsCampaigns) {
				this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(savingsAccount,
							campaign);
			}
		}

	}
	@SuppressWarnings("null")
	private void sendSmsForLoanRepayment(LoanTransaction loanTransaction) {
		List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("Loan Repayment");
		if (smsCampaigns.size() > 0) {
			for (SmsCampaign smsCampaign : smsCampaigns) {
				try {
					Loan loan = loanTransaction.getLoan();
					final Set<Client> groupClients = new HashSet<>();
					if (loan.hasInvalidLoanType()) {
						throw new InvalidLoanTypeException(
								"Loan Type cannot be Invalid for the Triggered Sms Campaign");
					}
					if (loan.isGroupLoan()) {
						Group group = this.groupRepository.findOne(loan.getGroupId());
						groupClients.addAll(group.getClientMembers());
					} else {
						groupClients.add(loan.client());
					}
					HashMap<String, String> campaignParams = new ObjectMapper().readValue(smsCampaign.getParamValue(),
							new TypeReference<HashMap<String, String>>() {
							});

					if (groupClients.size() > 0) {
						for (Client client : groupClients) {
							HashMap<String, Object> smsParams = processRepaymentDataForSms(loanTransaction, client);
							for (String key : campaignParams.keySet()) {
								String value = campaignParams.get(key);
								String spvalue = null;
								boolean spkeycheck = smsParams.containsKey(key);
								if (spkeycheck) {
									spvalue = smsParams.get(key).toString();
								}
								if (spkeycheck && !(value.equals("-1") || spvalue.equals(value))) {
									if (key.equals("officeId")) {
										Office campaignOffice = this.officeRepository.findOne(Long.valueOf(value));
										if (campaignOffice
												.doesNotHaveAnOfficeInHierarchyWithId(client.getOffice().getId())) {
											throw new SmsRuntimeException("error.msg.no.office",
													"Office not found for the id");
										}
									} else {
										throw new SmsRuntimeException("error.msg.no.id.attribute",
												"Office Id attribute is notfound");
									}
								}
							}
							String message = this.smsCampaignWritePlatformCommandHandler.compileSmsTemplate(
									smsCampaign.getMessage(), smsCampaign.getCampaignName(), smsParams);
							Object mobileNo = smsParams.get("mobileNo");
							if (mobileNo != null) {
								SmsMessage smsMessage = SmsMessage.pendingSms(null, null, client, null, message,
										mobileNo.toString(), smsCampaign);
								this.smsMessageRepository.save(smsMessage);
								Collection<SmsMessage> messages = new ArrayList<>();
								messages.add(smsMessage);
								Map<SmsCampaign, Collection<SmsMessage>> smsDataMap = new HashMap<>();
								smsDataMap.put(smsCampaign, messages);
								this.smsMessageScheduledJobService.sendTriggeredMessages(smsDataMap);
							}
						}
					}
				} catch (final IOException e) {
					logger.error("smsParams does not contain the key: " + e.getMessage());
				} catch (final RuntimeException e) {
					logger.debug("Client Office Id and SMS Campaign Office id doesn't match");
				}
			}
		}
	}
	@SuppressWarnings("null")
	private void sendSmsForSavingsTransaction(final SavingsAccountTransaction savingsTransaction, boolean isDeposit) {
		String campaignName = isDeposit ? "Savings Deposit" : "Savings Withdrawal";
		List<SmsCampaign> smsCampaigns = retrieveSmsCampaigns(campaignName);
		if (smsCampaigns.size() > 0) {
			for (SmsCampaign smsCampaign : smsCampaigns) {
				try {
					final SavingsAccount savingsAccount = savingsTransaction.getSavingsAccount();
					final Client client = savingsAccount.getClient();
					HashMap<String, String> campaignParams = new ObjectMapper().readValue(smsCampaign.getParamValue(),
							new TypeReference<HashMap<String, String>>() {
							});
					HashMap<String, Object> smsParams = processSavingsTransactionDataForSms(savingsTransaction, client);
					for (String key : campaignParams.keySet()) {
						String value = campaignParams.get(key);
						String spvalue = null;
						boolean spkeycheck = smsParams.containsKey(key);
						if (spkeycheck) {
							spvalue = smsParams.get(key).toString();
						}
						if (spkeycheck && !(value.equals("-1") || spvalue.equals(value))) {
							if (key.equals("officeId")) {
								Office campaignOffice = this.officeRepository.findOne(Long.valueOf(value));
								if (campaignOffice.doesNotHaveAnOfficeInHierarchyWithId(client.getOffice().getId())) {
									throw new SmsRuntimeException("error.msg.no.office",
											"Office not found for the id");
								}
							} else {
								throw new SmsRuntimeException("error.msg.no.id.attribute",
										"Office Id attribute is notfound");
							}
						}
					}
					String message = this.smsCampaignWritePlatformCommandHandler
							.compileSmsTemplate(smsCampaign.getMessage(), smsCampaign.getCampaignName(), smsParams);
					Object mobileNo = smsParams.get("mobileNo");
					if (mobileNo != null) {
						SmsMessage smsMessage = SmsMessage.pendingSms(null, null, client, null, message,
								mobileNo.toString(), smsCampaign);
						this.smsMessageRepository.save(smsMessage);
						Collection<SmsMessage> messages = new ArrayList<>();
						messages.add(smsMessage);
						Map<SmsCampaign, Collection<SmsMessage>> smsDataMap = new HashMap<>();
						smsDataMap.put(smsCampaign, messages);
						this.smsMessageScheduledJobService.sendTriggeredMessages(smsDataMap);
					}
				} catch (final IOException e) {
					logger.error("smsParams does not contain the key: " + e.getMessage());
				} catch (final RuntimeException e) {
					logger.debug("Client Office Id and SMS Campaign Office id doesn't match");
				}
			}
		}
	}
    
    private List<SmsCampaign> retrieveSmsCampaigns(String paramValue){
        List<SmsCampaign> smsCampaigns = smsCampaignRepository.findActiveSmsCampaigns("%"+paramValue+"%", SmsCampaignTriggerType.TRIGGERED.getValue());
        return smsCampaigns;
    }

    private HashMap<String, Object> processRepaymentDataForSms(final LoanTransaction loanTransaction, Client groupClient){

        HashMap<String, Object> smsParams = new HashMap<>();
        Loan loan = loanTransaction.getLoan();
        final Client client;
        if(loan.isGroupLoan() && groupClient != null){
            client = groupClient;
        }else if(loan.isIndividualLoan()){
            client = loan.getClient();
        }else{
            throw new InvalidParameterException("");
        }

        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMM:d:yyyy");

        smsParams.put("id",loanTransaction.getLoan().getClientId());
        smsParams.put("firstname",client.getFirstname());
        smsParams.put("middlename",client.getMiddlename());
        smsParams.put("lastname",client.getLastname());
        smsParams.put("FullName",client.getDisplayName());
        smsParams.put("mobileNo",client.mobileNo());
        smsParams.put("LoanAmount",loan.getPrincpal());
        smsParams.put("LoanOutstanding",loanTransaction.getOutstandingLoanBalance());
        smsParams.put("loanId",loan.getId());
        smsParams.put("LoanAccountId", loan.getAccountNumber());
        smsParams.put("officeId", client.getOffice().getId());
        
        if(client.getStaff() != null) {
        	smsParams.put("loanOfficerId", client.getStaff().getId());
        }else {
        	  smsParams.put("loanOfficerId", -1);
        }
        
        smsParams.put("repaymentAmount", loanTransaction.getAmount(loan.getCurrency()));
        smsParams.put("RepaymentDate", loanTransaction.getCreatedDate().toLocalDate().toString(dateFormatter));
        smsParams.put("RepaymentTime", loanTransaction.getCreatedDate().toLocalTime().toString(timeFormatter));
        
        if(loanTransaction.getPaymentDetail() != null) {
        	smsParams.put("receiptNumber", loanTransaction.getPaymentDetail().getReceiptNumber());
        }else {
        	smsParams.put("receiptNumber", -1);	
        }
        return smsParams;
    }

    private HashMap<String, Object> processSavingsTransactionDataForSms(final SavingsAccountTransaction savingsAccountTransaction, Client client){

    	// {{savingsId}} {{id}} {{firstname}} {{middlename}} {{lastname}} {{FullName}} {{mobileNo}} {{savingsAccountId}} {{depositAmount}} {{balance}}
    	
    	//transactionDate
        HashMap<String, Object> smsParams = new HashMap<>();
        SavingsAccount savingsAccount = savingsAccountTransaction.getSavingsAccount() ;
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMM:d:yyyy");
        smsParams.put("clientId",client.getId());
        smsParams.put("firstname",client.getFirstname());
        smsParams.put("middlename",client.getMiddlename());
        smsParams.put("lastname",client.getLastname());
        smsParams.put("FullName",client.getDisplayName());
        smsParams.put("mobileNo",client.mobileNo());
        smsParams.put("savingsId", savingsAccount.getId()) ;
        smsParams.put("savingsAccountNo",savingsAccount.getAccountNumber());
        smsParams.put("withdrawAmount",savingsAccountTransaction.getAmount(savingsAccount.getCurrency()));
        smsParams.put("depositAmount",savingsAccountTransaction.getAmount(savingsAccount.getCurrency()));
        smsParams.put("balance",savingsAccount.getWithdrawableBalance());
        smsParams.put("officeId", client.getOffice().getId());
        smsParams.put("transactionDate", savingsAccountTransaction.getTransactionLocalDate().toString(dateFormatter)) ;
        smsParams.put("savingsTransactionId", savingsAccountTransaction.getId()) ;
        
        if(client.getStaff() != null) {
        	smsParams.put("loanOfficerId", client.getStaff().getId());
        }else {
        	  smsParams.put("loanOfficerId", -1);
        }
        
        if(savingsAccountTransaction.getPaymentDetail() != null) {
        	smsParams.put("receiptNumber", savingsAccountTransaction.getPaymentDetail().getReceiptNumber());
        }else {
        	smsParams.put("receiptNumber", -1);	
        }
        return smsParams;
    }

    private boolean isSMSConfigurationEnabledForOffice(final Long officeId) {
        final String sql = "SELECT sms_enabled AS isSMSEnabled FROM officedetails WHERE office_id = ?";
        try {
            return this.jdbcTemplate.queryForObject(sql, Boolean.class, officeId);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private abstract class SmsBusinessEventAdapter implements BusinessEventListner {

        @Override
        public void businessEventToBeExecuted(@SuppressWarnings("unused") Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // Nothing to do
        }
    }

    private class SendSmsOnLoanApproved extends SmsBusinessEventAdapter{

        @Override
        public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
            if (entity instanceof Loan) {
                Loan loan = (Loan) entity;
                if (isSMSConfigurationEnabledForOffice(loan.getOfficeId())) {
                    notifyAcceptedLoanOwner(loan);
                }
            }
        }
    }
    
    private class SendSmsOnLoanDisbursed extends SmsBusinessEventAdapter{

        @Override
        public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
            if (entity instanceof Loan) {
                Loan loan = (Loan) entity;
                if (isSMSConfigurationEnabledForOffice(loan.getOfficeId())) {
                    notifyDisbursedLoanOwner(loan);
                }
            }
        }
    }

    private class SendSmsOnLoanRejected extends SmsBusinessEventAdapter{

        @Override
        public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
            if (entity instanceof Loan) {
                Loan loan = (Loan) entity;
                if (isSMSConfigurationEnabledForOffice(loan.getOfficeId())) {
                    notifyRejectedLoanOwner(loan);
                }
            }
        }
    }

    private class SendSmsOnLoanRepayment extends SmsBusinessEventAdapter{

        @Override
        public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN_TRANSACTION);
            if (entity instanceof LoanTransaction) {
                LoanTransaction loanTransaction = (LoanTransaction) entity;
                if (isSMSConfigurationEnabledForOffice(loanTransaction.getOfficeId())) {
                    sendSmsForLoanRepayment(loanTransaction);
                }
            }
        }
    }

    private class ClientCreatedListener extends SmsBusinessEventAdapter {

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.CLIENT);
            if (entity instanceof Client) {
                Client client = (Client) entity;
                if (isSMSConfigurationEnabledForOffice(client.officeId())) {
                    notifyClientCreated(client);
                }
            }
        }
    }

    private class ClientPaymentsListener extends SmsBusinessEventAdapter {

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.CLIENT);
            if (entity instanceof Client) {
                Client client = (Client) entity;
                if (isSMSConfigurationEnabledForOffice(client.officeId())) {
                    final String receiptNo = (String) businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.RECEIPT_NO);
                    notifyClientPaymentDone(client, receiptNo);
                }
            }
        }
    }

    private class ClientActivatedListener extends SmsBusinessEventAdapter {

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.CLIENT);
            if (entity instanceof Client) {
                Client client = (Client) entity;
                if (isSMSConfigurationEnabledForOffice(client.officeId())) {
                    notifyClientActivated(client);
                }
            }
        }
    }

    private class ClientRejectedListener extends SmsBusinessEventAdapter {

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.CLIENT);
            if (entity instanceof Client) {
                Client client = (Client) entity;
                if (isSMSConfigurationEnabledForOffice(client.officeId())) {
                    notifyClientRejected(client);
                }
            }

        }
    }

    private class SavingsAccountClosedListener extends SmsBusinessEventAdapter {

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.SAVING);
            if (entity instanceof SavingsAccount) {
                SavingsAccount savingsAccount = (SavingsAccount) entity;
                if (isSMSConfigurationEnabledForOffice(savingsAccount.officeId())) {
                    notifySavingsAccountClosed(savingsAccount);
                }
            }
        }
    }

    private class SavingsAccountActivatedListener extends SmsBusinessEventAdapter {

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.SAVING);
            if (entity instanceof SavingsAccount) {
                SavingsAccount savingsAccount = (SavingsAccount) entity;
                if (isSMSConfigurationEnabledForOffice(savingsAccount.officeId())) {
                    notifySavingsAccountActivated(savingsAccount);
                }
            }

        }
    }

    private class SavingsAccountRejectedListener extends SmsBusinessEventAdapter {

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.SAVING);
            if (entity instanceof SavingsAccount) {
                SavingsAccount savingsAccount = (SavingsAccount) entity;
                if (isSMSConfigurationEnabledForOffice(savingsAccount.officeId())) {
                    notifySavingsAccountRejected(savingsAccount);
                }
            }
        }
    }

    private class SavingsAccountTransactionListener extends SmsBusinessEventAdapter {

        final boolean isDeposit;

        public SavingsAccountTransactionListener(final boolean isDeposit) {
            this.isDeposit = isDeposit;
        }

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.SAVING_TRANSACTION);
            if (entity instanceof SavingsAccountTransaction) {
                SavingsAccountTransaction savingsAccountTransaction = (SavingsAccountTransaction) entity;
                if(isSMSConfigurationEnabledForOffice(savingsAccountTransaction.getOfficeId()))
                sendSmsForSavingsTransaction((SavingsAccountTransaction) entity, this.isDeposit);
            }
        }
    }

    /*private abstract class Task implements Runnable {
    	
    	protected final FineractPlatformTenant tenant;
    	
    	protected final String reportName ;
    	
    	private final Object entity ;
    	
    	public Task(final FineractPlatformTenant tenant, final String reportName, final Object entity) {
            this.tenant = tenant;
            this.reportName = reportName ;
            this.entity = entity ;
    	}
    }*/

    private class InboundSMSListener extends SmsBusinessEventAdapter {

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.INBOUND_SMS);
            if (entity instanceof InboundMessage) {
                final InboundMessage message = (InboundMessage) entity;
                final String mobileNumber = message.getMobileNumber();
                final String ussdCode = message.getUssdCode();
                final Client client = SmsCampaignDomainServiceImpl.this.clientRepository.findByMobileNo(mobileNumber);
                final Collection<SmsProviderData> smsProviderOptions = SmsCampaignDomainServiceImpl.this.smsCampaignDropdownReadPlatformService
                        .retrieveSmsProviders();
                if (!CollectionUtils.isEmpty(smsProviderOptions)) {
                    final Long providerId = smsProviderOptions.iterator().next().getId();
                    if (client == null) {
                        notifyMobileNotRegistered(mobileNumber, providerId);
                    } else {
                        if (isSMSConfigurationEnabledForOffice(client.officeId())) {
                            if (StringUtils.isNotBlank(ussdCode) && ussdCode.contains("balance")) {
                                final String balacesDescription = constructAccountBalancesMessage(client.getId());
                                if (StringUtils.isNotBlank(balacesDescription)) {
                                    notifyCustomerBalance(client, mobileNumber, providerId, balacesDescription);
                                } else {
                                    notifyCustomerNoAccountsExists(client, mobileNumber, providerId);
                                }
                            } else if (StringUtils.isNotBlank(ussdCode) && ussdCode.contains("mini")) {
                                final String balacesDescription = constructMiniStatementBalancesMessage(client.getId());
                                if (StringUtils.isNotBlank(balacesDescription)) {
                                    notifyCustomerMiniStatementBalance(balacesDescription, client, mobileNumber, providerId);
                                } else {
                                    notifyCustomerCannotMakeTransaction(client, mobileNumber, providerId);
                                }
                            } else {
                                notifyInvalidTextEntered(client, mobileNumber, providerId);
                            }
                        }
                    }
                }
            }
        }
    }

    private String constructMiniStatementBalancesMessage(final Long clientId) {
        final Collection<PaymentDetailCollectionData> paymentDetails = this.accountDetailsReadPlatformService
                .retrivePaymentDetail(clientId);
        String balanceDescription = "";
        if (!CollectionUtils.isEmpty(paymentDetails)) {
            for (PaymentDetailCollectionData paymentDetailCollectionData : paymentDetails) {
                String recno = paymentDetailCollectionData.getReceiptNumber();
                if (recno == null || recno.contains("dummy")) {
                    recno = "RPT: ";
                }
                balanceDescription = balanceDescription + "Rpt-" + recno + ":" + " " + paymentDetailCollectionData.getTransactionDate()
                        + " " + paymentDetailCollectionData.getAmount().setScale(2) + "  ";
            }
        }
        return balanceDescription;
    }

    private String constructAccountBalancesMessage(final Long clientId) {
        final AccountSummaryCollectionData clientAccounts = this.accountDetailsReadPlatformService.retrieveClientAccountDetails(clientId);
        final Collection<LoanAccountSummaryData> loanAccounts = clientAccounts.getLoanAccounts();
        String balanceDescription = "";
        if (!CollectionUtils.isEmpty(loanAccounts)) {
            for (LoanAccountSummaryData loanaccount : loanAccounts) {
                if (loanaccount.getLoanBalance() != null) {
                    balanceDescription = balanceDescription + " Loan Bal(ACCNO:" + loanaccount.getId() + ")- "
                            + loanaccount.getLoanBalance().setScale(2) + "";
                }
            }
        }
        final Collection<SharesAccountBalanceCollectionData> sharesBalance = this.accountDetailsReadPlatformService
                .retriveSharesBalance(clientId);
        if (!CollectionUtils.isEmpty(sharesBalance)) {
            for (SharesAccountBalanceCollectionData sharesAccountDetails : sharesBalance) {
                if (sharesAccountDetails.getAccountBalance() != null) {
                    balanceDescription = balanceDescription + " Saving Bal(ACCNO:" + sharesAccountDetails.getAccountNo() + ")- "
                            + sharesAccountDetails.getAccountBalance().setScale(2) + "";
                } else {
                    balanceDescription = balanceDescription + " Saving Bal(ACCNO:" + sharesAccountDetails.getAccountNo() + ")- " + "0.00"
                            + " ";
                }
            }
        }
        return balanceDescription;
    }

    private void notifyMobileNotRegistered(final String mobileNo, final Long providerId) {
        final String messageText = "Your MobileNo is not registered please contact to your branch.";
        sendTriggeredOutboundMessage(messageText, mobileNo, providerId);
    }

    private void notifyInvalidTextEntered(final Client client, final String mobileNo, final Long providerId) {
        final String messageText = "Dear " + client.getDisplayName()
                + ",  for balance enquiry please,type Caritas Balance and for mini statement type Caritas Mini Thanks, "
                + client.getOfficeName() + ".";
        sendTriggeredOutboundMessage(messageText, mobileNo, providerId);
    }

    private void notifyCustomerBalance(final Client client, final String mobileNo, final Long providerId, final String balacesDescription) {
        final String messageText = "Dear " + client.getDisplayName() + ",  Your " + balacesDescription + " thanks." + client.getOfficeName()
                + ".";
        sendTriggeredOutboundMessage(messageText, mobileNo, providerId);
    }

    private void notifyCustomerNoAccountsExists(final Client client, final String mobileNo, final Long providerId) {
        final String messageText = "Dear " + client.getDisplayName() + ", you don't have Loan and Savings Account Thanks, "
                + client.getOfficeName() + ".";
        sendTriggeredOutboundMessage(messageText, mobileNo, providerId);
    }

    private void notifyCustomerMiniStatementBalance(final String balacesDescription, final Client client, final String mobileNo,
            final Long providerId) {
        final String messageText = "Dear " + client.getDisplayName() + ",  Your MiniStmt is " + balacesDescription + " thanks."
                + client.getOfficeName() + ".";
        sendTriggeredOutboundMessage(messageText, mobileNo, providerId);
    }

    private void notifyCustomerCannotMakeTransaction(final Client client, final String mobileNo, final Long providerId) {
        final String messageText = "Dear " + client.getDisplayName() + ", cannot make a Transaction Thanks, " + client.getOfficeName()
                + ".";
        sendTriggeredOutboundMessage(messageText, mobileNo, providerId);
    }

    private void sendTriggeredOutboundMessage(final String messageText, final String mobileNo, final Long providerId) {
        final SmsMessage message = SmsMessage.instance(null, null, null, null, SmsMessageStatusType.PENDING, messageText, mobileNo, null);
        this.smsMessageRepository.save(message);
        final List<SmsMessage> smsMessages = new ArrayList<>();
        smsMessages.add(message);
        this.smsMessageScheduledJobService.sendTriggeredMessage(smsMessages, providerId);
    }
}
