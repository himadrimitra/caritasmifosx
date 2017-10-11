ALTER TABLE `m_loan` ADD COLUMN `emi_based_on_disbursements` TINYINT(1) NOT NULL DEFAULT '0';
ALTER TABLE `m_product_loan` ADD COLUMN `emi_based_on_disbursements` TINYINT(1) NOT NULL DEFAULT '0' ;


