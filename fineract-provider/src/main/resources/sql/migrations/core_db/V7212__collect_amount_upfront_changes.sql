ALTER TABLE `m_product_loan`
	CHANGE COLUMN `collect_interest_upfront` `allow_upfront_collection` TINYINT(1) NOT NULL DEFAULT '0' ;
	
ALTER TABLE `m_loan`
	ADD COLUMN `amount_for_upfront_collection` DECIMAL(19,6) NULL DEFAULT NULL ;
	
update m_loan ml join m_product_loan mp set amount_for_upfront_collection =  ((ml.flat_interest_rate * ml.principal_amount)/100)  where ml.collect_interest_upfront = 1;	

ALTER TABLE `m_loan`
 	DROP COLUMN `collect_interest_upfront`;

