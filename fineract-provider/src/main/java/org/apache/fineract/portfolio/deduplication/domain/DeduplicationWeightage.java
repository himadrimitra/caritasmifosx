package org.apache.fineract.portfolio.deduplication.domain;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.deduplication.api.DeduplicationApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "f_client_dedup_weightage")
public class DeduplicationWeightage extends AbstractPersistable<Long>{

        @Column(name = "legal_form", nullable = false)
        public Integer legalForm;

        @Column(name = "firstname_exact", nullable = true)
        public Integer firstnameExact;

        @Column(name = "firstname_like", nullable = true)
        public Integer firstnameLike;

        @Column(name = "middlename_exact", nullable = true)
        public Integer middlenameExact;

        @Column(name = "middlename_like", nullable = true)
        public Integer middlenameLike;

        @Column(name = "lastname_exact", nullable = true)
        public Integer lastnameExact;

        @Column(name = "lastname_like", nullable = true)
        public Integer lastnameLike;

        @Column(name = "fullname_exact", nullable = true)
        public Integer fullnameExact;

        @Column(name = "fullname_like", nullable = true)
        public Integer fullnameLike;

        @Column(name = "mobile_no", nullable = true)
        public Integer mobileNo;

        @Column(name = "date_of_birth", nullable = true)
        public Integer dateOfBirth;

        @Column(name = "gender_cv_id", nullable = true)
        public Integer genderCvId;

        @Column(name = "incorp_no", nullable = true)
        public Integer incorpNo;

        @Column(name = "client_identifier", nullable = true)
        public Integer clientIdentifier;

        protected DeduplicationWeightage(){}

        public Map<String, Object> update(JsonCommand command){
                Map<String, Object> changes = new HashMap<>();
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.firstnameExact, this.firstnameExact)){
                        this.firstnameExact = command.integerValueOfParameterNamed(DeduplicationApiConstants.firstnameExact);
                        changes.put(DeduplicationApiConstants.firstnameExact, this.firstnameExact);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.firstnameLike, this.firstnameLike)){
                        this.firstnameLike = command.integerValueOfParameterNamed(DeduplicationApiConstants.firstnameLike);
                        changes.put(DeduplicationApiConstants.firstnameLike, this.firstnameLike);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.middlenameExact, this.middlenameExact)){
                        this.middlenameExact = command.integerValueOfParameterNamed(DeduplicationApiConstants.middlenameExact);
                        changes.put(DeduplicationApiConstants.middlenameExact, this.middlenameExact);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.middlenameLike, this.middlenameLike)){
                        this.middlenameLike = command.integerValueOfParameterNamed(DeduplicationApiConstants.middlenameLike);
                        changes.put(DeduplicationApiConstants.middlenameLike, this.middlenameLike);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.lastnameExact, this.lastnameExact)){
                        this.lastnameExact = command.integerValueOfParameterNamed(DeduplicationApiConstants.lastnameExact);
                        changes.put(DeduplicationApiConstants.lastnameExact, this.lastnameExact);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.lastnameLike, this.lastnameLike)){
                        this.lastnameLike = command.integerValueOfParameterNamed(DeduplicationApiConstants.lastnameLike);
                        changes.put(DeduplicationApiConstants.lastnameLike, this.lastnameLike);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.fullnameExact, this.fullnameExact)){
                        this.fullnameExact = command.integerValueOfParameterNamed(DeduplicationApiConstants.fullnameExact);
                        changes.put(DeduplicationApiConstants.fullnameExact, this.fullnameExact);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.fullnameLike, this.fullnameLike)){
                        this.fullnameLike = command.integerValueOfParameterNamed(DeduplicationApiConstants.fullnameLike);
                        changes.put(DeduplicationApiConstants.fullnameLike, this.fullnameLike);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.mobileNo, this.mobileNo)){
                        this.mobileNo = command.integerValueOfParameterNamed(DeduplicationApiConstants.mobileNo);
                        changes.put(DeduplicationApiConstants.mobileNo, this.mobileNo);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.dateOfBirth, this.dateOfBirth)){
                        this.dateOfBirth = command.integerValueOfParameterNamed(DeduplicationApiConstants.dateOfBirth);
                        changes.put(DeduplicationApiConstants.dateOfBirth, this.dateOfBirth);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.genderCvId, this.genderCvId)){
                        this.genderCvId = command.integerValueOfParameterNamed(DeduplicationApiConstants.genderCvId);
                        changes.put(DeduplicationApiConstants.genderCvId, this.genderCvId);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.incorpNo, this.incorpNo)){
                        this.incorpNo = command.integerValueOfParameterNamed(DeduplicationApiConstants.incorpNo);
                        changes.put(DeduplicationApiConstants.incorpNo, this.incorpNo);
                }
                if(command.isChangeInIntegerParameterNamed(DeduplicationApiConstants.clientIdentifier, this.clientIdentifier)){
                        this.clientIdentifier = command.integerValueOfParameterNamed(DeduplicationApiConstants.clientIdentifier);
                        changes.put(DeduplicationApiConstants.clientIdentifier, this.clientIdentifier);
                }
                return changes;
        }

}
