package com.andriienko.proxx.cli.output.formatter;

import com.andriienko.proxx.application.dto.CellView;
import com.andriienko.proxx.cli.output.CellViewFormatter;

/**
 * This class formats individual cells for display on the console, with different colors representing different cell states (revealed, contains a black hole, etc).
 * The main responsibility of this class is to convert CellView objects into a human-readable, colored string representation that is appropriate for console output.
 */
public class ColorCellViewFormatter implements CellViewFormatter {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[91m";
    private static final String ANSI_YELLOW = "\u001B[93m";
    private static final String ANSI_BLUE = "\u001B[94m";

    @Override
    public String createCellView(CellView cell) {
        if(cell == null) {
            return "";
        }
        String cellString;
        if (cell.isRevealed()) {
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
         /* %-12s - offset to match header and footer
              ANSI COLORS counts as characters, even invisible */
        return String.format("%-12s", cellString);
    }
}
