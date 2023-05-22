package com.andriienko.proxx.adapter.in.cli;

import com.andriienko.proxx.GameStatus;
import com.andriienko.proxx.PlayMode;
import com.andriienko.proxx.adapter.in.ProxxUIAdapter;
import com.andriienko.proxx.adapter.in.formatter.BoardViewFormatter;
import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.application.dto.GameView;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;

import java.util.Scanner;
import java.util.function.Predicate;


public class ProxxCLIAdapter implements ProxxUIAdapter {
    private final PlayGameUseCase gameService;
    private final BoardViewFormatter boardViewFormatter;
    private final Scanner scanner;

    public ProxxCLIAdapter(PlayGameUseCase gameService, BoardViewFormatter boardViewFormatter) {
        this.gameService = gameService;
        this.boardViewFormatter = boardViewFormatter;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        printMainMenu();
        gameLoop(createGame());
    }

    void printMainMenu() {
        clearScreen();
        printBanner();
        String options = """
                1. Easy
                2. Medium
                3. Expert
                4. Custom: side is from  3 to 50 and black holes number... well at least leave 1 cell
                """;
        System.out.println(options);
    }

    GameView createGame() {
        PlayMode mode = getPlayMode();
        GameView gameView;
        if (mode == PlayMode.CUSTOM) {
            int boardSide = getIntInput(bs -> bs > 2 && bs <= 50, "Board side: ");
            int numberOfBlackHoles = getIntInput(bh -> bh > 1 && bh <= boardSide * boardSide - 1, "Black holes number: ");
            gameView = gameService.newGame(boardSide, boardSide, numberOfBlackHoles);
        } else {
            gameView = gameService.newGame(mode.getRows(), mode.getColumns(), mode.getBlackHoles());
        }
        return gameView;
    }

    void gameLoop(GameView gameView) {
        printBoard(gameView);

        do {
            BoardView finalBoardView = gameView.getBoardView();
            // Let's allow user to enter 1-based coordinates)
            int row = getIntInput(r -> r > 0 && r <= finalBoardView.getRows(),
                    "Row (q for exit): ");
            int column = getIntInput(c -> c > 0 && c <= finalBoardView.getColumns(),
                    "Col (q for exit): ");
            gameView = gameService.openCell(row - 1, column - 1);
            printBoard(gameView);
        } while (gameView.getStatus() == GameStatus.IN_PROGRESS);

        if (gameView.getStatus() == GameStatus.WIN) {
            System.out.println("Congratulations, you won!");
        } else {
            System.out.println("Sorry, you lose");
        }
        if (System.console() != null) {
            System.console().readLine("\nPress any key to continue...");
        }
        run();
    }

    private void printBoard(GameView gameView) {
        System.out.println("\n\n");
        clearScreen();
        System.out.println(boardViewFormatter.format(gameView.getBoardView()));
        System.out.printf("Opened %d of %d with %d black holes!%n%n", gameView.getNumberOfOpenedCells(), gameView.getSize(), gameView.getBlackHolesNumber());
    }

    private void printBanner() {
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

    private PlayMode getPlayMode() {
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

    private String getStringOrQuit() {
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("q")) {
            System.out.println("Thanks for playing! Goodbye!");
            System.exit(0);
        }
        return input;
    }

    int getIntInput(Predicate<Integer> predicate, String promptMessage) {
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

    /**
     *  Not all CLI supports this
     */
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
