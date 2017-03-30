package com.finflux.mandates.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MandatesProcessingApiConstants {

        public static final String RESOURCE_NAME = "mandates";

        public static final String officeId = "officeId";
        public static final String includeChildOffices = "includeChildOffices";
        public static final String includeMandateScans = "includeMandateScans";
        public static final String paymentDueStartDate = "paymentDueStartDate";
        public static final String paymentDueEndDate = "paymentDueEndDate";
        public static final String includeFailedTransactions = "includeFailedTransactions";

        public static final Set<String> ALLOWED_REQUEST_PARAMS_MANDATES_DOWNLOAD = new HashSet<>(Arrays.asList(officeId, includeChildOffices,
                includeMandateScans, "locale", "dateFormat"));

        public static final Set<String> ALLOWED_REQUEST_PARAMS_TRANSACTIONS_DOWNLOAD = new HashSet<>(Arrays.asList(officeId, includeChildOffices,
                paymentDueStartDate, paymentDueEndDate, includeFailedTransactions, "locale", "dateFormat"));
}
