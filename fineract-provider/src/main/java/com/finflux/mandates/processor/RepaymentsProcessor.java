package com.finflux.mandates.processor;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collection;

import org.apache.fineract.infrastructure.configuration.data.NACHCredentialsData;
import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.finflux.mandates.data.MandateProcessCounts;
import com.finflux.mandates.data.MandateTransactionsData;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.data.ProcessResponseData;
import com.finflux.mandates.fileformat.TransactionsFileFormatHelper;
import com.finflux.mandates.service.MandatesProcessingStatusPlatformWriteService;
import com.finflux.mandates.service.TransactionsProcessingReadPlatformService;
import com.finflux.mandates.service.TransactionsProcessingWritePlatformService;

@Component
public class RepaymentsProcessor {

        private final static String FAILED = "FAILED";
        private final static String SUCCESS = "SUCCESS";
        private final static String PROCESSED = "PROCESSED";
        private final static String NOT_PROCESSED = "NOT PROCESSED";
        private final MandatesProcessingStatusPlatformWriteService mandatesProcessingWritePlatformService;
        private final DocumentReadPlatformService documentReadPlatformService;
        private final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService;
        private final ApplicationContext applicationContext;
        private final TransactionsProcessingReadPlatformService transactionsProcessingReadPlatformService;
        private final TransactionsProcessingWritePlatformService transactionsProcessingWritePlatformService;
        private final LoanAccountDomainService loanAccountDomainService;

        @Autowired
        public RepaymentsProcessor(final DocumentReadPlatformService documentReadPlatformService,
                final MandatesProcessingStatusPlatformWriteService mandatesProcessingWritePlatformService,
                final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService,
                final ApplicationContext applicationContext,
                final TransactionsProcessingReadPlatformService transactionsProcessingReadPlatformService,
                final TransactionsProcessingWritePlatformService transactionsProcessingWritePlatformService,
                final LoanAccountDomainService loanAccountDomainService){

                this.mandatesProcessingWritePlatformService = mandatesProcessingWritePlatformService;
                this.documentReadPlatformService = documentReadPlatformService;
                this.externalServicesPropertiesReadPlatformService = externalServicesPropertiesReadPlatformService;
                this.applicationContext = applicationContext;
                this.transactionsProcessingReadPlatformService = transactionsProcessingReadPlatformService;
                this.transactionsProcessingWritePlatformService = transactionsProcessingWritePlatformService;
                this.loanAccountDomainService = loanAccountDomainService;
        }

        public void processTransactionsResponse(MandatesProcessData processData, MandateProcessCounts counts) throws IOException, InvalidFormatException,
                ParseException {
                FileData fileData = this.documentReadPlatformService.retrieveFileData("mandates", 1L, processData.getDocumentId());
                NACHCredentialsData nachProperties = this.externalServicesPropertiesReadPlatformService.getNACHCredentials();
                TransactionsFileFormatHelper formatter = this.applicationContext.getBean(nachProperties.getPROCESSOR_QUALIFIER()+"TransactionsFileFormatHelper",
                        TransactionsFileFormatHelper.class);
                Collection<ProcessResponseData> responseDatas = formatter.formatTransactionsResponseData(processData, nachProperties, fileData);
                if(null != responseDatas && responseDatas.size() > 0){
                        for(ProcessResponseData data : responseDatas){
                                processTransaction(data, processData.getId(), counts);
                        }
                }
                FileData processedFileData = formatter.updateProcessStatusToFile(processData, responseDatas, fileData);
                this.mandatesProcessingWritePlatformService.updateDocument(processData, processedFileData);
                counts.setTotalRecords(counts.getSuccessRecords()+counts.getFailedRecords()+counts.getUnprocessedRecords());
        }

        private void processTransaction(final ProcessResponseData data, final Long mandateProcessId, MandateProcessCounts counts) {
                MandateTransactionsData transactionToProcess = this.transactionsProcessingReadPlatformService
                        .findOneByLoanAccountNoAndInprocessStatus(data.getReference());
                if(null == transactionToProcess){
                        data.setProcessStatus(NOT_PROCESSED, "Couldn't find matching Transaction based on reference");
                        counts.setUnprocessedRecords(counts.getUnprocessedRecords()+1);
                }else{
                        if(data.getStatus().equalsIgnoreCase(FAILED)){
                                this.transactionsProcessingWritePlatformService.updateTransactionAsFailed(transactionToProcess.getId(),
                                        data.getFailureReason(), mandateProcessId.toString());
                                data.setProcessStatus(PROCESSED,"Mandate Transaction Id marked as failed:"+transactionToProcess.getId());
                                counts.setFailedRecords(counts.getFailedRecords()+1);
                        }else if(data.getStatus().equalsIgnoreCase(SUCCESS)){
                                try{
                                        Long repaymentTransactionId = processRepayment(data, transactionToProcess);
                                        this.transactionsProcessingWritePlatformService
                                                .updateTransactionAsSuccess(transactionToProcess.getId(),
                                                        repaymentTransactionId, mandateProcessId.toString());
                                        data.setProcessStatus(PROCESSED,"Repayment Transaction Id:"+repaymentTransactionId);
                                        counts.setSuccessRecords(counts.getSuccessRecords()+1);
                                }catch (RuntimeException e){
                                    AbstractPlatformDomainRuleException exc ;
                                    if(e instanceof AbstractPlatformDomainRuleException) {
                                        exc = (AbstractPlatformDomainRuleException) e ;
                                        this.transactionsProcessingWritePlatformService.updateTransactionAsFailed(transactionToProcess.getId(),
                                                exc.getDefaultUserMessage(), mandateProcessId.toString());
                                        data.setProcessStatus(PROCESSED,exc.getDefaultUserMessage());
                                        counts.setFailedRecords(counts.getFailedRecords()+1);
                                    }else {
                                        this.transactionsProcessingWritePlatformService.updateTransactionAsFailed(transactionToProcess.getId(),
                                                ""+e.getClass(), mandateProcessId.toString());
                                        data.setProcessStatus(PROCESSED,"Failed due to domain rule or platform error");
                                        counts.setFailedRecords(counts.getFailedRecords()+1);
                                    }
                                }
                        }else{
                                data.setProcessStatus(NOT_PROCESSED, "Couldn't parse status column");
                                counts.setUnprocessedRecords(counts.getUnprocessedRecords()+1);
                        }
                }
        }

        private Long processRepayment(ProcessResponseData data, MandateTransactionsData transactionToProcess) {
                final CommandProcessingResultBuilder builderResult = new CommandProcessingResultBuilder();
                final LocalDate transactionDate = new LocalDate(data.getTransactionDate());
                final BigDecimal transactionAmount = data.getAmount();
                final PaymentDetail paymentDetail = null;
                final String noteText = "Payment thru NACH transaction";
                final String txnExternalId = null;
                final boolean isRecoveryRepayment = false;
                final boolean isAccountTransfer = false;
                final HolidayDetailDTO holidayDetailDto = null;
                final Boolean isHolidayValidationDone = false;
                final boolean isLoanToLoanTransfer = false;
                final boolean isPrepayment = false;
                LoanTransaction transaction = this.loanAccountDomainService.makeRepayment(transactionToProcess.getLoanId(), builderResult, transactionDate,
                        transactionAmount, paymentDetail, noteText, txnExternalId, isRecoveryRepayment, isAccountTransfer,
                        holidayDetailDto, isHolidayValidationDone, isLoanToLoanTransfer, isPrepayment);
                return transaction.getId();
        }


}
