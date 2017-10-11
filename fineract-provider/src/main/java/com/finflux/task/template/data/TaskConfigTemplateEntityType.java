package com.finflux.task.template.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;


public enum TaskConfigTemplateEntityType 
{
    CENTER(1,"taskTemplateEntityType.center",TaskEntityType.CENTER,TaskConfigKey.CENTER_ID),
    CLIENT(2,"taskTemplateEntityType.client",TaskEntityType.CLIENT,TaskConfigKey.CLIENT_ID),
    VILLAGE(3,"taskTemplateEntityType.village",TaskEntityType.VILLAGE,TaskConfigKey.VILLAGE_ID),
    OFFICE(4,"taskTemplateEntityType.office",TaskEntityType.OFFICE,TaskConfigKey.OFFICE_ID);
    
    private final Integer value;
    private final String code;
    private final TaskEntityType correspondingTaskEntity;
    private final TaskConfigKey correspondingTaskConfigKey;
    
    private TaskConfigTemplateEntityType(final Integer value,final String code,final TaskEntityType correspondingTaskEntity,final TaskConfigKey correspondingTaskConfigKey)
    {
        this.value=value;
        this.code=code;
        this.correspondingTaskEntity=correspondingTaskEntity;
        this.correspondingTaskConfigKey=correspondingTaskConfigKey;
        
    }
    
    public TaskEntityType getCorrespondingTaskEntity() {
        return this.correspondingTaskEntity;
    }
    
    public TaskConfigKey getCorrespondingTaskConfigKey() {
        return this.correspondingTaskConfigKey;
    }
    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<String, TaskConfigTemplateEntityType> entityNameToEnumMap = new HashMap<>();
    private static final Map<Integer, TaskConfigTemplateEntityType> intToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;
    static {
        int i = 0;
        for (final TaskConfigTemplateEntityType entityType : TaskConfigTemplateEntityType.values()) {
            if (i == 0) {
                minValue = entityType.value;
            }
            intToEnumMap.put(entityType.value, entityType);
            entityNameToEnumMap.put(entityType.name().toLowerCase(), entityType);
            if (minValue >= entityType.value) {
                minValue = entityType.value;
            }
            if (maxValue < entityType.value) {
                maxValue = entityType.value;
            }
            i = i + 1;

        }

    }

    public static TaskConfigTemplateEntityType fromInt(final int i) {
        final TaskConfigTemplateEntityType type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }
    public static TaskConfigTemplateEntityType fromString(final String str) {
        final TaskConfigTemplateEntityType entityType = entityNameToEnumMap.get(str.toLowerCase());
        return entityType;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.name().toLowerCase());
    }
    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> taskTemplateEntityTypeOptions = new ArrayList<>();
        for (final TaskConfigTemplateEntityType enumType : values()) {
            final EnumOptionData enumOptionData = enumType.getEnumOptionData();
            if (enumOptionData != null) {
                taskTemplateEntityTypeOptions.add(enumOptionData);
            }
        }
        return taskTemplateEntityTypeOptions;
    }
    

    
}
