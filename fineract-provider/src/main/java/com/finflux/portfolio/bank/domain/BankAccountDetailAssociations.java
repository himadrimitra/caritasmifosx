package com.finflux.portfolio.bank.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

@Entity
@Table(name = "f_bank_account_detail_associations")
public class BankAccountDetailAssociations extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "bank_account_detail_id", nullable = false)
    private BankAccountDetails bankAccountDetails;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "entity_type_enum", nullable = false)
    private Integer entityTypeId;
    
    protected BankAccountDetailAssociations(){
        
    }

    public BankAccountDetailAssociations(BankAccountDetails bankAccountDetails, Integer entityTypeId, Long entityId) {
        this.bankAccountDetails = bankAccountDetails;
        this.entityId = entityId;
        this.entityTypeId = entityTypeId;
    }

    public BankAccountDetails getBankAccountDetails() {
        return this.bankAccountDetails;
    }

    public void setBankAccountDetails(BankAccountDetails bankAccountDetails) {
        this.bankAccountDetails = bankAccountDetails;
    }

}
