package com.andriienko.proxx.application.dto;


import com.andriienko.proxx.GameStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameView {
    int numberOfOpenedCells;
    int blackHolesNumber;
    int size;
    GameStatus status;
    BoardView boardView;
}
