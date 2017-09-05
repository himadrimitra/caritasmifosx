/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BusinessEventNotificationConstants {

    public static enum BUSINESS_EVENTS {
        LOAN_APPROVED("loan_approved"), LOAN_UNDO_APPROVAL("loan_undo_approval"), LOAN_UNDO_DISBURSAL("loan_undo_disbursal"), LOAN_UNDO_LASTDISBURSAL(
                "loan_undo_lastdisbursal"), LOAN_UNDO_TRANSACTION("loan_undo_transaction"), LOAN_ADJUST_TRANSACTION(
                "loan_adjust_transaction"), LOAN_MAKE_REPAYMENT("loan_repayment_transaction"), LOAN_WRITTEN_OFF("loan_writtenoff"), LOAN_UNDO_WRITTEN_OFF(
                "loan_undo_writtenoff"), LOAN_DISBURSAL("loan_disbursal"), LOAN_WAIVE_INTEREST("loan_waive_interest"), LOAN_CLOSE(
                "loan_close"), LOAN_CLOSE_AS_RESCHEDULE("loan_close_as_reschedule"), LOAN_ADD_CHARGE("loan_add_charge"), LOAN_UPDATE_CHARGE(
                "loan_update_charge"), LOAN_WAIVE_CHARGE("loan_waive_charge"), LOAN_DELETE_CHARGE("loan_delete_charge"), LOAN_CHARGE_PAYMENT(
                "loan_charge_payment"), LOAN_INITIATE_TRANSFER("loan_initiate_transfer"), LOAN_ACCEPT_TRANSFER("loan_accept_transfer"), LOAN_WITHDRAW_TRANSFER(
                "loan_withdraw_transfer"), LOAN_REJECT_TRANSFER("loan_reject_transfer"), LOAN_REASSIGN_OFFICER("loan_reassign_officer"), LOAN_REMOVE_OFFICER(
                "loan_remove_officer"), LOAN_APPLY_OVERDUE_CHARGE("loan_apply_overdue_charge"), LOAN_INTEREST_RECALCULATION(
                "loan_interest_recalculation"), LOAN_REFUND("loan_refund"), LOAN_ADD_SUBSIDY("loan_add_subsidy_transaction"), LOAN_REVOKE_SUBSIDY(
                "loan_revoke_subsidy_transaction"), LOAN_FORECLOSURE("loan_foreclosure"), CLIENT_UPDATE("client_update"), CLIENT_CLOSE(
                "client_close"), CLIENT_DISASSOCIATE("client_disassociate"), TRANSFER_CLIENT("transfer_client"), LOAN_CREATE("loan_create"), LOAN_MODIFY(
                "loan_modify"), SAVING_WITHDRAWAL("saving_withdrawal"), SAVING_TRANSFER("saving_transfer"), GL_ACCOUNT_CLOSURE(
                "gl_account_closure"), ADDRESS_UPDATE("address_update"), DOCUMENT_UPDATE("document_update"), FAMILY_DETAILS_UPDATE(
                "family_details_update"), CLIENT_IDENTIFIER_UPDATE("client_identifier_update"), CLIENT_INCOME_EXPENSE_UPDATE(
                "client_income_expense_update"), EXISTING_LOAN_UPDATE("existing_loan_update");

        private final String value;

        private BUSINESS_EVENTS(final String value) {
            this.value = value;
        }

        private static final Map<String,BUSINESS_EVENTS> valueMapping = new HashMap<>();
        static {
            for (final BUSINESS_EVENTS type : BUSINESS_EVENTS.values()) {
                valueMapping.put(type.value,type);
            }
        }

        public static Set<String> getAllValues() {
            return valueMapping.keySet();
        }
        
        public static BUSINESS_EVENTS from(final String value){
            return valueMapping.get(value);
        }

        public String getValue() {
            return this.value;
        }
    }

    public static enum BUSINESS_ENTITY {
        LOAN("loan"), LOAN_TRANSACTION("loan_transaction"), LOAN_CHARGE("loan_charge"), LOAN_ADJUSTED_TRANSACTION(
                "loan_adjusted_transaction"), CLIENT("client"), CLIENT_DISASSOCIATE("client_disassociate"), TRANSFER_CLIENT(
                "transfer_client"), CHANGED_TRANSACTION_DETAIL("changed_transaction_detail"), JSON_COMMAND("json_command"), BUSINESS_EVENT(
                "business_event"), SAVING("saving"), SAVING_TRANSACTION("saving_transaction"), GL_CLOSURE("gl_closure"), ENTITY_LOCK_STATUS("is_locked");

        private final String value;

        private BUSINESS_ENTITY(final String value) {
            this.value = value;
        }

        private static final Map<String,BUSINESS_ENTITY> valueMapping = new HashMap<>();
        static {
            for (final BUSINESS_ENTITY type : BUSINESS_ENTITY.values()) {
                valueMapping.put(type.value,type);
            }
        }
        
        public static BUSINESS_ENTITY from(final String value){
            return valueMapping.get(value);
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
