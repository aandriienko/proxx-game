package com.andriienko.proxx.application.cli;

import com.andriienko.proxx.application.dto.CellView;
import com.andriienko.proxx.application.printer.CellPrinter;
import com.andriienko.proxx.common.PlayMode;

public class ConsoleCellPrinter implements CellPrinter {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[91m";
    private static final String ANSI_YELLOW = "\u001B[93m";
    private static final String ANSI_BLUE = "\u001B[94m";


    @Override
    public String print(CellView cell) {
        if(cell == null) {
            return "";
        }

        String cellString;
        if (cell.isOpened()) {
            if (cell.isBlackHole()) {
                cellString =  ANSI_RED + "H" + ANSI_RESET;
            } else {
                if(cell.isEmpty()) {
                    cellString = ANSI_BLACK + cell.getAdjacentBlackHolesCount() + ANSI_RESET;
                } else {
                    cellString = ANSI_BLUE + cell.getAdjacentBlackHolesCount() + ANSI_RESET;
                }
            }
        } else {
            cellString =  ANSI_YELLOW + "*" + ANSI_RESET;
        }
         // %-12s - ANSI COLORS counts as characters, even invisible
        return String.format("%-12s", cellString);
    }
}
