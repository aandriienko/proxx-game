package com.andriienko;

import com.andriienko.proxx.adapter.in.ProxxUIAdapter;
import com.andriienko.proxx.adapter.in.cli.ProxxCLIAdapter;
import com.andriienko.proxx.adapter.in.cli.printer.ConsoleGamePrinter;
import com.andriienko.proxx.adapter.in.cli.resolver.ConsoleInputResolver;
import com.andriienko.proxx.adapter.in.printer.GamePrinter;
import com.andriienko.proxx.adapter.in.resolver.InputResolver;
import com.andriienko.proxx.adapter.out.InMemorySingleGameRepository;
import com.andriienko.proxx.application.port.out.GameRepository;
import com.andriienko.proxx.adapter.in.formatter.BoardViewFormatter;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.adapter.in.cli.formatter.ConsoleBoardViewFormatter;
import com.andriienko.proxx.adapter.in.cli.formatter.ConsoleCellFormatter;
import com.andriienko.proxx.application.service.ProxxGameService;

public class ProxxGameApplication {
    public static void main(String[] args) {
        GameRepository gameRepository = new InMemorySingleGameRepository();
        PlayGameUseCase gameService = new ProxxGameService(gameRepository);
        InputResolver inputResolver = new ConsoleInputResolver();
        BoardViewFormatter boardViewFormatter = new ConsoleBoardViewFormatter(new ConsoleCellFormatter());
        GamePrinter gamePrinter = new ConsoleGamePrinter(boardViewFormatter);
        ProxxUIAdapter cliAdapter = new ProxxCLIAdapter(gameService, gamePrinter, inputResolver);
        cliAdapter.run();
    }
}