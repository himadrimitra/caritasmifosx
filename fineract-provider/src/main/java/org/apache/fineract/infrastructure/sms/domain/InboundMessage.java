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
package org.apache.fineract.infrastructure.sms.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "sms_inbound_messages")
public class InboundMessage extends AbstractPersistable<Long> {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submitted_on_date", nullable = true)
    private Date submittedOnDate;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "ussd_code", nullable = false)
    private String ussdCode;

    protected InboundMessage() {
    }

    private InboundMessage(final String mobileNumber, final String ussdCode) {
        this.mobileNumber = mobileNumber;
        this.submittedOnDate = DateUtils.getLocalDateTimeOfTenant().toDate();
        this.ussdCode = ussdCode;
    }

    public static InboundMessage instance(final String mobileNumber, final String ussdCode) {
        return new InboundMessage(mobileNumber, ussdCode);
    }

    public String getMobileNumber() {
        return this.mobileNumber;
    }

    public String getUssdCode() {
        return this.ussdCode;
    }
}
