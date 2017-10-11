/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.reportmailingjob.service;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.fineract.infrastructure.configuration.data.SMTPCredentialsData;
import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.apache.fineract.infrastructure.reportmailingjob.data.ReportMailingJobEmailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class ReportMailingJobEmailServiceImpl implements ReportMailingJobEmailService {
    private final ExternalServicesPropertiesReadPlatformService externalServicesReadPlatformService;
    
    /** 
     * ReportMailingJobEmailServiceImpl constructor
     **/
    @Autowired
    public ReportMailingJobEmailServiceImpl(final ExternalServicesPropertiesReadPlatformService externalServicesReadPlatformService) {
        this.externalServicesReadPlatformService = externalServicesReadPlatformService;
        
    }

    @Override
    public void sendEmailWithAttachment(ReportMailingJobEmailData reportMailingJobEmailData) {
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
            
            mimeMessageHelper.setTo(reportMailingJobEmailData.getTo());
            mimeMessageHelper.setText(reportMailingJobEmailData.getText());
            mimeMessageHelper.setSubject(reportMailingJobEmailData.getSubject());
            if (reportMailingJobEmailData.getAttachment() != null) {
                mimeMessageHelper.addAttachment(reportMailingJobEmailData.getAttachment().getName(), reportMailingJobEmailData.getAttachment());
            }
            
            javaMailSenderImpl.send(mimeMessage);
        } 
        
        catch (MessagingException e) {
            // handle the exception
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
