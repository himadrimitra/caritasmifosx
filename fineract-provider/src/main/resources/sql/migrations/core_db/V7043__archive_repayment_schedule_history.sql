create TABLE archive_m_loan_repayment_schedule_history like m_loan_repayment_schedule_history;
DROP PROCEDURE IF EXISTS archive_m_loan_repayment_schedule_history;
DELIMITER $$
CREATE PROCEDURE archive_m_loan_repayment_schedule_history()

BEGIN
DECLARE n INT default 0;
DECLARE i INT DEFAULT 0;
DECLARE loanid BIGINT default 0;
declare loanversion int default 0;

drop table if exists historytemp;

CREATE TABLE if not exists historytemp(
	`id` BIGINT(20) NOT NULL,
	`version` BIGINT(20) NOT NULL)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;
	
	
insert into historytemp (SELECT ml.id,ml.repayment_history_version from m_loan ml where ml.loan_status_id in (600,601,602));

SELECT COUNT(*) FROM historytemp INTO n;

loop_label: LOOP

select ht.id, ht.version into loanid,loanversion from historytemp ht limit 1;

insert into archive_m_loan_repayment_schedule_history 
select h.* from m_loan_repayment_schedule_history h where h.loan_id = loanid and h.version < loanversion;
DELETE h from m_loan_repayment_schedule_history h where h.loan_id = loanid and h.version < loanversion;
delete from historytemp where id = loanid;
set i = i+1;
if(i<=n) then
ITERATE loop_label;
end if;
LEAVE loop_label;
end LOOP loop_label;

END $$
DELIMITER ; 
	