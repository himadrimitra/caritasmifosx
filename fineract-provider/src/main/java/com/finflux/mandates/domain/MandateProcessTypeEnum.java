package com.finflux.mandates.domain;

public enum MandateProcessTypeEnum {
        MANDATES_DOWNLOAD(1,"MANDATES_DOWNLOAD"),
        MANDATES_UPLOAD(2,"MANDATES_UPLOAD"),
        TRANSACTIONS_DOWNLOAD(3,"TRANSACTIONS_DOWNLOAD"),
        TRANSACTIONS_UPLOAD(4,"TRANSACTIONS_UPLOAD"),
        ALL(5,"ALL"),
        INVALID(0,"INVALID");

        private final Integer value;
        private final String type;

        private MandateProcessTypeEnum(final Integer value, final String type) {
                this.value = value;
                this.type = type;
        }

        public String getType(){
                return this.type;
        }

        public Integer getValue(){
                return this.value;
        }

        public static MandateProcessTypeEnum from(final String type) {

                MandateProcessTypeEnum enumeration = MandateProcessTypeEnum.INVALID;
                switch (type) {
                        case "MANDATES_DOWNLOAD":
                                enumeration = MandateProcessTypeEnum.MANDATES_DOWNLOAD;
                                break;
                        case "MANDATES_UPLOAD":
                                enumeration = MandateProcessTypeEnum.MANDATES_UPLOAD;
                                break;
                        case "TRANSACTIONS_DOWNLOAD":
                                enumeration = MandateProcessTypeEnum.TRANSACTIONS_DOWNLOAD;
                                break;
                        case "TRANSACTIONS_UPLOAD":
                                enumeration = MandateProcessTypeEnum.TRANSACTIONS_UPLOAD;
                                break;
                        case "ALL":
                                enumeration = MandateProcessTypeEnum.ALL;
                                break;
                }
                return enumeration;
        }

        public static MandateProcessTypeEnum fromInt(final Integer value) {

                MandateProcessTypeEnum enumeration = MandateProcessTypeEnum.INVALID;
                switch (value) {
                        case 1:
                                enumeration = MandateProcessTypeEnum.MANDATES_DOWNLOAD;
                                break;
                        case 2:
                                enumeration = MandateProcessTypeEnum.MANDATES_UPLOAD;
                                break;
                        case 3:
                                enumeration = MandateProcessTypeEnum.TRANSACTIONS_DOWNLOAD;
                                break;
                        case 4:
                                enumeration = MandateProcessTypeEnum.TRANSACTIONS_UPLOAD;
                                break;
                        case 5:
                                enumeration = MandateProcessTypeEnum.ALL;
                                break;
                }
                return enumeration;
        }

        public boolean hasStateOf(final MandateProcessTypeEnum state) {
                return this.type.equals(state.getType());
        }

}
