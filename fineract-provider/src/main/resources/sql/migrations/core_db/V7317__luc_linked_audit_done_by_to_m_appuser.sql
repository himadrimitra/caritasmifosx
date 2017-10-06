ALTER TABLE `f_loan_utilization_check`
	DROP FOREIGN KEY `FK_f_loan_utilization_check_audit_done_by`;
ALTER TABLE `f_loan_utilization_check`
	ADD CONSTRAINT `FK_f_loan_utilization_check_audit_done_by` FOREIGN KEY (`audit_done_by`) REFERENCES `m_appuser` (`id`);