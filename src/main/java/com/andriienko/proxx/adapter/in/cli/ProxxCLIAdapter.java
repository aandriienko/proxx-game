package com.andriienko.proxx.adapter.in.cli;

import com.andriienko.proxx.adapter.in.ProxxUIAdapter;
import com.andriienko.proxx.adapter.in.printer.GamePrinter;
import com.andriienko.proxx.adapter.in.resolver.InputResolver;
import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.application.dto.GameView;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.enums.GameStatus;
import com.andriienko.proxx.enums.PlayMode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProxxCLIAdapter implements ProxxUIAdapter {

    private final PlayGameUseCase gameService;
    private final GamePrinter gamePrinter;
    private final InputResolver consoleInputResolver;

    public void run() {
        gamePrinter.printMainMenu();
        gameLoop(createGame());
    }

    private void gameLoop(GameView gameView) {
        gamePrinter.printBoard(gameView);
        do {
            BoardView finalBoardView = gameView.getBoardView();
            // Let's allow user to enter 1-based coordinates)
            int row = consoleInputResolver.getIntegerInput((r -> r > 0 && r <= finalBoardView.getRows()),
                    "Row (q for exit): ");
            int column = consoleInputResolver.getIntegerInput(c -> c > 0 && c <= finalBoardView.getColumns(),
                    "Col (q for exit): ");
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
        PlayMode mode = consoleInputResolver.getPlayMode();
        GameView gameView;
        if (mode == PlayMode.CUSTOM) {
            int boardSide = consoleInputResolver.getIntegerInput(bs -> bs > 2 && bs <= 50, "Board side: ");
            int numberOfBlackHoles = consoleInputResolver.getIntegerInput(bh -> bh > 1 && bh <= boardSide * boardSide - 1, "Black holes number: ");
            gameView = gameService.newGame(boardSide, boardSide, numberOfBlackHoles);
        } else {
            gameView = gameService.newGame(mode.getRows(), mode.getColumns(), mode.getBlackHoles());
        }
        return gameView;
    }

}
