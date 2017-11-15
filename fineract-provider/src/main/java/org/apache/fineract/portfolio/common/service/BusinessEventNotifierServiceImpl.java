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
package org.apache.fineract.portfolio.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.springframework.stereotype.Service;

@Service
public class BusinessEventNotifierServiceImpl implements BusinessEventNotifierService {

    private final Map<BUSINESS_EVENTS, List<BusinessEventListner>> preListners = new HashMap<>(5);
    private final Map<BUSINESS_EVENTS, List<BusinessEventListner>> postListners = new HashMap<>(5);

    private final Map<String, Map<BUSINESS_EVENTS, List<BusinessEventListner>>> tenantPreListners = new HashMap<>(5);
    private final Map<String, Map<BUSINESS_EVENTS, List<BusinessEventListner>>> tenantPostListners = new HashMap<>(5);

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.fineract.portfolio.common.service.BusinessEventNotifierService
     * #notifyBusinessEventToBeExecuted
     * (org.apache.fineract.portfolio.common.BusinessEventNotificationConstants
     * .BUSINESS_EVENTS,
     * org.springframework.data.jpa.domain.AbstractPersistable)
     */
    @Override
    public void notifyBusinessEventToBeExecuted(final BUSINESS_EVENTS businessEvent,
            final Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        final List<BusinessEventListner> businessEventListners = getBusinessEventListners(businessEvent, this.preListners,
                this.tenantPreListners);
        businessEventEntity.put(BUSINESS_ENTITY.BUSINESS_EVENT, businessEvent);
        for (final BusinessEventListner eventListner : businessEventListners) {
            eventListner.businessEventToBeExecuted(businessEventEntity);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.fineract.portfolio.common.service.BusinessEventNotifierService
     * #notifyBusinessEventWasExecuted
     * (org.apache.fineract.portfolio.common.BusinessEventNotificationConstants
     * .BUSINESS_EVENTS,
     * org.springframework.data.jpa.domain.AbstractPersistable)
     */
    @Override
    public void notifyBusinessEventWasExecuted(final BUSINESS_EVENTS businessEvent,
            final Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        final List<BusinessEventListner> businessEventListners = getBusinessEventListners(businessEvent, this.postListners,
                this.tenantPostListners);
        businessEventEntity.put(BUSINESS_ENTITY.BUSINESS_EVENT, businessEvent);
        for (final BusinessEventListner eventListner : businessEventListners) {
            eventListner.businessEventWasExecuted(businessEventEntity);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.fineract.portfolio.common.service.BusinessEventNotifierService
     * #addBusinessEventPreListners
     * (org.apache.fineract.portfolio.common.BusinessEventNotificationConstants
     * .BUSINESS_EVENTS,
     * org.apache.fineract.portfolio.common.service.BusinessEventListner)
     */
    @Override
    public void addBusinessEventPreListners(final BUSINESS_EVENTS businessEvent, final BusinessEventListner businessEventListner) {
        addBusinessEventListners(businessEvent, businessEventListner, this.preListners);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.fineract.portfolio.common.service.BusinessEventNotifierService
     * #addBusinessEventPostListners
     * (org.apache.fineract.portfolio.common.BusinessEventNotificationConstants
     * .BUSINESS_EVENTS,
     * org.apache.fineract.portfolio.common.service.BusinessEventListner)
     */
    @Override
    public void addBusinessEventPostListners(final BUSINESS_EVENTS businessEvent, final BusinessEventListner businessEventListner) {
        addBusinessEventListners(businessEvent, businessEventListner, this.postListners);
    }

    private void addBusinessEventListners(final BUSINESS_EVENTS businessEvent, final BusinessEventListner businessEventListner,
            final Map<BUSINESS_EVENTS, List<BusinessEventListner>> businessEventListnerMap) {
        List<BusinessEventListner> businessEventListners = businessEventListnerMap.get(businessEvent);
        if (businessEventListners == null) {
            businessEventListners = new ArrayList<>();
            businessEventListnerMap.put(businessEvent, businessEventListners);
        }
        businessEventListners.add(businessEventListner);
    }

    @Override
    public void addBusinessEventTenantBasedPreListners(final Map<BUSINESS_EVENTS, List<BusinessEventListner>> businessEventListnerMap) {
        this.tenantPreListners.put(ThreadLocalContextUtil.getTenant().getTenantIdentifier(), businessEventListnerMap);
    }

    @Override
    public void addBusinessEventTenantBasedPostListners(final Map<BUSINESS_EVENTS, List<BusinessEventListner>> businessEventListnerMap) {
        this.tenantPostListners.put(ThreadLocalContextUtil.getTenant().getTenantIdentifier(), businessEventListnerMap);
    }

    private List<BusinessEventListner> getBusinessEventListners(final BUSINESS_EVENTS businessEvent,
            final Map<BUSINESS_EVENTS, List<BusinessEventListner>> listners,
            final Map<String, Map<BUSINESS_EVENTS, List<BusinessEventListner>>> tenantListners) {
        final List<BusinessEventListner> businessEventListners = new ArrayList<>();
        if (listners.get(businessEvent) != null) {
            businessEventListners.addAll(listners.get(businessEvent));
        }

        final Map<BUSINESS_EVENTS, List<BusinessEventListner>> tenantSpecificListners = tenantListners
                .get(ThreadLocalContextUtil.getTenant().getTenantIdentifier());
        if (tenantSpecificListners != null && tenantSpecificListners.get(businessEvent) != null) {
            businessEventListners.addAll(tenantSpecificListners.get(businessEvent));
        }

        return businessEventListners;
    }

}