package org.apache.fineract.portfolio.loanproduct.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;

public enum ClientProfileType {

    INVALID(0, "clientProfileType.invalid"), //
    LEGAL_FORM(1, "clientProfileType.legal.form"), //
    CLIENT_TYPE(2, "clientProfileType.client.type"), //
    CLIENT_CLASSIFICATION(3, "clientProfileType.client.classification");

    private final Integer value;
    private final String code;

    private ClientProfileType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static ClientProfileType fromInt(final Integer type) {
        ClientProfileType clientProfileType = ClientProfileType.INVALID;
        if (type != null) {
            switch (type) {
                case 1:
                    clientProfileType = ClientProfileType.LEGAL_FORM;
                break;
                case 2:
                    clientProfileType = ClientProfileType.CLIENT_TYPE;
                break;
                case 3:
                    clientProfileType = ClientProfileType.CLIENT_CLASSIFICATION;
                break;
            }
        }
        return clientProfileType;
    }

    public static Collection<EnumOptionData> typeOptions() {
        final Collection<EnumOptionData> typeOptions = new ArrayList<>();
        for (final ClientProfileType enumType : values()) {
            final EnumOptionData enumOptionData = type(enumType.getValue());
            if (enumOptionData != null) {
                typeOptions.add(enumOptionData);
            }
        }
        return typeOptions;
    }

    public static EnumOptionData type(final int id) {
        return type(ClientProfileType.fromInt(id));
    }

    public static EnumOptionData type(final ClientProfileType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case LEGAL_FORM:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), LoanProductConstants.LEGAL_FORM);
            break;
            case CLIENT_TYPE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), LoanProductConstants.CLIENT_TYPE);
            break;
            case CLIENT_CLASSIFICATION:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), LoanProductConstants.CLIENT_CLASSIFICATION);
            break;
            default:
            break;

        }
        return optionData;
    }

    public boolean isLegalForm() {
        return this.value.equals(ClientProfileType.LEGAL_FORM.getValue());
    }

    public boolean isClientType() {
        return this.value.equals(ClientProfileType.CLIENT_TYPE.getValue());
    }

    public boolean isClientClassification() {
        return this.value.equals(ClientProfileType.CLIENT_CLASSIFICATION.getValue());
    }
}
