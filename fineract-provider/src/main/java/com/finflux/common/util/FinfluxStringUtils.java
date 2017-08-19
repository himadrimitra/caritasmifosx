package com.finflux.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class FinfluxStringUtils {

    public static Map<String, String> convertJsonStringToMap(final String jsonString) {
        if (StringUtils.isNotBlank(jsonString)) { return new Gson().fromJson(jsonString,
                new TypeToken<LinkedHashMap<String, String>>() {}.getType()); }
        return null;
    }
}