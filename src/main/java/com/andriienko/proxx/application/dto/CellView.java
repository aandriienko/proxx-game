package com.andriienko.proxx.application.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CellView {
    int adjacentBlackHolesCount;
    boolean isBlackHole;
    boolean isOpened;
    boolean isEmpty;
}
