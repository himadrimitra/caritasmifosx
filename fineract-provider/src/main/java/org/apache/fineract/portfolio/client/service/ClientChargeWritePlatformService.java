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
package org.apache.fineract.portfolio.client.service;

import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.client.data.ClientRecurringChargeData;
import org.apache.fineract.portfolio.collectionsheet.command.CollectionSheetClientChargeRepaymentCommand;
import org.apache.fineract.portfolio.collectionsheet.domain.CollectionSheetTransactionDetails;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

public interface ClientChargeWritePlatformService {

    @Transactional
    CommandProcessingResult addCharge(Long clientId, JsonCommand command);

    @Transactional
    CommandProcessingResult updateCharge(Long clientId, JsonCommand command);

    @Transactional
    CommandProcessingResult deleteCharge(Long clientId, Long clientChargeId);

    @Transactional
    CommandProcessingResult waiveCharge(Long clientId, Long clientChargeId);

    @Transactional
    CommandProcessingResult payCharge(Long clientId, Long clientChargeId, JsonCommand command);

    @Transactional
    CommandProcessingResult inactivateCharge(Long clientId, Long clientChargeId);
    
    @Transactional
    void applyClientRecurringCharge(final LocalDate fromDate, final ClientRecurringChargeData clientRecurringChargeData, final StringBuilder sb);

    @Transactional
    Map<String, Object> payChargeFromCollectionsheet(CollectionSheetClientChargeRepaymentCommand ChargeRepaymentCommand,
            PaymentDetail paymentDetail, final List<CollectionSheetTransactionDetails> collectionSheetTransactionDetails);

    @Transactional
    void applyHolidaysToClientRecurringCharge(final ClientRecurringChargeData clientRecurringChargeData,
            final HolidayDetailDTO holidayDetailDTO, final StringBuilder sb);

}
