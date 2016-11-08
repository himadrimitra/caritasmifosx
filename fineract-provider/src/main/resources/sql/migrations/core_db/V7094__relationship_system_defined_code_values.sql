INSERT INTO `m_code_value` (`code_id`, `code_value`) VALUES
((select mc.id from m_code mc where mc.code_name = 'Relationship'), 'Father'),
((select mc.id from m_code mc where mc.code_name = 'Relationship'), 'Spouse'),
((select mc.id from m_code mc where mc.code_name = 'Relationship'), 'father-in-law');
