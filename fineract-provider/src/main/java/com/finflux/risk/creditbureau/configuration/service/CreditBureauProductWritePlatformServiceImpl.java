package com.finflux.risk.creditbureau.configuration.service;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProduct;
import com.finflux.risk.creditbureau.configuration.domain.CreditBureauProductRepository;

@Service
public class CreditBureauProductWritePlatformServiceImpl implements CreditBureauProductWritePlatformService {

    private final PlatformSecurityContext context;
    private final CreditBureauProductRepository creditBureauProductRepository;

    @Autowired
    public CreditBureauProductWritePlatformServiceImpl(final PlatformSecurityContext context,
            final CreditBureauProductRepository creditBureauProductRepository) {
        this.context = context;
        this.creditBureauProductRepository = creditBureauProductRepository;
    }

    @Override
    public CommandProcessingResult activateCreditBureau(Long creditBureauId) {
        this.context.authenticatedUser();
        final CreditBureauProduct creditBureauProduct = this.creditBureauProductRepository.findOne(creditBureauId);
        creditBureauProduct.activate();
        this.creditBureauProductRepository.saveAndFlush(creditBureauProduct);
        return new CommandProcessingResultBuilder().withEntityId(creditBureauProduct.getId()).build();
    }

    @Override
    public CommandProcessingResult deactivateCreditBureau(Long creditBureauId) {
        this.context.authenticatedUser();
        final CreditBureauProduct creditBureauProduct = this.creditBureauProductRepository.findOne(creditBureauId);
        creditBureauProduct.deactivate();
        this.creditBureauProductRepository.saveAndFlush(creditBureauProduct);
        return new CommandProcessingResultBuilder().withEntityId(creditBureauProduct.getId()).build();
    }
}
