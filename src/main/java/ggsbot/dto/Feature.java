package ggsbot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Feature {

    private Attributes attributes;

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Feature() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attributes {

        public Attributes() {
        }

        @JsonProperty("OBJECTID")
        private String objectId;

        @JsonProperty("CLASS")
        private String clazz;

        @JsonProperty("CENTER")
        private String center;

        @JsonProperty("LOCATION")
        private String location;

        @JsonProperty("B")
        private String lat;

        @JsonProperty("L")
        private String lon;

        @JsonProperty("NAME")
        private String name;

        @JsonProperty("NUM_MARK")
        private String mark;

        public String getClazz() {
            return clazz;
        }

        public void setClazz(String clazz) {
            this.clazz = clazz;
        }

        public String getCenter() {
            return center;
        }

        public void setCenter(String center) {
            this.center = center;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public String getObjectId() {
            return objectId;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }
    }
}
