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
package org.apache.fineract.infrastructure.sms.data;

import org.joda.time.LocalDate;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Immutable data object representing a SMS message.
 */
public class SmsData {

    private final Long id;
    private final Long externalId;
    private final Long groupId;
    private final Long clientId;
    private final Long staffId;
    private final EnumOptionData status;
    private final String sourceAddress;
    private final String mobileNo;
    private final String message;
    private final String campaignName;
	private final LocalDate sentDate;


	public static SmsData instance(final Long id, Long externalId, final Long groupId, final Long clientId, final Long staffId, final EnumOptionData status,
    		final String sourceAddress, final String mobileNo, final String message,final String campaignName,final LocalDate sentDate) {
        return new SmsData(id, externalId, groupId, clientId, staffId, status, sourceAddress, mobileNo, message, campaignName,sentDate);
    }

    private SmsData(final Long id, Long externalId, final Long groupId, final Long clientId, final Long staffId, final EnumOptionData status,
            final String sourceAddress, final String mobileNo, final String message, final String campaignName,final LocalDate sentDate) {
        this.id = id;
        this.externalId = externalId;
        this.groupId = groupId;
        this.clientId = clientId;
        this.staffId = staffId;
        this.status = status;
        this.sourceAddress = sourceAddress;
        this.mobileNo = mobileNo;
        this.message = message;
        this.campaignName = campaignName;
		this.sentDate = sentDate;
    }

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the externalId
	 */
	public Long getExternalId() {
		return externalId;
	}

	/**
	 * @return the groupId
	 */
	public Long getGroupId() {
		return groupId;
	}

	/**
	 * @return the clientId
	 */
	public Long getClientId() {
		return clientId;
	}

	/**
	 * @return the staffId
	 */
	public Long getStaffId() {
		return staffId;
	}

	/**
	 * @return the status
	 */
	public EnumOptionData getStatus() {
		return status;
	}

	/**
	 * @return the sourceAddress
	 */
	public String getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * @return the mobileNo
	 */
	public String getMobileNo() {
		return mobileNo;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	public String getCampaignName() {return this.campaignName;}

	public LocalDate getSentDate() { return this.sentDate; }
}