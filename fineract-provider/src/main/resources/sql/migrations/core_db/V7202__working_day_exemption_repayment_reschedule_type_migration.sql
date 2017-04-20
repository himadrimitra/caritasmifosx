ALTER TABLE `f_workingday_exumption`
	ADD COLUMN `temp_action_tobe_performed` INT(11) NULL DEFAULT NULL AFTER `action_tobe_performed`;

UPDATE f_workingday_exumption we SET we.temp_action_tobe_performed = we.action_tobe_performed;
UPDATE f_workingday_exumption we SET we.action_tobe_performed = 3 WHERE we.temp_action_tobe_performed = 5;
UPDATE f_workingday_exumption we SET we.action_tobe_performed = 5 WHERE we.temp_action_tobe_performed = 3;
	
ALTER TABLE `f_workingday_exumption`
	DROP COLUMN `temp_action_tobe_performed`;