/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.data;

import java.math.BigDecimal;
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
    
    private BigDecimal portFolioReconciledInflowAmount = null;
    private BigDecimal portFolioReconciledOutflowAmount = null;
    private BigDecimal portFolioUnReconciledInflowAmount = null;
    private BigDecimal portFolioUnReconciledOutflowAmount = null;
    
    private BigDecimal nonPortFolioReconciledInflowAmount = null;
    private BigDecimal nonPortFolioReconciledOutflowAmount = null;
    private BigDecimal nonPortFolioUnReconciledInflowAmount = null;
    private BigDecimal nonPortFolioUnReconciledOutflowAmount = null;
    
    private BigDecimal miscellaneousReconciledInflowAmount = null;
    private BigDecimal miscellaneousUnReconciledInflowAmount = null;
    private BigDecimal miscellaneousReconciledOutflowAmount = null;
    private BigDecimal miscellaneousUnReconciledOutflowAmount = null;
    

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

	public BigDecimal getPortFolioReconciledInflowAmount() {
		return this.portFolioReconciledInflowAmount;
	}

	public void setPortFolioReconciledInflowAmount(
			BigDecimal portFolioReconciledInflowAmount) {
		this.portFolioReconciledInflowAmount = portFolioReconciledInflowAmount;
	}

	public BigDecimal getPortFolioReconciledOutflowAmount() {
		return this.portFolioReconciledOutflowAmount;
	}

	public void setPortFolioReconciledOutflowAmount(
			BigDecimal portFolioReconciledOutflowAmount) {
		this.portFolioReconciledOutflowAmount = portFolioReconciledOutflowAmount;
	}

	public BigDecimal getPortFolioUnReconciledInflowAmount() {
		return this.portFolioUnReconciledInflowAmount;
	}

	public void setPortFolioUnReconciledInflowAmount(
			BigDecimal portFolioUnReconciledInflowAmount) {
		this.portFolioUnReconciledInflowAmount = portFolioUnReconciledInflowAmount;
	}

	public BigDecimal getPortFolioUnReconciledOutflowflowAmount() {
		return this.portFolioUnReconciledOutflowAmount;
	}

	public void setPortFolioUnReconciledOutflowAmount(
			BigDecimal portFolioUnReconciledOutflowAmount) {
		this.portFolioUnReconciledOutflowAmount = portFolioUnReconciledOutflowAmount;
	}

	public BigDecimal getNonPortFolioReconciledInflowAmount() {
		return this.nonPortFolioReconciledInflowAmount;
	}

	public void setNonPortFolioReconciledInflowAmount(
			BigDecimal nonPortFolioReconciledInflowAmount) {
		this.nonPortFolioReconciledInflowAmount = nonPortFolioReconciledInflowAmount;
	}

	public BigDecimal getNonPortFolioReconciledOutflowAmount() {
		return this.nonPortFolioReconciledOutflowAmount;
	}

	public void setNonPortFolioReconciledOutflowAmount(
			BigDecimal nonPortFolioReconciledOutflowAmount) {
		this.nonPortFolioReconciledOutflowAmount = nonPortFolioReconciledOutflowAmount;
	}

	public BigDecimal getNonPortFolioUnReconciledInflowAmount() {
		return this.nonPortFolioUnReconciledInflowAmount;
	}

	public void setNonPortFolioUnReconciledInflowAmount(
			BigDecimal nonPortFolioUnReconciledInflowAmount) {
		this.nonPortFolioUnReconciledInflowAmount = nonPortFolioUnReconciledInflowAmount;
	}

	public BigDecimal getNonPortFolioUnReconciledOutflowAmount() {
		return this.nonPortFolioUnReconciledOutflowAmount;
	}

	public void setNonPortFolioUnReconciledOutflowAmount(
			BigDecimal nonPortFolioUnReconciledOutflowAmount) {
		this.nonPortFolioUnReconciledOutflowAmount = nonPortFolioUnReconciledOutflowAmount;
	}

	public BigDecimal getMiscellaneousReconciledInflowAmount() {
		return this.miscellaneousReconciledInflowAmount;
	}

	public void setMiscellaneousReconciledInflowAmount(
			BigDecimal miscellaneousReconciledInflowAmount) {
		this.miscellaneousReconciledInflowAmount = miscellaneousReconciledInflowAmount;
	}

	public BigDecimal getMiscellaneousUnReconciledInflowAmount() {
		return this.miscellaneousUnReconciledInflowAmount;
	}

	public void setMiscellaneousUnReconciledInflowAmount(
			BigDecimal miscellaneousUnReconciledInflowAmount) {
		this.miscellaneousUnReconciledInflowAmount = miscellaneousUnReconciledInflowAmount;
	}

	public BigDecimal getMiscellaneousReconciledOutflowAmount() {
		return this.miscellaneousReconciledOutflowAmount;
	}

	public void setMiscellaneousReconciledOutflowAmount(
			BigDecimal miscellaneousReconciledOutflowAmount) {
		this.miscellaneousReconciledOutflowAmount = miscellaneousReconciledOutflowAmount;
	}

	public BigDecimal getMiscellaneousUnReconciledOutflowAmount() {
		return this.miscellaneousUnReconciledOutflowAmount;
	}

	public void setMiscellaneousUnReconciledOutflowAmount(
			BigDecimal miscellaneousUnReconciledOutflowAmount) {
		this.miscellaneousUnReconciledOutflowAmount = miscellaneousUnReconciledOutflowAmount;
	}
    
    

}
