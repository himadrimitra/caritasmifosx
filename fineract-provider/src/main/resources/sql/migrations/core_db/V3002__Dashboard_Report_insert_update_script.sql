INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('BranchOverView', 'Table', NULL, 'Client', NULL, 'Branch overview dashboard', 0, 1);


INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('LoanPortfolio', 'Table', NULL, 'Loan', NULL, 'Loan Portfolio dashboard', 0, 1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) VALUES ( (select sr.id from stretchy_report sr where sr.report_name='BranchOverView'), (select sp.id from stretchy_parameter sp where sp.parameter_name='OfficeIdSelectOne'), 'Branch');

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) VALUES ( (select sr.id from stretchy_report sr where sr.report_name='LoanPortfolio'), (select sp.id from stretchy_parameter sp where sp.parameter_name='OfficeIdSelectOne'), 'Branch');

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('dashboard', 'READ_BranchOverView', 'BranchOverView', 'READ', 0);

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('dashboard', 'READ_LoanPortFolio', 'LoanPortFolio', 'READ', 0);

update stretchy_report set report_sql='select "Loan Officer" is_loan_officer  ,count(stf.office_id)
from m_staff stf
where stf.office_id in (
select of.id from m_office of where 
 of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
) and stf.is_loan_officer=1 and stf.is_active=1

union

select "Staff" office_id,count(office_id)
from m_staff stf
where stf.office_id in (
select of.id from m_office of where 
 of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
) and stf.is_active=1

union

select "Center",count(mg.id)
from m_group mg
where mg.level_id=1 and mg.office_id in (
select of.id from m_office of where 
 of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
)

union

select "Groups",count(mg1.id)
from m_group mg1
where mg1.level_id=2  and mg1.office_id in (
select of.id from m_office of where 
 of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
)

union

select "Clients", count(mc.id)
from m_client mc
where mc.status_enum=300 and mc.office_id in (
select of.id from m_office of where 
 of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
)

union

select"Borrowers",count(distinct(ml.client_id))
from m_loan ml
left join m_client mc on ml.client_id=mc.id
where ml.loan_status_id=300 and mc.office_id in (
select of.id from m_office of where 
 of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
)

union

select "Loan Accounts",count(ml.id)
from m_loan ml
left join m_client mc on ml.client_id=mc.id
where ml.loan_status_id=300 and mc.office_id in (
select of.id from m_office of where 
 of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
)

union

select"Saving Accounts",count(msa.id)
from m_savings_account msa
left join m_client mc on msa.client_id=mc.id
where msa.status_enum=300 and mc.office_id in (
select of.id from m_office of where 
 of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
)'
where report_name = "BranchOverView";






update stretchy_report set report_sql='select mpl.short_name Product
,sum((ml.principal_disbursed_derived)) Disbursal
,sum(ml.total_outstanding_derived) Outstanding
,COALESCE (sum(agg.total_overdue_derived),0) Arrears
from m_product_loan mpl
left join m_loan ml on mpl.id=ml.product_id
left join m_loan_arrears_aging agg on agg.loan_id=ml.id
left join m_client mc on ml.client_id=mc.id
where mc.office_id in (
select of.id from m_office of where 
 of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
)
group by ml.product_id
order by ml.principal_disbursed_derived' 
where report_name = "LoanPortfolio";

update m_permission set grouping='dashboard' where entity_name in ('ClientTrendsByDay','LoanTrendsByDay', 'ClientTrendsByWeek', 'LoanTrendsByWeek', 'ClientTrendsByMonth', 'LoanTrendsByMonth'
 ,'Demand_Vs_Collection', 'Disbursal_Vs_Awaitingdisbursal');
