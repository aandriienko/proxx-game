package com.andriienko.proxx.adapter.in.cli;

import com.andriienko.proxx.adapter.in.ProxxUIAdapter;
import com.andriienko.proxx.adapter.in.printer.GamePrinter;
import com.andriienko.proxx.adapter.in.resolver.InputResolver;
import com.andriienko.proxx.application.dto.GameView;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.enums.GameStatus;
import com.andriienko.proxx.enums.PlayMode;
import lombok.AllArgsConstructor;

import java.util.function.Predicate;

@AllArgsConstructor
public class ProxxCLIAdapter implements ProxxUIAdapter {

    private final PlayGameUseCase gameService;
    private final GamePrinter gamePrinter;
    private final InputResolver inputResolver;

    public void run() {
        gamePrinter.printMainMenu();
        gameLoop(createGame());
    }

    private void gameLoop(GameView gameView) {
        gamePrinter.printBoard(gameView);
        do {
            int row = inputResolver.getIntegerInput(isValidRowNumber(gameView), "Row (q for exit): ");
            int column = inputResolver.getIntegerInput(isValidColumnNumber(gameView), "Col (q for exit): ");
            gameView = gameService.openCell(row - 1, column - 1);
            gamePrinter.printBoard(gameView);
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
