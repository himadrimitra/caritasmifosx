package com.finflux.reconcilation.bankstatement.data;

import java.util.Date;

import com.finflux.reconcilation.bank.data.BankData;

@SuppressWarnings("unused")
public class BankStatementData {

    private final Long id;
    private final String name;
    private final String description;
    private final Long cpifKeyDocumentId;
    private final Long orgStatementKeyDocumentId;
    private final Date createdDate;
    private final Date lastModifiedDate;
    private final Long createdById;
    private final Long lastModifiedById;
    private final String createdByName;
    private final String lastModifiedByName;
    private final String cpifFileName;
    private final String orgFileName;
    private final Boolean isReconciled;
    private final BankData bankData;

    public BankStatementData(final Long id, final String name, final String description, final Long cpifKeyDocumentId,
            final Long orgStatementKeyDocumentId, final Long createdById, final Date createdDate, final Long lastModifiedById,
            final Date lastModifiedDate, final String createdByName, final String lastModifiedByName, final String cpifFileName,
            final String orgFileName, final Boolean isReconciled, final BankData bankData) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cpifKeyDocumentId = cpifKeyDocumentId;
        this.orgStatementKeyDocumentId = orgStatementKeyDocumentId;
        this.createdById = createdById;
        this.createdDate = createdDate;
        this.lastModifiedById = lastModifiedById;
        this.lastModifiedDate = lastModifiedDate;
        this.createdByName = createdByName;
        this.lastModifiedByName = lastModifiedByName;
        this.cpifFileName = cpifFileName;
        this.orgFileName = orgFileName;
        this.isReconciled = isReconciled;
        this.bankData = bankData;
    }

    public static BankStatementData instance(final Long id, final String name, final String description, final Long cpifKeyDocumentId,
            final Long orgStatementKeyDocumentId, final Long createdById, final Date createdDate, final Long lastModifiedById,
            final Date lastModifiedDate, final String createdByName, final String lastModifiedByName, final String cpifFileName,
            final String orgFileName, final Boolean isReconciled, final BankData bankData) {
        return new BankStatementData(id, name, description, cpifKeyDocumentId, orgStatementKeyDocumentId, createdById, createdDate,
                lastModifiedById, lastModifiedDate, createdByName, lastModifiedByName, cpifFileName, orgFileName, isReconciled, bankData);
    }

    public Long getId() {
        return id;
    }

    public BankData getBankData() {
        return bankData;
    }

}
