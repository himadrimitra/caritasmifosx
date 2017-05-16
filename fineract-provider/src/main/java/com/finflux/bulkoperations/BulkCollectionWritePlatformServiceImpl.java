package com.finflux.bulkoperations;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.documentmanagement.domain.DocumentRepository;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformServiceJpaRepositoryImpl;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bank.domain.Bank;
import com.finflux.reconcilation.bankstatement.domain.BankStatement;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetails;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetailsRepositoryWrapper;
import com.finflux.reconcilation.bankstatement.domain.BankStatementRepositoryWrapper;
import com.finflux.reconcilation.bankstatement.exception.InvalidCIFRowException;
import com.finflux.reconcilation.bankstatement.helper.ExcelUtility;
import com.finflux.reconcilation.bankstatement.service.BankStatementReadPlatformService;
import com.google.gson.JsonElement;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetailType;

@Service
public class BulkCollectionWritePlatformServiceImpl implements BulkCollectionWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DocumentWritePlatformServiceJpaRepositoryImpl.class);
    private static final int ACCOUNT_TYPE = 0;
    private static final int ACCOUNT_NUMBER = 1;
    private static final int TRANSACTION_DATE = 2;
    private static final int AMOUNT = 3;
    private static final int PAYMENTTYPE_NAME = 4;
    private static final int PAYMENTDETAIL_ACCOUNT_NUMBER = 5;
    private static final int PAYMENTDETAIL_CHEQUE_NUMBER = 6;
    private static final int ROUTING_CODE = 7;
    private static final int RECEIPT_NUMBER = 8;
    private static final int PAYMENTDETAIL_BANK_NUMBER = 9;
    private static final int NOTE = 10;
    private final PlatformSecurityContext context;
    private final ContentRepositoryFactory contentRepositoryFactory;
    private final DocumentRepository documentRepository;
    private final BankStatementReadPlatformService bankStatementReadPlatformService;
    private final BankStatementRepositoryWrapper bankStatementRepository;
    private final BankStatementDetailsRepositoryWrapper bankStatementDetailsRepositoryWrapper;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public BulkCollectionWritePlatformServiceImpl(final PlatformSecurityContext context,
            final ContentRepositoryFactory contentRepositoryFactory, final DocumentRepository documentRepository,
            final BankStatementReadPlatformService bankStatementReadPlatformService,
            final BankStatementRepositoryWrapper bankStatementRepository,
            final BankStatementDetailsRepositoryWrapper bankStatementDetailsRepositoryWrapper,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService, FromJsonHelper fromApiJsonHelper) {
        this.context = context;
        this.contentRepositoryFactory = contentRepositoryFactory;
        this.documentRepository = documentRepository;
        this.bankStatementReadPlatformService = bankStatementReadPlatformService;
        this.bankStatementRepository = bankStatementRepository;
        this.bankStatementDetailsRepositoryWrapper = bankStatementDetailsRepositoryWrapper;
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
        this.fromApiJsonHelper=fromApiJsonHelper;
    }

    @Transactional
    @Override
    public Long createBulkTransactionStatement(FormDataMultiPart formParams) {
        try {
            this.context.authenticatedUser();
            Document cpifDocument = getDocument(ReconciliationApiConstants.FIRST_FILE, formParams);
            Document orgDocument = null;
            Bank bank = null;
            BankStatement bankStatement = BankStatement.instance(cpifDocument.getName(), cpifDocument.getDescription(), cpifDocument,
                    orgDocument, false, bank, BulkStatementEnumType.BULKTRANSACTIONS.getValue());
            bankStatement = saveBulkTransactionStatementDetails(cpifDocument.getId(), bankStatement);
            return bankStatement.getId();
        } catch (final DataIntegrityViolationException dve) {
            logger.error(dve.getMessage(), dve);
            throw new PlatformDataIntegrityException("error.msg.document.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        }
    }

    @SuppressWarnings("unchecked")
    public BankStatement saveBulkTransactionStatementDetails(final Long cpifDocumentId, final BankStatement bankStatement) {
        File file = this.bankStatementReadPlatformService.retrieveFile(cpifDocumentId);
        Map<String, Object> fileContent = getPortfolioTransactions(file);
        List<BankStatementDetails> bankStatementDetailsList = (List<BankStatementDetails>) fileContent
                .get(ReconciliationApiConstants.BANK_STATEMENT_DETAIL_LIST);
        Map<Integer, List<String>> errorRows = (Map<Integer, List<String>>) fileContent.get(ReconciliationApiConstants.ERROR_ROWS);
        if (errorRows.size() == 0) {
            this.bankStatementRepository.save(bankStatement);
            for (BankStatementDetails bankStatementDetail : bankStatementDetailsList) {
                bankStatementDetail.setBankStatement(bankStatement);
            }
            this.bankStatementDetailsRepositoryWrapper.save(bankStatementDetailsList);
        } else {
            throw new InvalidCIFRowException(errorRows);
        }

        return bankStatement;
    }

    private Document getDocument(String key, FormDataMultiPart formParams) {
        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
        final String name = formParams.getField(ReconciliationApiConstants.nameParamName).getValue();
        String description = null;
        if (formParams.getField(ReconciliationApiConstants.descriptionParamName) != null) {
            description = formParams.getField(ReconciliationApiConstants.descriptionParamName).getValue();
        }
        String size = (key.equals(ReconciliationApiConstants.FIRST_FILE)) ? ReconciliationApiConstants.CIF_FILE_SIZE
                : ReconciliationApiConstants.ORIGINAL_FILE_SIZE;
        final Long fileSize = new Long(formParams.getField(size).getValue());

        FormDataBodyPart bodyPart = formParams.getField(key);
        InputStream inputStream = bodyPart.getEntityAs(InputStream.class);
        String fileName = bodyPart.getFormDataContentDisposition().getFileName();
        final DocumentCommand documentCommand = new DocumentCommand(null, null, ReconciliationApiConstants.entityName,
                ReconciliationApiConstants.bankStatementFolder, name, fileName, fileSize, bodyPart.getMediaType().toString(), description,
                null);
        final String fileLocation = contentRepository.saveFile(inputStream, documentCommand);
        final Document document = Document.createNew(documentCommand.getParentEntityType(), documentCommand.getParentEntityId(),
                documentCommand.getName(), documentCommand.getFileName(), documentCommand.getSize(), documentCommand.getType(),
                documentCommand.getDescription(), fileLocation, contentRepository.getStorageType());

        this.documentRepository.save(document);
        return document;
    }

    public Map<String, Object> getPortfolioTransactions(File file) {
        Map<String, Object> fileDataMap = new HashMap<>();
        Set<Integer> errorRows = new TreeSet<>();
        Map<Integer, List<String>> errorRowMap = new LinkedHashMap<>();
        List<BankStatementDetails> bankStatementDetailsList = new LinkedList<>();
        List<Row> rows = ExcelUtility.getAllRows(file);
        List<String> paymentNameslist = this.paymentTypeReadPlatformService.retrieveAllPaymentTypeNames();

        try {
            for (Row row : rows) {
                String accountType = null;
                String loanAccountNumber = null;
                String savingsAccountNumber = null;
                String accountNumber = null;
                Date transactionDate = null;
                BigDecimal amount = null;
                String paymentTypeName = null;
                String paymentDetailAccountNumber = null;
                String paymentDetailChequeNumber = null;
                String receiptNumber = null;
                String routingCode = null;
                String paymentDetailBankNumber = null;
                String note = null;
                BankStatementDetailType bankStatementDetailType = BankStatementDetailType.INVALID;
                List<String> rowError = new ArrayList<>();
                Boolean isValid = true;
                if (row.getRowNum() != 0) {
                    for (int i = 0; i < ReconciliationApiConstants.HEADER_DATA.length; i++) {
                        Cell cell = row.getCell(i);

                        switch (i) {
                            case ACCOUNT_TYPE:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    accountType = ExcelUtility.getCellValueAsString(cell);
                                    if (accountType == null
                                            || !(accountType.equalsIgnoreCase("loans") || accountType.equalsIgnoreCase("deposits"))) {
                                        errorRows.add(row.getRowNum() + 1);
                                        rowError.add(ReconciliationApiConstants.INVALID_ACCOUNT_TYPE);
                                        isValid = false;
                                    }
                                } else {
                                    errorRows.add(row.getRowNum() + 1);
                                    rowError.add(ReconciliationApiConstants.ACCOUNT_TYPE_CAN_NOT_BE_BLANK);
                                    isValid = false;
                                }
                                if (accountType != null && accountType.equalsIgnoreCase("LOANS")) {
                                    bankStatementDetailType = BankStatementDetailType.LOANS;
                                } else {
                                    bankStatementDetailType = BankStatementDetailType.DEPOSITS;
                                }
                            break;
                            case ACCOUNT_NUMBER:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    accountNumber = ExcelUtility.getCellValueAsString(cell);
                                } else {
                                    errorRows.add(row.getRowNum() + 1);
                                    rowError.add(ReconciliationApiConstants.ACCOUNT_NUMBER_CAN_NOT_BE_BLANK);
                                    isValid = false;
                                }
                            break;
                            case TRANSACTION_DATE:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK
                                        && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    try {
                                        transactionDate = cell.getDateCellValue();
                                    } catch (Exception e) {
                                        errorRows.add(row.getRowNum() + 1);
                                        rowError.add(ReconciliationApiConstants.INVALID_TRANSACTION_DATE);
                                    }
                                } else {
                                    errorRows.add(row.getRowNum() + 1);
                                    rowError.add(ReconciliationApiConstants.TRANSACTION_DATE_CAN_NOT_BE_BLANK);
                                    isValid = false;
                                }
                            break;
                            case AMOUNT:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    try {
                                        amount = new BigDecimal(cell.toString());
                                    } catch (NumberFormatException e) {
                                        errorRows.add(row.getRowNum() + 1);
                                        rowError.add(ReconciliationApiConstants.AMOUNT_INVALID);
                                    }
                                } else {
                                    errorRows.add(row.getRowNum() + 1);
                                    rowError.add(ReconciliationApiConstants.AMOUNT_CAN_NOT_BE_BLANK);
                                    isValid = false;
                                }
                            break;
                            case PAYMENTTYPE_NAME:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    paymentTypeName = ExcelUtility.getCellValueAsString(cell);
                                    if (paymentTypeName != null) {
                                        if (!(paymentNameslist.contains(paymentTypeName.trim()))) {
                                            errorRows.add(row.getRowNum() + 1);
                                            rowError.add(ReconciliationApiConstants.INVALID_PAYMENT_TYPE);
                                            isValid = false;
                                        }
                                    }
                                }
                            break;
                            case PAYMENTDETAIL_ACCOUNT_NUMBER:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    paymentDetailAccountNumber = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case PAYMENTDETAIL_CHEQUE_NUMBER:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    paymentDetailChequeNumber = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case ROUTING_CODE:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    routingCode = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case RECEIPT_NUMBER:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    receiptNumber = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case PAYMENTDETAIL_BANK_NUMBER:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    paymentDetailBankNumber = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;
                            case NOTE:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    note = ExcelUtility.getCellValueAsString(cell);
                                }
                            break;

                        }

                    }
                    if (row.getRowNum() != 0 && isValid) {
                        boolean isReconciled = false;
                        if (bankStatementDetailType.isLoanType()) {
                            loanAccountNumber = accountNumber;
                        } else if (bankStatementDetailType.isDepositType()) {
                            savingsAccountNumber = accountNumber;
                        }
                        BankStatementDetails bankStatementDetails = BankStatementDetails.instance(null, accountType, loanAccountNumber,
                                transactionDate, amount, paymentTypeName, paymentDetailAccountNumber, paymentDetailChequeNumber,
                                routingCode, receiptNumber, paymentDetailBankNumber, note, bankStatementDetailType.getValue(),
                                savingsAccountNumber, isReconciled);
                        bankStatementDetails.setIsError(false);
                        bankStatementDetailsList.add(bankStatementDetails);
                    }
                    if (row.getRowNum() != 0) {
                        if (rowError.size() > 0) {
                            errorRowMap.put(row.getRowNum() + 1, rowError);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        fileDataMap.put(ReconciliationApiConstants.BANK_STATEMENT_DETAIL_LIST, bankStatementDetailsList);
        fileDataMap.put(ReconciliationApiConstants.ERROR_ROWS, errorRowMap);

        return fileDataMap;
    }

}
