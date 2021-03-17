package animals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.io.IOException;

public class DataStorage {
    private ObjectMapper objectMapper;
    private Type type;
    private File file;

    public DataStorage() {
    }

    public DataStorage(String[] args) {
        type = Type.JSON;
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("-type")) {
                type = Type.valueOf(args[1].toUpperCase());
            }
        }
        switch (type) {
            case XML:
                objectMapper = new XmlMapper();
                break;
            case YAML:
                objectMapper = new YAMLMapper();
                break;
            case JSON:
                objectMapper = new JsonMapper();
                break;
        }
        file = new File("animals." + type);
    }

    public void write(Node root) {
        try {
            objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(file, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node load() {
        Node root = null;
        try {
            root = objectMapper.readValue(file, Node.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }
}
