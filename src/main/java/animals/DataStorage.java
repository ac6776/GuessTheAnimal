package animals;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class DataStorage {
    private Type type;
    private File file;

    public DataStorage() {
    }

    public DataStorage(String[] args) {
        type = Type.JSON;
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("-type")) {
                type = Type.of(args[1].toUpperCase());
            }
        }
        String lang = Locale.getDefault().getLanguage().equals("en") ? "" : ("_" + Locale.getDefault().getLanguage());
        file = new File("animals" + lang + "." + type.getExtension());
    }

    public void write(Node root) {
        try {
            type.getObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(file, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node load() {
        Node root = null;
        try {
            root = type.getObjectMapper().readValue(file, Node.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }
}
