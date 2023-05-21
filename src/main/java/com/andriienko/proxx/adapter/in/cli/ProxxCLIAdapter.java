package com.andriienko.proxx.adapter.in.cli;

import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.application.printer.BoardViewPrinter;
import com.andriienko.proxx.common.GameStatus;
import com.andriienko.proxx.common.PlayMode;
import com.andriienko.proxx.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Scanner;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProxxCLIAdapter {
    PlayGameUseCase gameService;
    BoardViewPrinter boardViewPrinter;
    Scanner scanner;

    public void run() {
        printMainMenu();
        gameLoop(createBoard());
    }

    void printMainMenu() {
        clearScreen();
        printBanner();
        String options = """
                1. Easy
                2. Medium
                3. Expert
                4. Custom
                """;
        System.out.println(options);
    }

    BoardView createBoard() {
        PlayMode mode = getPlayMode();
        BoardView boardView;
        if (mode == PlayMode.CUSTOM) {
            System.out.println("Row, Columns, # of Black Holes: ");
            Position position = getPositionInput();
            int numberOfBlackHoles = getBlackHolesNumber();
            boardView = gameService.newGame(position.getRow() + 1, position.getColumn() + 1, numberOfBlackHoles);
        } else {
            boardView = gameService.newGame(mode.getRows(), mode.getColumns(), mode.getColumns());
        }
        return boardView;
    }

    private void gameLoop(BoardView boardView) {
        printBoard(boardView);
        do {
            boardView = gameService.openCell(getPositionInput());
            printBoard(boardView);
        } while (boardView.getStatus() == GameStatus.IN_PROGRESS);

        if (boardView.getStatus() == GameStatus.WIN) {
            System.out.println("Congratulations, you won!");
            System.console().readLine("\nPress any key to continue");
            scanner.next();
        } else {
            System.out.println("Sorry, you lose");
            System.console().readLine("\nPress any key to continue");
        }
        run();
    }

    private void printBoard(BoardView boardView) {
        System.out.println("\n\n");
        clearScreen();
        System.out.println(boardViewPrinter.print(boardView));
    }

    private PlayMode getPlayMode() {
        PlayMode playMode = null;
        while (playMode == null) {
            System.out.print("Enter play mode mode (q for exit): ");
            String input = getStringOrQuit(scanner);
            switch (input) {
                case "1" -> playMode = PlayMode.EASY;
                case "2" -> playMode = PlayMode.MEDIUM;
                case "3" -> playMode = PlayMode.EXPERT;
                case "4" -> playMode = PlayMode.CUSTOM;
            }
        }
        return playMode;
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

    private String getStringOrQuit(Scanner scan) {
        String input = scan.nextLine();
        if (input.equalsIgnoreCase("q")) {
            System.out.println("Thanks for playing! Goodbye!");
            System.exit(0);
        }
        return input;
    }

    public int getBlackHolesNumber() {
        int blackHolesNumber = 0;
        do {
            System.out.println("# of Black Holes:");
            if (!scanner.hasNextInt()) {
                getStringOrQuit(scanner);
                System.out.println("Invalid Black Holes #.");
                continue;
            }
            blackHolesNumber = scanner.nextInt();

        } while (!isBlackHolesNumberValid());
        return blackHolesNumber;

    }

    public Position getPositionInput() {
        Position input = new Position(0, 0);
        int row = 0;
        int column = 0;
        do {
            System.out.print("Row column (q foe exit): ");
            if (!scanner.hasNextInt()) {
                String res = getStringOrQuit(scanner);
                if(res.equalsIgnoreCase("q")) {
                    run();
                } else {
                    System.out.println("Invalid row.");
                }
                continue;
            }
            row = scanner.nextInt();
            if (!scanner.hasNextInt()) {
                getStringOrQuit(scanner);
                System.out.println("Invalid column.");
                continue;
            }
            column = scanner.nextInt();
        } while (!isPositionInputValid(input));
        return new Position(row - 1, column - 1);
    }

    private boolean isBlackHolesNumberValid() {
        //todo
        return true;
    }

    private boolean isPositionInputValid(Position position) {
        //todo
       /* if(!board.validPosition(position)) {
            System.out.println("Coordinate not inside the play space!");
            return false;
        }
        if(board.isCellRevealed(position)) {
            System.out.println("That cell is already opened!");
            return false;
        }*/
        return true;
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
