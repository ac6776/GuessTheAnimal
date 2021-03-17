package animals;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DataStorage storage = new DataStorage(args);
        Node root = storage.load();
        DialogManager diag = new DialogManager(root);

        diag.out();
        while (!diag.isFinished()) {
            diag.in(scanner.nextLine());
            diag.out();
        }
        storage.write(diag.getRoot());
    }
}

