/*some tenants might not have as on date parameter*/

INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`)
select 'asOnDate', 'asOn', 'As On', 'date', 'date', 'today', NULL, NULL, NULL, NULL, NULL 
from dual 
WHERE NOT EXISTS ( select 1 from stretchy_parameter sp where sp.parameter_name='asOnDate' );

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Branchwise Loan Summary', 'Pentaho', NULL, 'Loan', NULL, 'Branchwise Loan Summary', 0, 1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) VALUES ( (select sr.id from stretchy_report sr where sr.report_name='Branchwise Loan Summary'), (select sp.id from stretchy_parameter sp where sp.parameter_name='asOnDate'), 'endDate');

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('report', 'READ_Branchwise Loan Summary', 'Branchwise Loan Summary', 'READ', 0);
