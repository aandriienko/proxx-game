package com.andriienko.proxx.cli.output;

import com.andriienko.proxx.application.dto.BoardView;

public interface BoardViewFormatter {
    String format(BoardView boardView);
}
