package org.apache.fineract.infrastructure.campaigns.sms.service;

public interface SmsSchedulerService {

    void sendRepaymentReminderToClient();

    void sendFirstOverdueRepaymentReminderToClient();

    void sendSecondOverdueRepaymentReminderToClient();

    void sendThirdOverdueRepaymentReminderToClient();

    void sendFourthOverdueRepaymentReminderToClient();
}
