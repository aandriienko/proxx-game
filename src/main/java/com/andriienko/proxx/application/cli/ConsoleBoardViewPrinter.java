package com.andriienko.proxx.application.cli;

import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.application.dto.CellView;
import com.andriienko.proxx.application.printer.BoardViewPrinter;
import com.andriienko.proxx.application.printer.CellPrinter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.StringJoiner;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConsoleBoardViewPrinter implements BoardViewPrinter {

    CellPrinter cellPrinter;

    @Override
    public String print(BoardView boardView) {
        StringBuilder sb = new StringBuilder();

        // Print column numbers above the board
        StringJoiner topColumnNumbers = new StringJoiner(" ");
        for (int column = 0; column < boardView.getColumns(); column++) {
            topColumnNumbers.add(String.format("%-3d", column + 1));
        }
        sb.append("    ").append(topColumnNumbers).append("\n");

        // Print the board with row numbers
        for (int row = 0; row < boardView.getRows(); row++) {
            // Print the row number at the start of each row, left-justified in a field of 4 characters wide
            String formattedRowNumber = String.format("%-4d", row + 1);
            sb.append(formattedRowNumber);

            StringJoiner stringJoiner = new StringJoiner(" ", "", formattedRowNumber + "\n");
            for (int column = 0; column < boardView.getColumns(); column++) {
                // Print each cell
                CellView cell = boardView.getCells()[row][column];
                stringJoiner.add(cellPrinter.print(cell));
            }
            sb.append(stringJoiner);
        }
        // Print column numbers below the board
        sb.append("    ").append(topColumnNumbers).append("\n");
        return sb.toString();
    }

}
