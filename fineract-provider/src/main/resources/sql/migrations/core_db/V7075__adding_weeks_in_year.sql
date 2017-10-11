ALTER TABLE `m_product_loan` ADD COLUMN `weeks_in_year_enum` SMALLINT(5) NOT NULL DEFAULT 1;
ALTER TABLE `m_loan` ADD COLUMN `weeks_in_year_enum` SMALLINT(5) DEFAULT 1;