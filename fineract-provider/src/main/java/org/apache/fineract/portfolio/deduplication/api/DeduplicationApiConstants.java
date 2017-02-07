package org.apache.fineract.portfolio.deduplication.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DeduplicationApiConstants {
        public static final String RESOURCE_NAME = "CLIENTDEDUPWEIGHTAGES";

        public static final String legalForm = "legalForm";
        public static final String firstnameExact = "firstnameExact";
        public static final String firstnameLike = "firstnameLike";
        public static final String middlenameExact = "middlenameExact";
        public static final String middlenameLike = "middlenameLike";
        public static final String lastnameExact = "lastnameExact";
        public static final String lastnameLike = "lastnameLike";
        public static final String fullnameExact = "fullnameExact";
        public static final String fullnameLike = "fullnameLike";
        public static final String mobileNo = "mobileNo";
        public static final String dateOfBirth = "dateOfBirth";
        public static final String genderCvId = "genderCvId";
        public static final String incorpNo = "incorpNo";
        public static final String clientIdentifier = "clientIdentifier";

        public static final Set<String> LEGAL_FORM_PERSON_PARAMS = new HashSet<>(Arrays.asList("locale",legalForm, firstnameExact,
         firstnameLike, middlenameExact, middlenameLike,lastnameExact,lastnameLike,mobileNo,dateOfBirth,genderCvId,clientIdentifier));

        public static final Set<String> LEGAL_FORM_ENTITY_PARAMS = new HashSet<>(Arrays.asList("locale",legalForm,fullnameExact,fullnameLike,
                mobileNo,incorpNo,clientIdentifier));
}
