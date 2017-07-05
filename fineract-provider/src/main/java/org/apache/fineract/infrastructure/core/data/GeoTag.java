package org.apache.fineract.infrastructure.core.data;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.multipart.FormDataBodyPart;

public class GeoTag {

    @JsonProperty("longitude")
    private Long longitude ;
    
    @JsonProperty("latitude")
    private Long latitude ;

    public GeoTag() {
        
    }
    
    public static GeoTag from(final FormDataBodyPart bodyPart) {
        if(bodyPart == null) return null ;
        bodyPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        return bodyPart.getValueAs(GeoTag.class);
    }
    
    public static GeoTag from(final String geoTag) {
        if(StringUtils.isEmpty(geoTag)) return null ;
        final GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(geoTag, GeoTag.class) ;
    }
    public GeoTag(Long longitude, Long latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Long getLongitude() {
        return this.longitude;
    }

    public Long getLatitude() {
        return this.latitude;
    }
    
    @Override
    public String toString() {
        final GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        final String serializedResult = gson.toJson(this); 
        return "null".equals(serializedResult) ? null : serializedResult ;
    }
}
