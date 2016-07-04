package com.finflux.reconcilation.bankstatement.service;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.glaccount.data.GLAccountDataForLookup;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepository;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryType;
import org.apache.fineract.accounting.journalentry.exception.JournalEntriesNotFoundException;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
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
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bank.domain.Bank;
import com.finflux.reconcilation.bank.domain.BankRepositoryWrapper;
import com.finflux.reconcilation.bank.exception.BankNotAssociatedExcecption;
import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;
import com.finflux.reconcilation.bankstatement.domain.BankStatement;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetails;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetailsRepositoryWrapper;
import com.finflux.reconcilation.bankstatement.domain.BankStatementRepositoryWrapper;
import com.finflux.reconcilation.bankstatement.exception.GLAccountNotFoundException;
import com.finflux.reconcilation.bankstatement.exception.InvalidCIFRowException;
import com.finflux.reconcilation.bankstatement.exception.InvalidCifFileFormatException;
import com.finflux.reconcilation.bankstatement.exception.NotExcelFileException;
import com.finflux.reconcilation.bankstatement.handler.ReconcileBankStatementCommandHandler;
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
    private final BankStatementDetailsRepositoryWrapper bankStatementDetailsRepository;
    private final DocumentWritePlatformService documentWritePlatformService;
    private final LoanTransactionRepositoryWrapper loanTransactionRepository;
    private final BankRepositoryWrapper bankRepository;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final JournalEntryRepository journalEntryRepository;
    private final BankGLAccountReadPlatformService bankGLAccountReadPlatformService;

    @Autowired
    public BankStatementWritePlatformServiceJpaRepository(final PlatformSecurityContext context,
            final DocumentRepository documentRepository, final ContentRepositoryFactory contentRepositoryFactory,
            final BankStatementRepositoryWrapper bankStatementRepository, final BankStatementReadPlatformService bankStatementReadPlatformService,
            final BankStatementDetailsRepositoryWrapper bankStatementDetailsRepository,
            final DocumentWritePlatformService documentWritePlatformService, final LoanTransactionRepositoryWrapper loanTransactionRepository,
            final BankRepositoryWrapper bankRepository, final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final JournalEntryRepository journalEntryRepository, final BankGLAccountReadPlatformService bankGLAccountReadPlatformService) {
        this.context = context;
        this.documentRepository = documentRepository;
        this.contentRepositoryFactory = contentRepositoryFactory;
        this.bankStatementRepository = bankStatementRepository;
        this.bankStatementReadPlatformService = bankStatementReadPlatformService;
        this.bankStatementDetailsRepository = bankStatementDetailsRepository;
        this.documentWritePlatformService = documentWritePlatformService;
        this.loanTransactionRepository = loanTransactionRepository;
        this.bankRepository = bankRepository;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.journalEntryRepository = journalEntryRepository;
        this.bankGLAccountReadPlatformService = bankGLAccountReadPlatformService;
    }

    @Transactional
    @Override
    public Long createBankStatement(final FormDataMultiPart formParams) {
        try {
            this.context.authenticatedUser();
            Document cpifDocument = getDocument(ReconciliationApiConstants.FIRST_FILE, formParams);
            Document orgDocument = null;
            if (formParams.getFields().containsKey(ReconciliationApiConstants.SECOND_FILE)) {
                orgDocument = getDocument(ReconciliationApiConstants.SECOND_FILE, formParams);
            }
            Bank bank = null;
            if (formParams.getFields().containsKey(ReconciliationApiConstants.bankParamName)) {
                final String bankId = formParams.getField(ReconciliationApiConstants.bankParamName).getValue();
                if (bankId != null) {
                    bank = this.bankRepository.findOneWithNotFoundDetection(Long.valueOf(bankId));
                }
            }
            BankStatement bankStatement = BankStatement.instance(cpifDocument.getName(), cpifDocument.getDescription(), cpifDocument,
                    orgDocument, false, bank);
            validateExcel(cpifDocument);
            bankStatement = saveBankStatementDetails(cpifDocument.getId(), bankStatement);
            return bankStatement.getId();

        } catch (final DataIntegrityViolationException dve) {
            logger.error(dve.getMessage(), dve);
            throw new PlatformDataIntegrityException("error.msg.document.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        }
    }

    public void validateExcel(Document document) {
        if (ReconciliationApiConstants.EXCEL_FILE.indexOf(document.getType()) < 0) { throw new NotExcelFileException(); }

        boolean bool = ExcelUtility.isValidExcelHeader(this.bankStatementReadPlatformService.retrieveFile(document.getId()),
                ReconciliationApiConstants.HEADER_DATA);

        if (!bool) { throw new InvalidCifFileFormatException(); }
    }

    @SuppressWarnings({ "unused"})
    public static Map<String, Object> createMapOfFileData(File file) {
        Map<String, Object> fileDataMap = new HashMap<String, Object>();
        Set<Integer> errorRows = new TreeSet<Integer>();
        Map<Integer, List<String>> errorRowMap = new LinkedHashMap<Integer, List<String>>();
        List<BankStatementDetails> bankStatementDetailsList = new LinkedList<BankStatementDetails>();
        List<Row> rows = ExcelUtility.getAllRows(file);
        try {
            Row headerRow = rows.get(0);
            int indexOfTransactionType = ExcelUtility.getColumnNumberByColumnHeaderName(headerRow, "TransactionType");
            for (Row row : rows) {
                String transactionId = null;
                Date transactionDate = null;
                String description = null;
                BigDecimal amount = null;
                String mobileNumber = null;
                String clientAccountNumber = null;
                String loanAccountNumber = null;
                String groupExternalId = null;
                List<String> rowError = new ArrayList<String>();
                Boolean isValid = true;
                String branchExternalId = null;
                String accountingType = null;
                Boolean isJournalEntry = false;
                String type = "";
                String glCode = "";
                String bankStatementTransactionType = "";
                if (row.getCell(indexOfTransactionType) != null) {
                    type = getCellValueAsString(row.getCell(indexOfTransactionType));
                }
                if (row.getRowNum() != 0) {
                    isJournalEntry = type.equalsIgnoreCase("other");
                    for (int i = 0; i < ReconciliationApiConstants.HEADER_DATA.length; i++) {
                        Cell cell = row.getCell(i);
                        switch (i) {
                            case 0:
                                if (cell != null && !isJournalEntry) {
                                    if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
                                        errorRows.add(row.getRowNum() + 1);
                                        isValid = false;
                                    } else {
                                        transactionId = getCellValueAsString(cell);
                                    }
                                } else {
                                    if (!isJournalEntry) {
                                        errorRows.add(row.getRowNum() + 1);
                                        rowError.add(ReconciliationApiConstants.TRANSACTION_ID_CAN_NOT_BE_BLANK);
                                        isValid = false;
                                    }
                                }
                            break;
                            case 1:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
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
                            case 2:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    description = getCellValueAsString(cell);
                                }
                            break;
                            case 3:
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
                            case 4:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    mobileNumber = getCellValueAsString(cell);
                                }
                            break;
                            case 5:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    clientAccountNumber = getCellValueAsString(cell);
                                }
                            break;
                            case 6:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    loanAccountNumber = getCellValueAsString(cell);
                                }
                            break;
                            case 7:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    groupExternalId = getCellValueAsString(cell);
                                }
                            break;
                            case 8:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    branchExternalId = getCellValueAsString(cell);                                    
                                }
                            break;
                            case 9:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    glCode = getCellValueAsString(cell);
                                }
                            break;
                            case 10:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    accountingType = getCellValueAsString(cell);
                                }
                            break;
                            case 11:
                                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                                    bankStatementTransactionType = getCellValueAsString(cell);                                    
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
                    BankStatementDetails bankStatementDetails = BankStatementDetails.instance(null, transactionId, transactionDate,
                            description, amount, mobileNumber, clientAccountNumber, loanAccountNumber, groupExternalId, false, null,
                            branchExternalId, accountingType, glCode, isJournalEntry, bankStatementTransactionType);
                    bankStatementDetailsList.add(bankStatementDetails);
                }
                if (row.getRowNum() != 0) {
                    if (rowError.size() > 0) {
                        errorRowMap.put(row.getRowNum() + 1, rowError);
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

    private static String getCellValueAsString(Cell cell) {
        String param = "";
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            param = NumberToTextConverter.toText(cell.getNumericCellValue());
        } else {
            param = cell.getStringCellValue();
        }
        return param;
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteBankStatement(final Long bankStatementId) {

        BankStatement bankStatement = this.bankStatementRepository.findOneWithNotFoundDetection(bankStatementId);
        final Document cpifDocument = bankStatement.getCpifDocument();
        final Document orgDocument = bankStatement.getOrgStatementDocument();
        revertChanges(bankStatementId);
        this.bankStatementRepository.delete(bankStatement);
        this.documentRepository.delete(cpifDocument);
        if (orgDocument != null) {
            this.documentRepository.delete(orgDocument);
        }
        return new CommandProcessingResultBuilder() //
                .withEntityId(bankStatementId) //
                .build();
    }

    @SuppressWarnings("unused")
    private void revertChanges(final Long bankStatementId) {
        List<BankStatementDetailsData> changedBankStatementDetailsData = changedBankStatementDetailsData(bankStatementId);
        List<String> changedJournalEntriesData = new ArrayList<String>();
        List<Long> loanTransactionForUndoReconcile = new ArrayList<Long>();
        if (changedBankStatementDetailsData.size() > 0) {
            for (BankStatementDetailsData bankStatementDetailsData : changedBankStatementDetailsData) {
                if (bankStatementDetailsData.getIsJournalEntry()) {
                    changedJournalEntriesData.add(bankStatementDetailsData.getTransactionId());
                } else {
                    loanTransactionForUndoReconcile.add(bankStatementDetailsData.getLoanTransaction());
                }
            }
        }
        undoReconcileLoanTransactions(loanTransactionForUndoReconcile);
        revertJournalEntries(changedJournalEntriesData);

    }

    @SuppressWarnings("unused")
    private void undoReconcileLoanTransactions(List<Long> transactionList) {
        List<LoanTransaction> loanTransactions = new ArrayList<LoanTransaction>();
        for (Long transaction : transactionList) {
            LoanTransaction loanTransaction = this.loanTransactionRepository.findOneWithNotFoundDetection(transaction);
            loanTransaction.setReconciled(false);  
            loanTransactions.add(loanTransaction);
        }
        this.loanTransactionRepository.save(loanTransactions);
    }

    private void revertJournalEntries(List<String> journalEntryList) {
        for (String transactionId : journalEntryList) {
            final List<JournalEntry> journalEntries = this.journalEntryRepository
                    .findUnReversedManualJournalEntriesByTransactionId(transactionId);
            String reversalComment = "";
            if (journalEntries.size() <= 1) { throw new JournalEntriesNotFoundException(transactionId); }
            revertJournalEntry(journalEntries, reversalComment);
        }

    }

    public String revertJournalEntry(final List<JournalEntry> journalEntries, String reversalComment) {
        final Long officeId = journalEntries.get(0).getOffice().getId();
        final String reversalTransactionId = generateTransactionId(officeId);
        final boolean manualEntry = true;

        final boolean useDefaultComment = StringUtils.isBlank(reversalComment);
        List<JournalEntry> toUpdateJournalEntries = new ArrayList<JournalEntry>();
        for (final JournalEntry journalEntry : journalEntries) {
            JournalEntry reversalJournalEntry;
            if (useDefaultComment) {
                reversalComment = "Reversal entry for Journal Entry with Entry Id  :" + journalEntry.getId() + " and transaction Id "
                        + journalEntry.getTransactionId();
            }
            Long shareTransactionId = null;
            if (journalEntry.isDebitEntry()) {
                reversalJournalEntry = JournalEntry.createNew(journalEntry.getOffice(), journalEntry.getPaymentDetails(),
                        journalEntry.getGlAccount(), journalEntry.getCurrencyCode(), reversalTransactionId, manualEntry,
                        journalEntry.getTransactionDate(), JournalEntryType.CREDIT, journalEntry.getAmount(), reversalComment, null, null,
                        journalEntry.getReferenceNumber(), journalEntry.getLoanTransaction(), journalEntry.getSavingsTransaction(),
                        journalEntry.getClientTransaction(), shareTransactionId);
            } else {
                reversalJournalEntry = JournalEntry.createNew(journalEntry.getOffice(), journalEntry.getPaymentDetails(),
                        journalEntry.getGlAccount(), journalEntry.getCurrencyCode(), reversalTransactionId, manualEntry,
                        journalEntry.getTransactionDate(), JournalEntryType.DEBIT, journalEntry.getAmount(), reversalComment, null, null,
                        journalEntry.getReferenceNumber(), journalEntry.getLoanTransaction(), journalEntry.getSavingsTransaction(),
                        journalEntry.getClientTransaction(), shareTransactionId);
            }
            // save the reversal entry
            toUpdateJournalEntries.add(reversalJournalEntry);
            journalEntry.setReversed(true);
            journalEntry.setReversalJournalEntry(reversalJournalEntry);
            toUpdateJournalEntries.add(journalEntry);
            
        }
        this.journalEntryRepository.save(toUpdateJournalEntries);
        return reversalTransactionId;
    }

    private List<BankStatementDetailsData> changedBankStatementDetailsData(final Long bankStatementId) {

        return this.bankStatementReadPlatformService.changedBankStatementDetailsData(bankStatementId);
    }

    private Document getDocument(String key, FormDataMultiPart formParams) {
        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
        final String name = formParams.getField(ReconciliationApiConstants.nameParamName).getValue();
        String description = null;
        if (formParams.getField(ReconciliationApiConstants.descriptionParamName) != null) {
            description = formParams.getField(ReconciliationApiConstants.descriptionParamName).getValue();
        }
        String size = (key.equals(ReconciliationApiConstants.FIRST_FILE)) ? ReconciliationApiConstants.CIF_FILE_SIZE : ReconciliationApiConstants.ORIGINAL_FILE_SIZE;
        final Long fileSize = new Long(formParams.getField(size).getValue());

        FormDataBodyPart bodyPart = formParams.getField(key);
        InputStream inputStream = bodyPart.getEntityAs(InputStream.class);
        String fileName = bodyPart.getFormDataContentDisposition().getFileName();
        final DocumentCommand documentCommand = new DocumentCommand(null, null, ReconciliationApiConstants.entityName,
                ReconciliationApiConstants.bankStatementFolder, name, fileName, fileSize, bodyPart.getMediaType().toString(), description, null);
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
        BankStatement existingBankStatement = this.bankStatementRepository.findOneWithNotFoundDetection(new Long(bankStatementid));
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
        }
        existingBankStatement.setBank(bank);

        final Long cifFileSize = getSize(formParams.getField(ReconciliationApiConstants.CIF_FILE_SIZE).getValue() + "");
        final Long orgFileSize = getSize(formParams.getField(ReconciliationApiConstants.ORIGINAL_FILE_SIZE).getValue() + "");
        if (cifFileSize != null && orgFileSize != null) {
            cifDocumentId = getUpdatedDocument(formParams, existingBankStatement.getCpifDocument().getId(), name, description, cifFileSize,
                    ReconciliationApiConstants.FIRST_FILE);
            if (existingBankStatement.getOrgStatementDocument() == null) {
                orgDocumentId = getUpdatedDocument(formParams, null, name, description, orgFileSize, ReconciliationApiConstants.SECOND_FILE);
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
            Document document = this.documentRepository.getOne(cifDocumentId);
            validateExcel(document);
            Map<String, Object> fileContent = createMapOfFileData(this.bankStatementReadPlatformService.retrieveFile(cifDocumentId));
            @SuppressWarnings("unchecked")
            List<BankStatementDetails> bankStatementDetailsList = (List<BankStatementDetails>) fileContent.get(ReconciliationApiConstants.BANK_STATEMENT_DETAIL_LIST);
            @SuppressWarnings("unchecked")
            Map<Integer, List<String>> errorRows = (Map<Integer, List<String>>) fileContent.get(ReconciliationApiConstants.ERROR_ROWS);
            if (errorRows.size() == 0) {
                for (BankStatementDetails bankStatementDetail : bankStatementDetailsList) {
                    bankStatementDetail.setBankStatement(existingBankStatement);                    
                }
                this.bankStatementDetailsRepository.save(bankStatementDetailsList);
            } else {
                throw new InvalidCIFRowException(errorRows);
            }
            existingBankStatement.setIsReconciled(false);
            existingBankStatement.setCpifDocument(document);
        }
        if (orgDocumentId != null) {
            Document document = this.documentRepository.getOne(orgDocumentId);
            existingBankStatement.setOrgStatementDocument(document);
        }
        this.bankStatementRepository.save(existingBankStatement);
        return existingBankStatement.getId();

    }

    @SuppressWarnings("unchecked")
    public BankStatement saveBankStatementDetails(final Long cpifDocumentId, final BankStatement bankStatement) {
        Map<String, Object> fileContent = createMapOfFileData(this.bankStatementReadPlatformService.retrieveFile(cpifDocumentId));
        List<BankStatementDetails> bankStatementDetailsList = (List<BankStatementDetails>) fileContent.get(ReconciliationApiConstants.BANK_STATEMENT_DETAIL_LIST);
        Map<Integer, List<String>> errorRows = (Map<Integer, List<String>>) fileContent.get(ReconciliationApiConstants.ERROR_ROWS);
        if (errorRows.size() == 0) {
            this.bankStatementRepository.save(bankStatement);
            for (BankStatementDetails bankStatementDetail : bankStatementDetailsList) {
                bankStatementDetail.setBankStatement(bankStatement);                
            }
            this.bankStatementDetailsRepository.save(bankStatementDetailsList);
        } else {
            throw new InvalidCIFRowException(errorRows);
        }

        return bankStatement;
    }

    private Long getUpdatedDocument(FormDataMultiPart formParams, final Long documentId, final String name, final String description,
            final Long fileSize, final String key) {
        FormDataBodyPart bodyPart = formParams.getField(key);
        InputStream inputStream = bodyPart.getEntityAs(InputStream.class);
        String fileName = bodyPart.getFormDataContentDisposition().getFileName();
        final Set<String> modifiedParams = new HashSet<>();
        modifiedParams.add(ReconciliationApiConstants.nameParamName);
        modifiedParams.add(ReconciliationApiConstants.descriptionParamName);
        DocumentCommand documentCommand = null;
        if (inputStream != null && fileName != null) {
            modifiedParams.add(ReconciliationApiConstants.FILE_NAME);
            modifiedParams.add(ReconciliationApiConstants.SIZE);
            modifiedParams.add(ReconciliationApiConstants.TYPE);
            modifiedParams.add(ReconciliationApiConstants.LOCATION);
            documentCommand = new DocumentCommand(modifiedParams, documentId, ReconciliationApiConstants.entityName,
                    ReconciliationApiConstants.bankStatementFolder, name, fileName, fileSize, bodyPart.getMediaType().toString(), description, null);
        } else {
            documentCommand = new DocumentCommand(modifiedParams, documentId, ReconciliationApiConstants.entityName,
                    ReconciliationApiConstants.bankStatementFolder, name, null, null, null, description, null);
        }

        final DocumentCommandValidator validator = new DocumentCommandValidator(documentCommand);
        validator.validateForUpdate();
        if (documentId != null) {
            final Document documentForUpdate = this.documentRepository.findOne(documentId);
            if (documentForUpdate == null) { throw new DocumentNotFoundException(ReconciliationApiConstants.entityName,
                    ReconciliationApiConstants.bankStatementFolder, documentId); }

            if (inputStream != null && documentCommand.isFileNameChanged()) {
                final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
                documentCommand.setLocation(contentRepository.saveFile(inputStream, documentCommand));
                documentCommand.setStorageType(contentRepository.getStorageType().getValue());
            }
            documentForUpdate.update(documentCommand);
            this.documentRepository.save(documentForUpdate);
            return documentForUpdate.getId();
        }
        Long id = this.documentWritePlatformService.createDocument(documentCommand, inputStream);
        return id;
        
    }

    static Long getSize(String val) {
        Long value = null;
        if (!val.equals("undefined")) {
            value = new Long(val);
        }
        return value;
    }

    @Override
    public Long deleteBankStatementDetails(final Long bankStatementDetailsId) {
        BankStatementDetails bankStatementDetails = this.bankStatementDetailsRepository.findOneWithNotFoundDetection(bankStatementDetailsId);
        this.bankStatementDetailsRepository.delete(bankStatementDetails);
        return bankStatementDetailsId;
    }

    @Override
    public CommandProcessingResult reconcileBankStatementDetails(JsonCommand command) {
        if (command.parameterExists(ReconciliationApiConstants.transactionDataParamName)) {

            final JsonArray transactionDataArray = command.arrayOfParameterNamed(ReconciliationApiConstants.transactionDataParamName);
            if (!transactionDataArray.isJsonNull() && transactionDataArray.size() > 0) {

                for (int i = 0; i < transactionDataArray.size(); i++) {

                    final JsonObject jsonObject = transactionDataArray.get(i).getAsJsonObject();
                    final Long bankStatementId = jsonObject.get(ReconciliationApiConstants.bankTransctionIdParamName).getAsLong();
                    final Long loanTransactionId = jsonObject.get(ReconciliationApiConstants.loanTransactionIdParamName).getAsLong();
                    BankStatementDetails bankTransaction = null;
                    LoanTransaction loanTransaction = null;
                    if (loanTransactionId != null) {
                        loanTransaction = this.loanTransactionRepository.findOneWithNotFoundDetection(loanTransactionId);
                        if (loanTransaction != null && !loanTransaction.isReversed() && !loanTransaction.isRefund()
                                && !loanTransaction.isReconciled()) {
                            loanTransaction.setReconciled(true);
                            this.loanTransactionRepository.save(loanTransaction);
                            bankTransaction = this.bankStatementDetailsRepository.findOneWithNotFoundDetection(bankStatementId);
                            bankTransaction.setIsReconciled(true);
                            bankTransaction.setLoanTransaction(loanTransaction);
                            this.bankStatementDetailsRepository.save(bankTransaction);

                        }
                    }

                }

            }

        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(command.entityId()) //
                .build();
    }

    @Override
    public CommandProcessingResult reconcileBankStatement(JsonCommand command) {
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

    @SuppressWarnings("unused")
    @Override
    public String createJournalEntries(Long bankStatementId, String apiRequestBodyAsJson) {
        List<BankStatementDetailsData> bankStatementDetailsData = this.bankStatementReadPlatformService.retrieveBankStatementDetailsData(
                bankStatementId, ReconciliationApiConstants.JOURNAL_COMMAND_PARAMETER);
        Long defaultBankGLAccountId = null;
        if (bankStatementDetailsData.size() > 0) {
            Bank bank = this.bankStatementRepository.findOneWithNotFoundDetection(bankStatementId).getBank();
            if (bank != null) {
                defaultBankGLAccountId = bank.getGlAccount().getId();
            } else {
                throw new BankNotAssociatedExcecption();
            }
        }
        HashMap<String, Object> responseData = new HashMap<String, Object>();
        List<Object> resultList = new ArrayList<Object>();
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> requestBodyMap = gson.fromJson(apiRequestBodyAsJson, type);
        String locale = requestBodyMap.get(ReconciliationApiConstants.localeParamName);
        for (BankStatementDetailsData bankStatementDetail : bankStatementDetailsData) {
            HashMap<String, Object> responseMap = new HashMap<String, Object>();
            HashMap<String, Object> requestMap = new HashMap<String, Object>();
            requestMap.put(ReconciliationApiConstants.localeParamName, locale);
            requestMap.put(ReconciliationApiConstants.dateFormatParamName, "MMM DD, YYYY");
            requestMap.put(ReconciliationApiConstants.officeIdParamName, bankStatementDetail.getBranch());
            requestMap.put(ReconciliationApiConstants.transactionDateParamName, bankStatementDetail.getTransactionDate());
            requestMap.put(ReconciliationApiConstants.currencyCodeParamName, "USD");
            requestMap.putAll(getCreditAndDebitMap(bankStatementDetail, defaultBankGLAccountId));
            String requestBody = gson.toJson(requestMap);
            CommandProcessingResult result = null;
            final CommandWrapper commandRequest = new CommandWrapperBuilder().createJournalEntry().withJson(requestBody).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            BankStatementDetails toUpdate = this.bankStatementDetailsRepository.findOneWithNotFoundDetection(bankStatementDetail.getId());
            toUpdate.setTransactionId(result.getTransactionId());
            this.bankStatementDetailsRepository.save(toUpdate);
            responseMap.put(ReconciliationApiConstants.BANK_STATEMENT_DETAIL_ID, bankStatementDetail.getId());
            responseMap.put(ReconciliationApiConstants.JOURNAL_ENTRY_RESPONSE, result);
            resultList.add(responseMap);
        }
        responseData.put(ReconciliationApiConstants.RESOURCE, resultList);
        return gson.toJson(responseData);
    }

    @SuppressWarnings("unused")
    private HashMap<String, Object> getCreditAndDebitMap(BankStatementDetailsData bankStatementDetail, Long defaultBankGLAccountId) {

        HashMap<String, Object> creaditMap = new HashMap<String, Object>();
        HashMap<String, Object> debitMap = new HashMap<String, Object>();
        creaditMap.put(ReconciliationApiConstants.amountParamName, bankStatementDetail.getAmount());
        debitMap.put(ReconciliationApiConstants.amountParamName, bankStatementDetail.getAmount());
        String code = bankStatementDetail.getGlCode();
        GLAccountDataForLookup GLAccount = this.bankGLAccountReadPlatformService.retrieveGLAccountByGLCode(code);
        if (GLAccount == null) { throw new GLAccountNotFoundException(code); }
        if (bankStatementDetail.getAccountingType().equalsIgnoreCase(ReconciliationApiConstants.DEBIT)) {
            creaditMap.put(ReconciliationApiConstants.GL_ACCOUNT_ID, defaultBankGLAccountId);
            debitMap.put(ReconciliationApiConstants.GL_ACCOUNT_ID, GLAccount.getId());
        } else {
            creaditMap.put(ReconciliationApiConstants.GL_ACCOUNT_ID, GLAccount.getId());
            debitMap.put(ReconciliationApiConstants.GL_ACCOUNT_ID, defaultBankGLAccountId);
        }
        List<Object> creditList = new ArrayList<Object>();
        creditList.add(creaditMap);
        List<Object> debitList = new ArrayList<Object>();
        debitList.add(debitMap);
        HashMap<String, Object> creditAndDebitMap = new HashMap<String, Object>();
        creditAndDebitMap.put(ReconciliationApiConstants.CREDIT_ACCOUNT, creditList);
        creditAndDebitMap.put(ReconciliationApiConstants.DEBIT_ACCOUNT, debitList);
        return creditAndDebitMap;
    }

    private String generateTransactionId(final Long officeId) {
        final AppUser user = this.context.authenticatedUser();
        final Long time = System.currentTimeMillis();
        final String uniqueVal = String.valueOf(time) + user.getId() + officeId;
        final String transactionId = Long.toHexString(Long.parseLong(uniqueVal));
        return transactionId;
    }

}
