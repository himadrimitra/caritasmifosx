package org.apache.fineract.portfolio.client.domain;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ClientRecurringChargeRepository
		extends JpaRepository<ClientRecurringCharge, Long>, JpaSpecificationExecutor<ClientRecurringCharge> {

	@Query("from ClientRecurringCharge")
	Collection<ClientRecurringCharge> findClientRecurringCharges();

}
