package com.andriienko.proxx.cli.output.formatter;

import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.application.dto.CellView;
import com.andriienko.proxx.cli.output.BoardViewFormatter;
import lombok.AllArgsConstructor;

import java.util.StringJoiner;

@AllArgsConstructor
public class ColorBoardViewFormatter implements BoardViewFormatter {

    private final ColorCellViewFormatter cellFormatter;

    @Override
    public String format(BoardView boardView) {
        StringBuilder sb = new StringBuilder();

        //Print column numbers above the board
        StringJoiner topColumnNumbers = new StringJoiner(" ");
        for (int column = 0; column < boardView.getColumns(); column++) {
            topColumnNumbers.add(String.format("%-3d", column + 1));
        }
        sb.append("    ").append(topColumnNumbers).append("\n");

        // Prints the board with row numbers
        for (int row = 0; row < boardView.getRows(); row++) {

            // Print the row number at the start of each row, left-justified in a field of 4 characters wide
            String formattedRowNumber = String.format("%-4d", row + 1);
            sb.append(formattedRowNumber);

            StringJoiner stringJoiner = new StringJoiner(" ", "", formattedRowNumber + "\n");
            for (int column = 0; column < boardView.getColumns(); column++) {

                // Print each cell
                CellView cell = boardView.getCells()[row][column];
                stringJoiner.add(cellFormatter.createCellView(cell));
            }
            sb.append(stringJoiner);
        }
        // Print column numbers below the board
        sb.append("    ").append(topColumnNumbers).append("\n");
        return sb.toString();
    }

}
