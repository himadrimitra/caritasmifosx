UPDATE job j SET j.name = 'Apply Holidays' , j.display_name = 'Apply Holidays' WHERE j.name = 'Apply Holidays To Loans';

UPDATE m_mandatory_savings_schedule mss SET mss.actualduedate = mss.duedate WHERE mss.actualduedate is null;