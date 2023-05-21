package com.andriienko.proxx.application.dto;

import com.andriienko.proxx.common.GameStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BoardView {
    int rows;
    int columns;
    GameStatus status;
    CellView[][] cells;
}
