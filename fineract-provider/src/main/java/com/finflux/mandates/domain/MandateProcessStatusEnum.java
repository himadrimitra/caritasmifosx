package com.finflux.mandates.domain;

public enum MandateProcessStatusEnum {
        REQUESTED(1,"REQUESTED"),
        INPROCESS(2,"INPROCESS"),
        PROCESSED(3,"PROCESSED"),
        FAILED(4,"FAILED"),
        INVALID(0,"INVALID");

        private final Integer value;
        private final String status;

        private MandateProcessStatusEnum(final Integer value, final String status) {
                this.value = value;
                this.status = status;
        }

        public String getStatus(){
                return this.status;
        }

        public Integer getValue(){
                return this.value;
        }

        public static MandateProcessStatusEnum from(final String value) {

                MandateProcessStatusEnum enumeration = MandateProcessStatusEnum.INVALID;
                switch (value) {
                        case "REQUESTED":
                                enumeration = MandateProcessStatusEnum.REQUESTED;
                                break;
                        case "INPROCESS":
                                enumeration = MandateProcessStatusEnum.INPROCESS;
                                break;
                        case "PROCESSED":
                                enumeration = MandateProcessStatusEnum.PROCESSED;
                                break;
                        case "FAILED":
                                enumeration = MandateProcessStatusEnum.FAILED;
                                break;
                }
                return enumeration;
        }

        public static MandateProcessStatusEnum fromInt(final Integer value) {

                MandateProcessStatusEnum enumeration = MandateProcessStatusEnum.INVALID;
                switch (value) {
                        case 1:
                                enumeration = MandateProcessStatusEnum.REQUESTED;
                                break;
                        case 2:
                                enumeration = MandateProcessStatusEnum.INPROCESS;
                                break;
                        case 3:
                                enumeration = MandateProcessStatusEnum.PROCESSED;
                                break;
                        case 4:
                                enumeration = MandateProcessStatusEnum.FAILED;
                                break;
                }
                return enumeration;
        }

        public boolean hasStateOf(final MandateProcessStatusEnum state) {
                return this.status.equals(state.getStatus());
        }

}
