package com.andriienko.proxx.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum PlayMode {
    EASY(8, 8, 10),
    MEDIUM(16, 16, 40),
    EXPERT(24, 24, 99),
    CUSTOM(0, 0, 0);

    int rows;
    int columns;
    int blackHoles;
}

