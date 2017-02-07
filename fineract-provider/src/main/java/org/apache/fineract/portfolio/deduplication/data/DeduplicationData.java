package org.apache.fineract.portfolio.deduplication.data;

public class DeduplicationData {
        private final Integer legalForm;
        private final Integer firstnameExact;
        private final Integer firstnameLike;
        private final Integer middlenameExact;
        private final Integer middlenameLike;
        private final Integer lastnameExact;
        private final Integer lastnameLike;
        private final Integer fullnameExact;
        private final Integer fullnameLike;
        private final Integer mobileNo;
        private final Integer dateOfBirth;
        private final Integer genderCvId;
        private final Integer incorpNo;
        private final Integer clientIdentifier;

        public DeduplicationData(final Integer legalForm,
                final Integer firstnameExact,
                final Integer firstnameLike,
                final Integer middlenameExact,
                final Integer middlenameLike,
                final Integer lastnameExact,
                final Integer lastnameLike,
                final Integer fullnameExact,
                final Integer fullnameLike,
                final Integer mobileNo,
                final Integer dateOfBirth,
                final Integer genderCvId,
                final Integer incorpNo,
                final Integer clientIdentifier){

                this.legalForm = legalForm;
                this.firstnameExact = firstnameExact;
                this.firstnameLike = firstnameLike;
                this.middlenameExact = middlenameExact;
                this.middlenameLike = middlenameLike;
                this.lastnameExact = lastnameExact;
                this.lastnameLike = lastnameLike;
                this.fullnameExact = fullnameExact;
                this.fullnameLike = fullnameLike;
                this.mobileNo = mobileNo;
                this.dateOfBirth = dateOfBirth;
                this.genderCvId = genderCvId;
                this.incorpNo = incorpNo;
                this.clientIdentifier = clientIdentifier;
        }
}
