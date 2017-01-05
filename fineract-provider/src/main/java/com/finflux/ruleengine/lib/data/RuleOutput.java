package com.finflux.ruleengine.lib.data;

/**
 * Created by dhirendra on 06/09/16.
 */
public class RuleOutput {

    private String value;
    private String bucket;
    private ValueType type;
    private OutputReason reason;
    private String error;

    public RuleOutput(ValueType type, String outputValue, OutputReason outputReason, String bucket, String error) {
        this.value = outputValue;
        this.type = type;
        this.reason = outputReason;
        this.bucket = bucket;
        this.error = error;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ValueType getType() {
        return type;
    }

    public void setType(ValueType type) {
        this.type = type;
    }

    public OutputReason getReason() {
        return reason;
    }

    public void setReason(OutputReason reason) {
        this.reason = reason;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
