package com.finflux.risk.creditbureau.provider.cibil.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * The Telephone Segment contains the known phone numbers of the consumer
 *
 */
public class TelephoneSegment extends RequestSegment {
    public final static String PHONE_NOT_CLASSIFIED = "00" ;
    public final static String PHONE_MOBILE = "01" ;
    public final static String PHONE_HOME = "02" ;
    public final static String PHONE_OFFICE = "03" ;
    
    private final Integer TOTAL_RECORDS = 4 ;
    private final static String TELEPHONE_SEGMENT_TAGNAME = "PT" ;
    private final static String TELEPHONE_TAGNAME = "01" ;
    private final static String TELEPHONE_EXTENSION_TAGNAME = "02" ;
    private final static String TELEPHONE_TYPE_NAME = "03" ;
   
    private final static String[] TELEPHONE_SEGMENT_TAGS = { "T01", "T02", "T03", "T04" };
    
    private List<Telephone> telephones = new ArrayList<>();

    public void addTelephone(final String telephoneType, final String telephoneNumber, final String extension) {
        Telephone telePhone = new Telephone(telephoneType, telephoneNumber, extension) ;
        this.telephones.add(telePhone) ;
    }
    
    public final void addMobilePhone(final String telephoneNumber) {
        Telephone telePhone = new Telephone(PHONE_MOBILE, telephoneNumber, null) ;
        this.telephones.add(telePhone) ;
    }
    
    public final void addHomePhone(final String telephoneNumber, final String extension) {
        Telephone telePhone = new Telephone(PHONE_HOME, telephoneNumber, extension) ;
        this.telephones.add(telePhone) ;
    }
    
    public final void addOfficePhone(final String telephoneNumber, final String extension) {
        Telephone telePhone = new Telephone(PHONE_OFFICE, telephoneNumber, extension) ;
        this.telephones.add(telePhone) ;
    }
    
    public final void addPhoneNumber(final String telephoneNumber) {
        Telephone telePhone = new Telephone(PHONE_NOT_CLASSIFIED, telephoneNumber, null) ;
        this.telephones.add(telePhone) ;
    }
    
    @Override
    public String prepareTuefPacket() {
        //PT03T010110994019919702101234567890030200
        final Integer totalRecords = this.telephones.size() < TOTAL_RECORDS ? this.telephones.size() : TOTAL_RECORDS ;
        StringBuilder builder = new StringBuilder();
        StringBuilder unclassfiedNumberBuffer = new StringBuilder() ;
        int index = 0 ; 
        for (int i = 0; i < totalRecords; i++) {
            Telephone telephone = this.telephones.get(i);
            if(!telephone.getTelephoneType().equals(PHONE_NOT_CLASSIFIED)) {
                index++ ;
                builder.append(TELEPHONE_SEGMENT_TAGNAME) ;
                builder.append(getFormattedLength(TELEPHONE_SEGMENT_TAGS[i].length())) ;
                builder.append(TELEPHONE_SEGMENT_TAGS[i]) ;
                builder.append(TELEPHONE_TAGNAME) ;
                builder.append(getFormattedLength(telephone.getTelephoneNumber())) ; 
                builder.append(telephone.getTelephoneNumber());
                if(!StringUtils.isEmpty(telephone.getTelephoneExtension())) {
                    builder.append(TELEPHONE_EXTENSION_TAGNAME) ;
                    builder.append(getFormattedLength(telephone.getTelephoneExtension())) ; 
                    builder.append(telephone.getTelephoneExtension());    
                }else {
                    builder.append(TELEPHONE_EXTENSION_TAGNAME) ;
                    builder.append(SIZE_ZERO) ;
                }
                builder.append(TELEPHONE_TYPE_NAME) ;
                builder.append(getFormattedLength(telephone.getTelephoneType())) ;
                builder.append(telephone.getTelephoneType());
            }else {
                if(unclassfiedNumberBuffer.length() > 0) unclassfiedNumberBuffer.append(";") ;
                unclassfiedNumberBuffer.append(telephone.getTelephoneNumber()) ;
            }
        }
        if(unclassfiedNumberBuffer.length() > 0) {
            builder.append(TELEPHONE_SEGMENT_TAGNAME) ;
            builder.append(getFormattedLength(TELEPHONE_SEGMENT_TAGS[index].length())) ;
            builder.append(TELEPHONE_SEGMENT_TAGS[index]) ;
            builder.append(TELEPHONE_TAGNAME) ;
            builder.append(getFormattedLength(unclassfiedNumberBuffer.length())) ; 
            builder.append(unclassfiedNumberBuffer);
            builder.append(TELEPHONE_EXTENSION_TAGNAME) ;
            builder.append(SIZE_ZERO) ;
            builder.append(TELEPHONE_TYPE_NAME) ;
            builder.append(SIZE_TWO) ; 
            builder.append(SIZE_ZERO);
        }
        return builder.toString();
    }

    class Telephone {

        private final String telephoneNumber;
        private final String telephoneExtension ;
        private final String telephoneType;
        public Telephone(String telephoneType, String telephoneNumber, final String extension) {
            super();
            this.telephoneType = telephoneType;
            this.telephoneNumber = telephoneNumber;
            this.telephoneExtension = extension;
        }

        public String getTelephoneType() {
            return this.telephoneType;
        }

        public String getTelephoneNumber() {
            return this.telephoneNumber;
        }

        public String getTelephoneExtension() {
            return this.telephoneExtension;
        }

    }
}
