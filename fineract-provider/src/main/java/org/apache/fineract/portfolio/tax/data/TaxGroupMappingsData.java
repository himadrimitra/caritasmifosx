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
package org.apache.fineract.portfolio.tax.data;

import org.joda.time.LocalDate;

public class TaxGroupMappingsData {

    @SuppressWarnings("unused")
    private final Long id;
    private TaxComponentData taxComponent;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public TaxGroupMappingsData(final Long id, final TaxComponentData taxComponent, final LocalDate startDate, final LocalDate endDate) {
        this.id = id;
        this.taxComponent = taxComponent;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TaxComponentData getTaxComponent() {
        return this.taxComponent;
    }

    public boolean occursOnDayFromAndUpToAndIncluding(final LocalDate target) {
        if (this.endDate == null) { return target != null && target.isAfter(this.startDate); }
        return target != null && target.isAfter(this.startDate) && !target.isAfter(this.endDate);
    }
    
    public void setTaxComponent(final TaxComponentData taxComponent) {
        this.taxComponent = taxComponent;
    }
}
