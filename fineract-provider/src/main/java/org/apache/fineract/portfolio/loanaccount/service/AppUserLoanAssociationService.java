package org.apache.fineract.portfolio.loanaccount.service;


public interface AppUserLoanAssociationService {

    Boolean hasAccessToLoan(Long loanId, Long appUserId);

}
