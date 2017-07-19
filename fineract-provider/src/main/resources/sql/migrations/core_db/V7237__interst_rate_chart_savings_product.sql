
CREATE TABLE `f_floating_interest_rate_chart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `savings_product_id` bigint(20) DEFAULT NULL,
  `effective_date` date DEFAULT NULL,
  `interest_rate` decimal(20,6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_f_floating_interest_rate_chart_m_savings_product` (`savings_product_id`),
  CONSTRAINT `FK_f_floating_interest_rate_chart_m_savings_product` FOREIGN KEY (`savings_product_id`) REFERENCES `m_savings_product` (`id`)
);

