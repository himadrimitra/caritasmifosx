CREATE TABLE `f_workingday_exumption` (
	`portfolio_type` INT(11) NOT NULL,
	`applicable_property` INT(11) NOT NULL,
	`expression` VARCHAR(100) NOT NULL,
	`action_tobe_performed` INT(11) NOT NULL,
	`update_type` INT(11) NOT NULL
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;
