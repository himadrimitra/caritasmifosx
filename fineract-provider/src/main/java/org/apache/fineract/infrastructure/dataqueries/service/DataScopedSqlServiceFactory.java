package org.apache.fineract.infrastructure.dataqueries.service;

import org.apache.fineract.infrastructure.dataqueries.api.DataTableApiConstant;
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
            case DataTableApiConstant.JOURNAL_ENTRY_TABLE_NAME:
                scopedSqlService = new AccGlJournalEntryDataScopedSqlServiceImpl();
            break;
            case DataTableApiConstant.LOAN_APPLICATION_REFERENCE:
                scopedSqlService = new LoanApplicationreferenceDataScopedSqlServiceImpl ();
            break;    
            case DataTableApiConstant.VILLAGE:
                scopedSqlService = new VillageDataScopedSqlServiceImpl ();
            break;    
        }
        return scopedSqlService;
    }

}
