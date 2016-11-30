package org.apache.fineract.portfolio.cgt.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.cgt.domain.CgtDayClient;
import org.apache.fineract.portfolio.cgt.domain.CgtDayClientAttendanceStatusType;
import org.apache.fineract.portfolio.client.data.ClientData;

public class CgtDayClientData {

    private final Long id;
    private final Long cgtDayId;
    private final ClientData clientData;
    private final CgtData cgtData;
    private EnumOptionData attendance;

    public CgtDayClientData(final Long id, final Long cgtDayId, final ClientData clientData, final EnumOptionData attendance) {
        this.id = id;
        this.cgtDayId = cgtDayId;
        this.clientData = clientData;
        this.cgtData = null;
        this.attendance = attendance;
    }
    
    private CgtDayClientData(final ClientData clientData, final CgtData cgtData, final EnumOptionData attendance) {
        this.id = null;
        this.cgtDayId = null;
        this.clientData = clientData;
        this.cgtData = cgtData;
        this.attendance = attendance;
    }

    public static CgtDayClientData retriveCgtDayClientDataFromEntity(final CgtDayClient cgtDayClient) {
        final CgtData cgtData = null;
        final ClientData clientData = ClientData
                .formClientData(cgtDayClient.getClient().getId(), cgtDayClient.getClient().getDisplayName());
        return new CgtDayClientData(clientData, cgtData,
                CgtDayClientAttendanceStatusType.CgtDayClientAttendanceStatusTypeEnumDatafromInt(cgtDayClient.getAttendance().intValue()));
    }
    
}
