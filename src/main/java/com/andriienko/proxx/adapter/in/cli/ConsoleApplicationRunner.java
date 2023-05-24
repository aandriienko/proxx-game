package com.andriienko.proxx.adapter.in.cli;

import com.andriienko.proxx.adapter.in.ApplicationRunner;
import com.andriienko.proxx.cli.output.UIFactory;
import com.andriienko.proxx.cli.input.InputResolver;
import com.andriienko.proxx.application.dto.GameView;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.enums.GameStatus;
import com.andriienko.proxx.enums.PlayMode;
import lombok.AllArgsConstructor;

import java.util.function.Predicate;

/**
 * The ConsoleApplicationRunner class is an input adapter in the hexagonal architecture style (another example of input adapter - Rest Controller).
 * <p>
 * The primary goal of this class is to act as an intermediary between the user and the game's core logic.
 * This design allows changes to the core logic or the user interface to be made independently,
 * improving the system's flexibility and maintainability.
 */
@AllArgsConstructor
public class ConsoleApplicationRunner implements ApplicationRunner {
    private static final int MIN_BOARD_SIDE = 3;
    private static final int MAX_BOARD_SIDE = 50;

    private final PlayGameUseCase gameService;
    private final InputResolver inputResolver;
    private final UIFactory uiFactory;

    public void run() {
        printMainMenu();
        gameLoop(createGame());
    }

    /**
     * Handles the game loop swapping between printing the board and
     * handling input to reach the next state.
     * Once the game reaches either a won state where all safe cells
     * have been revealed or a black hole has been revealed the game ends.
     * When the game ends the full board is revealed and a victory/defeat message is shown.
     */
    private void gameLoop(GameView gameView) {
        printBoard(gameView);
        do {
            int row = inputResolver.getIntegerInput(isValidRowNumber(gameView), "Row [%d..%d] (q for exit): ".formatted(1, gameView.getBoardView().getRows()));
            int column = inputResolver.getIntegerInput(isValidColumnNumber(gameView), "Col [%d..%d] (q for exit): ".formatted(1, gameView.getBoardView().getColumns()));
            gameView = gameService.revealCell(row - 1, column - 1);
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

    /**
     * If the user chooses a custom mode, the method further asks the user to provide a board size and the number of black holes,
     * and then uses this data to create a new game.<p>
     * If a predefined mode is chosen, the new game is created using the predefined parameters for that mode.
     *
     * @return GameView - snapshot of the game state for display purposes.
     */
    private GameView createGame() {
        PlayMode mode = inputResolver.getPlayMode();
        GameView gameView;
        if (mode == PlayMode.CUSTOM) {
            int boardSide = inputResolver.getIntegerInput(isValidBoardSide(), "Board side [%d..%d]: ".formatted(MIN_BOARD_SIDE, MAX_BOARD_SIDE));
            int blackHolesNumber = inputResolver.getIntegerInput(isValidBlackHolesNumber(boardSide), "Black holes number (max = %d): ".formatted(boardSide * boardSide - 1));
            gameView = gameService.newGame(boardSide, boardSide, blackHolesNumber);
        } else {
            gameView = gameService.newGame(mode.getRows(), mode.getColumns(), mode.getBlackHoles());
        }
        return gameView;
    }

    private void printMainMenu() {
        clearScreen();
        printBanner();
        System.out.println(uiFactory.createMainMenu());
    }

    private void printBanner() {
        System.out.println(uiFactory.createBanner());
        System.out.println();
    }

    private void printBoard(GameView gameView) {
        clearScreen();
        System.out.println("\n\n");
        System.out.println(uiFactory.createBoard(gameView.getBoardView()));
        System.out.printf("Revealed %d of %d with %d black holes!%n%n", gameView.getRevealedCellsNumber(), gameView.getSize(), gameView.getBlackHolesNumber());
    }

    /**
     * Applies escape sequence to clear screen, unfortunately not all terminals supports this
     */
    private void clearScreen() {
        System.out.print("\u001B[H\u001B[2J");
        System.out.flush();
    }

    private Predicate<Integer> isValidRowNumber(GameView gameView) {
        return row -> row > 0 && row <= gameView.getBoardView().getRows();
    }

    private Predicate<Integer> isValidColumnNumber(GameView gameView) {
        return column -> column > 0 && column <= gameView.getBoardView().getColumns();
    }

    private Predicate<Integer> isValidBoardSide() {
        return boardSide -> boardSide >= MIN_BOARD_SIDE && boardSide <= MAX_BOARD_SIDE;
    }

    private Predicate<Integer> isValidBlackHolesNumber(int boardSide) {
        return blackHolesNumber -> blackHolesNumber > 0 && blackHolesNumber <= boardSide * boardSide - 1;
    }
}
