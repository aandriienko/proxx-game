package com.andriienko.proxx.adapter.in.cli.printer;

import com.andriienko.proxx.adapter.in.formatter.BoardViewFormatter;
import com.andriienko.proxx.adapter.in.printer.GamePrinter;
import com.andriienko.proxx.application.dto.GameView;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConsoleGamePrinter implements GamePrinter {

    private final BoardViewFormatter boardViewFormatter;

    public void printMainMenu() {
        printBanner();
        String options = """
                1. Easy
                2. Medium
                3. Expert
                4. Custom: side is from 3 to 50 and black holes number... Well at least leave 1 cell
                """;
        System.out.println(options);
    }

    public void printBanner() {
        String banner = """
                 ________  ________  ________     ___    ___ ___    ___       _______  ________    _______  ________    \s
                |\\   __  \\|\\   __  \\|\\   __  \\   |\\  \\  /  /|\\  \\  /  /|     /  ___  \\|\\   __  \\  /  ___  \\|\\_____  \\   \s
                \\ \\  \\|\\  \\ \\  \\|\\  \\ \\  \\|\\  \\  \\ \\  \\/  / | \\  \\/  / /    /__/|_/  /\\ \\  \\|\\  \\/__/|_/  /\\|____|\\ /_  \s
                 \\ \\   ____\\ \\   _  _\\ \\  \\\\\\  \\  \\ \\    / / \\ \\    / /     |__|//  / /\\ \\  \\\\\\  \\__|//  / /     \\|\\  \\ \s
                  \\ \\  \\___|\\ \\  \\\\  \\\\ \\  \\\\\\  \\  /     \\/   /     \\/          /  /_/__\\ \\  \\\\\\  \\  /  /_/__   __\\_\\  \\\s
                   \\ \\__\\    \\ \\__\\\\ _\\\\ \\_______\\/  /\\   \\  /  /\\   \\         |\\________\\ \\_______\\|\\________\\|\\_______\\
                    \\|__|     \\|__|\\|__|\\|_______/__/ /\\ __\\/__/ /\\ __\\         \\|_______|\\|_______| \\|_______|\\|_______|
                                                 |__|/ \\|__||__|/ \\|__|                                                 \s
                                                                                                                        \s
                                                                                                                                        """;

        System.out.println(banner);
        System.out.println();
    }

    public void printBoard(GameView gameView) {
        System.out.println("\n\n");
        clearScreen();
        System.out.println(boardViewFormatter.format(gameView.getBoardView()));
        System.out.printf("Opened %d of %d with %d black holes!%n%n", gameView.getNumberOfOpenedCells(), gameView.getSize(), gameView.getBlackHolesNumber());
    }

    /**
     * Not all CLI supports this
     */
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
