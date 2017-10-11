package org.apache.fineract.portfolio.client.domain;

import org.apache.fineract.organisation.office.domain.OrganisationCurrencyRepositoryWrapper;
import org.apache.fineract.portfolio.charge.exception.ChargeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientRecurringChargeRepositryWrapper {

    private final ClientRecurringChargeRepository repository;
    private final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepository;

    @Autowired
    public ClientRecurringChargeRepositryWrapper(ClientRecurringChargeRepository repository,
            final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepositoryWrapper) {
        this.repository = repository;
        this.organisationCurrencyRepository = organisationCurrencyRepositoryWrapper;
    }

    public ClientRecurringCharge findOneWithNotFoundDetection(final Long id) {
        final ClientRecurringCharge clientRecurringCharge = this.repository.findOne(id);
        if (clientRecurringCharge == null) { throw new ChargeNotFoundException(id); }
        // enrich Client charge with details of Organizational currency
        clientRecurringCharge.setCurrency(
                organisationCurrencyRepository.findOneWithNotFoundDetection(clientRecurringCharge.getCharge().getCurrencyCode()));
        return clientRecurringCharge;
    }

    public void save(final ClientRecurringCharge clientRecurringCharge) {
        this.repository.save(clientRecurringCharge);
    }

    public void saveAndFlush(final ClientRecurringCharge clientRecurringCharge) {
        this.repository.saveAndFlush(clientRecurringCharge);
    }

    public void delete(final ClientRecurringCharge clientRecurringCharge) {
        this.repository.delete(clientRecurringCharge);
        this.repository.flush();
    }

}
