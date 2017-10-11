package com.finflux.portfolio.loan.mandate.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.Collection;

public enum DebitFrequencyEnum {
        INVALID(0, "INVALID"), //
        MONTHLY(1, "MONTHLY"), //
        QUARTERLY(2, "QUARTERLY"), //
        HALF_YEARLY(3, "HALF YEARLY"), //
        YEARLY(4, "YEARLY"), //
        AS_AND_WHEN_PRESENTED(5, "AS AND WHEN PRESENTED");

        private final Integer value;
        private final String code;

        public static DebitFrequencyEnum fromInt(final Integer statusValue) {

                DebitFrequencyEnum enumeration = DebitFrequencyEnum.INVALID;
                switch (statusValue) {
                        case 1:
                                enumeration = DebitFrequencyEnum.MONTHLY;
                                break;
                        case 2:
                                enumeration = DebitFrequencyEnum.QUARTERLY;
                                break;
                        case 3:
                                enumeration = DebitFrequencyEnum.HALF_YEARLY;
                                break;
                        case 4:
                                enumeration = DebitFrequencyEnum.YEARLY;
                                break;
                        case 5:
                                enumeration = DebitFrequencyEnum.AS_AND_WHEN_PRESENTED;
                                break;
                }
                return enumeration;
        }

        private DebitFrequencyEnum(final Integer value, final String code) {
                this.value = value;
                this.code = code;
        }

        public boolean hasStateOf(final DebitFrequencyEnum state) {
                return this.value.equals(state.getValue());
        }

        public Integer getValue() {
                return this.value;
        }

        public String getCode() {
                return this.code;
        }

        public static boolean isValidDebitFrequencyEnumValue(final Integer value){
                return (value >= 1 && value <= 5);
        }

        public static Collection<EnumOptionData> getDebitFrequencyOptionData(){
                Collection<EnumOptionData> ret = new ArrayList<>();
                for (DebitFrequencyEnum option: DebitFrequencyEnum.values()) {
                        if(!option.hasStateOf(DebitFrequencyEnum.INVALID)){
                                ret.add(new EnumOptionData(option.getValue().longValue(), option.getCode(), option.getCode()));
                        }
                }
                return ret;
        }

        public static EnumOptionData enumOptionDataFrom(final Integer statusValue){
                DebitFrequencyEnum debitFrequencyEnum = fromInt(statusValue);
                return new EnumOptionData(debitFrequencyEnum.getValue().longValue(),
                        debitFrequencyEnum.getCode(), debitFrequencyEnum.getCode());
        }

}
