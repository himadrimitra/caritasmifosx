/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.service;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.glaccount.data.GLAccountDataForLookup;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepository;
import org.apache.fineract.accounting.journalentry.exception.JournalEntriesNotFoundException;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommandValidator;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.documentmanagement.domain.DocumentRepository;
import org.apache.fineract.infrastructure.documentmanagement.exception.DocumentNotFoundException;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformService;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformServiceJpaRepositoryImpl;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformService;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.service.AccountTransfersWritePlatformService;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.data.AdjustedLoanTransactionDetails;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.bulkoperations.BulkStatementEnumType;
import com.finflux.common.util.FinfluxCollectionUtils;
import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bank.domain.Bank;
import com.finflux.reconcilation.bank.domain.BankRepositoryWrapper;
import com.finflux.reconcilation.bank.exception.BankNotAssociatedExcecption;
import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;
import com.finflux.reconcilation.bankstatement.domain.BankStatement;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetailType;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetails;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetailsRepository;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetailsRepositoryWrapper;
import com.finflux.reconcilation.bankstatement.domain.BankStatementRepositoryWrapper;
import com.finflux.reconcilation.bankstatement.exception.GLAccountNotFoundException;
import com.finflux.reconcilation.bankstatement.exception.InvalidCIFRowException;
import com.finflux.reconcilation.bankstatement.exception.InvalidCifFileFormatException;
import com.finflux.reconcilation.bankstatement.exception.NotExcelFileException;
import com.finflux.reconcilation.bankstatement.helper.ExcelUtility;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

@Service
public class BankStatementWritePlatformServiceJpaRepository implements BankStatementWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DocumentWritePlatformServiceJpaRepositoryImpl.class);
    private final PlatformSecurityContext context;
    private final DocumentRepository documentRepository;
    private final ContentRepositoryFactory contentRepositoryFactory;
    private final BankStatementRepositoryWrapper bankStatementRepository;
    private final BankStatementReadPlatformService bankStatementReadPlatformService;
    private final BankStatementDetailsRepositoryWrapper bankStatementDetailsRepositoryWrapper;
    private final DocumentWritePlatformService documentWritePlatformService;
    private final LoanTransactionRepositoryWrapper loanTransactionRepository;
    private final BankRepositoryWrapper bankRepository;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final JournalEntryRepository journalEntryRepository;
    private final BankGLAccountReadPlatformService bankGLAccountReadPlatformService;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final BankStatementDetailsReadPlatformService bankStatementDetailsReadPlatformService;
    private final LoanAssembler loanAssembler;
    private final LoanAccountDomainService loanAccountDomainService;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final AccountTransfersWritePlatformService accountTransfersWritePlatformService;
    private final PaymentTypeRepository paymentTypeRepository;
    private final PaymentDetailRepository paymentDetailRepository;
    private final BankStatementDetailsRepository bankStatementDetailsRepository;
    private final OfficeReadPlatformService readPlatformService;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final SavingsAccountDomainService savingsAccountDomainService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;

    @Autowired
    public BankStatementWritePlatformServiceJpaRepository(final PlatformSecurityContext context,
            final DocumentRepository documentRepository, final ContentRepositoryFactory contentRepositoryFactory,
            final BankStatementRepositoryWrapper bankStatementRepository,
            final BankStatementReadPlatformService bankStatementReadPlatformService,
            final BankStatementDetailsRepositoryWrapper bankStatementDetailsRepositoryWrapper,
            final DocumentWritePlatformService documentWritePlatformService,
            final LoanTransactionRepositoryWrapper loanTransactionRepository, final BankRepositoryWrapper bankRepository,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final JournalEntryRepository journalEntryRepository, final BankGLAccountReadPlatformService bankGLAccountReadPlatformService,
            final CurrencyReadPlatformService currencyReadPlatformService,
            final BankStatementDetailsReadPlatformService bankStatementDetailsReadPlatformService, final LoanAssembler loanAssembler,
            final LoanAccountDomainService loanAccountDomainService, final BusinessEventNotifierService businessEventNotifierService,
            final AccountTransfersWritePlatformService accountTransfersWritePlatformService,
            final PaymentTypeRepository paymentTypeRepository, final PaymentDetailRepository paymentDetailRepository,
            final BankStatementDetailsRepository bankStatementDetailsRepository, final OfficeReadPlatformService readPlatformService,
            final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final SavingsAccountDomainService savingsAccountDomainService, final LoanReadPlatformService loanReadPlatformService,
            final SavingsAccountReadPlatformService savingsAccountReadPlatformService) {
        this.context = context;
        this.documentRepository = documentRepository;
        this.contentRepositoryFactory = contentRepositoryFactory;
        this.bankStatementRepository = bankStatementRepository;
        this.bankStatementReadPlatformService = bankStatementReadPlatformService;
        this.bankStatementDetailsRepositoryWrapper = bankStatementDetailsRepositoryWrapper;
        this.documentWritePlatformService = documentWritePlatformService;
        this.loanTransactionRepository = loanTransactionRepository;
        this.bankRepository = bankRepository;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.journalEntryRepository = journalEntryRepository;
        this.bankGLAccountReadPlatformService = bankGLAccountReadPlatformService;
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.bankStatementDetailsReadPlatformService = bankStatementDetailsReadPlatformService;
        this.loanAssembler = loanAssembler;
        this.loanAccountDomainService = loanAccountDomainService;
        this.businessEventNotifierService = businessEventNotifierService;
        this.accountTransfersWritePlatformService = accountTransfersWritePlatformService;
        this.paymentTypeRepository = paymentTypeRepository;
        this.paymentDetailRepository = paymentDetailRepository;
        this.bankStatementDetailsRepository = bankStatementDetailsRepository;
        this.readPlatformService = readPlatformService;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.loanReadPlatformService = loanReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
    }

    @Transactional
    @Override
    public Long createBankStatement(final FormDataMultiPart formParams) {
        try {
            this.context.authenticatedUser();
            final Document cpifDocument = getDocument(ReconciliationApiConstants.FIRST_FILE, formParams);
            Document orgDocument = null;
            if (formParams.getFields().containsKey(ReconciliationApiConstants.SECOND_FILE)) {
                orgDocument = getDocument(ReconciliationApiConstants.SECOND_FILE, formParams);
            }
            Bank bank = null;
            if (formParams.getFields().containsKey(ReconciliationApiConstants.bankParamName)) {

                final String bankId = formParams.getField(ReconciliationApiConstants.bankParamName).getValue();
                bank = this.bankRepository.findOneWithNotFoundDetection(Long.valueOf(bankId));
                if (!bank.getSupportSimplifiedStatement()) {
                    validateExcel(cpifDocument, bank.getSupportSimplifiedStatement());
                }
                BankStatement bankStatement = BankStatement.instance(cpifDocument.getName(), cpifDocument.getDescription(), cpifDocument,
                        orgDocument, false, bank, BulkStatementEnumType.BANKTRANSACTIONS.getValue());
                bankStatement = saveBankStatementDetails(cpifDocument.getId(), bankStatement, bank.getSupportSimplifiedStatement());
                return bankStatement.getId();
            }
            throw new BankNotAssociatedExcecption();

        } catch (final DataIntegrityViolationException dve) {
            logger.error(dve.getMessage(), dve);
            throw new PlatformDataIntegrityException("error.msg.document.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        }
    }

    public void validateExcel(final Document document, final boolean isSimplifiedExcel) {
        if (ReconciliationApiConstants.EXCEL_FILE.indexOf(document.getType()) < 0) { throw new NotExcelFileException(); }
        final String[] headerData = (isSimplifiedExcel) ? ReconciliationApiConstants.SIMPLIFIED_HEADER_DATA
                : ReconciliationApiConstants.HEADER_DATA;
        final boolean bool = ExcelUtility.isValidExcelHeader(this.bankStatementReadPlatformService.retrieveFile(document.getId()),
                headerData);

        if (!bool) { throw new InvalidCifFileFormatException(); }
    }

    public static Map<String, Object> createMapOfFileData(final File file) {
        final Map<String, Object> fileDataMap = new HashMap<>();
        final Set<Integer> errorRows = new TreeSet<>();
        final Map<Integer, List<String>> errorRowMap = new LinkedHashMap<>();
        final List<BankStatementDetails> bankStatementDetailsList = new LinkedList<>();
        final List<Row> rows = ExcelUtility.getAllRows(file);
        try {
            for (final Row row : rows) {
                String transactionId = null;
                Date transactionDate = null;
                String description = null;
                BigDecimal amount = null;
                String mobileNumber = null;
                String clientAccountNumber = null;
                String loanAccountNumber = null;
                String groupExternalId = null;
                final List<String> rowError = new ArrayList<>();
                Boolean isValid = true;
                String branchExternalId = null;
                String accountingType = null;
                String glCode = "";
                String bankStatementTransactionType = "";
                if (row.getRowNum() != 0) {
                    for (int i = 0; i < ReconciliationApiConstants.HEADER_DATA.length; i++) {
                        final Cell cell = row.getCell(i);
                        switch (i) {
                            case 0:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    transactionId = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case 1:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK
                                        && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    try {
                                        transactionDate = cell.getDateCellValue();
                                    } catch (final Exception e) {
                                        errorRows.add(row.getRowNum() + 1);
                                        rowError.add(ReconciliationApiConstants.INVALID_TRANSACTION_DATE);
                                    }
                                } else {
                                    errorRows.add(row.getRowNum() + 1);
                                    rowError.add(ReconciliationApiConstants.TRANSACTION_DATE_CAN_NOT_BE_BLANK);
                                    isValid = false;
                                }
                            break;
                            case 2:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    description = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case 3:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    try {
                                        amount = new BigDecimal(cell.toString());
                                    } catch (final NumberFormatException e) {
                                        errorRows.add(row.getRowNum() + 1);
                                        rowError.add(ReconciliationApiConstants.AMOUNT_INVALID);
                                    }
                                } else {
                                    errorRows.add(row.getRowNum() + 1);
                                    rowError.add(ReconciliationApiConstants.AMOUNT_CAN_NOT_BE_BLANK);
                                    isValid = false;
                                }
                            break;
                            case 4:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    mobileNumber = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case 5:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    clientAccountNumber = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case 6:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    loanAccountNumber = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case 7:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    groupExternalId = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case 8:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    branchExternalId = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case 9:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    glCode = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case 10:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    accountingType = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case 11:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    bankStatementTransactionType = ExcelUtility.getCellValueAsString(cell);
                                } else {
                                    errorRows.add(row.getRowNum() + 1);
                                    rowError.add(ReconciliationApiConstants.TRANSACTION_TYPE_CAN_NOT_BE_BLANK);
                                    isValid = false;
                                }
                            break;

                        }

                    }

                }

                if (row.getRowNum() != 0 && isValid) {
                    Integer bankStatementDetailType = 0;
                    if (bankStatementTransactionType.replaceAll(" ", "")
                            .equalsIgnoreCase((ReconciliationApiConstants.OTHER).replaceAll(" ", ""))) {
                        transactionId = null;
                        bankStatementDetailType = BankStatementDetailType.NONPORTFOLIO.getValue();
                    } else if (bankStatementTransactionType.replaceAll(" ", "")
                            .equalsIgnoreCase((ReconciliationApiConstants.ERROR).replaceAll(" ", ""))) {
                        transactionId = null;
                        bankStatementDetailType = BankStatementDetailType.MISCELLANEOUS.getValue();
                    } else {
                        bankStatementDetailType = BankStatementDetailType.PORTFOLIO.getValue();
                    }
                    final boolean isReconciled = false;
                    final boolean isManualReconciled = false;
                    final LoanTransaction loanTransaction = null;
                    final String receiptNumber = null;
                    final Long transactionIdForUpdate = null;
                    final BankStatementDetails bankStatementDetails = BankStatementDetails.instance(null, transactionId, transactionDate,
                            description, amount, mobileNumber, clientAccountNumber, loanAccountNumber, groupExternalId, isReconciled,
                            loanTransaction, branchExternalId, accountingType, glCode, bankStatementTransactionType,
                            bankStatementDetailType, receiptNumber, isManualReconciled, transactionIdForUpdate);
                    bankStatementDetails.setIsError(false);
                    bankStatementDetailsList.add(bankStatementDetails);
                }
                if (row.getRowNum() != 0) {
                    if (rowError.size() > 0) {
                        errorRowMap.put(row.getRowNum() + 1, rowError);
                    }
                }
            }

        } catch (final Exception ex) {}
        fileDataMap.put(ReconciliationApiConstants.BANK_STATEMENT_DETAIL_LIST, bankStatementDetailsList);
        fileDataMap.put(ReconciliationApiConstants.ERROR_ROWS, errorRowMap);

        return fileDataMap;
    }

    public static Map<String, Object> createMapOfSimplifiedFileData(final File file, final String paymentTypeName) {
        final Map<String, Object> fileDataMap = new HashMap<>();
        final Set<Integer> errorRows = new TreeSet<>();
        final Map<Integer, List<String>> errorRowMap = new LinkedHashMap<>();
        final List<BankStatementDetails> bankStatementDetailsList = new LinkedList<>();
        final List<Row> rows = ExcelUtility.getAllRows(file);
        final Row headerData = rows.get(ReconciliationApiConstants.headerIndex);

        final int transactioDateIndex = ExcelUtility.getColumnNumberByColumnHeaderName(headerData, ReconciliationApiConstants.transDate);
        final int receiptNumberIndex = ExcelUtility.getColumnNumberByColumnHeaderName(headerData, ReconciliationApiConstants.serial);
        final int amountIndex = ExcelUtility.getColumnNumberByColumnHeaderName(headerData, ReconciliationApiConstants.amount);
        final int loanIdIndex = ExcelUtility.getColumnNumberByColumnHeaderName(headerData, ReconciliationApiConstants.description);

        try {
            for (final Row row : rows) {
                Date transactionDate = null;
                String receiptNumber = null;
                BigDecimal amount = null;
                String loanAccountNumber = null;
                final List<String> rowError = new ArrayList<>();
                Boolean isValid = true;
                if (row.getRowNum() > 4) {
                    final Cell transactionDateCell = row.getCell(transactioDateIndex);
                    if (transactionDateCell != null && transactionDateCell.getCellType() != Cell.CELL_TYPE_BLANK
                            && transactionDateCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        try {
                            transactionDate = transactionDateCell.getDateCellValue();
                        } catch (final Exception e) {
                            errorRows.add(row.getRowNum() + 1);
                            rowError.add(ReconciliationApiConstants.INVALID_TRANSACTION_DATE);
                        }
                    } else {
                        errorRows.add(row.getRowNum() + 1);
                        rowError.add(ReconciliationApiConstants.TRANSACTION_DATE_CAN_NOT_BE_BLANK);
                        isValid = false;
                    }
                    final Cell receiptNumberCell = row.getCell(receiptNumberIndex);
                    if (receiptNumberCell != null && receiptNumberCell.getCellType() != Cell.CELL_TYPE_BLANK) {
                        receiptNumber = ExcelUtility.getCellValueAsString(receiptNumberCell);
                    }
                    final Cell amountCell = row.getCell(amountIndex);
                    if (amountCell != null && amountCell.getCellType() != Cell.CELL_TYPE_BLANK) {
                        try {
                            amount = new BigDecimal(amountCell.toString());
                        } catch (final NumberFormatException e) {
                            errorRows.add(row.getRowNum() + 1);
                            rowError.add(ReconciliationApiConstants.AMOUNT_INVALID);
                        }
                    } else {
                        errorRows.add(row.getRowNum() + 1);
                        rowError.add(ReconciliationApiConstants.AMOUNT_CAN_NOT_BE_BLANK);
                        isValid = false;
                    }
                    final Cell loanIdCell = row.getCell(loanIdIndex);
                    if (loanIdCell != null && loanIdCell.getCellType() != Cell.CELL_TYPE_BLANK) {
                        loanAccountNumber = ExcelUtility.getCellValueAsString(loanIdCell);
                    } else {
                        errorRows.add(row.getRowNum() + 1);
                        rowError.add(ReconciliationApiConstants.LOAN_ACCOUNT_NUMBER_CAN_NOT_BE_BLANK);
                        isValid = false;
                    }

                }

                if (row.getRowNum() > ((ReconciliationApiConstants.headerIndex) + 1)) {
                    if (isValid) {
                        final Integer bankStatementDetailType = BankStatementDetailType.SIMPLIFIED_PORTFOLIO.getValue();
                        final BankStatement bankStatement = null;
                        final boolean isManualReconciled = false;
                        final boolean isError = false;
                        final BankStatementDetails bankStatementDetails = BankStatementDetails.simplifiedBankDetails(bankStatement,
                                transactionDate, amount, loanAccountNumber, receiptNumber, bankStatementDetailType, isManualReconciled,
                                paymentTypeName, isError);
                        bankStatementDetailsList.add(bankStatementDetails);

                    } else if (rowError.size() > 0) {
                        errorRowMap.put(row.getRowNum() + 1, rowError);
                    }
                }
            }

        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        fileDataMap.put(ReconciliationApiConstants.BANK_STATEMENT_DETAIL_LIST, bankStatementDetailsList);
        fileDataMap.put(ReconciliationApiConstants.ERROR_ROWS, errorRowMap);

        return fileDataMap;
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteBankStatement(final JsonCommand command) {

        final BankStatement bankStatement = this.bankStatementRepository.findOneWithNotFoundDetection(command.entityId());
        final Document cpifDocument = bankStatement.getCpifDocument();
        final Document orgDocument = bankStatement.getOrgStatementDocument();
        revertChanges(command);
        this.bankStatementRepository.delete(bankStatement);
        this.documentRepository.delete(cpifDocument);
        if (orgDocument != null) {
            this.documentRepository.delete(orgDocument);
        }
        return new CommandProcessingResultBuilder() //
                .withEntityId(command.entityId()) //
                .build();
    }

    private void revertChanges(final JsonCommand command) {
        final Long bankStatementId = command.entityId();
        final List<BankStatementDetailsData> changedBankStatementDetailsData = this.bankStatementDetailsReadPlatformService
                .changedBankStatementDetailsData(bankStatementId);
        final List<String> changedJournalEntriesData = new ArrayList<>();
        final List<Long> loanTransactionForUndoReconcile = new ArrayList<>();
        final List<BankStatementDetailsData> loanTransactionsForUndo = new ArrayList<>();
        if (changedBankStatementDetailsData.size() > 0) {
            for (final BankStatementDetailsData bankStatementDetailsData : changedBankStatementDetailsData) {
                if (bankStatementDetailsData.getLoanAccountNumber() != null) {
                    loanTransactionsForUndo.add(bankStatementDetailsData);
                } else if (bankStatementDetailsData.getBankStatementDetailType() == BankStatementDetailType.NONPORTFOLIO.getValue()) {
                    changedJournalEntriesData.add(bankStatementDetailsData.getTransactionId());
                } else if (bankStatementDetailsData.getBankStatementDetailType() == BankStatementDetailType.PORTFOLIO.getValue()
                        && !bankStatementDetailsData.getIsManualReconciled()) {
                    loanTransactionForUndoReconcile.add(bankStatementDetailsData.getLoanTransactionId());
                }
            }
        }
        undoReconcileLoanTransactions(loanTransactionForUndoReconcile);
        revertJournalEntries(changedJournalEntriesData);
        undoLoanTransactions(loanTransactionsForUndo, command);

    }

    private void undoReconcileLoanTransactions(final List<Long> transactionList) {
        final List<LoanTransaction> loanTransactions = new ArrayList<>();
        for (final Long transaction : transactionList) {
            final LoanTransaction loanTransaction = this.loanTransactionRepository.findOneWithNotFoundDetection(transaction);
            loanTransaction.setReconciled(false);
            loanTransactions.add(loanTransaction);
        }
        this.loanTransactionRepository.save(loanTransactions);
    }

    private void undoLoanTransactions(final List<BankStatementDetailsData> bankStatementDetailsList, final JsonCommand command) {
        final BigDecimal transactionAmount = BigDecimal.ZERO;
        final String txnExternalId = null;
        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        final String noteText = null;
        final PaymentDetail paymentDetail = null;
        final boolean isAccountTransfer = false;
        final boolean isLoanToLoanTransfer = false;
        for (final BankStatementDetailsData bankStatementDetailsData : bankStatementDetailsList) {
            final Long transactionId = Long.parseLong(bankStatementDetailsData.getTransactionId());
            AdjustedLoanTransactionDetails changedLoanTransactionDetails = null;
            final Loan loan = this.loanAssembler.assembleFrom(Long.parseLong(bankStatementDetailsData.getLoanAccountNumber()));
            final LocalDate transactionDate = new LocalDate(bankStatementDetailsData.getTransactionDate());
            changedLoanTransactionDetails = this.loanAccountDomainService.reverseLoanTransactions(loan, transactionId, transactionDate,
                    transactionAmount, txnExternalId, locale, fmt, noteText, paymentDetail, isAccountTransfer, isLoanToLoanTransfer);
            this.accountTransfersWritePlatformService.reverseTransfersWithFromAccountType(loan.getId(), PortfolioAccountType.LOAN);
            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_ADJUST_TRANSACTION,
                    constructEntityMap(BUSINESS_ENTITY.LOAN_ADJUSTED_TRANSACTION, changedLoanTransactionDetails.getTransactionToAdjust()));
            final Map<BUSINESS_ENTITY, Object> entityMap = constructEntityMap(BUSINESS_ENTITY.LOAN_ADJUSTED_TRANSACTION,
                    changedLoanTransactionDetails.getTransactionToAdjust());
            if (changedLoanTransactionDetails.getNewTransactionDetail().isRepayment()
                    && changedLoanTransactionDetails.getNewTransactionDetail().isGreaterThanZero(loan.getPrincpal().getCurrency())) {
                entityMap.put(BUSINESS_ENTITY.LOAN_TRANSACTION, changedLoanTransactionDetails.getNewTransactionDetail());
            }
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_ADJUST_TRANSACTION, entityMap);

        }
    }

    private Map<BUSINESS_ENTITY, Object> constructEntityMap(final BUSINESS_ENTITY entityEvent, final Object entity) {
        final Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }

    private void revertJournalEntries(final List<String> journalEntryList) {
        for (final String transactionId : journalEntryList) {
            final List<JournalEntry> JournalEntryDetails = this.journalEntryRepository
                    .findUnReversedManualJournalEntriesByTransactionId(transactionId);
            final String reversalComment = "";
            if (JournalEntryDetails.isEmpty()) { throw new JournalEntriesNotFoundException(transactionId); }
            this.journalEntryWritePlatformService.revertJournalEntry(JournalEntryDetails, reversalComment);
        }

    }

    private Document getDocument(final String key, final FormDataMultiPart formParams) {
        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
        final String name = formParams.getField(ReconciliationApiConstants.nameParamName).getValue();
        String description = null;
        if (formParams.getField(ReconciliationApiConstants.descriptionParamName) != null) {
            description = formParams.getField(ReconciliationApiConstants.descriptionParamName).getValue();
        }
        final String size = (key.equals(ReconciliationApiConstants.FIRST_FILE)) ? ReconciliationApiConstants.CIF_FILE_SIZE
                : ReconciliationApiConstants.ORIGINAL_FILE_SIZE;
        final Long fileSize = new Long(formParams.getField(size).getValue());

        final FormDataBodyPart bodyPart = formParams.getField(key);
        final InputStream inputStream = bodyPart.getEntityAs(InputStream.class);
        final String fileName = bodyPart.getFormDataContentDisposition().getFileName();
        final Long reportIdentifier = null;
        final Long tagIdentifier = null;
        final DocumentCommand documentCommand = new DocumentCommand(null, null, ReconciliationApiConstants.entityName,
                ReconciliationApiConstants.bankStatementFolder, name, fileName, fileSize, bodyPart.getMediaType().toString(), description,
                null, reportIdentifier, tagIdentifier);
        final String fileLocation = contentRepository.saveFile(inputStream, documentCommand);
        final Document document = Document.createNew(documentCommand.getParentEntityType(), documentCommand.getParentEntityId(),
                documentCommand.getName(), documentCommand.getFileName(), documentCommand.getSize(), documentCommand.getType(),
                documentCommand.getDescription(), fileLocation, contentRepository.getStorageType());

        this.documentRepository.save(document);
        return document;
    }

    @Transactional
    @Override
    public Long updateBankStatement(final FormDataMultiPart formParams) {
        Long orgDocumentId = null;
        Long cifDocumentId = null;
        final String bankStatementid = formParams.getField(ReconciliationApiConstants.idParamName).getValue();
        final BankStatement existingBankStatement = this.bankStatementRepository.findOneWithNotFoundDetection(new Long(bankStatementid));
        final String name = formParams.getField(ReconciliationApiConstants.nameParamName).getValue();
        String description = null;
        if (formParams.getField(ReconciliationApiConstants.descriptionParamName) != null) {
            description = formParams.getField(ReconciliationApiConstants.descriptionParamName).getValue();
        }
        existingBankStatement.setName(name);
        existingBankStatement.setDescription(description);
        Bank bank = null;
        final String bankId = formParams.getField(ReconciliationApiConstants.bankParamName).getValue();
        if (bankId != null && !bankId.equalsIgnoreCase("undefined")) {
            bank = this.bankRepository.findOneWithNotFoundDetection(Long.valueOf(bankId));
        } else {
            throw new BankNotAssociatedExcecption();
        }
        existingBankStatement.setBank(bank);

        final Long cifFileSize = getSize(formParams.getField(ReconciliationApiConstants.CIF_FILE_SIZE).getValue() + "");
        final Long orgFileSize = getSize(formParams.getField(ReconciliationApiConstants.ORIGINAL_FILE_SIZE).getValue() + "");
        if (cifFileSize != null && orgFileSize != null) {
            cifDocumentId = getUpdatedDocument(formParams, existingBankStatement.getCpifDocument().getId(), name, description, cifFileSize,
                    ReconciliationApiConstants.FIRST_FILE);
            if (existingBankStatement.getOrgStatementDocument() == null) {
                orgDocumentId = getUpdatedDocument(formParams, null, name, description, orgFileSize,
                        ReconciliationApiConstants.SECOND_FILE);
            } else {
                orgDocumentId = getUpdatedDocument(formParams, existingBankStatement.getOrgStatementDocument().getId(), name, description,
                        orgFileSize, ReconciliationApiConstants.SECOND_FILE);
            }
        } else if (cifFileSize != null) {
            cifDocumentId = getUpdatedDocument(formParams, existingBankStatement.getCpifDocument().getId(), name, description, cifFileSize,
                    ReconciliationApiConstants.FIRST_FILE);
        } else if (orgFileSize != null) {
            if (existingBankStatement.getOrgStatementDocument() == null) {
                orgDocumentId = getUpdatedDocument(formParams, null, name, description, orgFileSize, ReconciliationApiConstants.FIRST_FILE);
            } else {
                orgDocumentId = getUpdatedDocument(formParams, existingBankStatement.getOrgStatementDocument().getId(), name, description,
                        orgFileSize, ReconciliationApiConstants.FIRST_FILE);
            }
        }

        if (cifDocumentId != null) {
            final Document document = this.documentRepository.getOne(cifDocumentId);
            validateExcel(document, bank.getSupportSimplifiedStatement());
            final Map<String, Object> fileContent = createMapOfFileData(this.bankStatementReadPlatformService.retrieveFile(cifDocumentId));
            @SuppressWarnings("unchecked")
            final List<BankStatementDetails> bankStatementDetailsList = (List<BankStatementDetails>) fileContent
                    .get(ReconciliationApiConstants.BANK_STATEMENT_DETAIL_LIST);
            @SuppressWarnings("unchecked")
            final Map<Integer, List<String>> errorRows = (Map<Integer, List<String>>) fileContent
                    .get(ReconciliationApiConstants.ERROR_ROWS);
            if (errorRows.size() == 0) {
                for (final BankStatementDetails bankStatementDetail : bankStatementDetailsList) {
                    bankStatementDetail.setBankStatement(existingBankStatement);
                }
                this.bankStatementDetailsRepositoryWrapper.save(bankStatementDetailsList);
            } else {
                throw new InvalidCIFRowException(errorRows);
            }
            existingBankStatement.setIsReconciled(false);
            existingBankStatement.setCpifDocument(document);
        }
        if (orgDocumentId != null) {
            final Document document = this.documentRepository.getOne(orgDocumentId);
            existingBankStatement.setOrgStatementDocument(document);
        }
        this.bankStatementRepository.save(existingBankStatement);
        return existingBankStatement.getId();

    }

    @SuppressWarnings("unchecked")
    public BankStatement saveBankStatementDetails(final Long cpifDocumentId, final BankStatement bankStatement,
            final Boolean isSimplifiedExcel) {
        final File file = this.bankStatementReadPlatformService.retrieveFile(cpifDocumentId);
        final String paymentTypeName = ExcelUtility.getPaymentType(file);
        final Map<String, Object> fileContent = isSimplifiedExcel ? createMapOfSimplifiedFileData(file, paymentTypeName)
                : createMapOfFileData(file);
        final List<BankStatementDetails> bankStatementDetailsList = (List<BankStatementDetails>) fileContent
                .get(ReconciliationApiConstants.BANK_STATEMENT_DETAIL_LIST);
        final Map<Integer, List<String>> errorRows = (Map<Integer, List<String>>) fileContent.get(ReconciliationApiConstants.ERROR_ROWS);
        if (errorRows.size() == 0) {
            if (isSimplifiedExcel) {
                if (paymentTypeName != null && StringUtils.isNotEmpty(paymentTypeName)) {
                    bankStatement.setPaymentType(paymentTypeName);
                    savePaymentType(paymentTypeName);
                }

            }
            this.bankStatementRepository.save(bankStatement);
            for (final BankStatementDetails bankStatementDetail : bankStatementDetailsList) {
                bankStatementDetail.setBankStatement(bankStatement);
            }
            this.bankStatementDetailsRepositoryWrapper.save(bankStatementDetailsList);
        } else {
            throw new InvalidCIFRowException(errorRows);
        }

        return bankStatement;
    }

    private void savePaymentType(final String paymentTypeName) {
        final PaymentType paymentType = this.paymentTypeRepository.findByPaymentTypeName(paymentTypeName);
        if (paymentType == null) {
            this.paymentTypeRepository.save(PaymentType.create(paymentTypeName));
        }
    }

    private Long getUpdatedDocument(final FormDataMultiPart formParams, final Long documentId, final String name, final String description,
            final Long fileSize, final String key) {
        final FormDataBodyPart bodyPart = formParams.getField(key);
        final InputStream inputStream = bodyPart.getEntityAs(InputStream.class);
        final String fileName = bodyPart.getFormDataContentDisposition().getFileName();
        final Set<String> modifiedParams = new HashSet<>();
        modifiedParams.add(ReconciliationApiConstants.nameParamName);
        modifiedParams.add(ReconciliationApiConstants.descriptionParamName);
        DocumentCommand documentCommand = null;
        final Long reportIdentifier = null;
        final Long tagIdentifier = null;
        if (inputStream != null && fileName != null) {
            modifiedParams.add(ReconciliationApiConstants.FILE_NAME);
            modifiedParams.add(ReconciliationApiConstants.SIZE);
            modifiedParams.add(ReconciliationApiConstants.TYPE);
            modifiedParams.add(ReconciliationApiConstants.LOCATION);
            documentCommand = new DocumentCommand(modifiedParams, documentId, ReconciliationApiConstants.entityName,
                    ReconciliationApiConstants.bankStatementFolder, name, fileName, fileSize, bodyPart.getMediaType().toString(),
                    description, null, reportIdentifier, tagIdentifier);
        } else {
            documentCommand = new DocumentCommand(modifiedParams, documentId, ReconciliationApiConstants.entityName,
                    ReconciliationApiConstants.bankStatementFolder, name, null, null, null, description, null, reportIdentifier,
                    tagIdentifier);
        }

        final DocumentCommandValidator validator = new DocumentCommandValidator(documentCommand);
        validator.validateForUpdate();
        if (documentId != null) {
            final Document documentForUpdate = this.documentRepository.findOne(documentId);
            if (documentForUpdate == null) { throw new DocumentNotFoundException(ReconciliationApiConstants.entityName,
                    ReconciliationApiConstants.bankStatementFolder, documentId); }
            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.DOCUMENT_UPDATE,
                    FinfluxCollectionUtils.constructEntityMap(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, documentForUpdate.isLocked()));
            if (inputStream != null && documentCommand.isFileNameChanged()) {
                final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
                documentCommand.setLocation(contentRepository.saveFile(inputStream, documentCommand));
                documentCommand.setStorageType(contentRepository.getStorageType().getValue());
            }
            documentForUpdate.update(documentCommand);
            this.documentRepository.save(documentForUpdate);
            return documentForUpdate.getId();
        }
        final Long id = this.documentWritePlatformService.createDocument(documentCommand, inputStream);
        return id;

    }

    static Long getSize(final String val) {
        Long value = null;
        if (!val.equals("undefined")) {
            value = new Long(val);
        }
        return value;
    }

    @Override
    public Long deleteBankStatementDetails(final Long bankStatementDetailsId) {
        final BankStatementDetails bankStatementDetails = this.bankStatementDetailsRepositoryWrapper
                .findOneWithNotFoundDetection(bankStatementDetailsId);
        this.bankStatementDetailsRepositoryWrapper.delete(bankStatementDetails);
        return bankStatementDetailsId;
    }

    @Override
    public CommandProcessingResult reconcileBankStatementDetails(final JsonCommand command) {
        boolean isManualReconcile = false;
        if (command.hasParameter("isManualReconcile")) {
            isManualReconcile = command.booleanPrimitiveValueOfParameterNamed("isManualReconcile");
        }
        if (command.parameterExists(ReconciliationApiConstants.transactionDataParamName)) {
            final JsonArray transactionDataArray = command.arrayOfParameterNamed(ReconciliationApiConstants.transactionDataParamName);
            if (!transactionDataArray.isJsonNull() && transactionDataArray.size() > 0) {
                final List<BankStatementDetails> bankDetailsList = new ArrayList<>();
                for (int i = 0; i < transactionDataArray.size(); i++) {
                    final JsonObject jsonObject = transactionDataArray.get(i).getAsJsonObject();
                    final Long bankStatementDetailId = jsonObject.get(ReconciliationApiConstants.bankTransctionIdParamName).getAsLong();
                    final BankStatementDetails bankStatementDetail = this.bankStatementDetailsRepositoryWrapper
                            .findOneWithNotFoundDetection(bankStatementDetailId);
                    bankStatementDetail.setUpdatedDate(new Date());
                    bankStatementDetail.setIsReconciled(true);
                    if (isManualReconcile) {
                        bankStatementDetail.setManualReconciled(true);
                        if (jsonObject.has(ReconciliationApiConstants.transactionIdParamName)) {
                            final String transactionId = jsonObject.get(ReconciliationApiConstants.transactionIdParamName).getAsString()
                                    .trim();
                            if (StringUtils.isNotBlank(transactionId)) {
                                final List<JournalEntry> journalEntries = this.journalEntryRepository
                                        .findUnReversedManualJournalEntriesByTransactionId(transactionId);
                                if (!journalEntries.isEmpty()) {
                                    bankStatementDetail.setTransactionId(transactionId);
                                    bankDetailsList.add(bankStatementDetail);
                                }
                            } else {
                                bankStatementDetail.setTransactionId(null);
                                bankDetailsList.add(bankStatementDetail);
                            }
                        } else {
                            bankStatementDetail.setTransactionId(null);
                            bankDetailsList.add(bankStatementDetail);
                        }
                    } else {
                        final Long loanTransactionId = jsonObject.get(ReconciliationApiConstants.loanTransactionIdParamName).getAsLong();
                        LoanTransaction loanTransaction = null;
                        if (loanTransactionId != null) {
                            loanTransaction = this.loanTransactionRepository.findOneWithNotFoundDetection(loanTransactionId);
                            if (loanTransaction != null && !loanTransaction.isReversed() && !loanTransaction.isRefund()
                                    && !loanTransaction.isReconciled()) {
                                loanTransaction.setReconciled(true);
                                bankStatementDetail.setLoanTransaction(loanTransaction);
                                bankDetailsList.add(bankStatementDetail);
                            }
                        }
                    }
                }
                this.bankStatementDetailsRepositoryWrapper.save(bankDetailsList);
            }
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(command.entityId()) //
                .build();
    }

    @Override
    public CommandProcessingResult reconcileBankStatement(final JsonCommand command) {
        BankStatement bankStatement = null;

        bankStatement = this.bankStatementRepository.findOneWithNotFoundDetection(command.entityId());
        if (bankStatement != null) {
            bankStatement.setIsReconciled(!bankStatement.getIsReconciled());
            this.bankStatementRepository.save(bankStatement);
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(command.entityId()) //
                .build();
    }

    @Override
    public String createJournalEntries(final Long bankStatementId, final String apiRequestBodyAsJson) {
        final BankStatement bankStatement = this.bankStatementRepository.findOneWithNotFoundDetection(bankStatementId);
        final List<BankStatementDetails> bankStatementDetails = this.bankStatementDetailsRepository
                .retrieveBankStatementDetailsToCreateNonPortfolioTransactions(bankStatement,
                        BankStatementDetailType.NONPORTFOLIO.getValue());
        final List<OfficeData> ofiices = this.readPlatformService.retrieveOfficeForJournalEntry();
        final boolean isCreateJournalEntry = validateForCreateJournalEntries(bankStatementDetails, ofiices);
        final List<BankStatementDetailsData> bankStatementDetailsData = this.bankStatementDetailsReadPlatformService
                .retrieveBankStatementNonPortfolioData(bankStatementId);
        Long defaultBankGLAccountId = null;
        if (bankStatementDetailsData.size() > 0) {
            final Bank bank = this.bankStatementRepository.findOneWithNotFoundDetection(bankStatementId).getBank();
            if (bank != null) {
                defaultBankGLAccountId = bank.getGlAccount().getId();
            } else {
                throw new BankNotAssociatedExcecption();
            }
        }
        final HashMap<String, Object> responseData = new HashMap<>();
        final List<Object> resultList = new ArrayList<>();
        final Gson gson = new Gson();
        final java.lang.reflect.Type type = new TypeToken<Map<String, String>>() {}.getType();

        final Map<String, String> requestBodyMap = gson.fromJson(apiRequestBodyAsJson, type);
        final String locale = requestBodyMap.get("locale");
        final String dateFormat = requestBodyMap.get("dateFormat");
        final String currencyCode = this.currencyReadPlatformService.getDefaultCurrencyCode();
        final List<BankStatementDetails> updatedList = new ArrayList<>();
        final DateFormat formatFromExcel = new SimpleDateFormat("yyyy-MM-dd", new Locale(locale));
        ;
        final DateFormat targetFormat = new SimpleDateFormat(dateFormat);

        for (final BankStatementDetails bankStatementDetail : bankStatementDetails) {
            final GLAccountDataForLookup glAccount = this.bankGLAccountReadPlatformService
                    .retrieveGLAccountByGLCode(bankStatementDetail.getGlCode());
            if (!ofiices.isEmpty() && isValidData(bankStatementDetail, ofiices) && glAccount != null) {
                final HashMap<String, Object> responseMap = new HashMap<>();
                final HashMap<String, Object> requestMap = new HashMap<>();
                requestMap.put(ReconciliationApiConstants.localeParamName, locale);
                requestMap.put(ReconciliationApiConstants.dateFormatParamName, dateFormat);
                requestMap.put(ReconciliationApiConstants.officeIdParamName, bankStatementDetail.getBrtanchid());
                requestMap.put(ReconciliationApiConstants.transactionDateParamName,
                        getFormattedDate(bankStatementDetail.getTransactionDate(), formatFromExcel, targetFormat));
                requestMap.put(ReconciliationApiConstants.currencyCodeParamName, currencyCode);
                requestMap.putAll(getCreditAndDebitMap(bankStatementDetail, defaultBankGLAccountId, glAccount));
                String requestBody = null;
                if (isCreateJournalEntry && requestMap.containsKey(ReconciliationApiConstants.officeIdParamName)) {
                    requestBody = gson.toJson(requestMap);
                    CommandProcessingResult result = null;
                    final CommandWrapper commandRequest = new CommandWrapperBuilder().createJournalEntry().withJson(requestBody).build();
                    result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
                    bankStatementDetail.setTransactionId(result.getTransactionId());
                    bankStatementDetail.setIsReconciled(true);
                    updatedList.add(bankStatementDetail);
                    responseMap.put(ReconciliationApiConstants.BANK_STATEMENT_DETAIL_ID, bankStatementDetail.getId());
                    responseMap.put(ReconciliationApiConstants.JOURNAL_ENTRY_RESPONSE, result);
                    resultList.add(responseMap);

                }
            } else {

                bankStatementDetail.setIsError(true);
                updatedList.add(bankStatementDetail);
            }
        }
        if (!updatedList.isEmpty()) {
            this.bankStatementDetailsRepositoryWrapper.save(updatedList);
        }
        responseData.put(ReconciliationApiConstants.RESOURCE, resultList);
        return gson.toJson(responseData);
    }

    private boolean isValidData(final BankStatementDetails bankStatementDetail, final List<OfficeData> offices) {
        return (!bankStatementDetail.isReconciled() && bankStatementDetail.getAmount() != null && bankStatementDetail.getGlCode() != null
                && bankStatementDetail.getTransactionDate() != null && bankStatementDetail.getAccountingType() != null
                && (bankStatementDetail.getAccountingType().equalsIgnoreCase("CREDIT")
                        || bankStatementDetail.getAccountingType().equalsIgnoreCase("DEBIT"))
                && branchExist(bankStatementDetail, offices));
    }

    private boolean branchExist(final BankStatementDetails bankStatementDetail, final List<OfficeData> offices) {
        for (final OfficeData office : offices) {
            if (office.getExternalId() != null && office.getExternalId().equals(bankStatementDetail.getBranchExternalId())) {
                bankStatementDetail.setBranchId(office.getId());
                return true;
            }
        }
        return false;
    }

    private boolean validateForCreateJournalEntries(final List<BankStatementDetails> bankStatementDetails, final List<OfficeData> ofiices) {
        for (final BankStatementDetails bankStatementDetail : bankStatementDetails) {
            final GLAccountDataForLookup glAccount = this.bankGLAccountReadPlatformService
                    .retrieveGLAccountByGLCode(bankStatementDetail.getGlCode());
            if (ofiices.isEmpty() || !isValidData(bankStatementDetail, ofiices) || glAccount == null) { return false; }
        }
        return true;
    }

    private HashMap<String, Object> getCreditAndDebitMap(final BankStatementDetails bankStatementDetail, final Long defaultBankGLAccountId,
            final GLAccountDataForLookup GLAccount) {

        final HashMap<String, Object> creaditMap = new HashMap<>();
        final HashMap<String, Object> debitMap = new HashMap<>();
        creaditMap.put(ReconciliationApiConstants.amountParamName, bankStatementDetail.getAmount());
        debitMap.put(ReconciliationApiConstants.amountParamName, bankStatementDetail.getAmount());
        final String code = bankStatementDetail.getGlCode();
        if (GLAccount == null) { throw new GLAccountNotFoundException(code); }
        if (bankStatementDetail.getAccountingType().equalsIgnoreCase(ReconciliationApiConstants.DEBIT)) {
            creaditMap.put(ReconciliationApiConstants.GL_ACCOUNT_ID, defaultBankGLAccountId);
            debitMap.put(ReconciliationApiConstants.GL_ACCOUNT_ID, GLAccount.getId());
        } else {
            creaditMap.put(ReconciliationApiConstants.GL_ACCOUNT_ID, GLAccount.getId());
            debitMap.put(ReconciliationApiConstants.GL_ACCOUNT_ID, defaultBankGLAccountId);
        }
        final List<Object> creditList = new ArrayList<>();
        creditList.add(creaditMap);
        final List<Object> debitList = new ArrayList<>();
        debitList.add(debitMap);
        final HashMap<String, Object> creditAndDebitMap = new HashMap<>();
        creditAndDebitMap.put(ReconciliationApiConstants.CREDIT_ACCOUNT, creditList);
        creditAndDebitMap.put(ReconciliationApiConstants.DEBIT_ACCOUNT, debitList);
        return creditAndDebitMap;
    }

    @Override
    public CommandProcessingResult undoReconcileBankStatementDetails(final JsonCommand command) {
        if (command.parameterExists(ReconciliationApiConstants.transactionDataParamName)) {
            final JsonArray transactionDataArray = command.arrayOfParameterNamed(ReconciliationApiConstants.transactionDataParamName);
            if (!transactionDataArray.isJsonNull() && transactionDataArray.size() > 0) {
                final List<LoanTransaction> loanTransactionList = new ArrayList<>();
                final List<BankStatementDetails> bankDetailsList = new ArrayList<>();
                for (int i = 0; i < transactionDataArray.size(); i++) {
                    final JsonObject jsonObject = transactionDataArray.get(i).getAsJsonObject();
                    final Long bankStatementDetailId = jsonObject.get(ReconciliationApiConstants.bankTransctionIdParamName).getAsLong();
                    final BankStatementDetails bankStatementDetail = this.bankStatementDetailsRepositoryWrapper
                            .findOneWithNotFoundDetection(bankStatementDetailId);
                    if (bankStatementDetail.isManualReconciled()) {
                        bankStatementDetail.setTransactionId(null);
                        bankStatementDetail.setManualReconciled(false);
                    } else {
                        final LoanTransaction loanTransaction = bankStatementDetail.getLoanTransaction();
                        loanTransaction.setReconciled(false);
                        loanTransactionList.add(loanTransaction);
                        bankStatementDetail.setLoanTransaction(null);
                    }

                    bankStatementDetail.setIsReconciled(false);
                }
                this.bankStatementDetailsRepositoryWrapper.save(bankDetailsList);
                this.loanTransactionRepository.save(loanTransactionList);
            }

        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(command.entityId()) //
                .build();

    }

    @Override
    public CommandProcessingResult generatePortfolioTransactions(final JsonCommand command) {
        this.context.authenticatedUser();
        final List<BankStatementDetailsData> bankStatementDetailsDataList = this.bankStatementDetailsReadPlatformService
                .retrieveGeneratePortfolioData(command.entityId(), " and bsd.is_reconciled = 0 and bsd.is_error = 0 ");
        completePortfolioTransactions(bankStatementDetailsDataList);
        return new CommandProcessingResultBuilder() //
                .withEntityId(command.entityId()) //
                .build();
    }

    @Override
    public CommandProcessingResult completePortfolioTransactions(final JsonCommand command) {
        this.context.authenticatedUser();
        final BankStatement bankStatement = this.bankStatementRepository.findOneWithNotFoundDetection(command.entityId());
        final List<BankStatementDetailsData> bankStatementDetailsDataList = this.bankStatementDetailsReadPlatformService
                .retrieveGeneratePortfolioData(command.entityId(), " and bsd.is_reconciled = 0 and bsd.is_error = 0 ");
        bankStatement.setIsReconciled(true);
        this.bankStatementRepository.save(bankStatement);
        completePortfolioTransactions(bankStatementDetailsDataList);

        return new CommandProcessingResultBuilder() //
                .withEntityId(command.entityId()) //
                .build();
    }

    private void completePortfolioTransactions(final List<BankStatementDetailsData> bankStatementDetailsDataList) {
        final DateTimeFormatter fmt = null;
        for (final BankStatementDetailsData bankStatementDetailsData : bankStatementDetailsDataList) {
            final BankStatementDetails bankStatementDetail = this.bankStatementDetailsRepositoryWrapper
                    .findOneWithNotFoundDetection(bankStatementDetailsData.getId());
            final LocalDate transactionDate = new LocalDate(bankStatementDetail.getTransactionDate());
            final BigDecimal transactionAmount = bankStatementDetail.getAmount();
            final String loanAccountNumber = bankStatementDetail.getLoanAccountNumber();
            final String savingsAccountNumber = bankStatementDetail.getSavingsAccountNumber();
            final String paymentTypeName = bankStatementDetail.getPaymentTypeName();
            final String paymentDetailAccountNumber = bankStatementDetail.getPaymentDetailAccountNumber();
            final String paymentDetailChequeNumber = bankStatementDetail.getPaymentDetailChequeNumber();
            final String routingCode = bankStatementDetail.getRoutingCode();
            final String paymentDetailBankNumber = bankStatementDetail.getPaymentDetailBankNumber();
            final String receiptNumber = bankStatementDetail.getReceiptNumber();
            final String note = bankStatementDetail.getNote();
            final String errmsg = bankStatementDetail.getErrmsg();
            final Long transactionIdForUpdate = bankStatementDetail.getTransactionIdForUpdate();
            final String accountType = bankStatementDetail.getAccountingType();
            if (errmsg != null) {
                continue;
            }
            Long paymentDetailId = null;

            try {
                if (transactionIdForUpdate != null) {
                    if (accountType == null || accountType.equals("loans")) {
                        paymentDetailId = this.loanReadPlatformService
                                .retrivePaymentDetailsIdWithLoanAccountNumberAndLoanTransactioId(transactionIdForUpdate, loanAccountNumber);
                    } else if (accountType.equals("deposits")) {
                        paymentDetailId = this.savingsAccountReadPlatformService
                                .retrivePaymentDetailsIdWithSavingsAccountNumberAndTransactioId(transactionIdForUpdate,
                                        savingsAccountNumber);
                    }
                    if (paymentDetailId != null) {
                        final PaymentDetail paymentDetail = this.paymentDetailRepository.findOne(paymentDetailId);
                        paymentDetail.updatePaymentDetail(paymentDetailAccountNumber, paymentDetailChequeNumber, routingCode, receiptNumber,
                                paymentDetailBankNumber);
                        bankStatementDetail.setTransactionId(transactionIdForUpdate.toString());
                        this.paymentDetailRepository.save(paymentDetail);
                        bankStatementDetail.setIsReconciled(true);
                    } else {
                        bankStatementDetail.setErrmsg(ReconciliationApiConstants.TRANSACTION_WITHOUT_PAYMENT_DETAILS_CANNOT_BE_UPDATED);
                    }
                } else {
                    if (loanAccountNumber != null) {
                        final LoanTransaction loanTransaction = this.loanAccountDomainService.makeRepayment(loanAccountNumber,
                                transactionDate, transactionAmount, paymentTypeName, paymentDetailAccountNumber, paymentDetailChequeNumber,
                                routingCode, paymentDetailBankNumber, receiptNumber, note);
                        bankStatementDetail.setLoanTransaction(loanTransaction);
                        bankStatementDetail.setTransactionId(loanTransaction.getId().toString());
                        bankStatementDetail.setIsReconciled(true);
                    } else if (savingsAccountNumber != null) {
                        final SavingsAccountTransaction savingsTransaction = this.savingsAccountDomainService.handleDeposit(
                                savingsAccountNumber, transactionDate, transactionAmount, paymentTypeName, paymentDetailAccountNumber,
                                paymentDetailChequeNumber, routingCode, paymentDetailBankNumber, receiptNumber, note, fmt);
                        bankStatementDetail.setTransactionId(savingsTransaction.getId().toString());
                        bankStatementDetail.setIsReconciled(true);
                    } else {
                        bankStatementDetail.setErrmsg("Account Number Not Found");
                    }
                }
            } catch (final RuntimeException e) {
                if (e instanceof PlatformApiDataValidationException) {
                    final PlatformApiDataValidationException exc = (PlatformApiDataValidationException) e;
                    final List<ApiParameterError> errors = exc.getErrors();
                    final StringBuilder sb = new StringBuilder();
                    for (final ApiParameterError error : errors) {
                        sb.append(error.getDeveloperMessage()).append(" : ");
                    }
                    bankStatementDetail.setErrmsg(sb.toString());
                } else if (e instanceof AbstractPlatformDomainRuleException) {
                    final AbstractPlatformDomainRuleException exc = (AbstractPlatformDomainRuleException) e;
                    bankStatementDetail.setErrmsg(exc.getDefaultUserMessage());
                } else if (e instanceof AbstractPlatformResourceNotFoundException) {
                    final AbstractPlatformResourceNotFoundException exc = (AbstractPlatformResourceNotFoundException) e;
                    bankStatementDetail.setErrmsg(exc.getDefaultUserMessage());
                } else {
                    bankStatementDetail.setErrmsg("Failed due to domain rule or platform error");
                }
            }
            this.bankStatementDetailsRepository.save(bankStatementDetail);
        }
    }

    public static String getFormattedDate(final Date transactionDate, final DateFormat formatFromExcel, final DateFormat targetFormat) {
        Date date;
        try {
            date = formatFromExcel.parse(transactionDate.toString());
        } catch (final ParseException e) {
            return transactionDate.toString();
        }
        return targetFormat.format(date);
    }
}