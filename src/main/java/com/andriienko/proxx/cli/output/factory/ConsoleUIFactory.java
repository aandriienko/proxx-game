package com.andriienko.proxx.cli.output.factory;

import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.cli.output.BoardViewFormatter;
import com.andriienko.proxx.cli.output.UIFactory;
import lombok.AllArgsConstructor;


/**
 *  This class is part of the application's output handling mechanism, focusing on creating a user interface for console-based execution.
 */
@AllArgsConstructor
public class ConsoleUIFactory implements UIFactory {

    private final BoardViewFormatter boardViewFormatter;

    public String createMainMenu() {
        return """
                1. Easy
                2. Medium
                3. Expert
                4. Custom: side is from 3 to 50 and black holes number... Well at least leave 1 cell
                """;
    }

    public String createBanner() {
        return """
                 ________  ________  ________     ___    ___ ___    ___       _______  ________    _______  ________    \s
                |\\   __  \\|\\   __  \\|\\   __  \\   |\\  \\  /  /|\\  \\  /  /|     /  ___  \\|\\   __  \\  /  ___  \\|\\_____  \\   \s
                \\ \\  \\|\\  \\ \\  \\|\\  \\ \\  \\|\\  \\  \\ \\  \\/  / | \\  \\/  / /    /__/|_/  /\\ \\  \\|\\  \\/__/|_/  /\\|____|\\ /_  \s
                 \\ \\   ____\\ \\   _  _\\ \\  \\\\\\  \\  \\ \\    / / \\ \\    / /     |__|//  / /\\ \\  \\\\\\  \\__|//  / /     \\|\\  \\ \s
                  \\ \\  \\___|\\ \\  \\\\  \\\\ \\  \\\\\\  \\  /     \\/   /     \\/          /  /_/__\\ \\  \\\\\\  \\  /  /_/__   __\\_\\  \\\s
                   \\ \\__\\    \\ \\__\\\\ _\\\\ \\_______\\/  /\\   \\  /  /\\   \\         |\\________\\ \\_______\\|\\________\\|\\_______\\
                    \\|__|     \\|__|\\|__|\\|_______/__/ /\\ __\\/__/ /\\ __\\         \\|_______|\\|_______| \\|_______|\\|_______|
                                                 |__|/ \\|__||__|/ \\|__|                                                 \s
                                                                                                                        \s
                """;
    }

    public String createBoard(BoardView boardView) {
        return boardViewFormatter.format(boardView);
    }
}
