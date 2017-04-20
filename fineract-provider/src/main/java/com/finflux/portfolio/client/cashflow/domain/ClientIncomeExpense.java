package com.finflux.portfolio.client.cashflow.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.client.domain.Client;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.familydetail.domain.FamilyDetail;
import com.finflux.portfolio.cashflow.domain.IncomeExpense;
import com.finflux.portfolio.client.cashflow.api.ClientIncomeExpenseApiConstants;

@Entity
@Table(name = "f_client_income_expense")
public class ClientIncomeExpense extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "family_details_id", nullable = true)
    private FamilyDetail familyDetail;

    @ManyToOne
    @JoinColumn(name = "income_expense_id", nullable = false)
    private IncomeExpense incomeExpense;

    @Column(name = "quintity", scale = 2, precision = 10)
    private BigDecimal quintity;

    @Column(name = "default_income", scale = 6, precision = 19, nullable = true)
    private BigDecimal defaultIncome;

    @Column(name = "default_expense", scale = 6, precision = 19, nullable = true)
    private BigDecimal defaultExpense;
    
    @Column(name = "total_income", scale = 6, precision = 19, nullable = true)
    private BigDecimal totalIncome;

    @Column(name = "total_expense", scale = 6, precision = 19, nullable = true)
    private BigDecimal totalExpense;

    @Column(name = "is_month_wise_income", length = 1, nullable = true)
    private Boolean isMonthWiseIncome;

    @Column(name = "is_primary_income", length = 1, nullable = true)
    private Boolean isPrimaryIncome;
    
    @Column(name = "is_remmitance_income", length = 1, nullable = true)
    private Boolean isRemmitanceIncome;

    @Column(name = "is_active", length = 1, nullable = false)
    private Boolean isActive;

    @SuppressWarnings("unused")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clientIncomeExpense", orphanRemoval = true)
    private List<ClientMonthWiseIncomeExpense> clientMonthWiseIncomeExpense = new ArrayList<ClientMonthWiseIncomeExpense>();

    protected ClientIncomeExpense() {}

    private ClientIncomeExpense(final Client client, final FamilyDetail familyDetail, final IncomeExpense incomeExpense,
            final BigDecimal quintity, final BigDecimal totalIncome, final BigDecimal totalExpense, final Boolean isMonthWiseIncome,
            final Boolean isPrimaryIncome, final Boolean isActive,final Boolean isRemmitanceIncome) {
        this.client = client;
        this.familyDetail = familyDetail;
        this.incomeExpense = incomeExpense;
        this.quintity = quintity;
        this.defaultIncome = incomeExpense.getDefaultIncome();
        this.defaultExpense = incomeExpense.getDefaultExpense();
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.isMonthWiseIncome = isMonthWiseIncome;
        this.isPrimaryIncome = isPrimaryIncome;
        this.isActive = isActive;
        this.isRemmitanceIncome=isRemmitanceIncome;
    }

    public static ClientIncomeExpense create(final Client client, final FamilyDetail familyDetail, final IncomeExpense incomeExpense,
            final BigDecimal quintity, final BigDecimal totalIncome, final BigDecimal totalExpense, final Boolean isMonthWiseIncome,
            final Boolean isPrimaryIncome, final Boolean isActive,final Boolean isRemmitanceIncome) {
        return new ClientIncomeExpense(client, familyDetail, incomeExpense, quintity, totalIncome, totalExpense, isMonthWiseIncome,
                isPrimaryIncome, isActive,isRemmitanceIncome);
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);
        if (command.isChangeInLongParameterNamed(ClientIncomeExpenseApiConstants.familyDetailsIdParamName, familyDetailId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientIncomeExpenseApiConstants.familyDetailsIdParamName);
            actualChanges.put(ClientIncomeExpenseApiConstants.familyDetailsIdParamName, newValue);
        }
        if (command.isChangeInLongParameterNamed(ClientIncomeExpenseApiConstants.incomeExpenseIdParamName, incomeExpenseId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientIncomeExpenseApiConstants.incomeExpenseIdParamName);
            actualChanges.put(ClientIncomeExpenseApiConstants.incomeExpenseIdParamName, newValue);
        }
        if (command.isChangeInBigDecimalParameterNamed(ClientIncomeExpenseApiConstants.quintityParamName, this.quintity)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientIncomeExpenseApiConstants.quintityParamName);
            actualChanges.put(ClientIncomeExpenseApiConstants.quintityParamName, newValue);
            this.quintity = newValue;
        }
        //this.defaultIncome = this.incomeExpense.getDefaultIncome();
        //this.defaultExpense = this.incomeExpense.getDefaultExpense();
        if (command.isChangeInBigDecimalParameterNamed(ClientIncomeExpenseApiConstants.totalIncomeParamName, this.totalIncome)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientIncomeExpenseApiConstants.totalIncomeParamName);
            actualChanges.put(ClientIncomeExpenseApiConstants.totalIncomeParamName, newValue);
            this.totalIncome = newValue;
        }
        if (command.isChangeInBigDecimalParameterNamed(ClientIncomeExpenseApiConstants.totalExpenseParamName, this.totalExpense)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(ClientIncomeExpenseApiConstants.totalExpenseParamName);
            actualChanges.put(ClientIncomeExpenseApiConstants.totalExpenseParamName, newValue);
            this.totalExpense = newValue;
        }
        if (command.isChangeInBooleanParameterNamed(ClientIncomeExpenseApiConstants.isMonthWiseIncomeParamName, this.isMonthWiseIncome)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(ClientIncomeExpenseApiConstants.isMonthWiseIncomeParamName);
            actualChanges.put(ClientIncomeExpenseApiConstants.isMonthWiseIncomeParamName, newValue);
            this.isMonthWiseIncome = newValue;
        }
        if (command.isChangeInBooleanParameterNamed(ClientIncomeExpenseApiConstants.isPrimaryIncomeParamName, this.isPrimaryIncome)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(ClientIncomeExpenseApiConstants.isPrimaryIncomeParamName);
            actualChanges.put(ClientIncomeExpenseApiConstants.isPrimaryIncomeParamName, newValue);
            this.isPrimaryIncome = newValue;
        }
        if (command.isChangeInBooleanParameterNamed(ClientIncomeExpenseApiConstants.isActiveParamName, this.isActive)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(ClientIncomeExpenseApiConstants.isActiveParamName);
            actualChanges.put(ClientIncomeExpenseApiConstants.isActiveParamName, newValue);
            this.isActive = newValue;
        }
        if (command.isChangeInBooleanParameterNamed(ClientIncomeExpenseApiConstants.isRemmitanceIncomeParamName, this.isRemmitanceIncome)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(ClientIncomeExpenseApiConstants.isRemmitanceIncomeParamName);
            actualChanges.put(ClientIncomeExpenseApiConstants.isRemmitanceIncomeParamName, newValue);
            this.isRemmitanceIncome = newValue;
        }
        return actualChanges;
    }

    private Long familyDetailId() {
        Long familyDetailId = null;
        if (this.familyDetail != null) {
            familyDetailId = this.familyDetail.getId();
        }
        return familyDetailId;
    }

    private Long incomeExpenseId() {
        Long incomeExpenseId = null;
        if (this.incomeExpense != null) {
            incomeExpenseId = this.incomeExpense.getId();
        }
        return incomeExpenseId;
    }

    public void updateFamilyDetail(final FamilyDetail familyDetail) {
        this.familyDetail = familyDetail;
    }

    public void updateIncomeExpense(final IncomeExpense incomeExpense) {
        this.incomeExpense = incomeExpense;
    }

    public void addAllClientMonthWiseIncomeExpense(List<ClientMonthWiseIncomeExpense> clientMonthWiseIncomeExpense) {
        this.clientMonthWiseIncomeExpense.clear();
        if (clientMonthWiseIncomeExpense != null && clientMonthWiseIncomeExpense.size() > 0) {
            this.clientMonthWiseIncomeExpense.addAll(clientMonthWiseIncomeExpense);
        }
    }
}