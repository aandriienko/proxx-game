package com.andriienko.proxx.cli.output;

import com.andriienko.proxx.application.dto.BoardView;

public interface UIFactory {
    String createMainMenu();
    String createBanner();
    String createBoard(BoardView gameView);
}
