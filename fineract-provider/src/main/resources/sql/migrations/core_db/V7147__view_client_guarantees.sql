/** R_enum altered to additionally track the enum_key used to identify enumerations in Finflux**/
alter table r_enum_value
add column enum_key varchar(100) default null;

/**Update all enumerations related to loan statuses **/
insert into r_enum_value VALUES ('loan_status_id', '304', 'Transfer on Hold', 'Transfer on Hold' , 0 , 'loanStatusType.transfer.on.hold');
insert into r_enum_value VALUES ('loan_status_id', '303', 'Transfer in progress', 'Transfer in progress' , 0 , 'loanStatusType.transfer.in.progress');

update r_enum_value set enum_key = "loanStatusType.submitted.and.pending.approval" where enum_name = "loan_status_id" and enum_id = 100;
update r_enum_value set enum_key = "loanStatusType.approved" where enum_name = "loan_status_id" and enum_id = 200;
update r_enum_value set enum_key = "loanStatusType.active" where enum_name = "loan_status_id" and enum_id = 300;
update r_enum_value set enum_key = "loanStatusType.withdrawn.by.client" where enum_name = "loan_status_id" and enum_id = 400;
update r_enum_value set enum_key = "loanStatusType.rejected" where enum_name = "loan_status_id" and enum_id = 500;
update r_enum_value set enum_key = "loanStatusType.closed.obligations.met" where enum_name = "loan_status_id" and enum_id = 600;
update r_enum_value set enum_key = "loanStatusType.closed.written.off" where enum_name = "loan_status_id" and enum_id = 601;
update r_enum_value set enum_key = "loanStatusType.closed.reschedule.outstanding.amount" where enum_name = "loan_status_id" and enum_id = 602;
update r_enum_value set enum_key = "loanStatusType.overpaid" where enum_name = "loan_status_id" and enum_id = 700;

/** Internal report that lists all loans guaranteed by a customer **/

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`, `track_usage`)
VALUES
  ('CLIENTGUARANTEE', 'Table', NULL, 'Client', '
select 
client.id as "clientId",
loan.id as "LoanId",
loan.account_no as "LoanAccountNo",
loan.loan_status_id as "LoanStatusId",
reportenum.enum_key as "LoanStatusKey",
product.`name` as "Product", 
loan.principal_amount as "LoanAmount"
from 
m_guarantor guarantor, m_loan loan, m_product_loan product , r_enum_value reportenum, m_client client, m_office office
where
guarantor.entity_id = ${clientId} 
and guarantor.type_enum = 1
and guarantor.is_active = 1
and guarantor.entity_id = client.id
and client.office_id = office.id
and guarantor.loan_id = loan.id
and loan.product_id = product.id
and loan.loan_status_id = reportenum.enum_id
and reportenum.enum_name = "loan_status_id"
and office.hierarchy like concat("${currentUserHierarchy}", "%")
order by loan.id desc
    ' , 'Report lists all loans guaranteed by a client', 0, 0, 0);


 /**Permission for this report. Note that to ease setting permissions on the UI, the reports permissions are grouped alongside a clients permissions***/
 INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES
  ('portfolio', 'READ_CLIENTGUARANTEE', 'CLIENTGUARANTEE', 'READ', 0);
