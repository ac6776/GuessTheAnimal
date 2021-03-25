package animals.localization;

import java.util.ListResourceBundle;
import java.util.function.UnaryOperator;

public class App_ru extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            {"hello", "Привет!"},
            {"bye", new String[]{
                    "Bye!",
                    "Bye, bye!",
                    "See you later!",
                    "See you soon!",
                    "Have a nice one!"
            }},
            {"animal.name", (UnaryOperator<String>) name -> {
                if (name.matches("[aeiou].*")) {
                    return "an " + name;
                } else {
                    return "a " + name;
                }
            }},
            {"animal.question", (UnaryOperator<String>) animal -> "Is it " + animal + "?"}
        };
    }
}