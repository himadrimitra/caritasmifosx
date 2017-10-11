package com.finflux.portfolio.loan.mandate.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.Collection;

public enum MandateStatusEnum {
        INVALID(0, "INVALID"), //
        CREATE_REQUESTED(100, "CREATE_REQUESTED"), //
        CREATE_INPROCESS(101, "CREATE_INPROCESS"), //
        CREATE_REJECTED(102, "CREATE_REJECTED"), //
        UPDATE_REQUESTED(200, "UPDATE_REQUESTED"), //
        UPDATE_INPROCESS(201, "UPDATE_INPROCESS"), //
        UPDATE_REJECTED(202, "UPDATE_REJECTED"), //
        CANCEL_REQUESTED(300, "CANCEL_REQUESTED"), //
        CANCEL_INPROCESS(301, "CANCEL_INPROCESS"), //
        CANCEL_REJECTED(302, "CANCEL_REJECTED"), //
        CANCEL_SUCCESS(303, "CANCEL_SUCCESS"), //
        ACTIVE(400, "ACTIVE"), //
        INACTIVE(500, "INACTIVE");

        private final Integer value;
        private final String code;

        public static MandateStatusEnum fromInt(final Integer statusValue) {

                MandateStatusEnum enumeration = MandateStatusEnum.INVALID;
                switch (statusValue) {
                        case 100:
                                enumeration = MandateStatusEnum.CREATE_REQUESTED;
                                break;
                        case 101:
                                enumeration = MandateStatusEnum.CREATE_INPROCESS;
                                break;
                        case 102:
                                enumeration = MandateStatusEnum.CREATE_REJECTED;
                                break;
                        case 200:
                                enumeration = MandateStatusEnum.UPDATE_REQUESTED;
                                break;
                        case 201:
                                enumeration = MandateStatusEnum.UPDATE_INPROCESS;
                                break;
                        case 202:
                                enumeration = MandateStatusEnum.UPDATE_REJECTED;
                                break;
                        case 300:
                                enumeration = MandateStatusEnum.CANCEL_REQUESTED;
                                break;
                        case 301:
                                enumeration = MandateStatusEnum.CANCEL_INPROCESS;
                                break;
                        case 302:
                                enumeration = MandateStatusEnum.CANCEL_REJECTED;
                                break;
                        case 303:
                                enumeration = MandateStatusEnum.CANCEL_SUCCESS;
                                break;
                        case 400:
                                enumeration = MandateStatusEnum.ACTIVE;
                                break;
                        case 500:
                                enumeration = MandateStatusEnum.INACTIVE;
                                break;
                }
                return enumeration;
        }

        private MandateStatusEnum(final Integer value, final String code) {
                this.value = value;
                this.code = code;
        }

        public boolean hasStateOf(final MandateStatusEnum state) {
                return this.value.equals(state.getValue());
        }

        public Integer getValue() {
                return this.value;
        }

        public String getCode() {
                return this.code;
        }

        public static EnumOptionData enumOptionDataFrom(final Integer statusValue){
                MandateStatusEnum mandateStatusEnum = fromInt(statusValue);
                return new EnumOptionData(mandateStatusEnum.getValue().longValue(),
                        mandateStatusEnum.getCode(), mandateStatusEnum.getCode());
        }

}
