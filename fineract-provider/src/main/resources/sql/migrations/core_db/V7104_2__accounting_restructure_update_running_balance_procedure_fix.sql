/* OFFICE RUNNING BALANCE */
DROP PROCEDURE IF EXISTS UpdateAccountingRunningBalances;
DELIMITER //
CREATE PROCEDURE `UpdateAccountingRunningBalances`()
LANGUAGE SQL
DETERMINISTIC
CONTAINS SQL
SQL SECURITY DEFINER
COMMENT ''

BEGIN

-- Declare variables 
DECLARE account_ids BIGINT;
DECLARE office_ids BIGINT;
DECLARE currency_ids VARCHAR(3);
DECLARE ids BIGINT DEFAULT 0;
DECLARE ido BIGINT DEFAULT 0;
DECLARE account_ido BIGINT;
DECLARE currency_ido VARCHAR(3);

DROP TABLE IF EXISTS f_running_balance_computation_detail_temp;
DROP TABLE IF EXISTS f_running_balance_computation_detail_temp_org;


-- Temporary tables for procedure

CREATE TABLE `f_running_balance_computation_detail_temp` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`account_id` BIGINT(20) NOT NULL,
	`office_id` BIGINT(20) NOT NULL,
	`computed_till_date` DATE NOT NULL,
	`currency_code` VARCHAR(3) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_running_balance_computation_detail_temp_gl_account` (`account_id`),
	INDEX `FK_running_balance_computation_detail_temp_office` (`office_id`),
	CONSTRAINT `FK_running_balance_computation_detail_temp_gl_account` FOREIGN KEY (`account_id`) REFERENCES `acc_gl_account` (`id`),
	CONSTRAINT `FK_running_balance_computation_detail_temp_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `f_running_balance_computation_detail_temp_org` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`account_id` BIGINT(20) NOT NULL,
	`computed_till_date` DATE NOT NULL,
	`currency_code` VARCHAR(3) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_running_balance_computation_detail_temp_org_gl_account` (`account_id`),
	CONSTRAINT `FK_running_balance_computation_detail_temp_org_gl_account` FOREIGN KEY (`account_id`) REFERENCES `acc_gl_account` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;



-- Replicate f_running_balance_computation_detail table

INSERT INTO f_running_balance_computation_detail_temp
SELECT * FROM f_running_balance_computation_detail;

INSERT INTO f_running_balance_computation_detail_temp_org
SELECT MIN(id),account_id,MIN(computed_till_date),currency_code
FROM f_running_balance_computation_detail 
GROUP BY 2,4;

-- Initial Value for variable ids & ido
SELECT MIN(ft.id) INTO ids FROM f_running_balance_computation_detail_temp ft;

SELECT MIN(ft.id) INTO ido FROM f_running_balance_computation_detail_temp_org ft;


-- ***************************************************************************** --

/* OFFICE RUNNING BALANCE */

-- ***************************************************************************** --

WHILE ids > 0
DO

SELECT ft.account_id INTO account_ids
		FROM f_running_balance_computation_detail_temp ft
		WHERE ft.id=ids;
SELECT ft.office_id INTO office_ids 
		FROM f_running_balance_computation_detail_temp ft
		WHERE ft.id=ids;
SELECT ft.currency_code INTO currency_ids 
		FROM f_running_balance_computation_detail_temp ft
		WHERE ft.id=ids;

-- Delete all entries after computed_till_date for an account in an office

DELETE FROM f_office_running_balance 
	WHERE account_id=account_ids 
	AND office_id=office_ids
	AND currency_code=currency_ids
	AND (to_date > (SELECT computed_till_date 
							FROM f_running_balance_computation_detail_temp rc
						WHERE rc.account_id=account_ids
						AND rc.office_id=office_ids
						AND rc.currency_code=currency_ids)
		OR date >  (SELECT computed_till_date 
							FROM f_running_balance_computation_detail_temp rc
						WHERE rc.account_id=account_ids
						AND rc.office_id=office_ids
						AND rc.currency_code=currency_ids));

UPDATE f_office_running_balance 
	SET to_date=(SELECT computed_till_date 
							FROM f_running_balance_computation_detail_temp rc
						WHERE rc.account_id=account_ids
						AND rc.office_id=office_ids
						AND rc.currency_code=currency_ids)
	WHERE account_id=account_ids 
	AND office_id=office_ids
	AND currency_code=currency_ids
	AND to_date IS NULL;
	
-- Insert entries after computed_till_date for an account and currency_code in an office
SET @running_sum=0;				
INSERT INTO f_office_running_balance (account_id,office_id,`date`,debit,credit,type_enum,amount,closing_balance,to_date,opening_balance,currency_code)
SELECT b.account_id,b.office_id,b.entry_date
       ,b.debit
       ,b.credit
       ,CASE b.classification_enum 
		 	WHEN 1 THEN 2
		 	WHEN 5 THEN 2
		 	ELSE 1 END type_enum
       ,b.balance amount
       ,@running_sum:=ifnull(@running_sum,0) + b.balance AS closing_balance
		 ,NULL to_date
       ,@running_sum2:=ifnull(@running_sum - b.balance,0)
	   ,b.currency_code
FROM
(SELECT 1 tt,a.office_id,a.account_id,a.entry_date,SUM(a.debit) debit,SUM(a.credit) credit
,CASE a.classification_enum
WHEN 1 THEN SUM(a.debit) - SUM(a.credit)
WHEN 5 THEN SUM(a.debit) - SUM(a.credit)
WHEN 2 THEN SUM(a.credit) - SUM(a.debit)
WHEN 3 THEN SUM(a.credit) - SUM(a.debit)
WHEN 4 THEN SUM(a.credit) - SUM(a.debit) END AS balance
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
WHERE fd.account_id=account_ids
AND fj.office_id=office_ids
AND fj.currency_code=currency_ids
AND fj.entry_date > (SELECT computed_till_date 
							FROM f_running_balance_computation_detail_temp rc
						WHERE rc.account_id=account_ids
						AND rc.office_id=office_ids
						AND rc.currency_code=currency_ids)
ORDER BY fj.office_id,fj.entry_date,fd.account_id) a
GROUP BY a.currency_code,a.office_id,a.entry_date,a.account_id
ORDER BY a.currency_code,3,1,2) b
LEFT JOIN (SELECT 1 as tt, @running_sum := ob.closing_balance 
			FROM f_office_running_balance ob 
		WHERE ob.account_id=account_ids 
		AND ob.office_id=office_ids 
		AND ob.currency_code=currency_ids
		AND ob.id= (SELECT MAX(id) FROM f_office_running_balance ob
						WHERE ob.to_date <= (SELECT computed_till_date 
													FROM f_running_balance_computation_detail_temp rc
												WHERE rc.account_id=account_ids
												AND rc.office_id=office_ids
												AND rc.currency_code=currency_ids)
						AND ob.account_id=account_ids 
						AND ob.office_id=office_ids 
						AND ob.currency_code=currency_ids)) temp on temp.tt=b.tt
JOIN (SELECT @running_sum2 := 0 AS temp2) temp2;

UPDATE f_office_running_balance fb
JOIN 
(SELECT f.id
,ADDDATE((SELECT MIN(fo.date) FROM f_office_running_balance fo
 WHERE fo.account_id=f.account_id AND fo.office_id=f.office_id AND fo.currency_code=f.currency_code
 AND f.date < fo.date),-1) to_date
FROM f_office_running_balance f) a ON a.id=fb.id
SET fb.to_date=a.to_date
WHERE fb.account_id=account_ids
AND fb.office_id=office_ids
AND fb.currency_code=currency_ids
AND (fb.to_date > (SELECT computed_till_date
								FROM f_running_balance_computation_detail_temp rc
								WHERE rc.account_id=account_ids 
								AND rc.office_id=office_ids
								AND rc.currency_code=currency_ids) OR fb.to_date IS NULL);

DELETE FROM f_running_balance_computation_detail_temp WHERE id=ids;
SELECT MIN(ft.id) INTO ids FROM f_running_balance_computation_detail_temp ft;
END WHILE;

-- ***************************************************************************** --

/* ORGANISATION RUNNING BALANCE */

-- ***************************************************************************** --

WHILE ido >0
DO
SELECT ft.account_id INTO account_ido
		FROM f_running_balance_computation_detail_temp_org ft
		WHERE ft.id=ido;
SELECT ft.currency_code INTO currency_ido
		FROM f_running_balance_computation_detail_temp_org ft
		WHERE ft.id=ido;
		
-- Delete all entries after computed_till_date for an account in an office

DELETE FROM f_org_running_balance 
	WHERE account_id=account_ido
	AND currency_code=currency_ido
	AND (to_date > (SELECT computed_till_date 
							FROM f_running_balance_computation_detail_temp_org rc
						WHERE rc.account_id=account_ido
						AND rc.currency_code=currency_ido)
		OR date > (SELECT computed_till_date 
							FROM f_running_balance_computation_detail_temp_org rc
						WHERE rc.account_id=account_ido
						AND rc.currency_code=currency_ido));

UPDATE f_org_running_balance
SET to_date=(SELECT computed_till_date 
							FROM f_running_balance_computation_detail_temp_org rc
						WHERE rc.account_id=account_ido
						AND rc.currency_code=currency_ido)
	WHERE account_id=account_ido
	AND currency_code=currency_ido
	AND to_date IS NULL;

SET @running_sum=0;
INSERT INTO f_org_running_balance (account_id,`date`,debit,credit,type_enum,amount,closing_balance,to_date,opening_balance,currency_code)
SELECT b.account_id,b.entry_date
       ,b.debit
       ,b.credit
       ,CASE b.classification_enum 
		 	WHEN 1 THEN 2
		 	WHEN 5 THEN 2
		 	ELSE 1 END type_enum
       ,b.balance amount
       ,@running_sum:=ifnull(@running_sum,0) + b.balance AS closing_balance
		 ,NULL to_date
       ,@running_sum2:=ifnull(@running_sum - b.balance,0)
	   ,b.currency_code
FROM
(SELECT 1 tt,a.office_id,a.account_id,a.entry_date,SUM(a.debit) debit,SUM(a.credit) credit
,CASE a.classification_enum
WHEN 1 THEN SUM(a.debit) - SUM(a.credit)
WHEN 5 THEN SUM(a.debit) - SUM(a.credit)
WHEN 2 THEN SUM(a.credit) - SUM(a.debit)
WHEN 3 THEN SUM(a.credit) - SUM(a.debit)
WHEN 4 THEN SUM(a.credit) - SUM(a.debit) END AS balance
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
WHERE fd.account_id=account_ido
AND fj.entry_date > (SELECT computed_till_date 
							FROM f_running_balance_computation_detail_temp_org rc
						WHERE rc.account_id=account_ido
						AND rc.currency_code=currency_ido)
ORDER BY fj.entry_date,fd.account_id) a
GROUP BY a.entry_date,a.account_id
ORDER BY 3,1) b
LEFT JOIN (SELECT 1 as tt, @running_sum := ob.closing_balance 
			FROM f_org_running_balance ob 
		WHERE ob.account_id=account_ido
		AND ob.currency_code=currency_ido
		AND ob.id= (SELECT MAX(id) FROM f_org_running_balance ob
						WHERE ob.to_date <= (SELECT computed_till_date 
													FROM f_running_balance_computation_detail_temp_org rc
												WHERE rc.account_id=account_ido
												AND rc.currency_code=currency_ido)
						AND ob.account_id=account_ido
						AND ob.currency_code=currency_ido)) temp on temp.tt=b.tt
JOIN (SELECT @running_sum2 := 0 AS temp2) temp2;

UPDATE f_org_running_balance fb
JOIN 
(SELECT f.id
,ADDDATE((SELECT MIN(fo.date) FROM f_org_running_balance fo
 WHERE fo.account_id=f.account_id and fo.currency_code=f.currency_code
 AND f.date < fo.date),-1) to_date
FROM 
f_org_running_balance f) a ON a.id=fb.id
SET fb.to_date=a.to_date
WHERE fb.account_id=account_ido
AND fb.currency_code=currency_ido
AND (fb.to_date > (SELECT computed_till_date
								FROM f_running_balance_computation_detail_temp_org rc
								WHERE rc.account_id=account_ido
								AND rc.currency_code=currency_ido) OR fb.to_date IS NULL);


DELETE FROM f_running_balance_computation_detail_temp_org WHERE id=ido;
SELECT MIN(ft.id) INTO ido FROM f_running_balance_computation_detail_temp_org ft;
END WHILE;


TRUNCATE f_running_balance_computation_detail;
DROP TABLE IF EXISTS f_running_balance_computation_detail_temp;
DROP TABLE IF EXISTS f_running_balance_computation_detail_temp_org;

END //
DELIMITER ;