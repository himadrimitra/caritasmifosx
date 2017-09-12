package com.finflux.fileprocess.data;

public enum FileProcessType {

    INVALID(0, "fileProcessType.invalid", "invalid", "invalid"), //
    SND(1, "fileProcessType.sanctioned.but.not.disbursed", "sanctionedButNotDisbursed", "Sanctioned but not disbursed");

    private final Integer value;
    private final String code;
    private final String systemName;
    private final String displayName;

    private FileProcessType(final Integer value, final String code, final String systemName, final String displayName) {
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

    public static FileProcessType fromInt(final Integer type) {
        FileProcessType fileProcessType = FileProcessType.INVALID;
        if (type != null) {
            switch (type) {
                case 1:
                    fileProcessType = FileProcessType.SND;
                break;
            }
        }
        return fileProcessType;
    }
    
    public static FileProcessType fromString(final String type) {
        FileProcessType fileProcessType = FileProcessType.INVALID;
        if (type != null) {
            switch (type) {
                case "snd":
                    fileProcessType = FileProcessType.SND;
                break;
            }
        }
        return fileProcessType;
    }
}
