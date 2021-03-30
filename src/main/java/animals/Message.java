package animals;

import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("message");
    private static final Pattern DELIMITER = Pattern.compile("\\f");
    private static final Random random = new Random();
    private static final ResourceBundle rules = ResourceBundle.getBundle("patterns");
    private static final Map<String, Pattern> patterns;

    static {
        patterns = new HashMap<>();
        Set<String> keys = rules.keySet();
        for (String key : keys) {
            if (!key.endsWith(".replace")) {
                patterns.put(key, Pattern.compile(rules.getString(key)));
            }
        }
    }

    private Message() {}

    public static String applyRule(String rule, String value) {
        for (int i = 1; ; i++) {
            String key = rule + "." + i;
            Pattern pattern = patterns.get(key  + ".pattern");
            if (pattern == null) {
                return value;
            }
            Matcher matcher = pattern.matcher(value);
            if(matcher.matches()) {
                return matcher.replaceFirst(rules.getString(key + ".replace"));
            }
        }
    }

    public static String get(String key, Object... args) {
        if (key.equals("greeting")) {
            return greetByTime();
        }
        if (key.equals("menu")) {
            return menu();
        }
        return getText(key, args);
    }

    private static String getText(String key, Object... args) {
        String[] strings = DELIMITER.split(bundle.getString(key));
        return MessageFormat.format(strings[random.nextInt(strings.length)], args);
    }

    private static String menu() {
        return String.format("1. %s\n2. %s\n3. %s\n4. %s\n5. %s\n0. %s",
                get("menu.entry.play"),
                get("menu.entry.list"),
                get("menu.entry.search"),
                get("menu.entry.statistics"),
                get("menu.entry.print"),
                get("menu.property.exit"));

    }

    private static String greetByTime() {
        String greeting = "";
        LocalTime time = LocalTime.now();
        String[] timeKeys = {"morning.time.after",
                "morning.time.before",
                "afternoon.time.after",
                "afternoon.time.before",
                "evening.time.after",
                "night.time.before",
                "early.time.after",
                "early.time.before"};
        Map<String, LocalTime> map = new HashMap<>();
        for (int i = 0; i < timeKeys.length; i++) {
            map.put(timeKeys[i], getTime(timeKeys[i]));
        }
        if (time.isAfter(map.get(timeKeys[0])) && time.isBefore(map.get(timeKeys[1]))) {
            greeting = get("greeting.morning");
        }
        if (time.isAfter(map.get(timeKeys[2])) && time.isBefore(map.get(timeKeys[3]))) {
            greeting = get("greeting.afternoon");
        }
        if (time.isAfter(map.get(timeKeys[4]))) {
            greeting = get("greeting.evening");
        }
        if (time.isBefore(map.get(timeKeys[5]))) {
            greeting = get("greeting.night");
        }
        if (time.isAfter(map.get(timeKeys[6])) && time.isBefore(map.get(timeKeys[7]))) {
            greeting = get("greeting.early");
        }
        return new String[]{getText("greeting"), greeting}[random.nextInt(2)];
    }


    private static LocalTime getTime(String key) {
        return LocalTime.parse(bundle.getString(key));
    }

    public static boolean isCorrect(String key, String value) {
        Pattern pattern = patterns.get(key + ".isCorrect");
        if (pattern == null) {
            return false;
        }
        return pattern.matcher(value).matches();
    }

    public static String getFacts(String animal1, String animal2, String fact, boolean isCorrect) {
        return get("game.learned") +
                    "\n- " + applyRule("definite", animal1) + " " + applyRule("animalFact", applyRule(isCorrect ? "negative" : "statement", fact)) + "." +
                    "\n- " + applyRule("definite", animal2) + " " + applyRule("animalFact", applyRule(!isCorrect ? "negative" : "statement", fact)) + "." +
                    "\n" + Message.get("game.distinguish") +
                    "\n- " + Message.applyRule("question", fact) +
                    "\n" + Message.get("game.again");
    }

    public static String getListOfAnimal(Node root) {
        StringBuilder sb = new StringBuilder(get("tree.list.animals"));
        List<Node> leaves = Node.getLeaves(root);
        List<String> values = new LinkedList<>();
        for (Node leaf : leaves) {
            values.add(applyRule("animalName", leaf.getVal()));
        }
        Collections.sort(values);
        for (String leaf : values) {
            sb.append("\n - ").append(leaf);
        }
        return sb.toString();
    }

    private static boolean hasNode(String txt, Node node, LinkedList<Node> path) {
        if (node == null) {
            return false;
        }
        path.add(node);
        if (node.getVal().equals(txt)) {
            return true;
        }
        if (hasNode(txt, node.getYes(), path) || hasNode(txt, node.getNo(), path)) {
            return true;
        }
        path.removeLast();
        return false;
    }

    public static String buildPath(String animal, Node node) {
        StringBuilder sb = new StringBuilder();
        LinkedList<Node> path = new LinkedList<>();
        if (hasNode(animal, node, path)) {
            sb.append(get("tree.search.facts", applyRule("animalName", animal)));
            for (int i = 0; i < path.size() - 1; i++) {
                sb.append("\n");
                if (path.get(i).getYes() == path.get(i+1)) {
                    sb.append(String.format(get("tree.search.printf"), path.get(i).getVal()));
                } else {
                    sb.append(String.format(get("tree.search.printf"), applyRule("negative", path.get(i).getVal())));
                }
            }
        } else {
            sb.append(get("tree.search.noFacts", applyRule("animalName", animal)));
        }
        return sb.toString();
    }

    public static String calculateStatistic(Node root) {
        return get("tree.stats.title") +
                "\n" + get("tree.stats.root", root.getVal()) +
                "\n" + get("tree.stats.nodes", Node.getAll(root).size()) +
                "\n" + get("tree.stats.animals", Node.getLeaves(root).size()) +
                "\n" + get("tree.stats.statements", Node.getAll(root).size() - Node.getLeaves(root).size()) +
                "\n" + get("tree.stats.height", Node.height(root)) +
                "\n" + get("tree.stats.minimum", Node.minDepth(root)) +
                "\n" + get("tree.stats.average", Node.averageDepth(root));


    }

    private static String buildTree(Node node, int count) {
        if (node == null) {
            return "";
        }
        String val;
        if (node.getParent() != null) {
            if (node.getParent().getYes() == node) {
                val = String.format("%s├ %s\n", " ".repeat(count), node.getVal());
            } else {
                val = String.format("%s└ %s\n", " ".repeat(count), node.getVal());
            }
        } else {
            val = String.format("%s└ %s\n", " ".repeat(count), node.getVal());
        }
        return val + buildTree(node.getYes(), count+1) + buildTree(node.getNo(), count+1);
    }

    public static String buildTree(Node root) {
        StringBuilder sb = new StringBuilder();
        String[] tree = buildTree(root, 0).split("\n");
        sb.append(tree[0]);
        for (int i = 1; i < tree.length; i++) {
            int j = getJ(tree[i]);
            if (j != -1) {
                int k = i;
                while (tree[k].charAt(j) != '└') {
                    if (tree[k].charAt(j) == ' ') {
                        tree[k] = tree[k].substring(0, j) + "|" + tree[k].substring(j + 1);
                    }
                    k++;
                }
            }
            sb.append("\n").append(tree[i]);
        }
        return sb.toString();
    }

    private static int getJ(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '├') {
                return i;
            }
        }
        return -1;
    }
}
