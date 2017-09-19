package com.finflux.fileprocess.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum FileStatus {

    INVALID(0, "fileStatus.invalid", "invalid", "Invalid"), //
    UPLODED(1, "fileStatus.uploded", "uploded", "Uploded"), //
    IN_PROGRESS(2, "fileStatus.in.progress", "inprogress", "In progress"), //
    COMPLETED(3, "fileStatus.completed", "completed", "Completed");

    private final Integer value;
    private final String code;
    private final String systemName;
    private final String displayName;

    private FileStatus(final Integer value, final String code, final String systemName, final String displayName) {
        this.value = value;
        this.code = code;
        this.systemName = systemName;
        this.displayName = displayName;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public String getSystemName() {
        return this.systemName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static FileStatus fromInt(final Integer type) {
        FileStatus fileStatus = FileStatus.INVALID;
        if (type != null) {
            switch (type) {
                case 1:
                    fileStatus = FileStatus.UPLODED;
                break;
                case 2:
                    fileStatus = FileStatus.IN_PROGRESS;
                break;
                case 3:
                    fileStatus = FileStatus.COMPLETED;
                break;
            }
        }
        return fileStatus;
    }
    
    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(getValue().longValue(), getCode(), getDisplayName());
    }
}
