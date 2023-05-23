package com.andriienko.proxx.application.dto;


import com.andriienko.proxx.enums.GameStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameView {
    int revealedCellsNumber;
    int blackHolesNumber;
    int size;
    GameStatus status;
    BoardView boardView;
}
