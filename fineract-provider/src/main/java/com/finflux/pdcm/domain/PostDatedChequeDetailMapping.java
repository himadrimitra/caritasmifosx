package com.finflux.pdcm.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_pdc_cheque_detail_mapping", uniqueConstraints = { @UniqueConstraint(columnNames = { "pdc_cheque_detail_id", "entity_type",
        "entity_id", "due_date" }, name = "f_pdc_cheque_detail_mapping_UNIQUE") })
public class PostDatedChequeDetailMapping extends AbstractPersistable<Long> {

    @OneToOne
    @JoinColumn(name = "pdc_cheque_detail_id", nullable = false)
    private PostDatedChequeDetail postDatedChequeDetail;

    @Column(name = "payment_type", length = 11, nullable = true)
    private Integer paymentType;

    @Column(name = "entity_type", length = 3, nullable = true)
    private Integer entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "due_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal dueAmount;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "paid_status", nullable = false)
    private boolean paidStatus = false;

    @Column(name = "transaction_id", nullable = true)
    private Long transactionId;

    @Column(name = "is_deleted", length = 1, nullable = false)
    private boolean isDeleted = false;

    protected PostDatedChequeDetailMapping() {}

    private PostDatedChequeDetailMapping(final PostDatedChequeDetail postDatedChequeDetail, final Integer paymentType,
            final Integer entityType, final Long entityId, final BigDecimal dueAmount, final Date dueDate) {
        this.postDatedChequeDetail = postDatedChequeDetail;
        this.paymentType = paymentType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.dueAmount = dueAmount;
        this.dueDate = dueDate;
    }

    public static PostDatedChequeDetailMapping create(final PostDatedChequeDetail postDatedChequeDetail, final Integer paymentType,
            final Integer entityType, final Long entityId, final BigDecimal dueAmount, final Date dueDate) {
        return new PostDatedChequeDetailMapping(postDatedChequeDetail, paymentType, entityType, entityId, dueAmount, dueDate);
    }

    public PostDatedChequeDetail getPostDatedChequeDetail() {
        return this.postDatedChequeDetail;
    }

    public void setPostDatedChequeDetail(PostDatedChequeDetail postDatedChequeDetail) {
        this.postDatedChequeDetail = postDatedChequeDetail;
    }

    public Integer getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getEntityType() {
        return this.entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public BigDecimal getDueAmount() {
        return this.dueAmount;
    }

    public void setDueAmount(BigDecimal dueAmount) {
        this.dueAmount = dueAmount;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isPaidStatus() {
        return this.paidStatus;
    }

    public void setPaidStatus(boolean paidStatus) {
        this.paidStatus = paidStatus;
    }

    public Long getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}