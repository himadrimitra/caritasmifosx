package com.finflux.risk.creditbureau.provider.cibil.response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.finflux.risk.creditbureau.provider.cibil.response.data.AccountNumberData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.AccountSummaryData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.AddressData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.ConsumerDisputeData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.ConsumerName;
import com.finflux.risk.creditbureau.provider.cibil.response.data.EmailData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.EmploymentData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.EnquiryData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.IdentifierData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.OtherAccountData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.OwnAccountData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.ScoreData;
import com.finflux.risk.creditbureau.provider.cibil.response.data.TelephoneData;

public class CibilResponse {

    public final static DateFormat dateFormat_DDMMYYYY = new SimpleDateFormat("ddMMyyyy");
    public final static String ERROR_SEGMENT_TAG = "ERRR";

    protected final byte[] responseData;

    private final boolean isError;

    private HeaderSegment headerSegment;
    private ResponseSegment<ConsumerName> nameSegment;
    private ResponseSegment<TelephoneData> telephoneSegment;
    private ResponseSegment<AddressData> addressSegment;
    private ResponseSegment<EnquiryData> enquirySegment;
    private ResponseSegment<IdentifierData> identifierSegment;
    private ResponseSegment<EmailData> emailContactSegment;
    private ResponseSegment<EmploymentData> employmentSegment;
    private ResponseSegment<AccountNumberData> enquiryAccountNumberSegment;
    private AccountSegment accountSegment;
    private ResponseSegment<ScoreData> scoreSegment;
    private ResponseSegment<ConsumerDisputeData> consumerDisputeSegment;

    private ErrorSegment errorSegment;

    private ResponseEndOfSegment endOfSegment;

    public CibilResponse(final byte[] responseData) {
        this.responseData = responseData;
        String error = new String(responseData, 0, ERROR_SEGMENT_TAG.length());
        parseEndOfSegment(responseData);
        isError = ERROR_SEGMENT_TAG.equalsIgnoreCase(error);
        if (isError) {
            parseErrorSegment();
        } else {
            parseResponseSections();
        }
    }

    private void parseResponseSections() {       
        this.headerSegment = new HeaderSegment();
        Integer headerLength = this.headerSegment.parseSection(this.responseData, 0);
        this.nameSegment = new ResponseSegment<>();
        Integer nameSegmentLength = this.nameSegment.parseSection(this.responseData, headerLength, ConsumerName.class);
        Integer startIndex = nameSegmentLength + headerLength;
        while (startIndex < this.responseData.length) {
            String nextRecordTag = new String(this.responseData, startIndex, 2);
            final Integer length = parseOtherSections(nextRecordTag, startIndex);
            startIndex += length;
        }
    }

    private Integer parseOtherSections(final String nextSectionRecord, final Integer startIndex) {
        Integer length = 0;
        switch (nextSectionRecord) {
            case ResponseSectionTags.TELEPHONE:
                if (this.telephoneSegment == null) {
                    this.telephoneSegment = new ResponseSegment<>();
                }
                length = this.telephoneSegment.parseSection(this.responseData, startIndex, TelephoneData.class);
            break;
            case ResponseSectionTags.ADDRESS:
                if (this.addressSegment == null) {
                    this.addressSegment = new ResponseSegment<>();
                }
                length = this.addressSegment.parseSection(responseData, startIndex, AddressData.class);
            break;
            case ResponseSectionTags.ENQUIRY:
                if (this.enquirySegment == null) {
                    this.enquirySegment = new ResponseSegment<>();
                }
                length = this.enquirySegment.parseSection(responseData, startIndex, EnquiryData.class);
            break;
            case ResponseSectionTags.ACCOUNTS:
                if (this.accountSegment == null) {
                    this.accountSegment = new AccountSegment();
                }
                length = this.accountSegment.parseSection(responseData, startIndex);
            break;
            case ResponseSectionTags.IDENTITY:
                if (this.identifierSegment == null) {
                    this.identifierSegment = new ResponseSegment<>();
                }
                length = this.identifierSegment.parseSection(responseData, startIndex, IdentifierData.class);
            break;
            case ResponseSectionTags.EMAIL:
                if (this.emailContactSegment == null) {
                    this.emailContactSegment = new ResponseSegment<>();
                }
                length = this.emailContactSegment.parseSection(responseData, startIndex, EmailData.class);
            break;
            case ResponseSectionTags.EMPLOYMENT:
                if (this.employmentSegment == null) {
                    this.employmentSegment = new ResponseSegment<>();
                }
                length = this.employmentSegment.parseSection(responseData, startIndex, EmploymentData.class);
            break;
            case ResponseSectionTags.ENQUIERYACCOUNT:
                if (this.enquiryAccountNumberSegment == null) {
                    this.enquiryAccountNumberSegment = new ResponseSegment<>();
                }
                length = this.enquiryAccountNumberSegment.parseSection(responseData, startIndex, AccountNumberData.class);
            break;
            case ResponseSectionTags.SCORE:
                if (this.scoreSegment == null) {
                    this.scoreSegment = new ResponseSegment<>();
                }
                length = this.scoreSegment.parseSection(responseData, startIndex, ScoreData.class);
            break;
            case ResponseSectionTags.DISPUTEREMARKS:
                if (this.consumerDisputeSegment == null) {
                    this.consumerDisputeSegment = new ResponseSegment<>();
                }
                length = this.consumerDisputeSegment.parseSection(responseData, startIndex, ConsumerDisputeData.class);
            break;
            default:
                // To come out of loop as other sections are not required for us
                //This will be removed once errors are handled and end of segments are handled
                length = Integer.MAX_VALUE-startIndex;
            break;
        }
        return length;
    }

    private void parseErrorSegment() {
        if (isError) {
            this.errorSegment = new ErrorSegment();
            this.errorSegment.parseSection(this.responseData, 0);
        }
    }

    private void parseEndOfSegment(final byte[] errorResponse) {
        this.endOfSegment = new ResponseEndOfSegment(errorResponse);
    }

    public boolean isValidPacket() {
        return this.endOfSegment.isValidPacket();
    }

    public String getResponseAsString() {
        return new String(this.responseData);
    }

    public boolean isError() {
        return this.isError;
    }

    public List<AccountSummaryData> getAccountSummaryData() {
        if (this.accountSegment != null) { return this.accountSegment.getAccoutSummaryList(); }
        return null;
    }

    public List<OwnAccountData> getOwnAccountsList() {
        if (this.accountSegment != null) { return this.accountSegment.getOwnAccountsList(); }
        return null;
    }

    public List<OtherAccountData> getOtherAccountsList() {
        if (this.accountSegment != null) { return this.accountSegment.getOtherAccountsList(); }
        return null;
    }

    public List<ScoreData> getCreditScore() {
        return this.scoreSegment.getSegmentData();
    }
    
    public ErrorSegment getErrorSegment() {
        return this.errorSegment ;
    }
}
