package com.andriienko.proxx.adapter.in.gui.render;

import com.andriienko.proxx.application.dto.BoardView;
import com.andriienko.proxx.application.dto.CellView;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import java.util.Arrays;

public final class BoardCanvas extends Canvas {

    public interface CellHandler {
        void onCell(int row, int column, boolean secondary);
    }

    private BoardView board;
    private TextureAtlas atlas;

    private double viewportWidth;
    private double viewportHeight;
    private double cellSize;
    private double originX;
    private double originY;

    private long[][] revealedAtMs;
    private long[][] revealOffsetMs;
    private int focusRow = -1;
    private int focusColumn = -1;

    private long startNanos;
    private long flashUntilMs = -1L;
    private AnimationTimer timer;
    private CellHandler handler;

    public BoardCanvas() {
        setOnMousePressed(event -> {
            if (handler == null || board == null) {
                return;
            }
            int row = rowAt(event.getY());
            int column = columnAt(event.getX());
            if (row < 0 || column < 0 || row >= board.getRows() || column >= board.getColumns()) {
                return;
            }
            handler.onCell(row, column, event.getButton() == MouseButton.SECONDARY);
        });
    }

    public void setViewport(double width, double height) {
        if (width == viewportWidth && height == viewportHeight) {
            return;
        }
        viewportWidth = width;
        viewportHeight = height;
        relayout();
    }

    public void setOnCellPressed(CellHandler cellHandler) {
        this.handler = cellHandler;
    }

    public void setFocus(int row, int column) {
        this.focusRow = row;
        this.focusColumn = column;
    }

    public void clearFocus() {
        this.focusRow = -1;
        this.focusColumn = -1;
    }

    public double cellSize() {
        return cellSize;
    }

    public double cellX(int column) {
        return originX + column * cellSize;
    }

    public double cellY(int row) {
        return originY + row * cellSize;
    }

    public int rowAt(double y) {
        return cellSize <= 0.0 ? -1 : (int) Math.floor((y - originY) / cellSize);
    }

    public int columnAt(double x) {
        return cellSize <= 0.0 ? -1 : (int) Math.floor((x - originX) / cellSize);
    }

    public void flash() {
        flashUntilMs = elapsedMs() + CellGeometry.FLASH_IN_MS + CellGeometry.FLASH_OUT_MS;
    }

    public void reset() {
        board = null;
        revealedAtMs = null;
        revealOffsetMs = null;
        flashUntilMs = -1L;
    }

    public void setBoard(BoardView newBoard) {
        boolean sameShape = board != null
                && board.getRows() == newBoard.getRows()
                && board.getColumns() == newBoard.getColumns();

        if (!sameShape) {
            revealedAtMs = new long[newBoard.getRows()][newBoard.getColumns()];
            for (long[] row : revealedAtMs) {
                Arrays.fill(row, -1L);
            }
            revealOffsetMs = new long[newBoard.getRows()][newBoard.getColumns()];
            flashUntilMs = -1L;
        }

        long now = elapsedMs();
        for (int row = 0; row < newBoard.getRows(); row++) {
            for (int column = 0; column < newBoard.getColumns(); column++) {
                if (newBoard.getCells()[row][column].isRevealed() && revealedAtMs[row][column] < 0L) {
                    revealedAtMs[row][column] = now;
                    revealOffsetMs[row][column] = revealOffsetMs(row, column, newBoard);
                }
            }
        }

        this.board = newBoard;
        relayout();
    }

    private void relayout() {
        if (board == null || viewportWidth <= 0.0 || viewportHeight <= 0.0) {
            return;
        }
        double fitted = CellGeometry.fitCellSize(viewportWidth, viewportHeight, board.getRows(), board.getColumns());
        cellSize = Math.max(CellGeometry.MIN_CELL_SIZE, Math.floor(fitted));

        double boardWidth = cellSize * board.getColumns();
        double boardHeight = cellSize * board.getRows();

        setWidth(Math.max(viewportWidth, boardWidth));
        setHeight(Math.max(viewportHeight, boardHeight));
        originX = (getWidth() - boardWidth) / 2.0;
        originY = (getHeight() - boardHeight) / 2.0;

        if (atlas == null) {
            atlas = TextureAtlas.shared(Screen.getPrimary().getOutputScaleX());
        }
    }



    public void start() {
        if (timer != null) {
            return;
        }
        startNanos = System.nanoTime();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                render();
            }
        };
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    private long elapsedMs() {
        return startNanos == 0L ? 0L : (System.nanoTime() - startNanos) / 1_000_000L;
    }

    private void render() {
        GraphicsContext g = getGraphicsContext2D();
        g.clearRect(0, 0, getWidth(), getHeight());
        if (board == null || atlas == null) {
            return;
        }
        long now = elapsedMs();

        for (int row = 0; row < board.getRows(); row++) {
            for (int column = 0; column < board.getColumns(); column++) {
                drawCell(g, board.getCells()[row][column], row, column, now);
            }
        }

        if (focusRow >= 0 && focusColumn >= 0) {
            double inset = CellGeometry.cellInset(cellSize);
            atlas.draw(g, TextureAtlas.FOCUS,
                    originX + focusColumn * cellSize + inset, originY + focusRow * cellSize + inset,
                    CellGeometry.drawableCellSize(cellSize));
        }

        drawFlash(g, now);
    }

    private void drawCell(GraphicsContext g, CellView cell, int row, int column, long now) {
        double inset = CellGeometry.cellInset(cellSize);
        double drawn = CellGeometry.drawableCellSize(cellSize);
        double dx = originX + column * cellSize + inset;
        double dy = originY + row * cellSize + inset;
        long ripple = idleRippleOffsetMs(row, column);

        if (!cell.isRevealed()) {
            g.save();
            g.setGlobalAlpha(CellGeometry.FADED_LINES_ALPHA);
            atlas.draw(g, TextureAtlas.idleFrame(now, ripple), dx, dy, drawn);
            g.restore();
            atlas.draw(g, TextureAtlas.OUTLINE, dx, dy, drawn);
            atlas.draw(g, TextureAtlas.INNER_CIRCLE, dx, dy, drawn);
            if (cell.isFlagged()) {
                atlas.draw(g, TextureAtlas.DOT, dx, dy, drawn);
                tintFlagged(g, dx, dy, drawn);
            }
            return;
        }

        double progress = revealProgress(row, column, now);
        g.save();
        g.setGlobalAlpha(progress);
        if (cell.isBlackHole()) {
            atlas.draw(g, TextureAtlas.MINE, dx, dy, drawn);
        } else if (cell.isEmpty()) {
            g.setGlobalAlpha(progress * CellGeometry.REVEALED_ALPHA);
            atlas.draw(g, TextureAtlas.OUTLINE, dx, dy, drawn);
        } else {
            atlas.draw(g, TextureAtlas.number(cell.getAdjacentBlackHolesCount()), dx, dy, drawn);
        }
        g.restore();
    }

    private void tintFlagged(GraphicsContext g, double dx, double dy, double size) {
        g.save();
        g.beginPath();
        g.rect(dx, dy, size, size);
        g.clip();
        g.setGlobalBlendMode(BlendMode.SRC_ATOP);
        g.setFill(Palette.TURQUOISE);
        g.fillRect(dx, dy, size, size);
        g.restore();
    }

    private long idleRippleOffsetMs(int row, int column) {
        double distance = CellGeometry.distanceFromCentre(row, column, board.getRows(), board.getColumns());
        return (long) (distance * CellGeometry.idleRippleSpreadMs(board.getRows(), board.getColumns()));
    }

    private long revealOffsetMs(int row, int column, BoardView forBoard) {
        if (CellGeometry.REVEAL_RIPPLE_SPEED_MS <= 0) {
            return 0L;
        }
        double distance = CellGeometry.distanceFromCentre(row, column, forBoard.getRows(), forBoard.getColumns());
        return (long) (distance * CellGeometry.REVEAL_RIPPLE_SPEED_MS * Math.max(forBoard.getRows(), forBoard.getColumns()));
    }

    private double revealProgress(int row, int column, long now) {
        long revealedAt = revealedAtMs[row][column];
        if (revealedAt < 0L) {
            return 1.0;
        }
        long since = now - revealedAt - revealOffsetMs[row][column];
        if (since <= 0L) {
            return 0.0;
        }
        return Easings.easeInOutCubic(Easings.clamp((double) since / CellGeometry.FADE_IN_MS));
    }

    private void drawFlash(GraphicsContext g, long now) {
        if (flashUntilMs < 0L || now > flashUntilMs) {
            return;
        }
        long remaining = flashUntilMs - now;
        double alpha = remaining > CellGeometry.FLASH_OUT_MS
                ? 1.0
                : Easings.easeOutQuad(Easings.clamp((double) remaining / CellGeometry.FLASH_OUT_MS));
        g.save();
        g.setGlobalAlpha(alpha * 0.85);
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.restore();
    }
}
