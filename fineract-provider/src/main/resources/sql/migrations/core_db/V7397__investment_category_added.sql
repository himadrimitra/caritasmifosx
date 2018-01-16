INSERT INTO `m_code` (`code_name`, `is_system_defined`, `parent_id`) VALUES ('InvestmentCategory', 0, NULL);

ALTER TABLE f_investment_product ADD COLUMN `category` INT(11) NULL DEFAULT NULL,
      ADD CONSTRAINT fk_category_m_code_value FOREIGN KEY (category) REFERENCES m_code_value(id);