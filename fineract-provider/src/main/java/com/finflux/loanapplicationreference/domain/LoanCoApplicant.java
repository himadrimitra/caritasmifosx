package com.finflux.loanapplicationreference.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "f_loan_coapplicants_mapping")
public class LoanCoApplicant  extends AbstractPersistable<Long> {

        @Column(name = "loan_application_reference_id", nullable = false)
        private Long loanApplicationReferenceId;

        @Column(name = "client_id", nullable = false)
        private Long clientId;

        protected LoanCoApplicant(){}

        private LoanCoApplicant(final Long loanApplicationReferenceId,
                final Long clientId) {
                this.loanApplicationReferenceId = loanApplicationReferenceId;
                this.clientId = clientId;
        }

        public static LoanCoApplicant instance(final Long loanApplicationReferenceId,
                final Long clientId){
                return new LoanCoApplicant(loanApplicationReferenceId, clientId);
        }

        public Long getLoanApplicationReferenceId(){
                return this.loanApplicationReferenceId;
        }

        public Long getClientId(){
                return this.clientId;
        }

}
