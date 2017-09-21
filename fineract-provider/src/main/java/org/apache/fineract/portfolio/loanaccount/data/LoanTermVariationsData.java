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
package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.joda.time.LocalDate;

public class LoanTermVariationsData {

    @SuppressWarnings("unused")
    private final Long id;
    private final EnumOptionData termType;
    private final LocalDate termVariationApplicableFrom;
    private final BigDecimal termValue;

    public LoanTermVariationsData(final Long id, final EnumOptionData termType, final LocalDate termVariationApplicableFrom,
            final BigDecimal termValue) {
        this.id = id;
        this.termType = termType;
        this.termVariationApplicableFrom = termVariationApplicableFrom;
        this.termValue = termValue;
    }

    public EnumOptionData getTermType() {
        return this.termType;
    }

    public LocalDate getTermApplicableFrom() {
        return this.termVariationApplicableFrom;
    }

    public BigDecimal getTermValue() {
        return this.termValue;
    }
}
