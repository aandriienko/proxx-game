package com.andriienko;

import com.andriienko.proxx.adapter.in.UIProxxAdapter;
import com.andriienko.proxx.adapter.in.cli.ConsoleProxxAdapter;
import com.andriienko.proxx.adapter.in.cli.formatter.ConsoleBoardViewFormatter;
import com.andriienko.proxx.adapter.in.cli.formatter.ConsoleCellFormatter;
import com.andriienko.proxx.adapter.in.cli.printer.ConsoleGamePrinter;
import com.andriienko.proxx.adapter.in.cli.resolver.ConsoleInputResolver;
import com.andriienko.proxx.adapter.in.formatter.BoardViewFormatter;
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

        BoardViewFormatter boardViewFormatter = new ConsoleBoardViewFormatter(new ConsoleCellFormatter());
        UIProxxAdapter cliAdapter = new ConsoleProxxAdapter(
                gameService,
                new ConsoleGamePrinter(boardViewFormatter),
                new ConsoleInputResolver()
        );
        cliAdapter.run();
    }
}