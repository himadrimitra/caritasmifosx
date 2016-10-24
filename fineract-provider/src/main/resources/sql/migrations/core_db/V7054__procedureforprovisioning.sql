DELIMITER $$

CREATE PROCEDURE `loanprovisioning`(
	IN `rundate` DATE

)
BEGIN

declare loanid BIGINT(20) default 0;

declare loancount INT default 0;

declare minduedate date default 0;

drop table if exists loanprovisioningentries;

drop table if exists loanprovisioningloans;

CREATE TABLE `loanprovisioningloans` (
	`loan_id` BIGINT(20) NOT NULL,
	`duedate` DATE NOT NULL,
	UNIQUE INDEX `loanprovisioningloansIndex 1` (`loan_id`, `duedate`)
);


CREATE TABLE `loanprovisioningentries` (
	`office_id` BIGINT(20) NOT NULL,
	`loan_type_enum` INT(11) NOT NULL,
	`criteriaid` BIGINT(20) NOT NULL,
	`product_id` BIGINT(20) NOT NULL,
	`currency_code` VARCHAR(50) NOT NULL,
	`numberofdaysoverdue` BIGINT(20) NOT NULL,
	`category_id` BIGINT(20) NOT NULL,
	`provision_percentage` BIGINT(20) NOT NULL,
	`loan_id` BIGINT(20) NOT NULL,
	`dueDate` DATE NOT NULL,
	`outstandingAsPerType` DECIMAL(10,6) NOT NULL,
	`liability_account` BIGINT(20) NOT NULL,
	`expense_account` BIGINT(20) NOT NULL,
	`provisioningAmountType` INT(11) NOT NULL
);


insert into loanprovisioningloans (select distinct ml.id,min(mlrs.duedate)
from 
m_loan ml 
join m_loan_repayment_schedule mlrs on ml.id = mlrs.loan_id
join m_product_loan mpl on ml.product_id = mpl.id
join m_loanproduct_provisioning_mapping mlpm on mlpm.product_id = mpl.id
where 
((
SELECT SUM(sch1.principal_amount)
FROM m_loan_repayment_schedule AS sch1
WHERE sch1.loan_id = mlrs.loan_id and sch1.duedate <= mlrs.duedate) > (
SELECT IFNULL(SUM(trans.principal_portion_derived),0)
FROM m_loan_transaction AS trans
WHERE trans.transaction_date <=rundate AND trans.is_reversed = 0 
AND trans.transaction_type_enum IN (2,6) 
AND trans.loan_id = mlrs.loan_id))
AND
ml.loan_status_id NOT IN (100,200, 400, 500, 602)
group by mlrs.loan_id);

insert into  loanprovisioningloans (select distinct ml.id,ml.disbursedon_date
from 
m_loan ml 
join m_product_loan mpl on ml.product_id = mpl.id
join m_loanproduct_provisioning_mapping mlpm on mlpm.product_id = mpl.id
where 
(rundate >= ml.disbursedon_date and rundate < (
select min(mlrs.duedate) from m_loan_repayment_schedule mlrs
where mlrs.loan_id = ml.id
))
AND
ml.loan_status_id NOT IN (100,200, 400, 500, 602)
group by ml.id);

select count(*) into loancount from loanprovisioningloans;

loopone : loop

select lpl.loan_id, lpl.duedate into loanid,minduedate from loanprovisioningloans lpl limit 1;

if minduedate = (select ml.disbursedon_date from m_loan ml where ml.id = loanid) then

set rundate = minduedate;

end if;

insert into loanprovisioningentries (
SELECT distinct IF(l.loan_type_enum=1, mc.office_id, mg.office_id) AS office_id, 
l.loan_type_enum, 
pcd.criteria_id AS criteriaid, 
l.product_id, 
l.currency_code, GREATEST(DATEDIFF(rundate, minduedate),0) AS numberofdaysoverdue, 
pcd.category_id, pcd.provision_percentage, 
sch.loan_id AS loan_id, minduedate AS dueDate, 
if(l.disbursedon_date = minduedate, l.principal_disbursed_derived,((
SELECT SUM(IFNULL(sch1.principal_amount,0))
FROM m_loan_repayment_schedule AS sch1
WHERE sch1.loan_id = sch.loan_id) - (
SELECT IFNULL(SUM(IFNULL(trans.principal_portion_derived,0)),0)
FROM m_loan_transaction AS trans
WHERE trans.transaction_date <=rundate
AND trans.is_reversed = 0 
AND trans.transaction_type_enum IN (2,6) 
AND trans.loan_id = sch.loan_id))) AS outstandingAsPerType, 
pcd.liability_account,
 pcd.expense_account, 
 criteria.provisioning_amount_type AS provisioningAmountType
FROM m_loan_repayment_schedule AS sch
JOIN m_loan AS l ON l.id = sch.loan_id
JOIN m_loanproduct_provisioning_mapping lpm ON lpm.product_id = l.product_id
JOIN m_provisioning_criteria_definition pcd ON pcd.criteria_id = lpm.criteria_id AND (pcd.min_age <= GREATEST(DATEDIFF(rundate,minduedate),0) AND GREATEST(DATEDIFF(rundate,minduedate),0) <= pcd.max_age)
JOIN m_provisioning_criteria criteria ON pcd.criteria_id = criteria.id AND pcd.criteria_id IS NOT NULL
LEFT JOIN m_client mc ON mc.id = l.client_id
LEFT JOIN m_group mg ON mg.id = l.group_id
where sch.loan_id = loanid
group by sch.loan_id);

delete from loanprovisioningloans where loan_id = loanid;

select count(*) into loancount from loanprovisioningloans;

if loancount <> 0 then
set loanid = 0;
set minduedate = 0;
iterate loopone;
end if;

leave loopone;

end loop;
END$$
DELIMITER ;