package com.finflux.infrastructure.gis.district.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.joda.time.LocalDate;

import com.finflux.infrastructure.gis.taluka.data.TalukaData;

public class DistrictData {

    private final Long districtId;
    private final Long stateId;
    private final String isoDistrictCode;
    private final String districtName;
    private final Collection<TalukaData> talukaDatas;
    private final Boolean isWorkflowEnabled;
    private final EnumOptionData status;
    private final LocalDate activationDate;
    private final LocalDate rejectedonDate;

    private DistrictData(final Long districtId, final Long stateId, final String isoDistrictCode, final String districtName,
            final Collection<TalukaData> talukaDatas, final Boolean isWorkflowEnabled, final EnumOptionData status,
            final LocalDate activationDate, final LocalDate rejectedonDate) {
        this.districtId = districtId;
        this.stateId = stateId;
        this.isoDistrictCode = isoDistrictCode;
        this.districtName = districtName;
        this.talukaDatas = talukaDatas;
        this.isWorkflowEnabled = isWorkflowEnabled;
        this.status = status;
        this.activationDate = activationDate;
        this.rejectedonDate = rejectedonDate;
    }

    public static DistrictData instance(final Long districtId, final Long stateId, final String isoDistrictCode, final String districtName,
            final Collection<TalukaData> talukaDatas, final Boolean isWorkflowEnabled, final EnumOptionData status,
            final LocalDate activationDate, final LocalDate rejectedonDate) {
        return new DistrictData(districtId, stateId, isoDistrictCode, districtName, talukaDatas, isWorkflowEnabled, status, activationDate,
                rejectedonDate);
    }

    public Long getDistrictId() {
        return this.districtId;
    }

    public Long getStateId() {
        return this.stateId;
    }

    public String getIsoDistrictCode() {
        return this.isoDistrictCode;
    }

    public String getDistrictName() {
        return this.districtName;
    }

    public Collection<TalukaData> getTalukaDatas() {
        return this.talukaDatas;
    }

    public Boolean getIsWorkflowEnabled() {
        return this.isWorkflowEnabled;
    }

    public EnumOptionData getStatus() {
        return this.status;
    }

    public LocalDate getActivationDate() {
        return this.activationDate;
    }

    public LocalDate getRejectedonDate() {
        return this.rejectedonDate;
    }
}
