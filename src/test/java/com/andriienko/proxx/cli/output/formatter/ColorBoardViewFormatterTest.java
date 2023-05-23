package com.andriienko.proxx.cli.output.formatter;

import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.application.dto.CellView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorBoardViewFormatterTest {
    ColorBoardViewFormatter colorBoardViewFormatter;
    ColorCellViewFormatter consoleCellFormatter;

    @BeforeEach
    public void setUp() {
        consoleCellFormatter = new ColorCellViewFormatter();
        colorBoardViewFormatter = new ColorBoardViewFormatter(consoleCellFormatter);
    }

    @Test
    @DisplayName("Prints board")
    public void shouldPrintBorderAndCells() {
        // Set up a 2x2 board
        CellView[][] cells = new CellView[2][2];
        cells[0][0] = new CellView(0, false, true, true);
        cells[0][1] = new CellView(1, false, true, false);
        cells[1][0] = new CellView(0, true, true, false);
        cells[1][1] = new CellView(0, false, false, false);

        BoardView boardView = new BoardView(2, 2, cells);
        String expected = "    1   2  \n" +
                "1   " + consoleCellFormatter.createCellView(cells[0][0]) + " " + consoleCellFormatter.createCellView(cells[0][1]) + "1   " + "\n" +
                "2   " + consoleCellFormatter.createCellView(cells[1][0]) + " " + consoleCellFormatter.createCellView(cells[1][1]) + "2   " + "\n" +
                          "    1   2  \n";

        System.out.println(expected);
        assertEquals(expected, colorBoardViewFormatter.format(boardView));
    }
}
