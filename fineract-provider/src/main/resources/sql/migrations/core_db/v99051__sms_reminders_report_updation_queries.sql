update stretchy_report a
join stretchy_report b on b.id = a.id
set a.report_sql = " select 
       a.client_name,
       a.Guarantor_name,
		 a.Guarantor_Branch,
		 a.client_mobile_no,
		 a.loan_id,a.client_id as id,
		 productshort_Name,
		 a.Guarantor_mobile_no as mobileNo,
		 a.comitted_Shares
from
		(select cl.firstname as client_name
				,ifnull(go.firstname,concat(gu.firstname,gu.lastname)) as Guarantor_name
				,ifnull(o.name,'NA ') as Guarantor_Branch
				,ifnull(go.mobile_no,gu.mobile_number) as Guarantor_mobile_no
				,co.name as Client_Branch
				,ifnull(cl.mobile_no,'NA ') as client_mobile_no
				,
				cl.id as client_id,l.id as loan_id,mp.short_name as productshort_Name,
				ifnull(gfd.amount_remaining_derived,0) as comitted_Shares, 
					max(mlt.transaction_date) as last_txn_date,
					ifnull(sum(mlaa.total_overdue_derived),0) as overdueAmount 
					   
					from m_loan l
					inner join m_loan_transaction mlt on mlt.loan_id = l.id and mlt.is_reversed = 0
					inner join m_product_loan mp on mp.id=l.product_id 
					inner join m_client cl on cl.id=l.client_id 
					inner join m_guarantor gu on gu.loan_id=l.id
					left join m_guarantor_funding_details gfd on gfd.guarantor_id=gu.id
					left join m_client go on go.id=gu.entity_id
				   join m_savings_account msa on go.id = msa.client_id and msa.client_id != l.client_id
					left join m_office o on o.id=go.office_id
					left join m_office co on co.id=cl.office_id
					inner join OfficeDetails od on od.office_id=co.id
					join m_loan_arrears_aging mlaa on mlaa.loan_id = l.id
					where 
					l.loan_status_id=300
					and   gu.is_active=1
					and gfd.status_enum = 100
					and od.sms_enabled=true
					and case when ${officeId} =0 then
			         1=1
			         else 
			         o.id = ${officeId}
			      end
			      and go.status_enum = 300
			      
			      and 
			      
					case when ( select 
									        count(esd.mobile_no)
									from sms_messages_outbound esd
									JOIN sms_campaign cam on cam.id = esd.campaign_id
									JOIN stretchy_report rp on rp.id = cam.report_id
									where (esd.status_enum = 200)
									and rp.report_name = 'DefaultWarning -  guarantors'
									and DATEDIFF('${startDate}',esd.submittedon_date) between ${fromX} and ${toY}
	     
		                   ) >0  then
		
		            ifnull(go.mobile_no,gu.mobile_number) not in (
		            
									select esd.mobile_no
									from sms_messages_outbound esd
									JOIN sms_campaign cam on cam.id = esd.campaign_id
									JOIN stretchy_report rp on rp.id = cam.report_id
									where (esd.status_enum = 200)
									and rp.report_name = 'DefaultWarning -  guarantors'
									and DATEDIFF('${startDate}',esd.submittedon_date) between ${fromX} and ${toY}
									group by esd.mobile_no
									
					    )
					   else
						   1
					   end
			      
					   group by l.id, gu.id
					
					
					UNION 
					
					
				 select cl.firstname as client_name
				,ifnull(go.firstname,concat(gu.firstname,gu.lastname)) as Guarantor_name
				,ifnull(o.name,'NA ') as Guarantor_Branch
				,ifnull(go.mobile_no,gu.mobile_number) as Guarantor_mobile_no
				,co.name as Client_Branch
				,ifnull(cl.mobile_no,'NA ') as client_mobile_no
				,cl.id as client_id,l.id as loan_id,mp.short_name as productshort_Name,
				ifnull(gfd.amount_remaining_derived,0) as comitted_Shares,  
				'No_Date' as last_txn_date,
				 ifnull(sum(mlaa.total_overdue_derived),0) as overdueAmount
					   
					from m_loan l
					inner join m_loan_transaction mlt on mlt.loan_id = l.id and mlt.is_reversed = 0
					inner join m_product_loan mp on mp.id=l.product_id 
					inner join m_client cl on cl.id=l.client_id 
					inner join m_guarantor gu on gu.loan_id=l.id
					left join m_guarantor_funding_details gfd on gfd.guarantor_id=gu.id
					left join m_client go on go.id=gu.entity_id
				   join m_savings_account msa on go.id = msa.client_id and msa.client_id != l.client_id
					left join m_office o on o.id=go.office_id
					left join m_office co on co.id=cl.office_id
					inner join OfficeDetails od on od.office_id=co.id
					join m_loan_arrears_aging mlaa on mlaa.loan_id = l.id
					
					where l.loan_status_id=300
					and   gu.is_active=1
					and gfd.status_enum = 100
					and od.sms_enabled=true
					and case when ${officeId} =0 then
			         1=1
			         else 
			         o.id = ${officeId}
			      end
			      
			      and go.status_enum = 300
			      
			      and 
			      
					case when (select 
					count(esd.mobile_no)
					from sms_messages_outbound esd
					JOIN sms_campaign cam on cam.id = esd.campaign_id
					JOIN stretchy_report rp on rp.id = cam.report_id
					where (esd.status_enum = 400)
					and rp.report_name = 'DefaultWarning -  guarantors'
					and DATEDIFF('${startDate}',esd.submittedon_date) between ${fromX} and ${toY}
	     
		            ) >0 then
		
		    ifnull(go.mobile_no,gu.mobile_number) in (
						select esd.mobile_no
						from sms_messages_outbound esd
						JOIN sms_campaign cam on cam.id = esd.campaign_id
						JOIN stretchy_report rp on rp.id = cam.report_id
						where (esd.status_enum = 400)
						and rp.report_name = 'DefaultWarning -  guarantors'
						and DATEDIFF('${startDate}',esd.submittedon_date) between ${fromX} and ${toY}
						group by esd.mobile_no
					)
					 else
						0
					  end
			      
					group by l.id, gu.id
					
					
		 )a
	  
	where a.overdueAmount > 0
	and
	datediff('${startDate}',a.last_txn_date) >= 150 or a.last_txn_date = 'No_Date'"

where b.report_name = 'DefaultWarning -  guarantors';




update stretchy_report a
join stretchy_report b on b.id = a.id

set a.report_sql = " update 

select a.display_name,a.branch_name,a.last_transaction_date,
       ifnull(a.mobile_no, 'NA') as mobileNo,
		 a.savings_Id,a.client_Id as id,a.product_Short_Name 
from
(select cl.display_name,sa.product_id,cl.id as client_Id ,
        cl.default_savings_product,sa.id as savings_Id,
		  mo.name as branch_name,mp.short_name as product_Short_Name,
        cl.mobile_no as mobile_no,
        MAX(transaction_date)as last_transaction_date 
from m_client cl 
inner join m_office mo on mo.id=cl.office_id
inner join OfficeDetails od on od.office_id=mo.id
inner join m_savings_account sa on sa.client_id=cl.id 
inner join m_savings_product mp on mp.id=sa.product_id
left join m_savings_account_transaction msa on msa.savings_account_id=sa.id
where  sa.id=cl.default_savings_account
and (msa.transaction_type_enum=1 or msa.transaction_type_enum is null)
and 
cl.status_enum = 300 
and sa.status_enum=300
and od.sms_enabled=true
and case when ${officeId} =0 then
         1=1
         else 
         mo.id = ${officeId}
         end
group by sa.account_no)a
where 
if(a.last_transaction_date is null, TIMESTAMPDIFF (MONTH,curDate(),'${startDate}')=0,
 TIMESTAMPDIFF (MONTH,a.last_transaction_date,'${startDate}')=4) "
 
 
where  b.report_name = 'DormancyWarning - Clients';



update stretchy_report a
join stretchy_report b on b.id = a.id

set a.report_sql = " select a.client_name,a.branch_name,a.overdue_amount,a.mobile_no as mobileNo,
       a.loan_id,a.client_id as id,a.productshort_Name
from
    (    select c.firstname as client_name,o.name as branch_name, c.mobile_no as mobile_no,
	             date_format(lrc.duedate,'%M ')as month,
                c.id as client_id,l.id as loan_id,
					 mp.short_name as productshort_Name,
					 lrc.duedate as duedate, 
					 max(mlt.transaction_date) as last_txn_date,
                ifnull(mlaa.total_overdue_derived,0) as overdue_amount,
              if (((lrc.principal_amount+lrc.interest_amount)>(ifnull(lrc.principal_completed_derived,0)
				       +ifnull(lrc.interest_completed_derived,0))),
						 (datediff('${startDate}',date_add(date_add(makedate(extract(year from lrc.duedate),
						 extract(day from lrc.duedate)),interval extract(month from lrc.duedate) +3 month),
						 interval 5 day))),null) as days,
						 
				   ifnull(sum(mlaa.total_overdue_derived),0) overdueAmount		   
						  
			     from m_client c 
				  inner join m_loan l on c.id=l.client_id
				  inner join m_loan_transaction mlt on mlt.loan_id = l.id
				  and mlt.is_reversed = 0
				  inner join m_product_loan mp on mp.id=l.product_id 
			     inner join m_loan_repayment_schedule lrc on lrc.loan_id=l.id
			     inner join m_office o on c.office_id=o.id
			     inner join OfficeDetails od on od.office_id=o.id and od.sms_enabled=true
			     join m_loan_arrears_aging mlaa on mlaa.loan_id = l.id
			     where datediff('${startDate}',lrc.duedate)>=1       
			     and  lrc.completed_derived=0
			     and l.loan_status_id=300
			     and case when ${officeId} =0 then
		         1=1
		         else 
		         o.id = ${officeId}
		        end
		        and case when ( select 
					count(esd.mobile_no)
					from sms_messages_outbound esd
					JOIN sms_campaign cam on cam.id = esd.campaign_id
					JOIN stretchy_report rp on rp.id = cam.report_id
					where (esd.status_enum = 200 OR esd.status_enum =300)
					and rp.report_name = 'DefaultWarning - Clients'
					and DATEDIFF('${startDate}',esd.submittedon_date) between ${fromX} and ${toY}
			 
				    ) >0 then
		
				c.mobile_no not in (
					select esd.mobile_no
					from sms_messages_outbound esd
					JOIN sms_campaign cam on cam.id = esd.campaign_id
					JOIN stretchy_report rp on rp.id = cam.report_id
					where (esd.status_enum = 200 OR esd.status_enum =300)
					and rp.report_name = 'DefaultWarning - Clients'
					and DATEDIFF('${startDate}',esd.submittedon_date) between ${fromX} and ${toY}
					group by esd.mobile_no
				)
				else
				1
			   end
						        
				group by lrc.loan_id
				  
				  
				  
				  UNION 
				  
				  
				  
				  select c.firstname as client_name,o.name as branch_name, c.mobile_no as mobile_no,
	             date_format(lrc.duedate,'%M ')as month,
                c.id as client_id,l.id as loan_id,
					 mp.short_name as productshort_Name,
					 lrc.duedate as duedate, 
					 'No_Date' as last_txn_date,
                ifnull(mlaa.total_overdue_derived,0) as overdue_amount,
              if (((lrc.principal_amount+lrc.interest_amount)>(ifnull(lrc.principal_completed_derived,0)
				       +ifnull(lrc.interest_completed_derived,0))),
						 (datediff('${startDate}',date_add(date_add(makedate(extract(year from lrc.duedate),
						 extract(day from lrc.duedate)),interval extract(month from lrc.duedate) +3 month),
						 interval 5 day))),null) as days,
						 
						 ifnull(sum(mlaa.total_overdue_derived),0) overdueAmount  
						  
			     from m_client c 
				  inner join m_loan l on c.id=l.client_id
				  inner join m_loan_transaction mlt on mlt.loan_id = l.id 
				  and mlt.is_reversed = 0
				  inner join m_product_loan mp on mp.id=l.product_id 
			     inner join m_loan_repayment_schedule lrc on lrc.loan_id=l.id
			     inner join m_office o on c.office_id=o.id
			     inner join OfficeDetails od on od.office_id=o.id and od.sms_enabled=true
			     join m_loan_arrears_aging mlaa on mlaa.loan_id = l.id
			     where
			       l.loan_status_id=300
			     and 
			     
			     case when ${officeId} =0 then
		         1=1
		         else 
		         o.id = ${officeId}
		        end
		        
		   and 
			
			case when (  select 
					count(esd.mobile_no)
					from sms_messages_outbound esd
					JOIN sms_campaign cam on cam.id = esd.campaign_id
					JOIN stretchy_report rp on rp.id = cam.report_id
					where (esd.status_enum = 400)
					and rp.report_name = 'DefaultWarning - Clients'
					and DATEDIFF('${startDate}',esd.submittedon_date) between ${fromX} and ${toY}
	     
		            ) >0 then
		
		    c.mobile_no in (
						select esd.mobile_no
						from sms_messages_outbound esd
						JOIN sms_campaign cam on cam.id = esd.campaign_id
						JOIN stretchy_report rp on rp.id = cam.report_id
						where (esd.status_enum = 400)
						and rp.report_name = 'DefaultWarning - Clients'
						and DATEDIFF('${startDate}',esd.submittedon_date) between ${fromX} and ${toY}
						group by esd.mobile_no
		   					)
					 else
						0
					  end
		        
				  group by lrc.loan_id
				  
				  
				  
				  
	  )a
	  
	  where a.overdue_amount > 0
     and datediff('${startDate}',a.last_txn_date) >= 150 OR a.last_txn_date = 'No_Date' "
 
 
where  b.report_name = 'DefaultWarning - Clients';



update stretchy_report a
join stretchy_report b on b.id = a.id
set a.report_sql = " (
		  SELECT 

			   mc.id, 
				 mc.firstname, 
				 mc.middlename as middlename,
			   mc.lastname, 
				 mc.display_name as FullName, 
				 mc.mobile_no as mobileNo, 
				 mc.group_name as GroupName, 
				 mo.name as officename, 
				 ml.id as loanId,
				 ml.account_no as accountnumber, 
				 ml.principal_amount_proposed as loanamount, 
				 ml.annual_nominal_interest_rate as annualinterestrate, 
										 mc.firstname as loneeName
			 FROM m_office mo 
			 JOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, '%') 
			 AND ounder.hierarchy like CONCAT('.', '%') 
			LEFT JOIN ( select  ml.id as loanId,  ifnull(mc.id,mc2.id) as id,  
							ifnull(mc.firstname,mc2.firstname) as firstname,  
								  ifnull(mc.middlename,ifnull(mc2.middlename,(''))) as middlename,  
								  ifnull(mc.lastname,mc2.lastname) as lastname,  
								  ifnull(mc.display_name,mc2.display_name) as display_name,  
								  ifnull(mc.status_enum,mc2.status_enum) as status_enum, 
								  ifnull(mc.mobile_no,mc2.mobile_no) as mobile_no, 
								  ifnull(mg.office_id,mc2.office_id) as office_id, 
								  ifnull(mg.staff_id,mc2.staff_id) as staff_id, 
								  mg.id as group_id, mg.display_name as group_name 
						 from m_loan ml 
						 left join m_group mg on mg.id = ml.group_id 
						 left join m_group_client mgc on mgc.group_id = mg.id 
						 left join m_client mc on mc.id = mgc.client_id 
						 left join m_client mc2 on mc2.id = ml.client_id 
						 order by loanId 

				   ) mc on mc.office_id = ounder.id  
			left join m_loan ml on ml.id = mc.loanId 
			
			
			WHERE mc.status_enum = 300 
			and mc.mobile_no is not null 
			and (mo.id = ${officeId} or ${officeId} = -1) 
			and (mc.staff_id = ${loanOfficerId} or ${loanOfficerId} = -1) 
			and (ml.id = ${loanId} or ${loanId} = -1)and (mc.id = ${clientId} or ${clientId} = -1)
			and (mc.group_id = ${groupId} or ${groupId} = -1)
			and (ml.loan_type_enum = ${loanType} or ${loanType} = -1)
			
			GROUP BY mc.id 
			
			)
			
			
			
		   UNION 
			
			
			
			( select 
			
							 extGuarantor.id, 
				 extGuarantor.firstname, 
				 extGuarantor.middlename as middlename,
										 extGuarantor.lastname, 
				 extGuarantor.display_name as FullName, 
				 extGuarantor.mobile_no as mobileNo, 
				 '' as GroupName, 
				 ounder.name as officename, 
				 ml.id as loanId,
				 ml.account_no as accountnumber, 
				 ml.principal_amount_proposed as loanamount, 
				 ml.annual_nominal_interest_rate as annualinterestrate, 
							mc.firstname as loneeName 
			
			from m_office mo 
					   JOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, '%') 
				 AND ounder.hierarchy like CONCAT('.', '%') 
			join m_client mc on mc.office_id = ounder.id
			join m_loan ml on ml.client_id = mc.id
			join m_guarantor mg on mg.loan_id = ml.id 
			join m_client extGuarantor on extGuarantor.id = mg.entity_id and extGuarantor.id != mc.id
			
			where extGuarantor.status_enum = 300 
			and extGuarantor.mobile_no is not null
			and (mo.id = ${officeId} or ${officeId} = -1) 
			and (extGuarantor.staff_id = ${loanOfficerId} or ${loanOfficerId} = -1) 
			and (ml.loan_type_enum = ${loanType} or ${loanType} = -1)
			and (ml.id = ${loanId} or ${loanId} = -1)and (mc.id = ${clientId} or ${clientId} = -1)
			
			group by mc.id
			 
			) "
 
 
where  b.report_name = 'Loan Approved';