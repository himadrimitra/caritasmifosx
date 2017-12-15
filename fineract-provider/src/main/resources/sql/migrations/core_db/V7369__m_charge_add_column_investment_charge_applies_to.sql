DROP TABLE IF EXISTS `f_charge_investment_details`;
ALTER TABLE `m_charge` ADD COLUMN `investment_charge_applies_to` SMALLINT(5) NULL DEFAULT NULL;
