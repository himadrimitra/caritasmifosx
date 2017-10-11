create table `m_email_messages_outbound`(
`id` BIGINT NOT NULL AUTO_INCREMENT,
`loanId` BIGINT NOT NULL,
`event_type` VARCHAR(50),
`product_type` INT,
`mail_status` VARCHAR(20),
`sent_date` DATE NOT NULL,
PRIMARY KEY(`id`),
FOREIGN KEY(`loanId`) REFERENCES `m_loan`(`id`));

INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('loanId', 'loanId', 'loanId', 'text', 'string', 'n/a', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('loanStatus', 'status', 'status', 'text', 'string', 'n/a', NULL, NULL, NULL, NULL, NULL);

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`, `track_usage`) VALUES ('Repayment Schedule', 'Pentaho', NULL, 'Loan', NULL, 'Repayment Schedule', 1, 1, 0);

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from stretchy_report where report_name = 'Repayment Schedule'),(select id from stretchy_parameter where parameter_name = 'loanId'), 'loanId');
INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES ((select id from stretchy_report where report_name = 'Repayment Schedule'), (select id from stretchy_parameter where parameter_name = 'loanStatus'), 'status');
