package com.finflux.risk.creditbureau.configuration.domain;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "f_creditbureau_product")
public class CreditBureauProduct extends AbstractPersistable<Long> {

    private String name;

    private String product;

    private String country;

    @Column(name = "implementation_key")
    private String implementationKey;

    @Column(name = "is_active")
    private boolean isActive;

    public CreditBureauProduct(String name, String product, String country, String implementationKey, boolean isActive) {
        this.name = name;
        this.product = product;
        this.country = country;
        this.implementationKey = implementationKey;
        this.isActive = isActive;
    }

    public CreditBureauProduct() {

    }

    public static CreditBureauProduct fromJson(final JsonCommand command) {
        final String tname = command.stringValueOfParameterNamed("name");
        final String tproduct = command.stringValueOfParameterNamed("product");
        final String tcountry = command.stringValueOfParameterNamed("country");
        final String timplementationKey = command.stringValueOfParameterNamed("implementationKey");
        final boolean tis_active = command.booleanPrimitiveValueOfParameterNamed("isActive");
        return new CreditBureauProduct(tname, tproduct, tcountry, timplementationKey, tis_active);
    }

    public void activate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource("creditbureau" + ".activate");

        final boolean currentStatus = this.isActive;
        if (currentStatus) {
            baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode("not.in.pending.for.activation.state");
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
        }
        this.isActive = true;
    }

    public void deactivate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("creditbureau"
                + ".deactivate");

        final boolean currentStatus = this.isActive;
        if (!currentStatus) {
            baseDataValidator.reset().failWithCodeNoParameterAddedToErrorCode("not.in.pending.for.activation.state");
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
        }
        this.isActive = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImplementationKey() {
        return implementationKey;
    }

    public void setImplementationKey(String implementationKey) {
        this.implementationKey = implementationKey;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
