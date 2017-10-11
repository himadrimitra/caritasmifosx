package com.finflux.risk.creditbureau.provider.equifax.xsd;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StateCodeOptions.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StateCodeOptions"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;maxLength value="2"/&gt;
 *     &lt;enumeration value="AN"/&gt;
 *     &lt;enumeration value="AP"/&gt;
 *     &lt;enumeration value="AR"/&gt;
 *     &lt;enumeration value="AS"/&gt;
 *     &lt;enumeration value="BR"/&gt;
 *     &lt;enumeration value="CH"/&gt;
 *     &lt;enumeration value="CG"/&gt;
 *     &lt;enumeration value="DN"/&gt;
 *     &lt;enumeration value="DD"/&gt;
 *     &lt;enumeration value="DL"/&gt;
 *     &lt;enumeration value="GA"/&gt;
 *     &lt;enumeration value="GJ"/&gt;
 *     &lt;enumeration value="HR"/&gt;
 *     &lt;enumeration value="HP"/&gt;
 *     &lt;enumeration value="JK"/&gt;
 *     &lt;enumeration value="JH"/&gt;
 *     &lt;enumeration value="KA"/&gt;
 *     &lt;enumeration value="KL"/&gt;
 *     &lt;enumeration value="LD"/&gt;
 *     &lt;enumeration value="MP"/&gt;
 *     &lt;enumeration value="MH"/&gt;
 *     &lt;enumeration value="MN"/&gt;
 *     &lt;enumeration value="ML"/&gt;
 *     &lt;enumeration value="MZ"/&gt;
 *     &lt;enumeration value="NL"/&gt;
 *     &lt;enumeration value="OR"/&gt;
 *     &lt;enumeration value="PY"/&gt;
 *     &lt;enumeration value="PB"/&gt;
 *     &lt;enumeration value="RJ"/&gt;
 *     &lt;enumeration value="SK"/&gt;
 *     &lt;enumeration value="TN"/&gt;
 *     &lt;enumeration value="TR"/&gt;
 *     &lt;enumeration value="UP"/&gt;
 *     &lt;enumeration value="UL"/&gt;
 *     &lt;enumeration value="WB"/&gt;
 *     &lt;enumeration value="TG"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "StateCodeOptions")
@XmlEnum
public enum StateCodeOptions {

    AN,
    AP,
    AR,
    AS,
    BR,
    CH,
    CG,
    DN,
    DD,
    DL,
    GA,
    GJ,
    HR,
    HP,
    JK,
    JH,
    KA,
    KL,
    LD,
    MP,
    MH,
    MN,
    ML,
    MZ,
    NL,
    OR,
    PY,
    PB,
    RJ,
    SK,
    TN,
    TR,
    UP,
    UL,
    WB,
    TG;

    public String value() {
        return name();
    }

    public static StateCodeOptions fromValue(String v) {
        return valueOf(v);
    }

}
