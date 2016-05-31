DROP TABLE IF EXISTS `sms_messages_outbound`;
DROP TABLE IF EXISTS `sms_configuration`;
DROP TABLE IF EXISTS `sms_campaign`;

CREATE TABLE `sms_campaign`(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `campaign_name` varchar(100) NOT NULL,
  `campaign_type` int NOT NULL,
  `runreport_id` int NOT NULL,
  `param_value` text, 
  `status_enum` int NOT NULL,
  `message` text NOT NULL,
  `closedon_date` date,
  `closedon_userid` bigint(20),
  `submittedon_date` date,
  `submittedon_userid` bigint(20),
  `approvedon_date` date,
  `approvedon_userid` bigint(20),
  `recurrence` varchar(100),
  `next_trigger_date` datetime,
  `last_trigger_date` datetime,
  `recurrence_start_date` datetime,
  `is_visible` TINYINT(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`runreport_id`) REFERENCES `stretchy_report` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if not exists sms_configuration(
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(250) NULL DEFAULT NULL,
	`value` VARCHAR(250) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

INSERT INTO `sms_configuration` (`name`, `value`) VALUES ('API_BASE_URL', 'http://localhost:8999/mifos-sms-gateway/api/v1/sms');

INSERT INTO `sms_configuration` (`name`, `value`) VALUES ('API_AUTH_USERNAME', 'root');

INSERT INTO `sms_configuration` (`name`, `value`) VALUES ('API_AUTH_PASSWORD', 'localhost');

INSERT INTO `sms_configuration` (`name`, `value`) VALUES ('SMS_CREDITS', '1000');

INSERT INTO `sms_configuration` (`name`, `value`) VALUES ('SMS_SOURCE_ADDRESS', 'Conflux Services');

INSERT INTO `sms_configuration` (`name`, `value`) VALUES ('COUNTRY_CALLING_CODE', '91');

CREATE TABLE `sms_messages_outbound` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`group_id` BIGINT(20) NULL DEFAULT NULL,
	`client_id` BIGINT(20) NULL DEFAULT NULL,
	`staff_id` BIGINT(20) NULL DEFAULT NULL,
	`status_enum` INT(5) NOT NULL DEFAULT '100',
	`mobile_no` VARCHAR(50) NOT NULL,
	`message` TEXT NOT NULL,
	`submittedon_date` DATE NULL DEFAULT NULL,
	`campaign_name` VARCHAR(200) NULL DEFAULT NULL,
	`source_address` VARCHAR(200) NULL DEFAULT NULL,
	`external_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FKGROUP000000001` (`group_id`),
	INDEX `FKCLIENT00000001` (`client_id`),
	INDEX `FKSTAFF000000001` (`staff_id`),
	CONSTRAINT `FKCLIENT00000001` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FKGROUP000000001` FOREIGN KEY (`group_id`) REFERENCES `m_group` (`id`),
	CONSTRAINT `FKSTAFF000000001` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

INSERT INTO  `m_permission` (`grouping` ,`code` ,`entity_name` ,`action_name` ,`can_maker_checker`)
VALUES ('organisation',  'READ_SMS_CAMPAIGN',  'SMS_CAMPAIGN',  'READ',  '0');

INSERT INTO  `m_permission` (`grouping` ,`code` ,`entity_name` ,`action_name` ,`can_maker_checker`)
VALUES ('organisation',  'CREATE_SMS_CAMPAIGN',  'SMS_CAMPAIGN',  'CREATE',  '0');

INSERT INTO  `m_permission` (`grouping` ,`code` ,`entity_name` ,`action_name` ,`can_maker_checker`)
VALUES ('organisation',  'CREATE_SMS_CAMPAIGN_CHECKER',  'SMS_CAMPAIGN',  'CREATE',  '0');

INSERT INTO  `m_permission` (`grouping` ,`code` ,`entity_name` ,`action_name` ,`can_maker_checker`)
VALUES ('organisation',  'UPDATE_SMS_CAMPAIGN',  'SMS_CAMPAIGN',  'UPDATE',  '0');

INSERT INTO  `m_permission` (`grouping` ,`code` ,`entity_name` ,`action_name` ,`can_maker_checker`)
VALUES ('organisation',  'UPDATE_SMS_CAMPAIGN_CHECKER',  'SMS_CAMPAIGN',  'UPDATE',  '0');

INSERT INTO  `m_permission` (`grouping` ,`code` ,`entity_name` ,`action_name` ,`can_maker_checker`)
VALUES ('organisation',  'DELETE_SMS_CAMPAIGN',  'SMS_CAMPAIGN',  'DELETE',  '0');

INSERT INTO  `m_permission` (`grouping` ,`code` ,`entity_name` ,`action_name` ,`can_maker_checker`)
VALUES ('organisation',  'DELETE_SMS_CAMPAIGN_CHECKER',  'SMS_CAMPAIGN',  'DELETE',  '0');

INSERT INTO  `m_permission` (`grouping` ,`code` ,`entity_name` ,`action_name` ,`can_maker_checker`)
VALUES ('organisation',  'ACTIVATE_SMS_CAMPAIGN',  'SMS_CAMPAIGN',  'ACTIVATE',  '0');

INSERT INTO  `m_permission` (`grouping` ,`code` ,`entity_name` ,`action_name` ,`can_maker_checker`)
VALUES ('organisation',  'CLOSE_SMS_CAMPAIGN',  'SMS_CAMPAIGN',  'CLOSE',  '0');

INSERT INTO  `m_permission` (`grouping` ,`code` ,`entity_name` ,`action_name` ,`can_maker_checker`)
VALUES ('organisation',  'REACTIVATE_SMS_CAMPAIGN',  'SMS_CAMPAIGN',  'REACTIVATE',  '0');


INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`,
`group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`,
`is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`) VALUES 
('Send messages to SMS gateway', 'Send messages to SMS gateway', '0 0/1 * * * ?', '2016-01-01 00:00:00', 5
, NULL, NULL, NULL, 'Send messages to SMS gateway_ DEFAULT', NULL, 1, 0, 1, 0, 0);


INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`,
`group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`,
`is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`) VALUES 
('Get delivery reports from SMS gateway', 'Get delivery reports from SMS gateway', '0 0/1 * * * ?', '2016-01-01 00:00:00', 5
, NULL, NULL, NULL, 'Get delivery reports from SMS gateway_ DEFAULT', NULL, 1, 0, 1, 0, 0);

INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`,
`group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`, 
`is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`) VALUES 
('Update Sms Outbound with campaign message', 'Update Sms Outbound with campaign message', '0 0/1 * * * ?', '2016-01-01 00:00:00', 5, NULL, '2016-05-11 16:03:00', '2016-05-11 16:04:00', 'Update Sms Outbound with campaign messageJobDetail1 _ DEFAULT', NULL, 1, 1, 1, 0, 0);


DROP TABLE IF EXISTS `m_report_mailing_job_run_history`;
DROP TABLE IF EXISTS `m_report_mailing_job_configuration`;
DROP TABLE IF EXISTS `m_report_mailing_job`;

create table `m_report_mailing_job` (
id bigint primary key auto_increment,
name varchar(100) not null,
description text null,
start_datetime datetime not null,
recurrence varchar(100) null,
created_on_date date not null,
created_by_userid bigint not null,
email_recipients text not null,
email_subject varchar(100) not null,
email_message text not null,
email_attachment_file_format varchar(10) not null,
stretchy_report_id int not null,
stretchy_report_param_map text null,
previous_run_datetime datetime null,
next_run_datetime datetime null,
previous_run_status varchar(10) null,
previous_run_error_log text null,
previous_run_error_message text null,
number_of_runs int not null default 0,
is_active tinyint(1) not null default 0,
is_deleted tinyint(1) not null default 0,
run_as_userid bigint not null,
foreign key (created_by_userid) references m_appuser(id),
foreign key (stretchy_report_id) references stretchy_report(id),
foreign key (run_as_userid) references m_appuser(id),
constraint unique_name unique (name)
);

create table if not exists `m_report_mailing_job_run_history` (
id bigint primary key auto_increment,
job_id bigint not null,
start_datetime datetime not null,
end_datetime datetime not null,
status varchar(10) not null,
error_message text null,
error_log text null,
foreign key (job_id) references m_report_mailing_job (id)
);

create table if not exists m_report_mailing_job_configuration (
id int primary key auto_increment,
name varchar(50) not null,
`value` varchar(200) not null,
constraint unique_name unique (name)
);

insert into m_permission (`grouping`, code, entity_name, action_name, can_maker_checker)
values ('jobs', 'CREATE_REPORTMAILINGJOB', 'REPORTMAILINGJOB', 'CREATE', 0), 
('jobs', 'UPDATE_REPORTMAILINGJOB', 'REPORTMAILINGJOB', 'UPDATE', 0), 
('jobs', 'DELETE_REPORTMAILINGJOB', 'REPORTMAILINGJOB', 'DELETE', 0), 
('jobs', 'READ_REPORTMAILINGJOB', 'REPORTMAILINGJOB', 'READ', 0);

insert into m_report_mailing_job_configuration (name, `value`)
values ('GMAIL_SMTP_SERVER', 'smtp.gmail.com'), ('GMAIL_SMTP_PORT', 587), ('GMAIL_SMTP_USERNAME', ''), ('GMAIL_SMTP_PASSWORD', '');

insert into job (name, display_name, cron_expression, create_time)
values ('Execute Report Mailing Jobs', 'Execute Report Mailing Jobs', '0 0/1 * * * ?', NOW());