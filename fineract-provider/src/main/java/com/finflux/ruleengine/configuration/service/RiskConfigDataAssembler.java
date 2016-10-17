package com.finflux.ruleengine.configuration.service;

import com.finflux.ruleengine.configuration.domain.RuleModel;
import com.finflux.ruleengine.configuration.form.RiskRuleForm;
import com.finflux.ruleengine.lib.data.Bucket;
import com.finflux.ruleengine.lib.data.EntityRuleType;
import com.finflux.ruleengine.lib.data.KeyValue;
import com.google.gson.JsonElement;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskConfigDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final ToApiJsonSerializer<List<KeyValue>> keyValueListToJsonSerializer;
    private final ToApiJsonSerializer<List<Bucket>> bucketListToJsonSerializer;


    @Autowired
    public RiskConfigDataAssembler(final FromJsonHelper fromApiJsonHelper,
                                   final ToApiJsonSerializer<List<KeyValue>> keyValueListToJsonSerializer,
                                   final ToApiJsonSerializer<List<Bucket>> bucketListToJsonSerializer) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.keyValueListToJsonSerializer = keyValueListToJsonSerializer;
        this.bucketListToJsonSerializer = bucketListToJsonSerializer;
    }

    public RuleModel assembleCreateRule(EntityRuleType entityRuleType, final JsonCommand command) {
        RiskRuleForm form = fromApiJsonHelper.fromJson(command.json(),RiskRuleForm.class);
        String bucketExpression = bucketListToJsonSerializer.serialize(form.getBuckets());
        String possibleOutputs = keyValueListToJsonSerializer.serialize(form.getOutputConfiguration().getOptions());
        return RuleModel.create(entityRuleType,form.getName(), form.getUname(), form.getDescription(),
                form.getOutputConfiguration().getDefaultValue(),form.getOutputConfiguration()
                        .getValueType(), possibleOutputs,bucketExpression, form.getActive());
    }

    public void assembleUpdateRule(RuleModel ruleModel, EntityRuleType entityRuleType, JsonCommand command) {
        RiskRuleForm form = fromApiJsonHelper.fromJson(command.json(),RiskRuleForm.class);
        String bucketExpression = bucketListToJsonSerializer.serialize(form.getBuckets());
        String possibleOutputs = keyValueListToJsonSerializer.serialize(form.getOutputConfiguration().getOptions());
        ruleModel.setName(form.getName());
        ruleModel.setUname(form.getUname());
        ruleModel.setDescription(form.getDescription());
        ruleModel.setDefaultValue(form.getOutputConfiguration().getDefaultValue());
        ruleModel.setValueType(form.getOutputConfiguration().getValueType().getValue());
        ruleModel.setPossibleOutputs(possibleOutputs);
        ruleModel.setExpression(bucketExpression);
        ruleModel.setActive(form.getActive());
    }
}