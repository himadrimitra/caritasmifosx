package com.finflux.common.sqlinjection;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.finflux.common.exception.SQLInjectionException;

public class ValidateSQLInjection {

    private final static String[] DDL_COMMANDS = { "create", "alter", "drop", "truncate", "comment", "rename" };

    private final static String[] DML_COMMANDS = { "select", "insert", "update", "delete", "merge", "upsert", "call", "explain plan",
            "lock table" };

    private final static String[] DCL_COMMANDS = { "grant", "revoke" };

    private final static String[] TCL_COMMANDS = { "commit", "rollback", "savepoint", "set transaction" };

    private final static String[] COMMENTS = { "--", "({", "/*", "#", "*/" };

    private final static String SQL_PATTERN_MATCH = "[a-zA-Z_=,\\-'!><.?\"`% ()0-9{}]*";

    public final static void validateSQLQuery(final String sqlQuery) {
        final String sqlQueryLowerCase = sqlQuery.toLowerCase();
        for (final String ddl : DDL_COMMANDS) {
            if (sqlQueryLowerCase.contains(ddl)) { throw new SQLInjectionException(); }
        }

        for (final String dml : DML_COMMANDS) {
            if (sqlQueryLowerCase.contains(dml)) { throw new SQLInjectionException(); }
        }

        for (final String dcl : DCL_COMMANDS) {
            if (sqlQueryLowerCase.contains(dcl)) { throw new SQLInjectionException(); }
        }

        for (final String tcl : TCL_COMMANDS) {
            if (sqlQueryLowerCase.contains(tcl)) { throw new SQLInjectionException(); }
        }

        for (final String comments : COMMENTS) {
            if (sqlQueryLowerCase.contains(comments)) { throw new SQLInjectionException(); }
        }

        // Removing the space before and after '=' operator
        // String s = "          \"              OR 1    =    1"; For the cases
        // like this
        boolean injectionFound = false;
        String inputSqlString = sqlQueryLowerCase;
        while (inputSqlString.indexOf(" =") > 0) { // Don't remove space before
                                                   // = operator
            inputSqlString = inputSqlString.replaceAll(" =", "=");
        }

        while (inputSqlString.indexOf("= ") > 0) { // Don't remove space after =
                                                   // operator
            inputSqlString = inputSqlString.replaceAll("= ", "=");
        }

        StringTokenizer tokenizer = new StringTokenizer(inputSqlString, " ");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.equals("'")) {
                if (tokenizer.hasMoreElements()) {
                    String nextToken = tokenizer.nextToken().trim();
                    if (!nextToken.equals("'")) {
                        injectionFound = true;
                        break;
                    }
                } else {
                    injectionFound = true;
                    break;
                }
            }
            if (token.equals("\"")) {
                if (tokenizer.hasMoreElements()) {
                    String nextToken = tokenizer.nextToken().trim();
                    if (!nextToken.equals("\"")) {
                        injectionFound = true;
                        break;
                    }
                } else {
                    injectionFound = true;
                    break;
                }
            } else if (token.indexOf('=') > 0) {
                StringTokenizer operatorToken = new StringTokenizer(token, "=");
                String operand = operatorToken.nextToken().trim();
                if (!operatorToken.hasMoreTokens()) {
                    injectionFound = true;
                    break;
                }
                String value = operatorToken.nextToken().trim();
                if (operand.equals(value)) {
                    injectionFound = true;
                    break;
                }
            }
        }
        if (injectionFound) { throw new SQLInjectionException(); }

        final Pattern patternMatch = Pattern.compile(SQL_PATTERN_MATCH);
        final Matcher matcher = patternMatch.matcher(sqlQuery);
        if (!matcher.matches()) { throw new SQLInjectionException(); }

    }
}