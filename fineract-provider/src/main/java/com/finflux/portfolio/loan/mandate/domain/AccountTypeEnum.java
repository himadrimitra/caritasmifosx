package com.finflux.portfolio.loan.mandate.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.Collection;

public enum AccountTypeEnum {
        INVALID(0, "INVALID"), //
        SB(1, "SB"), //
        CA(2, "CA"), //
        CC(3, "CC"), //
        SBNRE(4, "SB-NRE"), //
        SBNRO(5, "SB-NRO"), //
        Other(6, "Other");

        private final Integer value;
        private final String code;

        public static AccountTypeEnum fromInt(final Integer statusValue) {

                AccountTypeEnum enumeration = AccountTypeEnum.INVALID;
                switch (statusValue) {
                        case 1:
                                enumeration = AccountTypeEnum.SB;
                                break;
                        case 2:
                                enumeration = AccountTypeEnum.CA;
                                break;
                        case 3:
                                enumeration = AccountTypeEnum.CC;
                                break;
                        case 4:
                                enumeration = AccountTypeEnum.SBNRE;
                                break;
                        case 5:
                                enumeration = AccountTypeEnum.SBNRO;
                                break;
                        case 6:
                                enumeration = AccountTypeEnum.Other;
                                break;
                }
                return enumeration;
        }

        private AccountTypeEnum(final Integer value, final String code) {
                this.value = value;
                this.code = code;
        }

        public boolean hasStateOf(final AccountTypeEnum state) {
                return this.value.equals(state.getValue());
        }

        public Integer getValue() {
                return this.value;
        }

        public String getCode() {
                return this.code;
        }

        public static boolean isValidAccountTypeEnumValue(final Integer value){
                return (value >= 1 && value <= 6);
        }

        public static Collection<EnumOptionData> getAccountTypeOptionData(){
                Collection<EnumOptionData> ret = new ArrayList<>();
                for (AccountTypeEnum option: AccountTypeEnum.values()) {
                        if(!option.hasStateOf(AccountTypeEnum.INVALID)){
                                ret.add(new EnumOptionData(option.getValue().longValue(), option.getCode(), option.getCode()));
                        }
                }
                return ret;
        }

        public static EnumOptionData enumOptionDataFrom(final Integer statusValue){
                AccountTypeEnum accountTypeEnum = fromInt(statusValue);
                return new EnumOptionData(accountTypeEnum.getValue().longValue(),
                        accountTypeEnum.getCode(), accountTypeEnum.getCode());
        }

}
