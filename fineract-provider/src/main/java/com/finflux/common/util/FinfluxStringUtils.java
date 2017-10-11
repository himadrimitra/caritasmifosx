package com.finflux.common.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class FinfluxStringUtils {

    public static Map<String, String> convertJsonStringToMap(final String jsonString) {
        if (StringUtils.isNotBlank(jsonString)) { return new Gson().fromJson(jsonString,
                new TypeToken<LinkedHashMap<String, String>>() {}.getType()); }
        return null;
    }

    /**
     * String text = "One, two, {{four}} sir!"; 
     * new HashMap<String, Object>().put("four", "three");
     * mustache.execute(text, params)
     * Result : "One, two, three sir!"
     * @param templateText
     * @param identifyText
     * @param params
     * @return
     */
    public static String replaceTemplateText(final String templateText, final String identifyText, final Map<String, Object> params) {
        if (StringUtils.isNotBlank(templateText)) {
            final MustacheFactory mf = new DefaultMustacheFactory();
            final Mustache mustache = mf.compile(new StringReader(templateText), identifyText);
            final StringWriter stringWriter = new StringWriter();
            mustache.execute(stringWriter, params);
            return stringWriter.toString();
        }
        return templateText;
    }
}