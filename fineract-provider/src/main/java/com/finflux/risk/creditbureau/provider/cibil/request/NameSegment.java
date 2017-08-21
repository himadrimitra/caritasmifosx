package com.finflux.risk.creditbureau.provider.cibil.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

//Variable Length segment. At max 174 bytes
public class NameSegment extends RequestSegment {

    public final static String DISPLAY_NAME1 = "01";
    public final static String DISPLAY_NAME2 = "02";
    public final static String DISPLAY_NAME3 = "03";
    public final static String DISPLAY_NAME4 = "04";
    public final static String DISPLAY_NAME5 = "05";
    public final static String DOB = "07";
    public final static String GENDER = "08";
    private final String SEGMENT_TAG = "PN";
    private final String SEGMENT_TAG_LENGTH = "03";
    private final String segmentTag = "N01"; // Always N01, 3 bytes

    public final static Integer FEMALE = new Integer(1);
    public final static Integer MALE = new Integer(2);

    private final Integer NUMBER_OF_NAMES = 5;
    private final Integer NAME_LENGTH = 26;

    // Each display name at max 26 bytes
    private final List<String> displayNames = new ArrayList<>(5);

    private Date dateOfBirth = null;
    private Integer gender = null; // 1=FEMALE, 2=MALE

    private final CibilRequest request;

    public NameSegment(final CibilRequest request) {
        this.request = request;
    }

    @Override
    public String prepareTuefPacket() {
        StringBuilder builder = new StringBuilder();
        builder.append(SEGMENT_TAG);
        builder.append(SEGMENT_TAG_LENGTH);
        builder.append(segmentTag);
        for (int i = 0; i < NUMBER_OF_NAMES; i++) {
            if (i < this.displayNames.size()) {
                builder.append(getFormattedLength((i + 1)));
                String name = displayNames.get(i);
                builder.append(getFormattedLength(name));
                builder.append(displayNames.get(i));
            } else {
                builder.append(getFormattedLength((i + 1)));
                builder.append("00");
            }
        }

        if (this.dateOfBirth != null) {
            builder.append("07");
            String dob = this.request.dateFormat_DDMMYYYY.format(this.dateOfBirth);
            builder.append(getFormattedLength(dob.length()));
            builder.append(dob);
        }
        if (this.gender != null) {
            builder.append("08");
            builder.append(getFormattedLength(1));
            builder.append(gender);
        }
        return builder.toString();
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getGender() {
        return this.gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public void addName(final String name) {
        String workingName = name;
        if (!StringUtils.isEmpty(workingName)) {
            int length = workingName.getBytes().length;
            if (length > NAME_LENGTH) {
                if (this.request.truncateData) {
                    workingName = StringUtils.substring(name, 0, NAME_LENGTH);
                } else {
                    throw new RuntimeException();
                }
            }
            this.displayNames.add(workingName);
        }
    }

    public static String getFieldName(final String errorRecord) {
        final String fieldTag = errorRecord.substring(ERRORTAG_STARTINDEX, ERRORTAG_ENDINDEX);
        String fieldName = "";
        switch (fieldTag) {
            case DISPLAY_NAME1:
                fieldName = "Display Name1";
            break;
            case DISPLAY_NAME2:
                fieldName = "Display Name2";
            break;
            case DISPLAY_NAME3:
                fieldName = "Display Name3";
            break;
            case DISPLAY_NAME4:
                fieldName = "Display Name4";
            break;
            case DISPLAY_NAME5:
                fieldName = "Display Name5";
            break;
            case DOB:
                fieldName = "Date of Birth";
            break;
            case GENDER:
                fieldName = "Gender";
            break;
        }
        return fieldName;
    }
}
