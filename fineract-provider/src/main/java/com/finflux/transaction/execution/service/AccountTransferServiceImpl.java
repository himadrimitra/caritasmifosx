package com.finflux.transaction.execution.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.finflux.transaction.execution.data.AccountTransactionDetail;
import com.finflux.transaction.execution.data.AccountTransactionRequest;

/**
 * Created by dhirendra on 22/09/16.
 */
@Service
@Scope("singleton")
public class AccountTransferServiceImpl implements AccountTransferService {

	private final BankTransferProviderFactory bankTransferProviderFactory;

	@Autowired
	public AccountTransferServiceImpl(
			final BankTransferProviderFactory bankTransferProviderFactory) {
		this.bankTransferProviderFactory = bankTransferProviderFactory;
	}

	@Override
	public boolean validateAccount(Long beneficiary) {
		return false;
	}

	@Override
	public AccountTransactionDetail transactionEntry(
			Long externalPaymentServiceId,
			AccountTransactionRequest transactionRequest) {
		return null;
	}

	@Override
	public AccountTransactionDetail initiateTransaction(Long entityId,
			Integer entityTypeId, Long entityTxnId) {
		return null;
	}

	@Override
	public AccountTransactionDetail refreshStatus(Long transactionId) {
		return null;
	}

	@Override
	public List<AccountTransactionDetail> getAllTransaction(Long entityId,
			Integer entityTypeId) {
		return null;
	}

	@Override
	public AccountTransactionDetail getTransactionDetail(Long entityId,
			Integer entityTypeId, Long entityTxnId) {
		return null;
	}

}
