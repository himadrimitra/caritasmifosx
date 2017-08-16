package com.finflux.risk.creditbureau.provider.cibil;

public class CibilStateCodes {

    public static String toCibilCode(final String stateName) {
        String code = "";
        switch (stateName) {
            case "Jammu & Kashmir":
                code = "01";
            break;
            case "Himachal Pradesh":
                code = "02";
            break;
            case "Punjab":
                code = "03";
            break;
            case "Chandigarh":
                code = "04";
            break;
            case "Uttaranchal":
                code = "05";
            break;
            case "Haryana":
                code = "06";
            break;
            case "Delhi":
                code = "07";
            break;
            case "Rajasthan":
                code = "08";
            break;
            case "Uttar Pradesh":
                code = "09";
            break;
            case "Bihar":
                code = "10";
            break;
            case "Sikkim":
                code = "11";
            break;
            case "Arunachal Pradesh":
                code = "12";
            break;
            case "Nagaland":
                code = "13";
            break;
            case "Manipur":
                code = "14";
            break;
            case "Mizoram ":
                code = "15";
            break;
            case "Tripura 17":
                code = "16";
            break;
            case "Meghalaya":
                code = "17";
            break;
            case "Assam ":
                code = "18";
            break;
            case "West Bengal":
                code = "19";
            break;
            case "Jharkhand":
                code = "20";
            break;
            case "Orissa":
                code = "21";
            break;
            case "Chhattisgarh":
                code = "22";
            break;
            case "Madhya Pradesh":
                code = "23";
            break;
            case "Gujarat":
                code = "24";
            break;
            case "Daman & Diu":
                code = "25";
            break;
            case "Dadra & Nagar Haveli":
                code = "26";
            break;
            case "Maharashtra":
                code = "27";
            break;
            case "Andhra Pradesh":
                code = "28";
            break;
            case "Karnataka":
                code = "29";
            break;
            case "Goa":
                code = "30";
            break;
            case "Lakshadweep":
                code = "31";
            break;
            case "Kerala":
                code = "32";
            break;
            case "Tamil Nadu":
                code = "33";
            break;
            case "Pondicherry":
                code = "34";
            break;
            case "Andaman & Nicobar Islan":
                code = "35";
            break;
            case "Telangana":
                code = "36";
            break;
            case "APO Address":
                code = "99";
            break;
        }
        return code;
    }
}
