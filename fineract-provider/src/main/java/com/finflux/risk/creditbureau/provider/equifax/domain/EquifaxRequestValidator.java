/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package com.finflux.risk.creditbureau.provider.equifax.domain;

import org.springframework.stereotype.Component;

import com.finflux.risk.creditbureau.provider.equifax.xsd.RequestBodyType;
import com.finflux.risk.creditbureau.provider.exception.MandatoryDataNotFoundException;

@Component
public class EquifaxRequestValidator {

    public void validateEnquiryRequest(RequestBodyType request) {
        if (request.getFamilyDetails() == null) { 
            throw new MandatoryDataNotFoundException("error.msg.mandatory.data.familydetails.not.found",
                        "CB Request cannot be processed as family details for applicant:" + request.getFirstName() + " can not be found.",
                        request.getFirstName()); 
                }
        if (request.getInquiryAddresses() == null) { 
            throw new MandatoryDataNotFoundException("error.msg.mandatory.data.address.not.found",
                "CB Request cannot be processed as address details for applicant:" + request.getFirstName() + " can not be found.",
                request.getFirstName()); 
        }
        if (request.getDOB() == null) { throw new MandatoryDataNotFoundException("error.msg.mandatory.data.dob.not.found",
                "CB Request cannot be processed as date of birth for applicant:" + request.getFirstName() + " can not be found.",
                request.getFirstName()); 
        }
        if (request.getInquiryPhones() == null) { throw new MandatoryDataNotFoundException("error.msg.mandatory.phone.not.found",
                "CB Request cannot be processed as phone number for applicant:" + request.getFirstName() + " can not be found.",
                request.getFirstName()); 
        }
        if(!isAtleastOneIdentifierPresent(request)) {
            throw new MandatoryDataNotFoundException("error.msg.mandatory.identifier.not.found",
                    "CB Request cannot be processed as ateleast one indetifier should be present:",
                    request.getFirstName()); 
        }
    }
    
    private boolean isAtleastOneIdentifierPresent(final RequestBodyType request) {
        boolean isPresent = false;
        if (request.getPANId() != null || request.getPassportId() != null || request.getVoterId() != null
                || request.getNationalIdCard() != null || request.getRationCard() != null || request.getDriverLicense() != null
                || request.getAdditionalId1() != null || request.getAdditionalId2() != null) {
            isPresent = true;
        }
        return isPresent;
    }
}
