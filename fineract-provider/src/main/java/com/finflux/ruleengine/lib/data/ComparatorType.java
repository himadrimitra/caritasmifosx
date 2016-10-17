package com.finflux.ruleengine.lib.data;

import com.finflux.ruleengine.lib.InvalidExpressionException;

import java.text.MessageFormat;

/**
 * Created by dhirendra on 06/09/16.
 */
public enum ComparatorType {
    eq("eq","#{0} eq {1}","#{0} eq ''{1}''","#{0} eq {1}"),
    ne("ne","#{0} ne {1}","#{0} ne ''{1}''","#{0} ne {1}"),
    lt("lt","#{0} lt {1}",null,null),
    gt("gt","#{0} gt {1}",null,null),
    le("le","#{0} le {1}",null,null),
    ge("ge","#{0} ge {1}",null,null),
    contains("contains",null,"#{0}.contains(''{1}'')",null),
    startswith("startswith",null,"#{0}.startsWith(''{1}'')",null),
    endswith("endswith",null,"#{0}.endsWith(''{1}'')",null);


    private String name;
    private String numberExpr;
    private String stringExpr;
    private String booleanExpr;
    ComparatorType(String name, String numberExpr, String stringExpr, String booleanExpr) {
        this.name = name;
        this.numberExpr = numberExpr;
        this.stringExpr = stringExpr;
        this.booleanExpr = booleanExpr;
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
}
