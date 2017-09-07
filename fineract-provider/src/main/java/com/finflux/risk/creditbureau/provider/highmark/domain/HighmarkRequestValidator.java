/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package com.finflux.risk.creditbureau.provider.highmark.domain;

import org.springframework.stereotype.Component;

import com.finflux.risk.creditbureau.provider.exception.MandatoryDataNotFoundException;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.ADDRESSSEGMENT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.APPLICANTSEGMENT;
import com.finflux.risk.creditbureau.provider.highmark.xsd.request.REQUESTREQUESTFILE;

@Component
public class HighmarkRequestValidator {
    
    public void validateEnquiryRequest(REQUESTREQUESTFILE request) {
        APPLICANTSEGMENT applicantSegment=request.getINQUIRY().getAPPLICANTSEGMENT();
        ADDRESSSEGMENT addressSegment=request.getINQUIRY().getADDRESSSEGMENT();
        if(applicantSegment.getDOB()==null) {
            throw new MandatoryDataNotFoundException("error.msg.mandatory.data.dob.not.found","CB Request cannot be processed as DOB for applicant:"+applicantSegment.getAPPLICANTNAME().getNAME1()+" can not be found.",applicantSegment.getAPPLICANTNAME().getNAME1());
        }
        if(applicantSegment.getIDS()==null || applicantSegment.getIDS().getID().isEmpty()) {
            if((applicantSegment.getRELATIONS()==null || applicantSegment.getRELATIONS().getRELATION().isEmpty()) && applicantSegment.getKEYPERSON()==null && applicantSegment.getNOMINEE()==null) {
                throw new MandatoryDataNotFoundException("error.msg.mandatory.data.id.spouse.father.not.found","CB Request cannot be processed as any one of Relative or ID for applicant:"+applicantSegment.getAPPLICANTNAME().getNAME1()+" can not be found.",applicantSegment.getAPPLICANTNAME().getNAME1());
            }
        }
        if(addressSegment.getADDRESS().isEmpty()) {
            throw new MandatoryDataNotFoundException("error.msg.mandatory.data.address.not.found","CB Request cannot be processed as address for applicant:"+applicantSegment.getAPPLICANTNAME().getNAME1()+" can not be found.",applicantSegment.getAPPLICANTNAME().getNAME1());
        }
    }
}
