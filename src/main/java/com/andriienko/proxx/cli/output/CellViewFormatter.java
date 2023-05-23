package com.andriienko.proxx.cli.output;

import com.andriienko.proxx.application.dto.CellView;

public interface CellViewFormatter {
    String createCellView(CellView cell);
}
