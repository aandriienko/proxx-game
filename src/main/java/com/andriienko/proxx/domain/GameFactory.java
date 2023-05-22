package com.andriienko.proxx.domain;

import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor
public class GameFactory {

    public Game createGameWithRandomlyDistributedHoles(int rows, int columns, int blackHolesNumber) {
        Game game = new Game(rows, columns);
        placeBlackHoles(game, blackHolesNumber);
        return game;
    }

    private void placeBlackHoles(Game game, int blackHolesNumber) {
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