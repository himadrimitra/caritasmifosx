INSERT INTO `c_configuration` (`name`, `value`, `date_value`, `enabled`, `is_trap_door`, `description`) VALUES ('center-client-association', '0', NULL, 0, 0, 'Will allow adding and managing members to center');
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio_center', 'ASSOCIATECLIENTS_CENTER_CHECKER', 'CENTER', 'ASSOCIATECLIENTS_CHECKER', 0)
,('portfolio_center', 'TRANSFERCLIENTS_CENTER_CHECKER', 'CENTER', 'TRANSFERCLIENTS_CHECKER', 0)
,('portfolio_center', 'TRANSFERCLIENTS_CENTER', 'CENTER', 'TRANSFERCLIENTS', 0)
,('portfolio_center', 'ASSOCIATECLIENTS_CENTER', 'CENTER', 'ASSOCIATECLIENTS', 0);
