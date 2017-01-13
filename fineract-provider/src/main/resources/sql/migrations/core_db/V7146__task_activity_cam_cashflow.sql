INSERT IGNORE INTO f_task_activity(name,identifier,config_values,supported_actions,type)
VALUES
("Criteria Check","criteriacheck",null,null,3),
("Cashflow Analysis","cashflow",null,null,3),
("CAM","cam",null,null,3);

INSERT INTO `f_risk_field` (`name`, `uname`, `value_type`, `options`, `code_name`, `is_active`)
VALUES
  ('No. of Client Address', 'clientAddressCount', 0, NULL, NULL, 1);

