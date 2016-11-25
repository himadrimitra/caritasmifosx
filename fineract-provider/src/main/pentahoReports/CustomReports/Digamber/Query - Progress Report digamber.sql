

/*Since it has too many sub reports Only loan arreas is added*/
select '#loan in arres' ,count(loan_id) 
from m_loan_arrears_aging mlar
left join m_loan ml
on mlar.loan_id=ml.id
left join m_client mc 
on ml.client_id= mc.id
left join m_office mo
on mc.office_id=mo.id
where mo.id=${Branch} and ml.loan_status_id=300 and mc.status_enum=300 and mlar.principal_overdue_derived > 0 
 union 
 select '#client in arres', count(mc.id)
from m_client mc
left join m_loan ml
on ml.client_id=mc.id
left join m_loan_arrears_aging  mlarr
on mlarr.loan_id=ml.id
left join m_office mo
on mc.office_id= mo.id
where mc.status_enum=300 and mlarr.principal_overdue_derived > 0 and ml.loan_status_id=300 and mo.id=${Branch}
union
select 'Amount in arres',sum( mlarr.total_overdue_derived)
from m_loan_arrears_aging mlarr
inner join m_loan ml
on mlarr.loan_id=ml.id
left join m_client mc 
on ml.client_id= mc.id
left join m_office mo
on mc.office_id=mo.id
where mo.id=${Branch}
union
select  'Amount outstanding Arre', sum(ml.principal_outstanding_derived)
from m_loan ml                                                                                                                                                                                                                                                                              