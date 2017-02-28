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
package org.apache.fineract.accounting.journalentry.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ChargePaymentDTO {

    private final Long chargeId;
    private final BigDecimal amount;
    private final Long loanChargeId;
    private final boolean isCapitalized;
    private List<TaxPaymentDTO> taxPaymentDTO;

    public ChargePaymentDTO(final Long chargeId, final Long loanChargeId, final BigDecimal amount, final boolean isCapitalized) {
        this.chargeId = chargeId;
        this.amount = amount;
        this.loanChargeId = loanChargeId;
        this.isCapitalized = isCapitalized;
        this.taxPaymentDTO = null;
    }
    

    public Long getChargeId() {
        return this.chargeId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Long getLoanChargeId() {
        return this.loanChargeId;
    }
    
    public void updateTaxPaymentDTO(final TaxPaymentDTO taxPaymentDTO) {
        if(this.taxPaymentDTO == null) {
            this.taxPaymentDTO = new ArrayList<TaxPaymentDTO>();
        }
        this.taxPaymentDTO.add(taxPaymentDTO);
    }
    
    public List<TaxPaymentDTO> getTaxPaymentDTO() {
        return this.taxPaymentDTO;
    }
    
    public boolean isCapitalized() {
        return this.isCapitalized;
    }

}
