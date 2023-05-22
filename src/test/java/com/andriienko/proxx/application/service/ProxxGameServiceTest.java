package com.andriienko.proxx.application.service;

import com.andriienko.proxx.application.dto.GameView;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.application.port.out.GameRepository;
import com.andriienko.proxx.application.mapper.GameMapper;
import com.andriienko.proxx.domain.Game;
import com.andriienko.proxx.domain.GameFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProxxGameServiceTest {

    @Mock
    private GameFactory gameFactory;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private Game game;

    @Mock
    private GameView gameView;

    private PlayGameUseCase gameService;

    @BeforeEach
    public void setup() {
        gameService = new ProxxGameService(gameFactory, gameRepository, gameMapper);
        when(gameMapper.transformToGameView(any())).thenReturn(gameView);
    }

    @Test
    @DisplayName("Should create new game")
    void shouldCreateNewGame() {
        int rows = 3;
        int columns = 3;
        int blackHoles = 3;

        when(gameFactory.createGameWithRandomlyDistributedHoles(anyInt(), anyInt(), anyInt())).thenReturn(game);
        when(gameRepository.save(any())).thenReturn(game);

        gameService.newGame(rows, columns, blackHoles);

        verify(gameFactory).createGameWithRandomlyDistributedHoles(rows, columns, blackHoles);
        verify(gameRepository).save(game);
        verify(gameMapper).transformToGameView(game);
    }

    @Test
    @DisplayName("Should open cell in existing game")
    void shouldOpenCellInExistingGame() {
        int row = 1;
        int column = 1;
        when(gameRepository.get()).thenReturn(game);
        when(gameMapper.transformToGameView(any())).thenReturn(gameView);

        gameService.openCell(row, column);

        verify(game).openCell(row, column);
        verify(gameMapper).transformToGameView(game);
    }
}
