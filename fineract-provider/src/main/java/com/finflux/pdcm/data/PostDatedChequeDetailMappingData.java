package com.finflux.pdcm.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.joda.time.LocalDate;

public class PostDatedChequeDetailMappingData {

    private final Long id;
    private final Integer paymentType;
    private final EnumOptionData entityType;
    private final Long entityId;
    private final BigDecimal dueAmount;
    private final LocalDate dueDate;
    private final boolean paidStatus;

    private PostDatedChequeDetailMappingData(final Long id, final Integer paymentType, final EnumOptionData entityType,
            final Long entityId, final BigDecimal dueAmount, final LocalDate dueDate, final boolean paidStatus) {
        this.id = id;
        this.paymentType = paymentType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.dueAmount = dueAmount;
        this.dueDate = dueDate;
        this.paidStatus = paidStatus;

    }

    public static PostDatedChequeDetailMappingData instance(final Long id, final Integer paymentType, final EnumOptionData entityType,
            final Long entityId, final BigDecimal dueAmount, final LocalDate dueDate, final boolean paidStatus) {
        return new PostDatedChequeDetailMappingData(id, paymentType, entityType, entityId, dueAmount, dueDate, paidStatus);
    }

    public Long getId() {
        return this.id;
    }

    public Integer getPaymentType() {
        return this.paymentType;
    }

    public EnumOptionData getEntityType() {
        return this.entityType;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public BigDecimal getDueAmount() {
        return this.dueAmount;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public boolean isPaidStatus() {
        return this.paidStatus;
    }

}
