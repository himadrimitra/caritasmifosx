package org.apache.fineract.accounting.journalentry.data;

import java.math.BigDecimal;
import java.util.Date;

import com.finflux.portfolio.investmenttracker.data.InvestmentTransactionEnumData;


public class InvestmentTransactionDTO {
    
    private final Long officeId;
    private final String transactionId;
    private final Date transactionDate;
    private final InvestmentTransactionEnumData transactionType;
    private final BigDecimal amount;
    private final boolean reversed;
    
    public InvestmentTransactionDTO(Long officeId, String transactionId, Date transactionDate,
            InvestmentTransactionEnumData transactionType, BigDecimal amount,final boolean reversed) {
        this.officeId = officeId;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.amount = amount;
        this.reversed = reversed;
    }

    
    public Long getOfficeId() {
        return this.officeId;
    }

    
    public String getTransactionId() {
        return this.transactionId;
    }

    
    public Date getTransactionDate() {
        return this.transactionDate;
    }

    
    public InvestmentTransactionEnumData getTransactionType() {
        return this.transactionType;
    }

    
    public BigDecimal getAmount() {
        return this.amount;
    }


    
    public boolean isReversed() {
        return this.reversed;
    }
    
    

}
