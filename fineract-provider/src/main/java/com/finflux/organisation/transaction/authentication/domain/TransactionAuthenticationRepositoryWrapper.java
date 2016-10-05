package com.finflux.organisation.transaction.authentication.domain;

import java.util.HashMap;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.organisation.transaction.authentication.exception.TransactionAuthenticationNotFoundException;

@Service
public class TransactionAuthenticationRepositoryWrapper {

	private final TransactionAuthenticationRepository repository;
	private static HashMap<String, Integer> portfolioTypeCounts = new HashMap<>();

	@Autowired
	public TransactionAuthenticationRepositoryWrapper(final TransactionAuthenticationRepository repository) {
		this.repository = repository;
	}

	public void save(final TransactionAuthentication transactionAuthentication) {
		this.repository.save(transactionAuthentication);
	}

	public TransactionAuthentication findOneWithNotFoundDetection(final Long id) {
		TransactionAuthentication transactionAuthentication = this.repository.findOne(id);
		if (transactionAuthentication != null) {
			return this.repository.findOne(id);
		} else {
			throw new TransactionAuthenticationNotFoundException(id);
		}
	}

	public CommandProcessingResult deletTransactionAuthentication(final Long transactionAuthenticationId) {
		TransactionAuthentication transactionAuthentication = this.repository.findOne(transactionAuthenticationId);
		this.repository.delete(transactionAuthenticationId);
		updatePortfolioTypeAndTransactionType(transactionAuthentication.getPortfolioType(),
				transactionAuthentication.getTransactionTypeId());
		return new CommandProcessingResultBuilder().withEntityId(transactionAuthenticationId).build();
	}

	public void updatePortfolioTypeAndTransactionType(final Integer portfolioTypeId, final Integer transactionTypeId) {
		final String identifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
		final String key = identifier + "_" + portfolioTypeId + "_" + transactionTypeId;
		if (portfolioTypeCounts.containsKey(key)) {
			List<TransactionAuthentication> transactionAuthentications = this.repository
					.findByPortfolioTypeAndTransactionTypeId(portfolioTypeId, transactionTypeId);
			if (transactionAuthentications.isEmpty()) {
				portfolioTypeCounts.remove(key);
			} else {
				portfolioTypeCounts.put(key, transactionAuthentications.size());
			}
		}
	}

	public Integer getPortfolioTypeAndTransactionType(final Integer portfolioTypeId, final Integer transactionTypeId) {
		final String identifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
		final String key = identifier + "_" + portfolioTypeId + "_" + transactionTypeId;
		if (!portfolioTypeCounts.containsKey(key)) {
			List<TransactionAuthentication> transactionAuthentications = this.repository
					.findByPortfolioTypeAndTransactionTypeId(portfolioTypeId, transactionTypeId);
			if (transactionAuthentications.isEmpty()) {
				return 0;
			} else {
				portfolioTypeCounts.put(key, transactionAuthentications.size());
			}
		}
		return portfolioTypeCounts.get(key);
	}

	public List<TransactionAuthentication> findByPortfolioTypeAndTransactionTypeId(final Integer portfolioType,
			final Integer transactionTypeId) {
		return this.repository.findByPortfolioTypeAndTransactionTypeId(portfolioType, transactionTypeId);
	}

}
