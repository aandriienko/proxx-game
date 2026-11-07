package com.andriienko;

import com.andriienko.proxx.adapter.in.ApplicationRunner;
import com.andriienko.proxx.adapter.in.gui.JavaFxApplicationRunner;
import com.andriienko.proxx.adapter.out.InMemorySingleGameRepository;
import com.andriienko.proxx.application.mapper.GameMapper;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.application.service.ProxxGameService;
import com.andriienko.proxx.domain.GameFactory;

public class ProxxGameApplication {
    public static void main(String[] args) {
        PlayGameUseCase gameService = new ProxxGameService(
                new GameFactory(),
                new InMemorySingleGameRepository(),
                new GameMapper()
        );

        ApplicationRunner guiAdapter = new JavaFxApplicationRunner(gameService);
        guiAdapter.run();
    }
}
