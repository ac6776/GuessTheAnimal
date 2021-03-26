package animals;

import java.util.LinkedList;
import java.util.List;

public class DialogManager {
    private final List<String> out;
    private State state;
    private boolean finished;
    private String animal1;
    private String animal2;
    private String fact;
    private Node node;


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
                animal1 = Message.applyRule("animal", input);
                node = new Node(animal1, null);
                state = State.MAIN_MENU;
                out.addAll(List.of(
                        Message.get("welcome"),
                        Message.get("menu.property.title"),
                        Message.get("menu")));
                break;
            case PRESS_ENTER:
                if (input.length() == 0) {
                    String question = Message.applyRule(node.isLeaf() ? "guessAnimal" : "question", node.getVal());
                    out.add(question);
                    state = State.GUESSING_ANIMAL;
                }
                break;
            case MAIN_MENU:
                int selection;
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
                        out.add(Message.getListOfAnimal(node));
                        finished = true;
                        break;
                    case 3:
                        out.add(Message.get("animal.prompt"));
                        state = State.SEARCH_ANIMAL;
                        break;
                    case 4:
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
                String search = Message.applyRule("animal", input);
                out.add(Message.buildPath(search, Node.getRoot(node)));
                finished = true;
                break;
            case GUESSING_ANIMAL:
                if (Message.isCorrect("positiveAnswer", input)) {
                    if (node.isLeaf()) {
                        out.add(Message.get("game.win"));
                        out.add(Message.get("game.again"));
                        state = State.PLAY_AGAIN;
                    } else {
                        node = node.getYes();
                        out.add(Message.applyRule("guessAnimal", node.getVal()));
                    }
                } else if (Message.isCorrect("negativeAnswer", input)) {
                    if (node.isLeaf()) {
                        animal1 = node.getVal();
                        out.add(Message.get("game.giveUp"));
                        state = State.INPUT_SECOND_ANIMAL;
                    } else {
                        node = node.getNo();
                        out.add(Message.applyRule("guessAnimal", node.getVal()));
                    }
                } else {
                    out.add(Message.get("ask.again"));
                }
                break;
            case INPUT_SECOND_ANIMAL:
                animal2 = Message.applyRule("animal", input);
                state = State.INPUT_FACT;
                out.add(Message.get("statement.prompt", animal1, animal2));
                break;
            case INPUT_FACT:
                if (Message.isCorrect("statement", input)) {
                    fact = input;
                    state = State.INPUT_ANSWER;
                    out.add(Message.get("game.isCorrect", animal2));
                    break;
                }
                out.addAll(List.of(
                        Message.get("statement.error"),
                        Message.get("statement.prompt", animal1, animal2)));
                break;
            case INPUT_ANSWER:
                boolean yes = Message.isCorrect("positiveAnswer", input);
                boolean no = Message.isCorrect("negativeAnswer", input);
                if (yes || no) {
                    node.setVal(Message.applyRule("statement", fact));
                    node.setYes(new Node(yes ? animal2 : animal1, node));
                    node.setNo(new Node(yes ? animal1 : animal2, node));
                    out.add(Message.getFacts(animal1, animal2, fact, yes));
                    state = State.PLAY_AGAIN;
                } else {
                    out.add(Message.get("ask.again"));
                }
                break;
            case PLAY_AGAIN:
                if (Message.isCorrect("positiveAnswer", input)) {
                    out.addAll(List.of(
                            Message.get("game.think"),
                            Message.get("game.enter")));
                    state = State.PRESS_ENTER;
                } else if (Message.isCorrect("negativeAnswer", input)) {
                    out.addAll(List.of(
                            Message.get("welcome"),
                            Message.get("menu.property.title"),
                            Message.get("menu")));
                    state = State.MAIN_MENU;
                } else {
                    out.add(Message.get("ask.again"));
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

    public Node getRoot() {
        return Node.getRoot(node);
    }

    enum State {
        INPUT_FIRST_ANIMAL, INPUT_SECOND_ANIMAL, INPUT_FACT, INPUT_ANSWER, PRESS_ENTER, GUESSING_ANIMAL, PLAY_AGAIN, MAIN_MENU, SEARCH_ANIMAL
    }
}

