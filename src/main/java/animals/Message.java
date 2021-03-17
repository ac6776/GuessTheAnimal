package animals;

import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {

    private Message() {}

    public static String greeting() {
        LocalTime time = LocalTime.now();
        if (time.isAfter(LocalTime.of(4, 59, 59)) &&
                time.isBefore(LocalTime.of(12, 0))) {
            return "Good morning";
        }
        if (time.isAfter(LocalTime.of(11, 59, 59)) &&
                time.isBefore(LocalTime.of(18, 0))) {
            return "Good afternoon";
        }
        return "Good evening";
    }

    private static String clarify() {
        List<String> list = List.of("I'm not sure I caught you: was it yes or no?",
                "Funny, I still don't understand, is it yes or no?",
                "Oh, it's too complicated for me: just tell me yes or no.",
                "Could you please simply say yes or no?",
                "Oh, no, don't try to confuse me: say yes or no.");
        return getRandom(list);
    }

    private static String getRandom(List<String> messages) {
        return messages.get(new Random().nextInt(messages.size()));
    }

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
        return clarify();
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
        return getRandom(list);
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
        StringBuilder sb = new StringBuilder("Here are the animals I know:\n");
        List<Node> leaves = Node.getLeaves(root);
        List<String> values = new LinkedList<>();
        for (Node leaf : leaves) {
            values.add(leaf.getVal());
        }
        Collections.sort(values);
        for (String leaf : values) {
            sb.append(" - ").append(leaf).append("\n");
        }
        return sb.toString();
    }

    public static String searchAnimal(String input, Node root) {
        StringBuilder sb = new StringBuilder();
        List<Node> leaves = Node.getLeaves(root);
        Node animal = leaves.stream()
                .filter(val -> val.getVal().contains(input.toLowerCase()))
                .findFirst()
                .orElse(null);
        if (animal != null) {
           return listFacts(animal);
        }
        return String.format("No facts about the %s.", getAnimal(input).split("\\s")[1]);
    }

//    private static String listFacts(StringBuilder sb, Node node) {
//        if (node.getParent() == null) {
//            return sb.toString();
//        }
//        sb.append(node.getVal()).append("\n");
//        return listFacts(sb, node.getParent());
//    }

    private static String listFacts(Node node) {
        if (node.getParent() == null) {
            return "\n";
        }
        return node.getParent().getVal() + "\n" + listFacts(node.getParent());
    }

//    public static void printFacts(Node node, String txt) {
//        if (node.isLeaf()) {
//            return;
//        }
//        printFacts(node.getYes(), txt);
//        printFacts(node.getNo(), txt);
//        System.out.println(node.getVal());
//    }

//    public static String printFacts(Node node, String txt) {
//        if (node.getYes() )
//        if (node)
//        if (node.isLeaf()) {
//            return "";
//        }
//        if (node.getVal().equals(txt)) {
//
//        }
//        printFacts(node.getYes(), txt);
//        printFacts(node.getNo(), txt);
//        System.out.println(node.getVal());
//    }
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

    public static String knowledgeTree(Node root) {
        return "└ " + root.getVal();
    }
}
