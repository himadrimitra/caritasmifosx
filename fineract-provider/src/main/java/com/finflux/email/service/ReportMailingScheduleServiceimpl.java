/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.email.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.fineract.infrastructure.configuration.data.SMTPCredentialsData;
import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.finflux.email.domain.EmailData;

@Service
public class ReportMailingScheduleServiceimpl implements ReportMailingScheduleService {

    private final ExternalServicesPropertiesReadPlatformService externalServicesReadPlatformService;

    @Autowired
    public ReportMailingScheduleServiceimpl(final ExternalServicesPropertiesReadPlatformService externalServicesReadPlatformService) {
        this.externalServicesReadPlatformService = externalServicesReadPlatformService;

    }

    @Override
    public void sendEmailWithAttachment(EmailData emailData) {
        try {
            final SMTPCredentialsData smtpCredentialsData = this.externalServicesReadPlatformService.getSMTPCredentials();
            JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
            javaMailSenderImpl.setHost(smtpCredentialsData.getHost());
            javaMailSenderImpl.setPort(Integer.parseInt(smtpCredentialsData.getPort()));
            javaMailSenderImpl.setUsername(smtpCredentialsData.getUsername());
            javaMailSenderImpl.setPassword(smtpCredentialsData.getPassword());
            javaMailSenderImpl.setJavaMailProperties(this.getJavaMailProperties(smtpCredentialsData.getHost()));

            MimeMessage mimeMessage = javaMailSenderImpl.createMimeMessage();

            // use the true flag to indicate you need a multipart message
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(new InternetAddress(smtpCredentialsData.getUsername(), emailData.getCenterDisplayName()));
            mimeMessageHelper.setTo(emailData.getTo());
            mimeMessageHelper.setText(emailData.getText());
            mimeMessageHelper.setSubject(emailData.getSubject());
            if (emailData.getAttachment() != null) {
                mimeMessageHelper.addAttachment(emailData.getAttachment().getName(), emailData.getAttachment());
            }

            javaMailSenderImpl.send(mimeMessage);
        }

        catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @return Properties object containing JavaMail properties
     **/
    private Properties getJavaMailProperties(String host) {
        Properties properties = new Properties();

        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.trust", host);

        return properties;
    }

}
