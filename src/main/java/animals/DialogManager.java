package animals;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class DialogManager {
    private final List<String> out;
    private State state;
    private boolean finished;
    private String animal1;
    private String animal2;
    private String[] fact;
    private Node node;
//    private Node root;


    public DialogManager(Node node) {
        finished = false;
        out = new LinkedList<>();
        if (node == null) {
            state = State.INPUT_FIRST_ANIMAL;
            out.addAll(List.of(
                    Message.get("greeting"),
                    Message.get("animal.wantLearn"),
                    Message.get("animal.askFavorite")));
        } else {
            this.node = node;
            out.addAll(List.of(
                    Message.get("welcome"),
                    Message.get("menu.property.title"),
                    Message.get("menu")));
            state = State.MAIN_MENU;
        }
    }

    public void in(String input) {
        switch (state) {
            case INPUT_FIRST_ANIMAL:
//                if (input.matches(Message.getPattern("animal.isCorrect"))) {
//                    System.out.println(input);
//                }
//                animal1 = Message.getAnimal(input);
                animal1 = Message.applyRule("animal", input);
//                System.out.println(animal1);
                node = new Node(animal1, null);
                state = State.MAIN_MENU;
                out.addAll(List.of(
                        Message.get("welcome"),
                        Message.get("menu.property.title"),
                        Message.get("menu")));
                break;
            case PRESS_ENTER:
                if (input.length() == 0) {
//                    String question = Message.getDistinguishQuestion(node);
                    String question = Message.applyRule(node.isLeaf() ? "guessAnimal" : "question", node.getVal());
                    out.add(question);
                    state = State.GUESSING_ANIMAL;
                }
                break;
            case MAIN_MENU:
                int selection = -1;
                try {
                    selection = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    out.add(Message.get("menu.property.error", 5));
                    e.printStackTrace();
                    break;
                }
                switch (selection) {
                    case 1:
                        out.addAll(List.of(
                                Message.get("game.letsPlay"),
                                Message.get("game.think"),
                                Message.get("game.enter")));
                        state = State.PRESS_ENTER;
                        break;
                    case 2:
                        //todo
                        out.add(Message.getListOfAnimal(node));
                        finished = true;
                        break;
                    case 3:
                        out.add(Message.get("animal.prompt"));
                        state = State.SEARCH_ANIMAL;
                        break;
                    case 4:
                        //todo
                        out.add(Message.calculateStatistic(Node.getRoot(node)));
                        finished = true;
                        break;
                    case 5:
                        out.add(Message.buildTree(Node.getRoot(node)));
                        finished = true;
                        break;
                    case 0:
                        out.add("\n" + Message.get("farewell"));
                        finished = true;
                        break;
                    default:
                        out.add(Message.get("menu.property.error", 5));
                        break;
                }
                break;
            case SEARCH_ANIMAL:
                //todo#1
                String search = Message.getAnimal(input);
                out.add(Message.buildPath(search, Node.getRoot(node)));
                finished = true;
                break;
            case GUESSING_ANIMAL:
                //todo
                String answer = Message.checkAnswer(input);
                if (answer.contains("Yes")) {
                    if (node.getYes() == null) {
                        out.add(Message.get("game.win"));
                        out.add(Message.get("game.again"));
                        state = State.PLAY_AGAIN;
                    } else {
                        node = node.getYes();
                        //todo
                        out.add(Message.getDistinguishQuestion(node));
                    }
                } else if (answer.contains("No")) {
                    if (node.getNo() == null) {
                        animal1 = node.getVal();
                        out.add(Message.get("game.giveUp"));
                        state = State.INPUT_SECOND_ANIMAL;
                    } else {
                        node = node.getNo();
                        //todo
                        out.add(Message.getDistinguishQuestion(node));
                    }
                } else {
                    //todo
                    out.add(answer);
                }
                break;
            case INPUT_SECOND_ANIMAL:
//                animal2 = Message.getAnimal(input);
                animal2 = Message.applyRule("animal", input);
                state = State.INPUT_FACT;
                out.add(Message.get("statement.prompt", animal1, animal2));
                break;
            case INPUT_FACT:
//                fact = Message.getFact(input);
                boolean statementIsCorrect = Message.isCorrect("statement", input);
                if (statementIsCorrect) {
                    state = State.INPUT_ANSWER;
                    out.add(Message.get("game.isCorrect", animal2));
                    break;
                }
                out.addAll(List.of(
                        Message.get("statement.error"),
                        Message.get("statement.prompt", animal1, animal2)));
                break;
            case INPUT_ANSWER:
                //todo
                answer = Message.checkAnswer(input);
                if (answer.contains("Yes") || answer.contains("No")) {
                    boolean animal2Is = answer.contains("Yes");
                    node.setVal("It " + fact[0] + " " + fact[1]);
                    if (animal2Is) {
                        node.setYes(new Node(animal2, node));
                        node.setNo(new Node(animal1, node));
                    } else {
                        node.setYes(new Node(animal1, node));
                        node.setNo(new Node(animal2, node));
                    }
                    out.add(Message.get("game.learned"));
                    out.add("- The " + animal1.split("\\s")[1] + " " + Message.getVerb(fact[0], !animal2Is) + " " + fact[1] + ".");
                    out.add("- The " + animal2.split("\\s")[1] + " " + Message.getVerb(fact[0], animal2Is) + " " + fact[1] + ".");
                    out.add(Message.get("game.distinguish"));
                    out.add("- " + Message.getDistinguishQuestion(fact[0], fact[1]));
                    out.add(Message.get("game.again"));
                    state = State.PLAY_AGAIN;
                } else {
                    out.add(answer);
                }
                break;
            case PLAY_AGAIN:
                answer = Message.checkAnswer(input);
                if (answer.contains("Yes")) {
                    out.addAll(List.of(
                            Message.get("game.think"),
                            Message.get("game.enter")));
                    state = State.PRESS_ENTER;
                } else if (answer.contains("No")) {
                    state = State.MAIN_MENU;
                    out.addAll(List.of(
                            Message.get("welcome"),
                            Message.get("menu.property.title"),
                            Message.get("menu")));
                } else {
                    out.add(answer);
                }
                node = Node.getRoot(node);
                break;
        }
    }

    public void out() {
        out.forEach(System.out::println);
        out.clear();
    }

    public boolean isFinished() {
        return finished;
    }

    public Node getNode() {
        return node;
    }

    public Node getRoot() {
        return Node.getRoot(node);
    }

    enum State {
        INPUT_FIRST_ANIMAL, INPUT_SECOND_ANIMAL, INPUT_FACT, INPUT_ANSWER, PRESS_ENTER, GUESSING_ANIMAL, PLAY_AGAIN, MAIN_MENU, SEARCH_ANIMAL
    }
}

