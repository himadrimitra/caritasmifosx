package org.apache.fineract.portfolio.cgt.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.portfolio.client.domain.Client;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_cgt_day_client")
public class CgtDayClient extends AbstractPersistable<Long> {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cgt_day_id")
    private CgtDay cgtDay;

    @Column(name = "attendance")
    private Integer attendance;

    protected CgtDayClient() {
        super();
    }

    private CgtDayClient(final Client client, final CgtDay cgtDay, final Integer attendance) {
        super();
        this.client = client;
        this.cgtDay = cgtDay;
        this.attendance = attendance;
    }

    public static CgtDayClient assembleWithAttendance(final Client client, final CgtDay cgtDay, final Integer attendance) {
        return new CgtDayClient(client, cgtDay, attendance);
    }

    public static CgtDayClient assembleWithClient(final Client client) {
        final CgtDay cgtDay = null;
        final Integer attendance = null;
        return new CgtDayClient(client, cgtDay, attendance);
    }

    public void updateCgtDay(CgtDay cgtDay) {
        this.cgtDay = cgtDay;
    }

    public void updateAttendance(Integer attendance) {
        this.attendance = attendance;
    }

    public Client getClient() {
        return this.client;
    }

    public Integer getAttendance() {
        return this.attendance;
    }

}
