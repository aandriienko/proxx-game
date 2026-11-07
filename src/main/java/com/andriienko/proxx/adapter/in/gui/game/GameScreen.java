package com.andriienko.proxx.adapter.in.gui.game;

import com.andriienko.proxx.adapter.in.gui.Navigator;
import com.andriienko.proxx.adapter.in.gui.intro.GameSettings;
import com.andriienko.proxx.adapter.in.gui.render.BoardCanvas;
import com.andriienko.proxx.adapter.in.gui.render.NebulaCanvas;
import com.andriienko.proxx.adapter.in.gui.render.Palette;
import com.andriienko.proxx.application.dto.GameView;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import com.andriienko.proxx.enums.GameStatus;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public final class GameScreen {

    private final PlayGameUseCase gameService;
    private final Navigator navigator;
    private final NebulaCanvas nebula;
    private final GameSettings settings;

    private final BoardCanvas boardCanvas = new BoardCanvas();
    private final ScrollPane scroller = new ScrollPane(boardCanvas);
    private final StackPane boardHolder = new StackPane();
    private final Label counters = new Label();
    private final ModeSwitch modeSwitch = new ModeSwitch(newMode -> applyMode(newMode, true));
    private final BorderPane root = new BorderPane();
    private final EndOverlay endOverlay = new EndOverlay(this::onTryAgain, this::onMainMenu);

    private InteractionMode mode = InteractionMode.CLEAR;
    private GameView gameView;
    private int focusRow;
    private int focusColumn;
    private boolean focusVisible;
    private TranslateTransition shake;

    public GameScreen(PlayGameUseCase gameService, Navigator navigator, NebulaCanvas nebula, GameSettings settings) {
        this.gameService = gameService;
        this.navigator = navigator;
        this.nebula = nebula;
        this.settings = settings;

        buildLayout();
        boardCanvas.setOnCellPressed(this::onCell);
        boardCanvas.start();
        applyMode(InteractionMode.CLEAR, false);
        startNewGame();
        installKeyboardNavigation();
    }

    public Parent getRoot() {
        return root;
    }

    public void dispose() {
        boardCanvas.stop();
        stopShake();
    }

    private void buildLayout() {
        counters.getStyleClass().add("bar-text");

        HBox topBar = new HBox(counters);
        topBar.getStyleClass().add("bar");
        topBar.setAlignment(Pos.CENTER_RIGHT);
        modeSwitch.getRoot().setFocusTraversable(false);

        Button back = backButton();

        StackPane bottomBar = new StackPane(modeSwitch.getRoot(), back);
        StackPane.setAlignment(modeSwitch.getRoot(), Pos.CENTER);
        StackPane.setAlignment(back, Pos.CENTER_LEFT);
        bottomBar.getStyleClass().add("bar");

        scroller.getStyleClass().add("board-scroll");
        scroller.setFocusTraversable(false);
        scroller.viewportBoundsProperty().addListener((observable, previous, bounds) ->
                boardCanvas.setViewport(bounds.getWidth(), bounds.getHeight()));

        boardHolder.setPadding(new Insets(8));
        boardHolder.getChildren().addAll(scroller, endOverlay.getRoot());

        root.getStyleClass().add("screen");
        root.setTop(topBar);
        root.setCenter(boardHolder);
        root.setBottom(bottomBar);
    }

    private Button backButton() {
        SVGPath arrow = new SVGPath();
        arrow.setContent("M 10 2 L 12 4 L 7 9 L 20 9 L 20 11 L 7 11 L 12 16 L 10 18 L 2 10 Z");
        arrow.getStyleClass().add("icon-glyph");

        Button back = new Button();
        back.setGraphic(arrow);
        back.getStyleClass().add("icon-button");
        back.setFocusTraversable(false);
        back.setAccessibleText("Back to main menu");
        back.setOnAction(event -> {
            dispose();
            navigator.showIntro();
        });
        return back;
    }

    void startNewGame() {
        boardCanvas.reset();
        stopShake();
        focusRow = 0;
        focusColumn = 0;
        focusVisible = false;
        boardCanvas.clearFocus();
        update(gameService.newGame(settings.rows(), settings.columns(), settings.blackHoles()));
        root.requestFocus();
    }

    private void applyMode(InteractionMode newMode, boolean animate) {
        this.mode = newMode;
        modeSwitch.setMode(newMode, animate);
        if (newMode == InteractionMode.CLEAR) {
            nebula.setColors(Palette.NEBULA_DANGER_LIGHT, Palette.NEBULA_DANGER_DARK, animate);
        } else {
            nebula.setColors(Palette.NEBULA_SAFE_LIGHT, Palette.NEBULA_SAFE_DARK, animate);
        }
    }

    private void onCell(int row, int column, boolean secondary) {
        if (gameView == null || gameView.getStatus() != GameStatus.IN_PROGRESS) {
            return;
        }
        if (secondary || mode == InteractionMode.FLAG) {
            update(gameService.toggleFlag(row, column));
        } else {
            update(gameService.revealCell(row, column));
        }
    }

    void update(GameView view) {
        this.gameView = view;
        boardCanvas.setBoard(view.getBoardView());
        counters.setText("Revealed %d / %d   Black holes %d"
                .formatted(view.getRevealedCellsNumber(), view.getSize(), view.getBlackHolesNumber()));
        onStatus(view.getStatus());
    }

    void onStatus(GameStatus status) {
        if (status == GameStatus.IN_PROGRESS) {
            return;
        }
        focusVisible = false;
        boardCanvas.clearFocus();
        if (status == GameStatus.LOSE) {
            boardCanvas.flash();
            shakeBoard();
        }
        endOverlay.show(status);
    }

    private void shakeBoard() {
        stopShake();
        shake = new TranslateTransition(Duration.millis(60), scroller);
        shake.setFromX(0);
        shake.setByX(12);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(event -> scroller.setTranslateX(0));
        shake.play();
    }

    private void stopShake() {
        if (shake != null) {
            shake.stop();
            shake = null;
        }
        scroller.setTranslateX(0);
    }

    private void onTryAgain() {
        endOverlay.hide();
        startNewGame();
    }

    private void onMainMenu() {
        dispose();
        navigator.showIntro();
    }

    private void installKeyboardNavigation() {
        root.setFocusTraversable(true);
        root.setOnKeyPressed(event -> {
            if (gameView == null || gameView.getStatus() != GameStatus.IN_PROGRESS) {
                return;
            }
            int rows = gameView.getBoardView().getRows();
            int columns = gameView.getBoardView().getColumns();
            switch (event.getCode()) {
                case UP, DOWN, LEFT, RIGHT, SPACE, ENTER, F -> {
                    if (!focusVisible) {
                        focusVisible = true;
                        boardCanvas.setFocus(focusRow, focusColumn);
                        event.consume();
                        return;
                    }
                    switch (event.getCode()) {
                        case UP -> moveFocus(-1, 0, rows, columns);
                        case DOWN -> moveFocus(1, 0, rows, columns);
                        case LEFT -> moveFocus(0, -1, rows, columns);
                        case RIGHT -> moveFocus(0, 1, rows, columns);
                        case SPACE, ENTER -> onCell(focusRow, focusColumn, false);
                        default -> onCell(focusRow, focusColumn, true);
                    }
                }
                default -> {
                    return;
                }
            }
            event.consume();
        });
        root.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                root.requestFocus();
            }
        });
    }

    private void moveFocus(int rowDelta, int columnDelta, int rows, int columns) {
        focusRow = Math.max(0, Math.min(rows - 1, focusRow + rowDelta));
        focusColumn = Math.max(0, Math.min(columns - 1, focusColumn + columnDelta));
        boardCanvas.setFocus(focusRow, focusColumn);
        scrollFocusIntoView();
    }

    private void scrollFocusIntoView() {
        double cell = boardCanvas.cellSize();
        if (cell <= 0.0) {
            return;
        }
        Bounds viewport = scroller.getViewportBounds();
        scroller.setHvalue(scrolledTo(boardCanvas.cellX(focusColumn), cell,
                viewport.getWidth(), boardCanvas.getWidth(), scroller.getHvalue()));
        scroller.setVvalue(scrolledTo(boardCanvas.cellY(focusRow), cell,
                viewport.getHeight(), boardCanvas.getHeight(), scroller.getVvalue()));
    }

    private static double scrolledTo(double start, double cell, double viewportSpan,
                                     double contentSpan, double current) {
        double scrollable = contentSpan - viewportSpan;
        if (scrollable <= 0.0) {
            return current;
        }
        double visibleStart = current * scrollable;
        if (start < visibleStart) {
            return start / scrollable;
        }
        double end = start + cell;
        if (end > visibleStart + viewportSpan) {
            return (end - viewportSpan) / scrollable;
        }
        return current;
    }
}
