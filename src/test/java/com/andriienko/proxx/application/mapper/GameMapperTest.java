package com.andriienko.proxx.application.mapper;

import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.application.dto.CellView;
import com.andriienko.proxx.application.dto.GameView;
import com.andriienko.proxx.domain.Cell;
import com.andriienko.proxx.domain.Game;
import com.andriienko.proxx.domain.GameFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.andriienko.proxx.TestUtils.BOARD_DIMENSION_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameMapperTest {
    GameFactory gameFactory;
    GameMapper gameMapper;

    @BeforeEach
    void setUp() {
        gameFactory = new GameFactory();
        gameMapper = new GameMapper();
    }

    @Test
    @DisplayName("Game -> GameView transformation test")
    void shouldTransformGameToGameView() {   // Create a real game instance
        Game game = gameFactory.createGameWithRandomlyDistributedHoles(BOARD_DIMENSION_SIZE, BOARD_DIMENSION_SIZE, 1);
        GameView gameView = gameMapper.transformToGameView(game);

        assertEquals(game.getRevealedCellsNumber(), gameView.getRevealedCellsNumber());
        assertEquals(game.getBlackHolesNumber(), gameView.getBlackHolesNumber());
        assertEquals(game.getSize(), gameView.getSize());
        assertEquals(game.getStatus(), gameView.getStatus());

        // Assert that the board was also transformed correctly
        BoardView boardView = gameView.getBoardView();
        assertEquals(game.getBoard().getRows(), boardView.getRows());
        assertEquals(game.getBoard().getColumns(), boardView.getColumns());

        // Assert that every cell was transformed correctly
        for (int row = 0; row < boardView.getRows(); row++) {
            for (int col = 0; col < boardView.getColumns(); col++) {
                CellView cellView = boardView.getCells()[row][col];
                Cell cell = game.getBoard().getCellAt(row, col);
                assertEquals(cell.getAdjacentBlackHolesCount(), cellView.getAdjacentBlackHolesCount());
                assertEquals(cell.isBlackHole(), cellView.isBlackHole());
                assertEquals(cell.isRevealed(), cellView.isRevealed());
                assertEquals(cell.isEmpty(), cellView.isEmpty());
            }
        }
    }
}