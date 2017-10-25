DELIMITER //
CREATE PROCEDURE `doClientSubStatusUpdates`()
	LANGUAGE SQL
	NOT DETERMINISTIC
	CONTAINS SQL
	SQL SECURITY DEFINER
	COMMENT ''
BEGIN

  DECLARE v_finished INT     DEFAULT 0;
 
  DECLARE v_client_id INTEGER;
  DECLARE v_client_substatus INTEGER;
  DECLARE v_client_new_substatus INTEGER;
  DECLARE newClient_CodeValue INTEGER DEFAULT 26;
  DECLARE v_shares_acc_no INTEGER;
  DECLARE v_last_txn_date DATE DEFAULT NULL;
 
  DECLARE v_substatus_updated_date DATE DEFAULT NULL;
  DECLARE numMonths_for_Dormancy INT DEFAULT 4;   
  DECLARE numMonths_for_Default INT DEFAULT 4;
  DECLARE dormantClient_CodeValue INT DEFAULT 28;
  DECLARE activeGoodClient_CodeValue INT DEFAULT 27;
  DECLARE defaultClient_CodeValue INT DEFAULT 29;   
  DECLARE v_repayment_last_txn_date DATE DEFAULT NULL;
  DECLARE v_substatus_from_migrated_tble INTEGER;
  DECLARE v_savings_acc_activation_date DATE DEFAULT NULL;
  DECLARE v_loan_disbursed_date DATE DEFAULT NULL;
  DECLARE v_shares_account_status INT;
  DECLARE v_is_substatus_updated INT DEFAULT 0;
  DECLARE v_loan_account LONG;
  DECLARE numMonths_WithRegularDeposits_forActiveGood INT DEFAULT 6;
  DECLARE v_client_migrated_substatus INTEGER;
  DECLARE v_client_migrated_date DATE DEFAULT NULL;
  DECLARE v_client_substatus_from_db INTEGER;                       
  DECLARE v_count_of_txn INT DEFAULT 0;
  DECLARE v_count_of_active_loan INT DEFAULT 0;   
 
  

    DECLARE active_client_cursor CURSOR FOR
    SELECT mc.id, mc.sub_status, mc.default_savings_account, msac.status_enum,
    csmd.sub_status, csmd.migrated_date
    FROM  m_client mc
    LEFT JOIN m_savings_account msac on mc.id = msac.client_id AND msac.id = mc.default_savings_account 
    LEFT JOIN client_sub_status_migration_date csmd on mc.id = csmd.client_id
    WHERE mc.status_enum = 300
    GROUP BY mc.id
    ORDER BY mc.id;
   
    DECLARE CONTINUE HANDLER
    FOR NOT FOUND SET v_finished = 1;   
   
   
    SELECT value INTO numMonths_for_Dormancy FROM c_configuration
                            WHERE name = 'Num-Months-WithoutDeposits-ForDormancySubStatus';
                            
     SELECT value INTO numMonths_WithRegularDeposits_forActiveGood FROM c_configuration 
				WHERE name = 'Num-Months-WithDeposits-ForGoodStandingSubStatus';                       
                           
                
     SELECT value INTO numMonths_for_Default FROM c_configuration
                WHERE name = 'Num-Months-WithoutRepayment-ForDefaultSubStatus';                           
                           
                           
    SELECT id INTO newClient_CodeValue FROM m_code_value WHERE
                            code_id = (SELECT id FROM m_code WHERE code_name = 'ClientSubStatus') AND
                            code_value = 'New Client';
                           
     SELECT id INTO activeGoodClient_CodeValue FROM m_code_value WHERE
            code_id = (SELECT id FROM m_code WHERE code_name = 'ClientSubStatus') AND
            code_value = 'Active in Good Standing';
           
     SELECT id INTO defaultClient_CodeValue FROM m_code_value WHERE
            code_id = (SELECT id FROM m_code WHERE code_name = 'ClientSubStatus') AND
            code_value = 'Default';   
           
    SELECT id INTO dormantClient_CodeValue FROM m_code_value WHERE
            code_id = (SELECT id FROM m_code WHERE code_name = 'ClientSubStatus') AND
            code_value = 'Dormant';           
           
           
     OPEN active_client_cursor;
   
        get_client : LOOP   
       
         FETCH active_client_cursor INTO v_client_id,v_client_substatus_from_db,v_shares_acc_no,v_shares_account_status,
			v_client_migrated_substatus, v_client_migrated_date ;
           
           IF v_finished = 1 THEN
             LEAVE get_client;
           END IF;
          
          SET v_client_new_substatus = NULL;
          SET v_client_substatus = NULL;
          
          
          
          IF v_client_migrated_substatus IS NOT NULL
            AND (v_client_migrated_substatus != '')
             THEN 
                 SET v_client_substatus = v_client_migrated_substatus;
          ELSE   
			    SET v_client_substatus = v_client_substatus_from_db;     
          END IF ;      
          
          
          BLOCK1: BEGIN 
          
            IF    (v_client_substatus IS NULL OR v_client_substatus = '')      
            THEN
                SET v_client_new_substatus = newClient_CodeValue;
            END IF; 
            
			 END BLOCK1;	  
            
            
            
            
          BLOCK2: BEGIN
                
			   SET  v_count_of_txn = 0;  
				
			  IF v_shares_acc_no IS NOT NULL  and (v_shares_account_status = 300)             
               THEN                 
                                
            SELECT  COUNT(*) kount into v_count_of_txn FROM (
				SELECT sav.id, sav.client_id, client.display_name AS client_name, MONTH(sav_txn.transaction_date), COUNT( * ) 
				FROM  m_savings_account sav
					LEFT OUTER JOIN m_savings_account_transaction sav_txn on sav.id = sav_txn.savings_account_id
					LEFT JOIN m_client client on sav.client_id = client.id
					LEFT JOIN client_sub_status_migration_date migration on migration.client_id=client.id
				WHERE
					sav.id = v_shares_acc_no AND
					client.status_enum = 300 AND
					sav.status_enum = 300 AND 
					sav_txn.transaction_type_enum = 1 AND 
					sav_txn.is_reversed =0 AND
					
 				   IF(NOW() >= DATE_ADD(IFNULL(v_client_migrated_date,'2099-01-01'),INTERVAL numMonths_WithRegularDeposits_forActiveGood MONTH),
					sav_txn.transaction_date BETWEEN v_client_migrated_date AND DATE_ADD(v_client_migrated_date,INTERVAL numMonths_WithRegularDeposits_forActiveGood MONTH),
				-- 	sav_txn.transaction_date BETWEEN   DATE_SUB(NOW(),INTERVAL numMonths_WithRegularDeposits_forActiveGood MONTH)  AND NOW()
				-- as discussed with John, we need to consider savings transaction between saving account activation date and today date
					
					sav_txn.transaction_date BETWEEN sav.activatedon_date AND NOW()
					 
					)
					 
					 GROUP BY 
					sav.id, sav.client_id, MONTH( sav_txn.transaction_date ) 
				) client_sav_txns
			GROUP BY client_sav_txns.client_id;
			
			        
			
			         IF v_count_of_txn IS NOT NULL AND v_count_of_txn >= 6
			            AND v_client_substatus IS NOT NULL
			            AND v_client_substatus IN (newClient_CodeValue)
					    	THEN 
					    	SET v_client_new_substatus = activeGoodClient_CodeValue;
							  
						END IF;   
               END IF;   
               
                  
          END BLOCK2;
            
               
              
          BLOCK3 : BEGIN   
              
                  
						SELECT 
	       					COUNT(*) INTO v_count_of_active_loan
						FROM m_loan mc
						WHERE mc.client_id = v_client_id
						AND mc.loan_status_id = 300;  
              
              
            IF v_shares_acc_no IS NOT NULL  and (v_shares_account_status = 300)             
            THEN 
           
                SELECT MAX(mst.transaction_date), msac.activatedon_date INTO v_last_txn_date, v_savings_acc_activation_date 
					 FROM m_savings_account msac
                LEFT JOIN m_savings_account_transaction mst on mst.savings_account_id = msac.id
                WHERE  msac.id = v_shares_acc_no and (mst.id is null or (mst.transaction_type_enum = 1
                AND mst.is_reversed = 0));
                 
                IF v_last_txn_date IS NOT NULL 
                   AND v_client_substatus IS NOT NULL
                
                THEN
                   
                    
                       IF v_client_migrated_date IS NOT NULL
                          AND(NOW() >= DATE_ADD(IFNULL(v_client_migrated_date,'2099-01-01'),INTERVAL numMonths_for_Dormancy MONTH))
						         THEN 
						             IF(v_last_txn_date < DATE_SUB(v_client_migrated_date,INTERVAL numMonths_for_Dormancy MONTH))
						                AND v_client_substatus NOT IN (newClient_CodeValue)
						                THEN 
										        SET  v_client_new_substatus = dormantClient_CodeValue;
										  ELSEIF(v_last_txn_date > DATE_SUB(v_client_migrated_date,INTERVAL numMonths_for_Dormancy MONTH))
										     AND v_client_substatus NOT IN (newClient_CodeValue,defaultClient_CodeValue,activeGoodClient_CodeValue) THEN
											     SET v_client_new_substatus =  activeGoodClient_CodeValue;
									    END IF;
									    
						      ELSEIF(v_last_txn_date < DATE_SUB(NOW(),INTERVAL numMonths_for_Dormancy MONTH))			  	   
						            AND v_client_substatus NOT IN (newClient_CodeValue,defaultClient_CodeValue)						  
						                THEN
                                       SET  v_client_new_substatus = dormantClient_CodeValue;
                        
                        
                        ELSEIF(v_last_txn_date > DATE_SUB(NOW(),INTERVAL numMonths_for_Dormancy MONTH))
							        AND v_client_substatus NOT IN (newClient_CodeValue,defaultClient_CodeValue)
									  THEN
                    		           SET v_client_new_substatus =  activeGoodClient_CodeValue;
                    		           
                    		ELSEIF(v_last_txn_date < DATE_SUB(NOW(),INTERVAL numMonths_for_Dormancy MONTH))			  	   
						            AND v_client_substatus IN (defaultClient_CodeValue)
										AND v_count_of_active_loan = 0						  
						                THEN
                                       SET  v_client_new_substatus = dormantClient_CodeValue;
			               
								ELSEIF(v_last_txn_date > DATE_SUB(NOW(),INTERVAL numMonths_for_Dormancy MONTH))
							        AND v_client_substatus IN (defaultClient_CodeValue)
									  AND v_count_of_active_loan = 0
									  THEN
                    		           SET v_client_new_substatus =  activeGoodClient_CodeValue;										           
                    		           
                    		           
                    END IF;
                END IF;               
                  
            END IF;
            
          END BLOCK3;
			   
                                        
         SET v_loan_disbursed_date = NULL;  
         
           
               
              
           BLOCK4 : BEGIN
           
                    DECLARE v_finished_2 INT     DEFAULT FALSE;  
                    DECLARE loanAccounts CURSOR FOR
                        SELECT  mc.id
                        FROM m_loan mc
                        WHERE mc.client_id = v_client_id
                        AND mc.loan_status_id = 300
                        GROUP BY mc.id;
                   
                    DECLARE CONTINUE HANDLER
                    FOR NOT FOUND SET v_finished_2 = TRUE;                       
                   
                    OPEN     loanAccounts;  
                              
                    get_loan_account : LOOP   
                   
                   
   
                        FETCH loanAccounts INTO v_loan_account;
                            SELECT v_loan_account, v_finished_2;
                        IF v_finished_2 THEN
                            LEAVE get_loan_account;
                        END IF;
                             
                        SELECT
                        max(mlt.transaction_date), ml.disbursedon_date INTO v_repayment_last_txn_date, v_loan_disbursed_date
                        FROM m_loan ml
                        JOIN m_loan_transaction mlt ON ml.id = mlt.loan_id
                        WHERE ml.client_id = v_client_id
                        AND ml.loan_status_id = 300
                        AND mlt.transaction_type_enum in (1,2)
                        AND mlt.is_reversed = 0
                        AND ml.id = v_loan_account;
                       
                        SELECT     v_client_id,            v_repayment_last_txn_date;
                                         
                        IF v_repayment_last_txn_date IS NOT NULL
                           AND v_client_substatus IS NOT NULL
                            THEN
                                IF v_client_migrated_date IS NOT NULL
										     AND (NOW() >= DATE_ADD(IFNULL(v_client_migrated_date,'2099-01-01'),INTERVAL numMonths_for_Default MONTH))
										     AND  v_repayment_last_txn_date < DATE_SUB(v_client_migrated_date,INTERVAL numMonths_for_Default MONTH)
											  AND v_client_substatus NOT IN (newClient_CodeValue)
                                
										    THEN
										    
                                    SET v_client_new_substatus = defaultClient_CodeValue;  
										  
		                          ELSEIF(v_repayment_last_txn_date < DATE_SUB(NOW(),INTERVAL numMonths_for_Default MONTH))
                                 AND v_client_substatus NOT IN (newClient_CodeValue)
               
										  THEN
										  
                                    SET v_client_new_substatus = defaultClient_CodeValue;  
												                                          
                                ELSEIF(v_repayment_last_txn_date > DATE_SUB(NOW(),INTERVAL numMonths_for_Default MONTH))
										   AND (v_client_substatus NOT IN (dormantClient_CodeValue, newClient_CodeValue, activeGoodClient_CodeValue))
                               
										  THEN
                                        SET v_client_new_substatus = activeGoodClient_CodeValue;                                       
                         END IF;                                                     
                                     
                                 
                        ELSEIF (v_repayment_last_txn_date IS NULL) OR (v_repayment_last_txn_date = '')
                        AND v_client_substatus IS NOT NULL
                        THEN
                                     
                            SELECT
                                        ml.disbursedon_date INTO v_loan_disbursed_date
                            FROM m_loan ml
                            WHERE ml.id = v_loan_account
                            AND ml.loan_status_id = 300;

                            CASE
                                 WHEN   v_loan_disbursed_date IS NOT NULL
                                    AND IF(NOW() >= DATE_ADD(IFNULL(v_client_migrated_date,'2099-01-01'),INTERVAL numMonths_for_Default MONTH),
		                              v_loan_disbursed_date < DATE_SUB(v_client_migrated_date,INTERVAL numMonths_for_Default MONTH),
					                     v_loan_disbursed_date < DATE_SUB(NOW(),INTERVAL numMonths_for_Default MONTH))
                                    
                                    AND v_client_substatus NOT IN (newClient_CodeValue)
                                    THEN
                                            set v_client_new_substatus = defaultClient_CodeValue;                                                                                        
                            END CASE;
                                       
                                       
                        END IF;                         

                    END LOOP get_loan_account;
                      
                    CLOSE loanAccounts;      
          
           END BLOCK4;
           
             
              
           
               BLOCK5 : BEGIN
                   
                                 CASE
                                          WHEN
                                                v_shares_account_status NOT IN(300)
                                                AND v_client_substatus IS NOT NULL
                                                         AND  v_loan_disbursed_date IS NULL
																			AND v_client_substatus NOT IN (newClient_CodeValue,defaultClient_CodeValue)                                                          
                                            THEN
                                                   
                                                   SET v_client_new_substatus = newClient_CodeValue;
                                                   
                                          
													 WHEN 	  
													           v_shares_account_status IN (300)
                                                AND v_client_substatus IS NOT NULL
                                                AND v_loan_disbursed_date IS NULL
																AND v_client_substatus IN (newClient_CodeValue,defaultClient_CodeValue)                                                                    AND v_count_of_txn >= numMonths_WithRegularDeposits_forActiveGood
                                            THEN       
                                                
                                                SET v_client_new_substatus = activeGoodClient_CodeValue;
                                         
													WHEN          
													          v_shares_account_status IN (300)
                                                AND v_client_substatus IS NOT NULL
                                                AND v_loan_disbursed_date IS NULL
																AND v_client_substatus IN (defaultClient_CodeValue,activeGoodClient_CodeValue)                                                             AND v_last_txn_date < DATE_SUB(NOW(),INTERVAL numMonths_for_Dormancy MONTH)
														  
														  THEN       
                                                
                                                SET v_client_new_substatus = dormantClient_CodeValue;
																
														            
                                      ELSE BEGIN END;
                                           
                                 END CASE;       
                   
                    END BLOCK5;
           
           
           
           
         SET v_finished =0;
         
			
			IF v_client_new_substatus IS NOT NULL
			   THEN
	          
				 UPDATE m_client mc
	              SET mc.sub_status = v_client_new_substatus
	           WHERE mc.id = v_client_id;  
           
			END IF;
			
			
		DELETE  FROM  client_sub_status_migration_date
		WHERE sub_status=newClient_CodeValue AND migrated_date <= DATE_SUB(now(),INTERVAL numMonths_WithRegularDeposits_forActiveGood MONTH)
		AND client_id = v_client_id;
		
		DELETE FROM client_sub_status_migration_date
		WHERE sub_status=activeGoodClient_CodeValue AND migrated_date <= DATE_SUB(now(),INTERVAL numMonths_for_Default MONTH)
		AND client_id = v_client_id;
		
		DELETE FROM client_sub_status_migration_date
		WHERE sub_status=defaultClient_CodeValue AND migrated_date <= DATE_SUB(now(),INTERVAL numMonths_for_Dormancy MONTH)
		AND client_id = v_client_id;
			
			
         
     END LOOP get_client;
               
   CLOSE active_client_cursor;       

     
	  
	 


END

//