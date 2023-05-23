package com.andriienko.proxx.domain;

import lombok.NoArgsConstructor;

import java.util.Random;

/**
 *  GameFactory class follows Factory Pattern and encapsulates the logic for setting up a new game.
 *  This includes the creation of a new Game object and the placement of black holes on the game board.
 */
@NoArgsConstructor
public class GameFactory {

    public Game createGameWithRandomlyDistributedHoles(int rows, int columns, int blackHolesNumber) {
        Game game = new Game(rows, columns);
        placeBlackHolesRandomly(game, blackHolesNumber);
        return game;
    }

    private void placeBlackHolesRandomly(Game game, int blackHolesNumber) {
        validateBlackHolesNumber(blackHolesNumber);
        Random random = new Random();
        int blackHolesCount = 0;
        Board board = game.getBoard();
        while (blackHolesCount < blackHolesNumber) {
            if (game.placeBlackHole(random.nextInt(board.getRows()), random.nextInt(board.getColumns()))) {
                blackHolesCount++;
            }
        }
    }

    private void validateBlackHolesNumber(int blackHolesNumber) {
        if (blackHolesNumber < 1) {
            throw new IllegalArgumentException("Board should contain at least 1 black hole");
        }
    }
}