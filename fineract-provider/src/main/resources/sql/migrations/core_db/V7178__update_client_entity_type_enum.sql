UPDATE acc_gl_journal_entry journalEntry
set journalEntry.entity_type_enum = 5
where journalEntry.entity_type_enum = 2 and journalEntry.client_transaction_id IS NOT NULL;