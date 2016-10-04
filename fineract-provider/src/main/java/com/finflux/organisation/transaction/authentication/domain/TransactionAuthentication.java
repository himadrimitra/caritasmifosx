package com.finflux.organisation.transaction.authentication.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.Role;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.infrastructure.external.authentication.domain.SecondaryAuthenticationService;
import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;

@Entity
@Table(name = "f_transaction_authentication", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "portfolio_type", "transaction_type_enum", "payment_type_id",
				"amount" }, name = "portfolio_type_transaction_type_enum_payment_type_id_amount") })
public class TransactionAuthentication extends AbstractPersistable<Long> {

	@Column(name = "portfolio_type", length = 3, nullable = false)
	private Integer portfolioType;

	@Column(name = "transaction_type_enum", length = 3, nullable = false)
	private Integer transactionTypeId;

	@ManyToOne
	@JoinColumn(name = "payment_type_id", nullable = false)
	private PaymentType paymentTypeId;

	@Column(name = "amount", scale = 6, precision = 19, nullable = false)
	private BigDecimal amount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "second_app_user_role_id", nullable = true)
	private Role secondAppUserRoleId;

	@Column(name = "is_second_app_user_enabled", length = 1)
	private boolean isSecondAppUserEnabled;

	@ManyToOne
	@JoinColumn(name = "authentication_id", nullable = false)
	private SecondaryAuthenticationService authenticationTypeId;

	@Column(name = "lastmodified_date", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date lastModifiedDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lastmodifiedby_id", nullable = false)
	private AppUser lastModifiedById;

	public static TransactionAuthentication newTransactionAuthentication(final Integer productTypeId,
			final Integer transationTypeId, final PaymentType paymentTypeId, final BigDecimal amountGreaterThan,
			final Role secondAppUserRoleId, final boolean isSecondAppUserEnabled,
			final SecondaryAuthenticationService authenticationTypeId, final AppUser lastModifiedById) {
		final Date lastModifiedDate = DateUtils.getLocalDateTimeOfTenant().toDate();
		return new TransactionAuthentication(productTypeId, transationTypeId, paymentTypeId, amountGreaterThan,
				secondAppUserRoleId, isSecondAppUserEnabled, authenticationTypeId, lastModifiedDate, lastModifiedById);
	}

	private TransactionAuthentication(final Integer portfolioType, final Integer transactionTypeId,
			PaymentType pamentTypeId, final BigDecimal amountGreaterThan, final Role secondAppUserRoleId,
			final boolean isSecondAppUserEnabled, final SecondaryAuthenticationService authenticationTypeId,
			final Date lastModifiedDate, final AppUser lastModifiedById) {
		this.portfolioType = portfolioType;
		this.transactionTypeId = transactionTypeId;
		this.paymentTypeId = pamentTypeId;
		this.amount = amountGreaterThan;
		this.secondAppUserRoleId = secondAppUserRoleId;
		this.isSecondAppUserEnabled = isSecondAppUserEnabled;
		this.authenticationTypeId = authenticationTypeId;
		this.lastModifiedDate = lastModifiedDate;
		this.lastModifiedById = lastModifiedById;
	}

	public Map<String, Object> update(final JsonCommand command, final AppUser modifiedUser,
			final SecondaryAuthenticationService secondaryAuthenticationType, final PaymentType paymentType) {
		final Map<String, Object> actualChanges = new LinkedHashMap<>(5);

		if (command.hasParameter(TransactionAuthenticationApiConstants.AMOUNT)) {
			if (command.isChangeInBigDecimalParameterNamed(TransactionAuthenticationApiConstants.AMOUNT, this.amount)) {
				final BigDecimal newValue = command
						.bigDecimalValueOfParameterNamed(TransactionAuthenticationApiConstants.AMOUNT);
				actualChanges.put(TransactionAuthenticationApiConstants.AMOUNT, newValue);
				this.amount = newValue;
			}
		}

		if (command.hasParameter(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE_ID)) {
			if (command.isChangeInIntegerParameterNamed(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE_ID,
					this.portfolioType)) {
				final Integer newValue = command
						.integerValueOfParameterNamed(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE_ID);
				actualChanges.put(TransactionAuthenticationApiConstants.PORTFOLIO_TYPE_ID, newValue);
				this.portfolioType = newValue;
			}
		}

		if (command.hasParameter(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID)) {
			if (command.isChangeInIntegerParameterNamed(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID,
					this.transactionTypeId)) {
				final Integer newValue = command
						.integerValueOfParameterNamed(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID);
				actualChanges.put(TransactionAuthenticationApiConstants.TRANSACTION_TYPE_ID, newValue);
				this.transactionTypeId = newValue;
			}
		}

		if (command.hasParameter(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID)) {
			if (command.isChangeInLongParameterNamed(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID,
					this.paymentTypeId.getId())) {
				final Integer newValue = command
						.integerValueOfParameterNamed(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID);
				actualChanges.put(TransactionAuthenticationApiConstants.PAYMENT_TYPE_ID, newValue);
				this.paymentTypeId = paymentType;
			}
		}

		if (command.hasParameter(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID)) {
			if (command.isChangeInLongParameterNamed(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID,
					this.authenticationTypeId.getId())) {
				final Long newValue = command
						.longValueOfParameterNamed(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID);
				actualChanges.put(TransactionAuthenticationApiConstants.AUTHENTICATION_TYPE_ID, newValue);
				this.authenticationTypeId = secondaryAuthenticationType;
			}
		}

		if (!actualChanges.isEmpty()) {
			this.lastModifiedDate = DateUtils.getLocalDateTimeOfTenant().toDate();
			this.lastModifiedById = modifiedUser;
		}
		return actualChanges;
	}

	private TransactionAuthentication() {
	}

	public Integer getPortfolioType() {
		return this.portfolioType;
	}

	public Integer getTransactionTypeId() {
		return this.transactionTypeId;
	}

	public Long getPaymentTypeId() {
		return this.paymentTypeId.getId();
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public Role getSecondAppUserRoleId() {
		return this.secondAppUserRoleId;
	}

	public boolean isSecondAppUserEnabled() {
		return this.isSecondAppUserEnabled;
	}

	public Long getAuthenticationTypeId() {
		return this.authenticationTypeId.getId();
	}

}
