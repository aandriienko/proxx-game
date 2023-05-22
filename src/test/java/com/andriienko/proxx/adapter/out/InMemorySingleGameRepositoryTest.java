package com.andriienko.proxx.adapter.out;

import com.andriienko.proxx.application.port.out.GameRepository;
import com.andriienko.proxx.domain.Game;
import com.andriienko.proxx.domain.GameFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemorySingleGameRepositoryTest {

    GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository = new InMemorySingleGameRepository();
    }

    @Test
    @DisplayName("Exception is thrown when no saved game")
    void shouldThrowExceptionWhenGettingUninitialized() {
        assertThrows(IllegalStateException.class, gameRepository::get);
    }

    @Test
    @DisplayName("Saves and get game")
    void shouldSaveAndGetGame() {
        Game expectedGame = new GameFactory().createGameWithRandomlyDistributedHoles(5, 5, 1);
        Game actualGame = gameRepository.save(expectedGame);
        assertEquals(expectedGame, actualGame);

        actualGame = gameRepository.get();
        assertEquals(expectedGame, actualGame);
    }
}
