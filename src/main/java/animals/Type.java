package animals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public enum Type {
    JSON(new JsonMapper()),
    XML(new XmlMapper()),
    YAML(new YAMLMapper());

    private final ObjectMapper objectMapper;

    Type(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static Type of (String type) {
        return Type.valueOf(type.toUpperCase());
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public String getExtension() {
        return this.toString().toLowerCase();
    }
}
