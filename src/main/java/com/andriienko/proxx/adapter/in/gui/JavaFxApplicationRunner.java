package com.andriienko.proxx.adapter.in.gui;

import com.andriienko.proxx.adapter.in.ApplicationRunner;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import javafx.application.Application;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JavaFxApplicationRunner implements ApplicationRunner {

    private final PlayGameUseCase gameService;

    @Override
    public void run() {
        ProxxFxApp.setUseCase(gameService);
        Application.launch(ProxxFxApp.class);
    }
}
