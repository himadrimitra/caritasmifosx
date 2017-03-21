package com.finflux.ruleengine.execution.service.impl;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class DataFieldUtils {

    private final FromJsonHelper fromApiJsonHelper;
    private final String MEMBER_SEPARATOR = "\\.";
    @Autowired
    public DataFieldUtils(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public  void updateParamMap(final Map<String, Object> paramMap, final String paramDetail,
            Map<String, Object> fieldParamObjects) {
        final JsonElement element = this.fromApiJsonHelper.parse(paramDetail);
        JsonArray inputparams = this.fromApiJsonHelper.extractJsonArrayNamed("inputValues", element);
        for (int i = 0; i < inputparams.size(); i++) {
            final JsonObject jsonObject = inputparams.get(i).getAsJsonObject();
            String key = this.fromApiJsonHelper.extractStringNamed("key", jsonObject);
            boolean isValue = this.fromApiJsonHelper.extractBooleanNamed("isValue", jsonObject);
            if (isValue) {
                String sqlParamName = this.fromApiJsonHelper.extractStringNamed("sqlParamName", jsonObject);
                paramMap.put(sqlParamName, fieldParamObjects.get(key));
            }
            if (!isValue) {
                JsonArray properties = this.fromApiJsonHelper.extractJsonArrayNamed("properties", jsonObject);
                switch (key) {
                    case "json_command":
                        JsonCommand jsonCommand = (JsonCommand) fieldParamObjects.get(key);
                        final Map<String, Object> commandAsJsonMap = jsonCommand.extractObjectMap();

                        for (int j = 0; j < properties.size(); j++) {
                            final JsonObject propertyObject = properties.get(j).getAsJsonObject();
                            String feildName = this.fromApiJsonHelper.extractStringNamed("feildName", propertyObject);
                            String sqlParamName = this.fromApiJsonHelper.extractStringNamed("sqlParamName", propertyObject);
                            paramMap.put(sqlParamName, commandAsJsonMap.get(feildName));
                        }
                    break;
                    default:
                        Object object = fieldParamObjects.get(key);

                        for (int j = 0; j < properties.size(); j++) {
                            try {
                                final JsonObject propertyObject = properties.get(j).getAsJsonObject();
                                String feildName = this.fromApiJsonHelper.extractStringNamed("feildName", propertyObject);
                                String[] members = feildName.split(MEMBER_SEPARATOR);
                                Object value = object;
                                for (String member : members) {
                                    Field feild = FieldUtils.getField(object.getClass(), member, true);
                                    value = FieldUtils.readField(feild, value, true);
                                }
                                String sqlParamName = this.fromApiJsonHelper.extractStringNamed("sqlParamName", propertyObject);
                                paramMap.put(sqlParamName, value);
                            } catch (IllegalAccessException e) {

                            }
                        }

                    break;

                }

            }
        }
    }
}
