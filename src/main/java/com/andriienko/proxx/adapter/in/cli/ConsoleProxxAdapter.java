package com.andriienko.proxx.adapter.in.cli;

import com.andriienko.proxx.adapter.in.UIProxxAdapter;
import com.andriienko.proxx.cli.output.UIFactory;
import com.andriienko.proxx.cli.input.InputResolver;
import com.andriienko.proxx.application.dto.GameView;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.enums.GameStatus;
import com.andriienko.proxx.enums.PlayMode;
import lombok.AllArgsConstructor;

import java.util.function.Predicate;

@AllArgsConstructor
public class ConsoleProxxAdapter implements UIProxxAdapter {

    private final PlayGameUseCase gameService;
    private final InputResolver inputResolver;
    private final UIFactory uiFactory;

    public void run() {
        printMainMenu();
        gameLoop(createGame());
    }

    private void gameLoop(GameView gameView) {
        printBoard(gameView);
        do {
            int row = inputResolver.getIntegerInput(isValidRowNumber(gameView), "Row (q for exit): ");
            int column = inputResolver.getIntegerInput(isValidColumnNumber(gameView), "Col (q for exit): ");
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

    private GameView createGame() {
        PlayMode mode = inputResolver.getPlayMode();
        GameView gameView;
        if (mode == PlayMode.CUSTOM) {
            int boardSide = inputResolver.getIntegerInput(isValidBoardSide(), "Board side: ");
            int blackHolesNumber = inputResolver.getIntegerInput(isValidBlackHolesNumber(boardSide), "Black holes number: ");
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
        System.out.println("\n\n");
        clearScreen();
        System.out.println(uiFactory.createBoard(gameView.getBoardView()));
        System.out.printf("Revealed %d of %d with %d black holes!%n%n", gameView.getRevealedCellsNumber(), gameView.getSize(), gameView.getBlackHolesNumber());
    }

    /**
     * Not all CLI supports this
     */
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private Predicate<Integer> isValidRowNumber(GameView gameView) {
        return row -> row > 0 && row <= gameView.getBoardView().getRows();
    }

    private Predicate<Integer> isValidColumnNumber(GameView gameView) {
        return column -> column > 0 && column <= gameView.getBoardView().getColumns();
    }

    private Predicate<Integer> isValidBoardSide() {
        return boardSide -> boardSide > 2 && boardSide <= 50;
    }

    private Predicate<Integer> isValidBlackHolesNumber(int boardSide) {
        return blackHolesNumber -> blackHolesNumber > 1 && blackHolesNumber < boardSide * boardSide - 1;
    }
}
