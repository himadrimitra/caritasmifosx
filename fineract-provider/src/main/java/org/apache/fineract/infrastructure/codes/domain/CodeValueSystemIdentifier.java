package org.apache.fineract.infrastructure.codes.domain;

public enum CodeValueSystemIdentifier {
        AADHAAR("UID"), PAN("PAN"), PASSPORT("PAS"), DRIVING_LICENSE("VDL"), VOTER_ID("VID");

        private final String systemIdentifier;

        CodeValueSystemIdentifier(final String systemIdentifier) {
                this.systemIdentifier = systemIdentifier;
        }

        @Override
        public String toString() {
                return this.systemIdentifier;
        }

}
