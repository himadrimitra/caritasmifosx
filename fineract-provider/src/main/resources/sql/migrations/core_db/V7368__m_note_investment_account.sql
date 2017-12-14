ALTER TABLE `m_note` ADD COLUMN `investment_account_id` BIGINT(20) NULL DEFAULT NULL;

ALTER TABLE `m_note` 
ADD FOREIGN KEY (`investment_account_id` ) REFERENCES `f_investment_account` (`id` );
 