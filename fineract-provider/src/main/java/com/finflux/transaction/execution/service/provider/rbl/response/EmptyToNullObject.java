package com.finflux.transaction.execution.service.provider.rbl.response;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class EmptyToNullObject extends JsonDeserializer<String> {


        @Override
        public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                JsonNode node = jp.getCodec().readTree(jp);
                String aString = node.asText();
                if(aString == null || aString.equals("") || aString.equals("{}") ) {
                        return null;
                } else {
                        return aString;
                }
        }
}
