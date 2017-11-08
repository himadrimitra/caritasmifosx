package org.apache.fineract.infrastructure.campaigns.sms.service;

import org.apache.fineract.infrastructure.core.service.RoutingDataSourceServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsSchedulerServiceImpl implements SmsSchedulerService {

    private final RoutingDataSourceServiceFactory dataSourceServiceFactory;
    
    @Autowired
    public SmsSchedulerServiceImpl(final RoutingDataSourceServiceFactory dataSourceServiceFactory) {
        this.dataSourceServiceFactory = dataSourceServiceFactory;
    }
    
    @Override
    public void sendRepaymentReminderToClient() {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendFirstOverdueRepaymentReminderToClient() {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendSecondOverdueRepaymentReminderToClient() {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendThirdOverdueRepaymentReminderToClient() {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendFourthOverdueRepaymentReminderToClient() {
        // TODO Auto-generated method stub

    }

}
