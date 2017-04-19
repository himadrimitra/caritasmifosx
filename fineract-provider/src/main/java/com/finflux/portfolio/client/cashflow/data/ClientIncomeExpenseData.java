package com.finflux.portfolio.client.cashflow.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.finflux.portfolio.cashflow.data.IncomeExpenseData;

public class ClientIncomeExpenseData {

    private final Long id;
    private final Long clientId;
    private final Long familyDetailId;
    private final IncomeExpenseData incomeExpenseData;
    private final BigDecimal quintity;
    private final BigDecimal defaultIncome;
    private final BigDecimal defaultExpense;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;
    private final Boolean isMonthWiseIncome;
    private final Boolean isPrimaryIncome;
    private final Boolean isActive;
    private List<ClientMonthWiseIncomeExpenseData> clientMonthWiseIncomeExpenseDatas;
    private final Boolean isRemmitanceIncome;

    private ClientIncomeExpenseData(final Long id, final Long clientId, final Long familyDetailId,
            final IncomeExpenseData incomeExpenseData, final BigDecimal quintity, final BigDecimal defaultIncome,
            final BigDecimal defaultExpense, final BigDecimal totalIncome, final BigDecimal totalExpense, final Boolean isMonthWiseIncome,
            final Boolean isPrimaryIncome, final Boolean isActive,final Boolean isRemmitanceIncome) {
        this.id = id;
        this.clientId = clientId;
        this.familyDetailId = familyDetailId;
        this.incomeExpenseData = incomeExpenseData;
        this.quintity = quintity;
        this.defaultIncome = defaultIncome;
        this.defaultExpense = defaultExpense;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.isMonthWiseIncome = isMonthWiseIncome;
        this.isPrimaryIncome = isPrimaryIncome;
        this.isActive = isActive;
        this.isRemmitanceIncome=isRemmitanceIncome;
    }

    public static ClientIncomeExpenseData instance(final Long id, final Long clientId, final Long familyDetailId,
            final IncomeExpenseData incomeExpenseData, final BigDecimal quintity, final BigDecimal defaultIncome,
            final BigDecimal defaultExpense, final BigDecimal totalIncome, final BigDecimal totalExpense, final Boolean isMonthWiseIncome,
            final Boolean isPrimaryIncome, final Boolean isActive,final Boolean isRemmitanceIncome) {
        return new ClientIncomeExpenseData(id, clientId, familyDetailId, incomeExpenseData, quintity, defaultIncome, defaultExpense,
                totalIncome, totalExpense, isMonthWiseIncome, isPrimaryIncome, isActive,isRemmitanceIncome);
    }

    public void addClientMonthWiseIncomeExpenseData(final ClientMonthWiseIncomeExpenseData clientMonthWiseIncomeExpenseData) {
        if (this.clientMonthWiseIncomeExpenseDatas == null) {
            this.clientMonthWiseIncomeExpenseDatas = new ArrayList<>();
        }
        this.clientMonthWiseIncomeExpenseDatas.add(clientMonthWiseIncomeExpenseData);
    }
}