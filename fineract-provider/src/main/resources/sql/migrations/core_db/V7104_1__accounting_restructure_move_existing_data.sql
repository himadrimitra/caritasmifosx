-- Split and move data from acc_gl_journal_entry to f_journal_entry and f_journal_entry_detail
	
set foreign_key_checks=0;
TRUNCATE f_journal_entry_detail;
TRUNCATE f_journal_entry;
set foreign_key_checks=1;

-- Insert data to f_journal_entry

INSERT INTO f_journal_entry
SELECT MIN(je.id) id
,je.office_id office_id
,IFNULL(pdt.id,IFNULL(pds.id,pdc.id)) payment_detail_id
,je.currency_code currency_code
,je.transaction_id transaction_identifier
,je.manual_entry manual_entry
,je.reversed reversed
,MIN(je.reversal_id) reversal_id
,je.entry_date entry_date
,je.entry_date value_date
,je.entry_date effective_date
,CASE je.manual_entry
	WHEN 1 THEN NULL
	ELSE IF(je.client_transaction_id IS NULL,IF(je.savings_transaction_id IS NULL,1,2),3)
	END entity_type_enum
,je.entity_id entity_id
,IFNULL(je.client_transaction_id,IFNULL(je.savings_transaction_id,je.loan_transaction_id)) entity_transaction_id
,je.ref_num ref_num
,je.description description
,je.createdby_id createdby_id
,je.lastmodifiedby_id lastmodifiedby_id
,je.created_date created_date
,je.lastmodified_date lastmodified_date
 FROM acc_gl_journal_entry je
 LEFT JOIN m_loan_transaction t ON t.id=je.loan_transaction_id
 LEFT JOIN m_payment_detail pdt ON pdt.id=t.payment_detail_id
 LEFT JOIN m_savings_account_transaction st ON st.id=je.savings_transaction_id
 LEFT JOIN m_payment_detail pds ON pds.id=st.payment_detail_id
 LEFT JOIN m_client_transaction ct ON ct.id=je.client_transaction_id
 LEFT JOIN m_payment_detail pdc ON pdc.id=ct.payment_detail_id
 WHERE je.reversed=0
GROUP BY je.transaction_id

UNION

SELECT MIN(je.id) id
,je.office_id office_id
,IFNULL(pdt.id,IFNULL(pds.id,pdc.id)) payment_detail_id
,je.currency_code currency_code
,je.transaction_id transaction_identifier
,je.manual_entry manual_entry
,je.reversed reversed
,MIN(je.reversal_id) reversal_id
,je.entry_date entry_date
,je.entry_date value_date
,je.entry_date effective_date
,CASE je.manual_entry
	WHEN 1 THEN NULL
	ELSE IF(je.client_transaction_id IS NULL,IF(je.savings_transaction_id IS NULL,1,2),3)
	END entity_type_enum
,je.entity_id entity_id
,IFNULL(je.client_transaction_id,IFNULL(je.savings_transaction_id,je.loan_transaction_id)) entity_transaction_id
,je.ref_num ref_num
,je.description description
,je.createdby_id createdby_id
,je.lastmodifiedby_id lastmodifiedby_id
,je.created_date created_date
,je.lastmodified_date lastmodified_date
 FROM acc_gl_journal_entry je
 LEFT JOIN m_loan_transaction t ON t.id=je.loan_transaction_id
 LEFT JOIN m_payment_detail pdt ON pdt.id=t.payment_detail_id
 LEFT JOIN m_savings_account_transaction st ON st.id=je.savings_transaction_id
 LEFT JOIN m_payment_detail pds ON pds.id=st.payment_detail_id
 LEFT JOIN m_client_transaction ct ON ct.id=je.client_transaction_id
 LEFT JOIN m_payment_detail pdc ON pdc.id=ct.payment_detail_id
 WHERE je.reversed=1
GROUP BY je.transaction_id
ORDER BY 1 DESC;

-- Insert data to f_journal_entry_detail

INSERT INTO f_journal_entry_detail (journal_entry_id,account_id,type_enum,amount)
SELECT fj.id,je.account_id,je.type_enum,je.amount
FROM f_journal_entry fj
JOIN acc_gl_journal_entry je ON je.transaction_id=fj.transaction_identifier
WHERE fj.reversed=0
AND je.reversed=0

UNION ALL

SELECT fj.id,je.account_id,je.type_enum,je.amount
FROM f_journal_entry fj
JOIN acc_gl_journal_entry je ON je.transaction_id=fj.transaction_identifier
WHERE fj.reversed=1
AND je.reversed=1



DROP PROCEDURE IF EXISTS ofRunBal;
DELIMITER //
CREATE PROCEDURE `ofRunBal`()
LANGUAGE SQL
DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''

BEGIN

DECLARE ids BIGINT DEFAULT 0;
DECLARE ido BIGINT DEFAULT 0;
DECLARE account_ids BIGINT;
DECLARE account_ido BIGINT;
DECLARE currency_ido VARCHAR(3);
DECLARE currency_ids VARCHAR(3);
DECLARE office_ids BIGINT;
TRUNCATE f_office_running_balance;
TRUNCATE f_org_running_balance;

DROP TABLE IF EXISTS f_journal_temp;
DROP TABLE IF EXISTS f_journal_temp_org;

CREATE TABLE `f_journal_temp` (
	`id` BIGINT(20) NULL AUTO_INCREMENT,
	`account_id` BIGINT(20) NOT NULL,
	`office_id` BIGINT(20) NULL,
	`currency_code` VARCHAR(3) NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `f_journal_temp_org` (
	`id` BIGINT(20) NULL AUTO_INCREMENT,
	`account_id` BIGINT(20) NOT NULL,
	`currency_code` VARCHAR(3) NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
INSERT INTO f_journal_temp (account_id,office_id,currency_code) 
SELECT distinct fd.account_id,fj.office_id,fj.currency_code
 FROM f_journal_entry_detail fd
 JOIN f_journal_entry fj ON fj.id=fd.journal_entry_id
ORDER BY 3,1,2;

INSERT INTO f_journal_temp_org (account_id,currency_code) 
SELECT distinct fd.account_id,fj.currency_code
 FROM f_journal_entry_detail fd
 JOIN f_journal_entry fj ON fj.id=fd.journal_entry_id
ORDER BY 2,1;

TRUNCATE f_office_running_balance;
TRUNCATE f_org_running_balance;
SELECT MIN(ft.id) INTO ids FROM f_journal_temp ft;
SELECT MIN(ft.id) INTO ido FROM f_journal_temp_org ft;

-- Loop to populate balances at office level

WHILE ids > 0
DO
SELECT ft.account_id INTO account_ids
		FROM f_journal_temp ft
		WHERE ft.id=ids;
SELECT ft.office_id INTO office_ids
		FROM f_journal_temp ft
		WHERE ft.id=ids;
SELECT ft.currency_code INTO currency_ids
		FROM f_journal_temp ft
		WHERE ft.id=ids;
INSERT INTO f_office_running_balance (account_id,office_id,`date`,debit,credit,type_enum,amount,closing_balance,to_date,opening_balance,currency_code)
SELECT b.account_id,b.office_id,b.entry_date
       ,b.debit
       ,b.credit
       ,CASE b.classification_enum 
		 	WHEN 1 THEN 2
		 	WHEN 5 THEN 2
		 	ELSE 1 END type_enum
       ,b.balance amount
       ,@running_sum:=@running_sum + b.balance AS closing_balance
		 ,NULL to_date
       ,@running_sum2:=@running_sum - b.balance
	   ,b.currency_code
FROM
(SELECT a.office_id,a.account_id,a.entry_date,sum(a.debit) debit,sum(a.credit) credit
,CASE a.classification_enum
WHEN 1 THEN sum(a.debit) - sum(a.credit)
WHEN 5 THEN sum(a.debit) - sum(a.credit)
WHEN 2 THEN sum(a.credit) - sum(a.debit)
WHEN 3 THEN sum(a.credit) - sum(a.debit)
WHEN 4 THEN sum(a.credit) - sum(a.debit) END AS balance
,a.name
,a.classification_enum
,a.transaction_identifier
,a.currency_code
FROM ( SELECT fj.office_id,fd.account_id,fj.entry_date,CASE fd.type_enum
WHEN 1 THEN fd.amount 
ELSE 0 END credit
,CASE fd.type_enum
WHEN 2 THEN fd.amount 
ELSE 0 END debit
,gl.name,gl.classification_enum
,fj.transaction_identifier
,fj.currency_code
FROM f_journal_entry fj
JOIN f_journal_entry_detail fd ON fd.journal_entry_id=fj.id
JOIN acc_gl_account gl ON gl.id=fd.account_id
WHERE fd.account_id=account_ids AND fj.office_id=office_ids AND fj.currency_code=currency_ids
ORDER BY fj.office_id,fj.entry_date,fd.account_id ) a
GROUP BY a.currency_code,a.office_id,a.entry_date,a.account_id
ORDER BY a.currency_code,3,1,2) b
JOIN (SELECT @running_sum := 0 AS temp) temp
JOIN (SELECT @running_sum2 := 0 AS temp2) temp2;
DELETE FROM f_journal_temp WHERE id=ids;
SELECT MIN(ft.id) INTO ids FROM f_journal_temp ft;
END WHILE;

-- Loop to populate balances at organization level

WHILE ido > 0
DO 
SELECT ft.account_id INTO account_ido 
		FROM f_journal_temp_org ft
		WHERE ft.id=ido;
SELECT ft.currency_code INTO currency_ido
		FROM f_journal_temp_org ft
		WHERE ft.id=ido;

INSERT INTO f_org_running_balance (account_id,`date`,debit,credit,type_enum,amount,closing_balance,to_date,opening_balance,currency_code)
SELECT b.account_id,b.entry_date
       ,b.debit
       ,b.credit
       ,CASE b.classification_enum 
		 	WHEN 1 THEN 2
		 	WHEN 5 THEN 2
		 	ELSE 1 END type_enum
       ,b.balance amount
       ,@running_sum:=@running_sum + b.balance AS closing_balance
		 ,NULL to_date
       ,@running_sum2:=@running_sum - b.balance
	   ,b.currency_code
FROM
(SELECT a.office_id,a.account_id,a.entry_date,sum(a.debit) debit,sum(a.credit) credit
,CASE a.classification_enum
WHEN 1 THEN sum(a.debit) - sum(a.credit)
WHEN 5 THEN sum(a.debit) - sum(a.credit)
WHEN 2 THEN sum(a.credit) - sum(a.debit)
WHEN 3 THEN sum(a.credit) - sum(a.debit)
WHEN 4 THEN sum(a.credit) - sum(a.debit) END AS balance
,a.name
,a.classification_enum
,a.transaction_identifier
,a.currency_code
FROM (SELECT fj.office_id,fd.account_id,fj.entry_date,CASE fd.type_enum
WHEN 1 THEN fd.amount 
ELSE 0 END credit
,CASE fd.type_enum
WHEN 2 THEN fd.amount 
ELSE 0 END debit
,gl.name,gl.classification_enum
,fj.transaction_identifier
,fj.currency_code
FROM f_journal_entry fj
JOIN f_journal_entry_detail fd ON fd.journal_entry_id=fj.id
JOIN acc_gl_account gl ON gl.id=fd.account_id
WHERE fd.account_id=account_ido and fj.currency_code = currency_ido
ORDER BY fj.entry_date,fd.account_id) a
GROUP BY a.currency_code,a.entry_date,a.account_id
ORDER BY a.currency_code,3,1) b
JOIN (SELECT @running_sum := 0 AS temp) temp
JOIN (SELECT @running_sum2 := 0 AS temp2) temp2;

DELETE FROM f_journal_temp_org WHERE id=ido;
SELECT MIN(ft.id) INTO ido FROM f_journal_temp_org ft;
END WHILE;

UPDATE f_office_running_balance fb
JOIN 
(SELECT f.id
,ADDDATE((SELECT MIN(fo.date) FROM f_office_running_balance fo
 WHERE fo.account_id=f.account_id AND fo.office_id=f.office_id AND fo.currency_code = f.currency_code
 AND f.date < fo.date),-1) to_date
FROM 
f_office_running_balance f) a ON a.id=fb.id
SET fb.to_date=a.to_date;

UPDATE f_org_running_balance fb
JOIN 
(SELECT f.id
,ADDDATE((SELECT MIN(fo.date) FROM f_org_running_balance fo
 WHERE fo.account_id=f.account_id AND fo.currency_code = f.currency_code
 AND f.date < fo.date),-1) to_date
FROM 
f_org_running_balance f) a ON a.id=fb.id
SET fb.to_date=a.to_date;


DROP TABLE IF EXISTS f_journal_temp;
DROP TABLE IF EXISTS f_journal_temp_org;

END //
DELIMITER ;

CALL ofRunBal();
DROP PROCEDURE IF EXISTS ofRunBal;
-- END One-Time Procedure