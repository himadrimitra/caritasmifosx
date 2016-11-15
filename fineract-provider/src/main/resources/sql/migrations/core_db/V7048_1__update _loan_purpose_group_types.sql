UPDATE `m_code_value` SET `code_value`='Income Generation' WHERE  `code_value`='Grouping' 
and `code_id`= (SELECT id FROM m_code WHERE code_name = 'LoanPurposeGroupType');