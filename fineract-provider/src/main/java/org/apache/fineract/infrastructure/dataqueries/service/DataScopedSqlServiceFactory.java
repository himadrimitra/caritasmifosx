package org.apache.fineract.infrastructure.dataqueries.service;

import org.springframework.stereotype.Service;

@Service
public class DataScopedSqlServiceFactory {

    public static DataScopedSqlService getDataScopedSqlService(final String dataScopedSqlService) {

        DataScopedSqlService scopedSqlService = null;
        switch (dataScopedSqlService) {
            case "m_loan":
                scopedSqlService = new MLoanDataScopedSqlServiceImpl();
            break;
            case "m_savings_account":
                scopedSqlService = new MSavingsAccountDataScopedSqlServiceImpl();
            break;
            case "m_client":
                scopedSqlService = new MClientDataScopedSqlServiceImpl();
            break;
            case "m_group":
                scopedSqlService = new MGroupOrMCenterDataScopedSqlServiceImpl();
            break;
            case "m_center":
                scopedSqlService = new MGroupOrMCenterDataScopedSqlServiceImpl();
            break;
            case "m_office":
                scopedSqlService = new MOfficeDataScopedSqlServiceImpl();
            break;
            case "m_product_loan":
                scopedSqlService = new MProductLoanDataScopedSqlServiceImpl();
            break;
            case "m_savings_product":
                scopedSqlService = new MSavingsProductDataScopedSqlServiceImpl();
            break;
            case "acc_gl_journal_entry":
                scopedSqlService = new AccGlJournalEntryDataScopedSqlServiceImpl();
            break;
        }
        return scopedSqlService;
    }

}
