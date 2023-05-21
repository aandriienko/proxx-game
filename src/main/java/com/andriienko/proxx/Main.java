package com.andriienko.proxx;

import com.andriienko.proxx.adapter.in.cli.ProxxCLIAdapter;
import com.andriienko.proxx.adapter.out.InMemorySingleBoardRepository;
import com.andriienko.proxx.application.port.out.BoardRepository;
import com.andriienko.proxx.application.printer.BoardViewPrinter;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.application.cli.ConsoleBoardViewPrinter;
import com.andriienko.proxx.application.cli.ConsoleCellPrinter;
import com.andriienko.proxx.application.service.ProxxGameService;
import com.andriienko.proxx.domain.BoardFactory;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BoardViewPrinter boardViewPrinter = new ConsoleBoardViewPrinter(new ConsoleCellPrinter());
        BoardRepository boardRepository = new InMemorySingleBoardRepository(new BoardFactory());
        PlayGameUseCase gameService = new ProxxGameService(boardRepository);
        ProxxCLIAdapter cliAdapter = new ProxxCLIAdapter(gameService, boardViewPrinter, new Scanner(System.in));
        cliAdapter.run();
    }
}