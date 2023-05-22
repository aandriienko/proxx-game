package com.andriienko.proxx.adapter.in.cli.resolver;
import com.andriienko.proxx.adapter.in.resolver.InputResolver;
import com.andriienko.proxx.enums.PlayMode;
import java.util.Scanner;
import java.util.function.Predicate;

public class ConsoleInputResolver implements InputResolver {
    private final Scanner scanner;

    public ConsoleInputResolver() {
        this.scanner = new Scanner(System.in);
    }

    public PlayMode getPlayMode() {
        PlayMode playMode = null;
        while (playMode == null) {
            System.out.print("Choose play mode (q for exit): ");
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

    public String getStringOrQuit() {
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("q")) {
            System.out.println("Thanks for playing! Goodbye!");
            System.exit(0);
        }
        return input;
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
}
