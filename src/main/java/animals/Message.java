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

//    public static String getPattern(String key, Object... args) {
//        return MessageFormat.format(rules.getString(key), args);
//    }

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

//    private static String clarify() {
//        return getMessage("ask.again");
//    }

    public static String checkAnswer(String answer) {
        Pattern pattern = Pattern.compile("([a-z ']+)([!.])?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(answer.trim());
        if (matcher.matches()) {
            if (possibleYes().contains(matcher.group(1).toLowerCase())) {
                return "You answered: Yes";
            }
            if (possibleNo().contains(matcher.group(1).toLowerCase())) {
                return "You answered: No";
            }
        }
//        return clarify();
        return "";
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

    private static List<String> possibleYes() {
        return List.of(
                "y",
                "yes",
                "yeah",
                "yep",
                "sure",
                "right",
                "affirmative",
                "correct",
                "indeed",
                "you bet",
                "exactly",
                "you said it"
        );
    }

    public static List<String> possibleNo() {
        return List.of(
                "n",
                "no",
                "no way",
                "nah",
                "nope",
                "negative",
                "i don't think so",
                "yeah no"
        );
    }

    public static String[] getFact(String input) {
        Pattern pattern = Pattern.compile("(it )(can|has|is)\\s([\\w ]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String[] fact = new String[2];
            do {
                fact[0] = matcher.group(2).toLowerCase();
                fact[1] = matcher.group(3).toLowerCase();
            } while (matcher.find());
            return fact;
        }
        return null;
    }

    public static String getAnimal(String input) {
        if (input.matches("(?i)an?\\s[a-z]+")) {
            return input.toLowerCase().trim();
        }
        String article = input.matches("(?i)[aeiouy][a-z]+") ? "an" : "a";
        return String.format("%s %s", article, input.toLowerCase().trim());
    }

    public static String onExit() {
        List<String> list = List.of("Have a nice day!",
                "Bye, Bye!",
                "Bye!",
                "See you soon!");
//        return getRandom(list);
        return "";
    }

    public static String getVerb(String verb, boolean is) {
        if (!is) {
            switch (verb) {
                case "can":
                    return "can't";
                case "has":
                    return "doesn't have";
                case "is":
                    return "isn't";
            }
        }
        return verb;
    }

    public static String getDistinguishQuestion(String verb, String fact) {
        switch (verb) {
            case "can":
                return "Can it " + fact + "?";
            case "has":
                return "Does it have " + fact + "?";
            case "is":
                return "Is it " + fact + "?";
        }
        return null;
    }

    public static String getDistinguishQuestion(Node node) {
        if (node.isLeaf()) {
            return "Is it " + node.getVal() + "?";
        }  else {
            String[] fact = getFact(node.getVal());
            assert fact != null;
            return getDistinguishQuestion(fact[0], fact[1]);
        }
    }

    public static String getListOfAnimal(Node root) {
        StringBuilder sb = new StringBuilder("Here are the animals I know:");
        List<Node> leaves = Node.getLeaves(root);
        List<String> values = new LinkedList<>();
        for (Node leaf : leaves) {
            values.add(leaf.getVal().split("\\s")[1]);
//            values.add(leaf.getVal().split("\\s")[0]);
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

    public static String buildPath(String txt, Node node) {
        StringBuilder sb = new StringBuilder();
        LinkedList<Node> path = new LinkedList<>();
        if (hasNode(txt, node, path)) {
            sb.append(String.format("Facts about the %s:", txt.split("\\s")[1]));
            for (int i = 0; i < path.size() - 1; i++) {
                sb.append("\n");
                if (path.get(i).getYes() == path.get(i+1)) {
                    sb.append(String.format(" - %s.", path.get(i).getVal()));
                } else {
                    String[] fact = getFact(path.get(i).getVal());
                    if (fact == null) {
                        return "Something went wrong";
                    }
                    String verb = fact[0];
                    String sentence = fact[1];
                    sb.append(String.format(" - It %s %s.", getVerb(verb, false), sentence));
                }
            }
        } else {
            sb.append(String.format("No facts about the %s.", getAnimal(txt).split("\\s")[1]));
        }
        return sb.toString();
    }

    public static String calculateStatistic(Node root) {
        int totalNum = Node.getAll(root).size();
        int animalsNum = Node.getLeaves(root).size();
        int statementsNum = totalNum - animalsNum;
        int height = Node.height(root);
        int minDepth = Node.minDepth(root) - 1;
        double averageDepth = Node.averageDepth(root);
        return String.format("The Knowledge Tree stats\n" +
                "\n" +
                "- root node                    %s\n" +
                "- total number of nodes        %d\n" +
                "- total number of animals      %d\n" +
                "- total number of statements   %d\n" +
                "- height of the tree           %d\n" +
                "- minimum animal's depth       %d\n" +
                "- average animal's depth       %.1f",
                root.getVal(), totalNum, animalsNum, statementsNum, height, minDepth, averageDepth);
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
