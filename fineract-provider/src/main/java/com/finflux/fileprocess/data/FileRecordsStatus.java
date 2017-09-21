package com.finflux.fileprocess.data;

public enum FileRecordsStatus {

    INVALID(0, "fileRecordsStatus.invalid", "invalid", "Invalid"), //
    PENDING(1, "fileRecordsStatus.uploded", "pending", "Pending"), //
    SUCCESS(2, "fileRecordsStatus.uploded", "success", "Success"), //
    FAILED(3, "fileRecordsStatus.faild", "faild", "Faild");

    private final Integer value;
    private final String code;
    private final String systemName;
    private final String displayName;

    private FileRecordsStatus(final Integer value, final String code, final String systemName, final String displayName) {
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

    public static FileRecordsStatus fromInt(final Integer type) {
        FileRecordsStatus fileRecordsStatus = FileRecordsStatus.INVALID;
        if (type != null) {
            switch (type) {
                case 1:
                    fileRecordsStatus = FileRecordsStatus.PENDING;
                break;
            }
        }
        return fileRecordsStatus;
    }
}
