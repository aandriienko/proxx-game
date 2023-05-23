package com.andriienko.proxx.cli.input.resolver;
import com.andriienko.proxx.cli.input.InputResolver;
import com.andriienko.proxx.enums.PlayMode;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * This class is responsible for resolving the inputs from the console.
 * It provides methods to get play mode and other integer inputs from the user via the console.
 * It encapsulates the logic of user interaction and input validation, thus separating the concern of user input from other parts of the application.
 * This class also manages the termination of the game if the user wishes to quit.
 */
public class ConsoleInputResolver implements InputResolver {
    private final Scanner scanner;

    public ConsoleInputResolver() {
        this.scanner = new Scanner(System.in);
    }

    public PlayMode getPlayMode() {
        PlayMode playMode = null;
        while (playMode == null) {
            System.out.print("Choose play mode [1..4] (q for exit): ");
            String input = getStringOrQuit();
            switch (input) {
                case "1" -> playMode = PlayMode.EASY;
                case "2" -> playMode = PlayMode.MEDIUM;
                case "3" -> playMode = PlayMode.EXPERT;
                case "4" -> playMode = PlayMode.CUSTOM;
            }
        }
        return playMode;
    }

    public int getIntegerInput(Predicate<Integer> predicate, String promptMessage) {
        int input = 0;
        while (!predicate.test(input)) {
            System.out.print(promptMessage);
            if (!scanner.hasNextInt()) {
                getStringOrQuit();
            } else {
                input = scanner.nextInt();
                scanner.nextLine();
            }
        }
        return input;
    }
    private String getStringOrQuit() {
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("q")) {
            System.out.println("Thanks for playing! Goodbye!");
            System.exit(0);
        }
        return input;
    }
}
