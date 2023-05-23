package com.andriienko;

import com.andriienko.proxx.adapter.in.UIProxxAdapter;
import com.andriienko.proxx.adapter.in.cli.ConsoleProxxAdapter;
import com.andriienko.proxx.adapter.out.InMemorySingleGameRepository;
import com.andriienko.proxx.application.mapper.GameMapper;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.application.service.ProxxGameService;
import com.andriienko.proxx.cli.input.resolver.ConsoleInputResolver;
import com.andriienko.proxx.cli.output.BoardViewFormatter;
import com.andriienko.proxx.cli.output.factory.ConsoleUIFactory;
import com.andriienko.proxx.cli.output.formatter.ColorBoardViewFormatter;
import com.andriienko.proxx.cli.output.formatter.ColorCellViewFormatter;
import com.andriienko.proxx.domain.GameFactory;

public class ProxxGameApplication {
    public static void main(String[] args) {
        PlayGameUseCase gameService = new ProxxGameService(
                new GameFactory(),
                new InMemorySingleGameRepository(),
                new GameMapper()
        );

        BoardViewFormatter boardViewFormatter = new ColorBoardViewFormatter(new ColorCellViewFormatter());
        UIProxxAdapter cliAdapter = new ConsoleProxxAdapter(
                gameService,
                new ConsoleInputResolver(),
                new ConsoleUIFactory(boardViewFormatter)
        );
        cliAdapter.run();
    }
}