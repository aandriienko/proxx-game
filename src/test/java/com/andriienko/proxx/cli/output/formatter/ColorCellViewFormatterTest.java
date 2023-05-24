package com.andriienko.proxx.cli.output.formatter;

import com.andriienko.proxx.application.dto.CellView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorCellViewFormatterTest {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[91m";
    private static final String ANSI_YELLOW = "\u001B[93m";
    private static final String ANSI_CYAN = "\u001B[96m";

    ColorCellViewFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new ColorCellViewFormatter();
    }

    static Stream<Arguments> cellViewData() {
        return Stream.of(
                Arguments.of("", "", new CellView(0, true, true, false)),
                Arguments.of(ANSI_RED, "H", new CellView(0, true, true, false)),
                Arguments.of(ANSI_BLACK, "0", new CellView(0, false, true, true)),
                Arguments.of(ANSI_CYAN, "3", new CellView(3, false, true, false)),
                Arguments.of(ANSI_YELLOW, "*", new CellView(0, false, false, false))
        );
    }

    @ParameterizedTest
    @MethodSource("cellViewData")
    void shouldFormatCell(String color, String value, CellView cellView) {
        if("".equals(color)) {
            assertEquals("", formatter.createCellView(null));
        } else {
            assertEquals(String.format("%-12s", color + value + ANSI_RESET) , formatter.createCellView(cellView));
        }
    }
}
