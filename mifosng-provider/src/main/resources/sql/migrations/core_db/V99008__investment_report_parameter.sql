

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Saving Investment', 'Table', NULL, 'Investment', 'select mg.display_name as \'Group Name\',\nmsa.activatedon_date as \'Deposit Date\',\nconcat(msa.account_no, \'-\' , msp.name) as \'Deposit Account\',\nmo.name as \'Office\',\nmc.display_name as \'Invested With\', \nconcat(ml.account_no, \'-\', mpl.name) as \'Investment Account\',\nml.principal_amount as \'Invested Amount\',\nml.maturedon_date as \'Maturing On\',\n(ml.principal_amount + ml.interest_outstanding_derived) as \'Maturity Value\'\n\nfrom m_investment msi\nleft join m_loan ml on msi.loan_id = ml.id\nleft join m_product_loan mpl on ml.product_id = mpl.id\nleft join m_client mc on ml.client_id = mc.id\nleft join m_savings_account msa on msi.saving_id = msa.id\nleft join m_savings_product msp on msa.product_id = msp.id\nleft join m_group mg on msa.group_id = mg.id\nleft join m_office mo on mg.office_id = mo.id\n\nwhere mo.id = ${officeId} \n\nand msa.activatedon_date between \'${startDate}\' and \'${endDate}\'', 'Saving Investment', 0, 1);
