/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.email.domain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.fineract.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.dataqueries.service.ReadReportingService;
import org.apache.fineract.infrastructure.report.provider.ReportingProcessServiceProvider;
import org.apache.fineract.infrastructure.report.service.ReportingProcessService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.email.service.ReportMailingScheduleService;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Service
public class EmailReportGenerator {

    private final ReportingProcessServiceProvider reportingProcessServiceProvider;
    private final ReportMailingScheduleService reportMailingScheduleService;
    private final ReadReportingService readReportingService;
    private final EmailRepaymentScheduleRepository emailRepaymentScheduleRepository;
    private final String attachmentType = new String("PDF");
    private final String reportName = new String("Repayment Schedule");

    public static final String MIFOS_BASE_DIR = System.getProperty("user.home") + File.separator + ".mifosx";

    @Autowired
    public EmailReportGenerator(final ReportingProcessServiceProvider reportingProcessServiceProvider,
            final ReportMailingScheduleService reportMailingScheduleService, final ReadReportingService readReportingService,
            final EmailRepaymentScheduleRepository emailRepaymentScheduleRepository) {
        this.reportingProcessServiceProvider = reportingProcessServiceProvider;
        this.reportMailingScheduleService = reportMailingScheduleService;
        this.readReportingService = readReportingService;
        this.emailRepaymentScheduleRepository = emailRepaymentScheduleRepository;
    }

    /**
     * @param loan
     *            - Loan entity
     * @param emailId
     *            - email address of the customer
     * @param reportName
     *            - report name used to generate re-payment schedule
     * @param reportParams
     *            - parameters required by the report
     */
    void generateReportOutputStream(final Loan loan, String emailId, final String reportName,
            final MultivaluedMap<String, String> reportParams) {

        try {
            final String reportType = this.readReportingService.getReportType(reportName);

            final ReportingProcessService reportingProcessService = this.reportingProcessServiceProvider
                    .findReportingProcessService(reportType);

            if (reportingProcessService != null) {
                final Response processReport = reportingProcessService.processRequest(reportName, reportParams);
                final Object reponseObject = (processReport != null) ? processReport.getEntity() : null;

                if (reponseObject != null) {
                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byteArrayOutputStream.write((byte[]) reponseObject);

                    final String fileLocation = MIFOS_BASE_DIR + File.separator + "";
                    final String fileNameWithoutExtension = fileLocation + reportName.replace(" ", "");

                    // check if file directory exists, if not create directory
                    if (!new File(fileLocation).isDirectory()) {
                        new File(fileLocation).mkdirs();
                    }

                    else if (byteArrayOutputStream.size() > 0) {
                        final String fileName = fileNameWithoutExtension + "." + attachmentType;
                        // send the file to customer
                        this.sendReportFileToEmailRecipients(loan, emailId, fileName, byteArrayOutputStream);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param messages
     *            - list of mails that weren't sent to customers. These messages
     *            will be picked up by the boot up service and will be sent
     */
    void generateReportOutputStream(final Collection<EmailOutboundMessage> messages) {
        MultivaluedMap<String, String> reportParams;

        for (EmailOutboundMessage emailOutboundMessage : messages) {
            reportParams = new MultivaluedMapImpl();
            reportParams.add("R_loanId", emailOutboundMessage.getLoan().getId().toString());
            reportParams.add("output-type", attachmentType);
            reportParams.add("R_status", LoanStatus.ACTIVE.getValue().toString());
            generateReportOutputStream(emailOutboundMessage.getLoan(), emailOutboundMessage.getLoan().getClient().getEmailId(),
                    reportName, reportParams);

        }
    }

    private void sendReportFileToEmailRecipients(Loan loan, String emailId, final String fileName,
            final ByteArrayOutputStream byteArrayOutputStream) {
        File attachment = null ;
        if (emailId != null && !emailId.isEmpty()) {
            try {
                attachment = new File(fileName);
                final FileOutputStream outputStream = new FileOutputStream(attachment);

                byteArrayOutputStream.writeTo(outputStream);

                final EmailData reportEmailData = new EmailData(loan.getAccountNumber(), emailId, attachment);

              
                EmailOutboundMessage emailOutboundMessage = EmailOutboundMessage.instance(loan,
                        EmailSupportedEvents.LOAN_DISBURSAL.toString(), PortfolioProductType.LOAN.getValue(),
                        EmailStatus.PENDING.toString(),DateUtils.getLocalDateOfTenant().toDate());
                
                this.emailRepaymentScheduleRepository.save(emailOutboundMessage);

                this.reportMailingScheduleService.sendEmailWithAttachment(reportEmailData);

                emailOutboundMessage.setStatus(EmailStatus.SUCCESS.toString());
                this.emailRepaymentScheduleRepository.saveAndFlush(emailOutboundMessage);

                outputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(attachment != null) {
                    FileUtils.deleteQuietly(attachment) ;
                }
            }
        }
    }

}
