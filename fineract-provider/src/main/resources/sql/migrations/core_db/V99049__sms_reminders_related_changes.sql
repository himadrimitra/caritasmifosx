INSERT IGNORE INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('DefaultOffice', 'officeId', 'Office', 'none', 'number', '0', NULL, NULL, 'Y', 'select id,concat(substring(\'........................................\', 1,((LENGTH(`hierarchy`) - LENGTH(REPLACE(`hierarchy`, \'.\', \'\')) - 1) * 4)),`name`) as tc from m_office where hierarchy like concat(\'${currentUserHierarchy}\', \'%\') order by hierarchy', NULL);
INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from stretchy_report where report_name='Loan Repayment Reminders'), (select id from stretchy_parameter where parameter_name = 'DefaultOffice'), 'officeId'), ((select id from stretchy_report where report_name='Loan Repayment Reminders'), (select id from stretchy_parameter where parameter_name = 'fromXSelect'), 'fromX'), ((select id from stretchy_report where report_name='Loan Repayment Reminders'), (select id from stretchy_parameter where parameter_name = 'toYSelect'), 'toY');
INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from stretchy_report where report_name='Loan First Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'DefaultOffice'), 'officeId'), ((select id from stretchy_report where report_name='Loan First Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'fromXSelect'), 'fromX'), ((select id from stretchy_report where report_name='Loan First Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'toYSelect'), 'toY');
INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from stretchy_report where report_name='Loan Second Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'DefaultOffice'), 'officeId'), ((select id from stretchy_report where report_name='Loan Second Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'fromXSelect'), 'fromX'), ((select id from stretchy_report where report_name='Loan Second Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'toYSelect'), 'toY');
INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from stretchy_report where report_name='Loan Third Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'DefaultOffice'), 'officeId'), ((select id from stretchy_report where report_name='Loan Third Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'fromXSelect'), 'fromX'), ((select id from stretchy_report where report_name='Loan Third Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'toYSelect'), 'toY');
INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from stretchy_report where report_name='Loan Fourth Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'DefaultOffice'), 'officeId'), ((select id from stretchy_report where report_name='Loan Fourth Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'fromXSelect'), 'fromX'), ((select id from stretchy_report where report_name='Loan Fourth Overdue Repayment Reminder'), (select id from stretchy_parameter where parameter_name = 'toYSelect'), 'toY');
INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from stretchy_report where report_name='DefaultWarning -  guarantors'), (select id from stretchy_parameter where parameter_name = 'DefaultOffice'), 'officeId'), ((select id from stretchy_report where report_name='DefaultWarning -  guarantors'), (select id from stretchy_parameter where parameter_name = 'fromXSelect'), 'fromX'), ((select id from stretchy_report where report_name='DefaultWarning -  guarantors'), (select id from stretchy_parameter where parameter_name = 'toYSelect'), 'toY');
INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from stretchy_report where report_name='DefaultWarning - Clients'), (select id from stretchy_parameter where parameter_name = 'DefaultOffice'), 'officeId'), ((select id from stretchy_report where report_name='DefaultWarning - Clients'), (select id from stretchy_parameter where parameter_name = 'fromXSelect'), 'fromX'), ((select id from stretchy_report where report_name='DefaultWarning - Clients'), (select id from stretchy_parameter where parameter_name = 'toYSelect'), 'toY');
