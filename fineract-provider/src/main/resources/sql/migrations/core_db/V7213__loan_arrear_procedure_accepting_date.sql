DROP PROCEDURE IF EXISTS `ArrearsAging`;


DELIMITER $$
CREATE PROCEDURE ArrearsAging(IN `currentdate` VARCHAR(50))

BEGIN
declare maxloans bigint(20) default 1;
declare l int(10) default 0;

declare outstanding DECIMAL(19,6) default 0;

CREATE TABLE if not exists `arrears_amounts` (
	`loan_id` BIGINT(20) NOT NULL,
	`paid_principal` DECIMAL(19,6) NULL DEFAULT NULL,
	`paid_interest` DECIMAL(19,6) NULL DEFAULT NULL,
	`paid_fees` DECIMAL(19,6) NULL DEFAULT NULL,
	`paid_penalty` DECIMAL(19,6) NULL DEFAULT NULL,
	`principal_overdue` DECIMAL(19,6) NULL DEFAULT NULL,
	`interest_overdue` DECIMAL(19,6) NULL DEFAULT NULL,
	`fees_overdue` DECIMAL(19,6) NULL DEFAULT NULL,
	`penalties_overdue` DECIMAL(19,6) NULL DEFAULT NULL,
	`total_overdue` DECIMAL(19,6) NULL DEFAULT NULL,
	PRIMARY KEY (`loan_id`),
	INDEX `loan_id` (`loan_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;
CREATE TABLE if not exists `arrears_loans` (
	`loan_id` BIGINT(20) NOT NULL,
	PRIMARY KEY (`loan_id`),
	INDEX `loan_id` (`loan_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE if not exists `arrears_maxid` (
	`loan_id` BIGINT(20) NOT NULL,
	`history_max_id` INT(10) NULL DEFAULT NULL,
	PRIMARY KEY (`loan_id`),
	INDEX `loan_id` (`loan_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE if not exists `arrears_paid` (
	`loan_id` BIGINT(20) NULL DEFAULT NULL,
	`duedate` DATE NULL DEFAULT NULL,
	`principal` DECIMAL(19,6) NULL DEFAULT NULL,
	`interest` DECIMAL(19,6) NULL DEFAULT NULL,
	`fee` DECIMAL(19,6) NULL DEFAULT NULL,
	`penalty` DECIMAL(19,6) NULL DEFAULT NULL
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

select count(*) into maxloans  from 
(select ml.id as loanId FROM m_loan ml
INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id
inner join m_product_loan_recalculation_details prd on prd.product_id = ml.product_id and prd.arrears_based_on_original_schedule = 1 
WHERE ml.loan_status_id = 300  
and mr.completed_derived is false  
and mr.duedate < SUBDATE(currentdate,INTERVAL  ifnull(ml.grace_on_arrears_ageing,0) day) 
group by ml.id
)a;

truncate arrears_loans;
truncate arrears_maxid;
truncate arrears_paid;
truncate arrears_amounts;
insert into arrears_loans
select SQL_CALC_FOUND_ROWS ml.id as loanId FROM m_loan ml
INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id
inner join m_product_loan_recalculation_details prd on prd.product_id = ml.product_id and prd.arrears_based_on_original_schedule = 1 
WHERE ml.loan_status_id = 300  
and mr.completed_derived is false  
and mr.duedate < SUBDATE(currentdate,INTERVAL  ifnull(ml.grace_on_arrears_ageing,0) day) 
group by ml.id;

insert into arrears_maxid
select h.id,h.repayment_history_version from m_loan h
inner join arrears_loans al on al.loan_id = h.id
group by h.id;

insert into arrears_amounts
select a.al,ifnull(b.bp,0),ifnull(b.bi,0),ifnull(b.bf,0),ifnull(b.bpe,0),
case when ifnull(a.ap,0)-ifnull(b.bp,0) > 0 then ifnull(a.ap,0)-ifnull(b.bp,0) else 0 end as principle_arrear ,
case when ifnull(a.ai,0)-ifnull(b.bi,0)  > 0 then ifnull(a.ai,0)-ifnull(b.bi,0) else 0 end as interest_arrear,
case when ifnull(a.af,0)-ifnull(b.bf,0)  > 0 then ifnull(a.af,0)-ifnull(b.bf,0) else 0 end as fees_arrear,
case when ifnull(a.ape,0)-ifnull(b.bpe,0)  > 0 then ifnull(a.ape,0)-ifnull(b.bpe,0) else 0 end as penalty_arrear,
case when ifnull(a.ap,0)-ifnull(b.bp,0) > 0 then ifnull(a.ap,0)-ifnull(b.bp,0) else 0 end  + 
case when ifnull(a.ai,0)-ifnull(b.bi,0)  > 0 then ifnull(a.ai,0)-ifnull(b.bi,0) else 0 end  +
case when ifnull(a.af,0)-ifnull(b.bf,0)  > 0 then ifnull(a.af,0)-ifnull(b.bf,0) else 0 end  +
case when ifnull(a.ape,0)-ifnull(b.bpe,0)  > 0 then ifnull(a.ape,0)-ifnull(b.bpe,0) else 0 end as total_overdue

from (select h.loan_id al,
ifnull(sum(h.principal_amount),0)ap,
ifnull(sum(h.interest_amount),0)ai,
ifnull(sum(h.fee_charges_amount),0)af,
ifnull(sum(h.penalty_charges_amount),0)ape from m_loan_repayment_schedule_history h
inner join m_loan ml on ml.id = h.loan_id
inner join arrears_maxid am on am.loan_id = h.loan_id and am.history_max_id = h.version
and h.duedate < SUBDATE(currentdate,INTERVAL  ifnull(ml.grace_on_arrears_ageing,0) day)
where ml.loan_status_id = 300
group by h.loan_id)a
left join
(select ifnull(am.loan_id,0) bl,ifnull(sum(t.principal_portion_derived),0) bp
,ifnull(sum(t.interest_portion_derived),0) bi,
ifnull(sum(t.fee_charges_portion_derived),0)bf,
ifnull(sum(t.penalty_charges_portion_derived),0)bpe  from arrears_maxid am
left join m_loan_transaction  t on am.loan_id = t.loan_id
and t.transaction_type_enum = 2 and t.is_reversed = 0
group by t.loan_id)b on a.al = b.bl;

INSERT INTO m_loan_arrears_aging(`loan_id`,`principal_overdue_derived`,`interest_overdue_derived`,
`fee_charges_overdue_derived`,`penalty_charges_overdue_derived`,`total_overdue_derived`)
select a.loan_id,a.principal_overdue,a.interest_overdue,a.fees_overdue,a.penalties_overdue,a.total_overdue from arrears_amounts a
where a.total_overdue > 0
and a.principal_overdue > 0;
insertloop : LOOP
truncate arrears_loans;
truncate arrears_maxid;
insert into arrears_loans
select SQL_CALC_FOUND_ROWS ml.id as loanId FROM m_loan ml
INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id
inner join m_product_loan_recalculation_details prd on prd.product_id = ml.product_id and prd.arrears_based_on_original_schedule = 1 
WHERE ml.loan_status_id = 300  
and mr.completed_derived is false  
and mr.duedate < SUBDATE(currentdate,INTERVAL  ifnull(ml.grace_on_arrears_ageing,0) day) 
group by ml.id
limit l,1000;
insert into arrears_maxid
select h.id,h.repayment_history_version from m_loan h
inner join arrears_loans al on al.loan_id = h.id
group by h.id;


insert into arrears_paid
select
r1.loan_id loan_id,
r1.duedate duedate,
sum(r.principal_amount) sum_principle,
sum(r.interest_amount) sum_interest,
sum(r.fee_charges_amount) sum_fee,
sum(r.penalty_charges_amount) sum_penalty
from m_loan_repayment_schedule_history r
inner join m_loan_repayment_schedule_history r1 on r.loan_id = r1.loan_id
inner join arrears_maxid am on am.loan_id = r1.loan_id and am.loan_id = r.loan_id and am.history_max_id = r.version and am.history_max_id = r1.version
where r.loan_id in (select loan_id from arrears_loans)
and r.duedate <= r1.duedate
group by r1.loan_id,r1.duedate
order by 1,2;

update m_loan_arrears_aging a
inner join (select a.loan_id,min(a.mdate)odate from (
select p.loan_id,min(p.duedate) mdate from arrears_paid p
inner join arrears_amounts aa on aa.loan_id = p.loan_id
where p.principal > aa.paid_principal
group by p.loan_id

)a
group by a.loan_id)ad on ad.loan_id = a.loan_id
set a.overdue_since_date_derived = ad.odate where a.loan_id = ad.loan_id;

if l = 0 then
set l = 1001;
else
set l = l+1000;

end if;

if l > maxloans then
leave insertloop;
end if;
end loop insertloop;
END $$
DELIMITER;