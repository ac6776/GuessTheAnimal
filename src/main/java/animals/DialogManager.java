package animals;

import java.util.LinkedList;
import java.util.List;

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
            out.addAll(List.of(Message.greeting(),
                    "I want to learn about animals.",
                    "Which animal do you like most?"));
        } else {
            this.node = node;
//            this.root = node;
            out.add("Welcome to the animal expert system!\n" +
                    "\n" +
                    "What do you want to do:\n" +
                    "\n" +
                    "1. Play the guessing game\n" +
                    "2. List of all animals\n" +
                    "3. Search for an animal\n" +
                    "4. Calculate statistics\n" +
                    "5. Print the Knowledge Tree\n" +
                    "0. Exit");
            state = State.MAIN_MENU;
        }
    }

    public void in(String input) {
        switch (state) {
            case INPUT_FIRST_ANIMAL:
                animal1 = Message.getAnimal(input);
                node = new Node(animal1, null);
//                state = State.PRESS_ENTER;
//                out.add("Wonderful! I've learned so much about animals!");
//                out.add("Let's play a game!\n" +
//                        "You think of an animal, and I guess it.\n" +
//                        "Press enter when you're ready.");
                state = State.MAIN_MENU;
                out.add("Welcome to the animal expert system!\n" +
                        "\n" +
                        "What do you want to do:\n" +
                        "\n" +
                        "1. Play the guessing game\n" +
                        "2. List of all animals\n" +
                        "3. Search for an animal\n" +
                        "4. Calculate statistics\n" +
                        "5. Print the Knowledge Tree\n" +
                        "0. Exit");
                break;
            case PRESS_ENTER:
                if (input.length() == 0) {
                    String question = Message.getDistinguishQuestion(node);
                    out.add(question);
                    state = State.GUESSING_ANIMAL;
                }
                break;
            case MAIN_MENU:
                int selection = -1;
                try {
                    selection = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    out.add("Not a number");
                    e.printStackTrace();
                    break;
                }
                switch (selection) {
                    case 1:
                        out.add("You think of an animal, and I guess it.\n" +
                            "Press enter when you're ready.");
                        state = State.PRESS_ENTER;
                        break;
                    case 2:
                        out.add(Message.getListOfAnimal(node));
                        finished = true;
                        break;
                    case 3:
                        out.add("Enter the animal:");
                        state = State.SEARCH_ANIMAL;
                        break;
                    case 4:
                        out.add(Message.calculateStatistic(Node.getRoot(node)));
                        finished = true;
                        break;
                    case 5:
//                        out.add(Message.knowledgeTree(Node.getRoot(node)));
//                        Message.printTree(Node.getRoot(node));
                        out.add(Message.buildTree(Node.getRoot(node)));
                        finished = true;
                        break;
                    case 0:
                        out.add("\n" + Message.onExit());
                        finished = true;
                        break;
                    default:
                        out.add("Oops, you have entered wrong number. Please, try again.");
                        break;
                }
                break;
            case SEARCH_ANIMAL:
                String search = Message.getAnimal(input);
//                out.add(Message.searchAnimal(search, Node.getRoot(node)));
//                Message.printFacts(Node.getRoot(node), search);
//                Message.printPath(search, Node.getRoot(node));
                out.add(Message.buildPath(search, Node.getRoot(node)));
                finished = true;
                break;
            case GUESSING_ANIMAL:
                String answer = Message.checkAnswer(input);
                if (answer.contains("Yes")) {
                    if (node.getYes() == null) {
                        out.add("Well done!");
                        out.add("Would you like to play again?");
                        state = State.PLAY_AGAIN;
                    } else {
                        node = node.getYes();
                        out.add(Message.getDistinguishQuestion(node));
                    }
                } else if (answer.contains("No")) {
                    if (node.getNo() == null) {
                        animal1 = node.getVal();
                        out.add("I give up. What animal do you have in mind?");
                        state = State.INPUT_SECOND_ANIMAL;
                    } else {
                        node = node.getNo();
                        out.add(Message.getDistinguishQuestion(node));
                    }
                } else {
                    out.add(answer);
                }
                break;
            case INPUT_SECOND_ANIMAL:
                animal2 = Message.getAnimal(input);
                state = State.INPUT_FACT;
                out.add(String.format("Specify a fact that distinguishes %s from %s.",
                        animal1,
                        animal2));
                out.add("The sentence should be of the format: 'It can/has/is ...'.");
                break;
            case INPUT_FACT:
                fact = Message.getFact(input);
                if (fact != null) {
                    state = State.INPUT_ANSWER;
                    out.add(String.format("Is the statement correct for %s?",
                            animal2));
                    break;
                }
                out.add("The examples of a statement:\n" +
                        " - It can fly\n" +
                        " - It has horn\n" +
                        " - It is a mammal");
                out.add(String.format("Specify a fact that distinguishes %s from %s.",
                        animal1,
                        animal2));
                out.add("The sentence should be of the format: 'It can/has/is ...'.");
                break;
            case INPUT_ANSWER:
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
                    out.add("I have learned the following facts about animals:");
                    out.add("- The " + animal1.split("\\s")[1] + " " + Message.getVerb(fact[0], !animal2Is) + " " + fact[1] + ".");
                    out.add("- The " + animal2.split("\\s")[1] + " " + Message.getVerb(fact[0], animal2Is) + " " + fact[1] + ".");
                    out.add("I can distinguish these animals by asking the question:");
                    out.add("- " + Message.getDistinguishQuestion(fact[0], fact[1]));
                    out.add("Would you like to play again?");
                    state = State.PLAY_AGAIN;
                } else {
                    out.add(answer);
                }
                break;
            case PLAY_AGAIN:
                answer = Message.checkAnswer(input);
                if (answer.contains("Yes")) {
                    out.add("You think of an animal, and I guess it.\n" +
                            "Press enter when you're ready.");
                    state = State.PRESS_ENTER;
                } else if (answer.contains("No")) {
                    out.add("\n" + Message.onExit());
                    finished = true;
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

