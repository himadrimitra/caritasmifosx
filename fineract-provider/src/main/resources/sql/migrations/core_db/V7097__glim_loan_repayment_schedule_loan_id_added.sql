ALTER TABLE `f_loan_glim_repayment_schedule` DROP FOREIGN KEY `FK_f_loan_glim_rc_lrs_id`;

ALTER TABLE `f_loan_glim_repayment_schedule`
   ADD CONSTRAINT `FK_f_loan_glim_rc_lrs_id`
   FOREIGN KEY (`loan_repayment_schedule_id` )
   REFERENCES `m_loan_repayment_schedule` (`id` )
   ON DELETE CASCADE;