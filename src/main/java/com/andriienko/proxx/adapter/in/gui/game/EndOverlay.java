package com.andriienko.proxx.adapter.in.gui.game;

import com.andriienko.proxx.adapter.in.gui.render.CellGeometry;
import com.andriienko.proxx.enums.GameStatus;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public final class EndOverlay {

    private final Label title = new Label();
    private final VBox root = new VBox();

    public EndOverlay(Runnable onTryAgain, Runnable onMainMenu) {
        title.getStyleClass().add("overlay-title");

        Button tryAgain = new Button("Try again");
        tryAgain.setOnAction(event -> onTryAgain.run());

        Button mainMenu = new Button("Main menu");
        mainMenu.getStyleClass().add("button-ghost");
        mainMenu.setOnAction(event -> onMainMenu.run());

        HBox buttons = new HBox(14, tryAgain, mainMenu);
        buttons.setAlignment(Pos.CENTER);

        root.getStyleClass().add("overlay");
        root.setAlignment(Pos.CENTER);
        root.setSpacing(22);
        root.getChildren().addAll(title, buttons);
        root.setVisible(false);
        root.setMouseTransparent(true);
    }

    public Parent getRoot() {
        return root;
    }

    public void show(GameStatus status) {
        title.setText(status == GameStatus.WIN ? "You won" : "Game over");
        root.setOpacity(0.0);
        root.setVisible(true);
        root.setMouseTransparent(true);

        FadeTransition fade = new FadeTransition(Duration.millis(CellGeometry.FADE_IN_MS), root);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setDelay(Duration.millis(status == GameStatus.WIN ? 0 : CellGeometry.FLASH_OUT_MS + 400));
        fade.setOnFinished(event -> root.setMouseTransparent(false));
        fade.play();
    }

    public void hide() {
        root.setVisible(false);
        root.setMouseTransparent(true);
    }
}
