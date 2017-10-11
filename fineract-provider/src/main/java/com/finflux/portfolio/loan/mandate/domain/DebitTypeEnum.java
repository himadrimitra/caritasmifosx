package com.finflux.portfolio.loan.mandate.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.Collection;

public enum DebitTypeEnum {
        INVALID(0, "INVALID"), //
        FIXED_AMOUNT(1, "FIXED AMOUNT"), //
        MAXIMUM_AMOUNT(2, "MAXIMUM AMOUNT");

        private final Integer value;
        private final String code;

        public static DebitTypeEnum fromInt(final Integer statusValue) {

                DebitTypeEnum enumeration = DebitTypeEnum.INVALID;
                switch (statusValue) {
                        case 1:
                                enumeration = DebitTypeEnum.FIXED_AMOUNT;
                                break;
                        case 2:
                                enumeration = DebitTypeEnum.MAXIMUM_AMOUNT;
                                break;
                }
                return enumeration;
        }

        private DebitTypeEnum(final Integer value, final String code) {
                this.value = value;
                this.code = code;
        }

        public boolean hasStateOf(final DebitTypeEnum state) {
                return this.value.equals(state.getValue());
        }

        public Integer getValue() {
                return this.value;
        }

        public String getCode() {
                return this.code;
        }

        public static boolean isValidDebitTypeEnumValue(final Integer value){
                return (value >= 1 && value <= 2);
        }

        public static Collection<EnumOptionData> getDebitTypeOptionData(){
                Collection<EnumOptionData> ret = new ArrayList<>();
                for (DebitTypeEnum option: DebitTypeEnum.values()) {
                        if(!option.hasStateOf(DebitTypeEnum.INVALID)){
                                ret.add(new EnumOptionData(option.getValue().longValue(), option.getCode(), option.getCode()));
                        }
                }
                return ret;
        }

        public static EnumOptionData enumOptionDataFrom(final Integer statusValue){
                DebitTypeEnum debitTypeEnum = fromInt(statusValue);
                return new EnumOptionData(debitTypeEnum.getValue().longValue(),
                        debitTypeEnum.getCode(), debitTypeEnum.getCode());
        }

}
