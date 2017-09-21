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
package org.apache.fineract.infrastructure.core.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.springframework.stereotype.Service;

@Service
public class GmailBackedPlatformEmailService implements PlatformEmailService {

    @Override
    public void sendToUserAccount(final EmailDetail emailDetail, final String unencodedPassword) {
        final Email email = new SimpleEmail();

        final String authuserName = "it.caritas.nairobi@gmail.com";

        final String authuser = "it.caritas.nairobi@gmail.com";
        final String authpwd = "downbytheR123";

        // Very Important, Don't use email.setAuthentication()
        email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
        email.setDebug(false); // true if you want to debug
        email.setHostName("smtp.gmail.com");
        try {
            email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
            email.setFrom(authuser, authuserName);

            final StringBuilder subjectBuilder = new StringBuilder().append("Caritas Nairobi Mifos X Credentials");
            
            email.setSubject(subjectBuilder.toString());

            final String sendToEmail = emailDetail.getAddress();
            
            final StringBuilder messageBuilder = new StringBuilder().append("Hello ").append(emailDetail.getContactName()).append(",")
            		 .append("\n\n")
            		 .append("You are receiving this email as your email account: ")
            		 .append(sendToEmail).append(" has being used to create a user account for a group named ")
            		 .append(emailDetail.getOrganisationName())
            		 .append(" on Caritas Nairobi MifosX.")
            		 .append("\n\n")
            		 .append("You can login using the following credentials:")
            		 .append("\n\n")
            		 .append("URL Link: https://livemis.caritasnairobi.org ")
            		 .append("\n")
            		 .append("Username: ").append(emailDetail.getUsername())
            		 .append("\n")
            		 .append("Password: ").append(unencodedPassword)
            		 .append("\n\n")
            		 .append("For any queries please email it@caritasnairobi.org ")
            		 .append("\n\n\n")
            		 .append("Thanks,")
            		 .append("\n")
            		 .append("Caritas Nairobi IT Team");
            
            email.setMsg(messageBuilder.toString());

            email.addTo(sendToEmail, emailDetail.getContactName());
            email.send();
        } catch (final EmailException e) {
            throw new PlatformEmailSendException(e);
        }
    }
}