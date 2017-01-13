package com.finflux.ruleengine.lib.data;

import com.finflux.ruleengine.lib.InvalidExpressionException;

import java.text.MessageFormat;

/**
 * Created by dhirendra on 06/09/16.
 */
public enum ComparatorType {
    eq("eq","#{0} eq {1}","#{0} eq ''{1}''","#{0} eq {1}", false),
    ne("ne","#{0} ne {1}","#{0} ne ''{1}''","#{0} ne {1}", false),
    lt("lt","#{0} lt {1}",null,null, false),
    gt("gt","#{0} gt {1}",null,null, false),
    le("le","#{0} le {1}",null,null, false),
    ge("ge","#{0} ge {1}",null,null, false),
    contains("contains",null,"#{0}!=null && #{0}.contains(''{1}'')",null, true),
    startswith("startswith",null,"#{0}!=null && #{0}.startsWith(''{1}'')",null, true),
    endswith("endswith",null,"#{0}!=null && #{0}.endsWith(''{1}'')",null, true),
    isempty("isempty",null,"#{0}==null || #{0}.isEmpty()",null, true),
    isnotempty("isnotempty",null,"#{0}!=null && !(#{0}.isEmpty())",null, true);


    private String name;
    private String numberExpr;
    private String stringExpr;
    private String booleanExpr;
    private Boolean supportNullValue;
    ComparatorType(String name, String numberExpr, String stringExpr, String booleanExpr, Boolean supportNullValue) {
        this.name = name;
        this.numberExpr = numberExpr;
        this.stringExpr = stringExpr;
        this.booleanExpr = booleanExpr;
        this.supportNullValue = supportNullValue;
    }

    public String buildSpringExpression(final String field, final String value, final ValueType valueType) throws InvalidExpressionException {
        if(ValueType.NUMBER.equals(valueType)){
            if(this.numberExpr == null){
                throw new InvalidExpressionException("No settings found for  comparator ["+this+"], type ["+valueType+"] and parameter ["+field+"]for parameter");
            }
            return MessageFormat.format(this.numberExpr, field, value);
        } else if(ValueType.BOOLEAN.equals(valueType)){
            if(this.booleanExpr == null){
                throw new InvalidExpressionException("No settings found for  comparator ["+this+"], type ["+valueType+"] and parameter ["+field+"]for parameter");
            }
            return MessageFormat.format(this.booleanExpr, field, value);
        } else {
            if(this.stringExpr == null){
                throw new InvalidExpressionException("No settings found for  comparator ["+this+"], type ["+valueType+"] and parameter ["+field+"]for parameter");
            }
            return MessageFormat.format(this.stringExpr, field, value);
        }
    }

    public String getNumberExpr() {
        return numberExpr;
    }

    public void setNumberExpr(String numberExpr) {
        this.numberExpr = numberExpr;
    }

    public String getStringExpr() {
        return stringExpr;
    }

    public void setStringExpr(String stringExpr) {
        this.stringExpr = stringExpr;
    }

    public Boolean getSupportNullValue() {
        return supportNullValue;
    }
}
