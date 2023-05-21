package com.andriienko.proxx;

import com.andriienko.proxx.adapter.in.ProxxUIAdapter;
import com.andriienko.proxx.adapter.in.cli.ProxxCLIAdapter;
import com.andriienko.proxx.adapter.out.InMemorySingleGameRepository;
import com.andriienko.proxx.application.port.out.GameRepository;
import com.andriienko.proxx.adapter.in.formatter.BoardViewFormatter;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.adapter.in.cli.formatter.ConsoleBoardViewFormatter;
import com.andriienko.proxx.adapter.in.cli.formatter.ConsoleCellFormatter;
import com.andriienko.proxx.application.service.ProxxGameService;

public class Main {
    public static void main(String[] args) {
        BoardViewFormatter boardViewFormatter = new ConsoleBoardViewFormatter(new ConsoleCellFormatter());
        GameRepository gameRepository = new InMemorySingleGameRepository();
        PlayGameUseCase gameService = new ProxxGameService(gameRepository);
        ProxxUIAdapter cliAdapter = new ProxxCLIAdapter(gameService, boardViewFormatter);
        cliAdapter.run();
    }
}