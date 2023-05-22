package com.andriienko.proxx.adapter.in.printer;

import com.andriienko.proxx.application.dto.GameView;

public interface GamePrinter {
    void printMainMenu();
    void printBanner();
    void printBoard(GameView gameView);
    void clearScreen();
}
