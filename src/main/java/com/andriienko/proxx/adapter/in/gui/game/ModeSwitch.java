package com.andriienko.proxx.adapter.in.gui.game;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.function.Consumer;

public final class ModeSwitch {

    private static final double TRACK_WIDTH = 46.0;
    private static final double TRACK_HEIGHT = 24.0;
    private static final double KNOB_SIZE = 18.0;
    private static final double KNOB_INSET = 3.0;
    private static final double TRAVEL = TRACK_WIDTH - 2 * KNOB_INSET - KNOB_SIZE;
    private static final Duration SLIDE = Duration.millis(140);

    private final Label clearLabel = new Label("Clear");
    private final Label flagLabel = new Label("Flag");
    private final Region knob = new Region();
    private final StackPane track = new StackPane(knob);
    private final HBox root = new HBox(clearLabel, track, flagLabel);

    private final Consumer<InteractionMode> onModePicked;
    private InteractionMode mode = InteractionMode.CLEAR;
    private TranslateTransition slide;

    public ModeSwitch(Consumer<InteractionMode> onModePicked) {
        this.onModePicked = onModePicked;

        clearLabel.getStyleClass().add("mode-switch-label");
        flagLabel.getStyleClass().add("mode-switch-label");

        knob.getStyleClass().add("mode-switch-knob");
        knob.setPrefSize(KNOB_SIZE, KNOB_SIZE);
        knob.setMinSize(KNOB_SIZE, KNOB_SIZE);
        knob.setMaxSize(KNOB_SIZE, KNOB_SIZE);

        track.getStyleClass().add("mode-switch-track");
        track.setPrefSize(TRACK_WIDTH, TRACK_HEIGHT);
        track.setMinSize(TRACK_WIDTH, TRACK_HEIGHT);
        track.setMaxSize(TRACK_WIDTH, TRACK_HEIGHT);
        track.setPadding(new Insets(KNOB_INSET));
        track.setAlignment(Pos.CENTER_LEFT);

        root.setAlignment(Pos.CENTER);
        root.setSpacing(12);

        track.setOnMouseClicked(event -> pick(other(mode)));
        clearLabel.setOnMouseClicked(event -> pick(InteractionMode.CLEAR));
        flagLabel.setOnMouseClicked(event -> pick(InteractionMode.FLAG));

        apply(false);
    }

    public HBox getRoot() {
        return root;
    }

    public void setMode(InteractionMode newMode, boolean animate) {
        if (newMode == mode) {
            return;
        }
        mode = newMode;
        apply(animate);
    }

    private void pick(InteractionMode picked) {
        if (picked == mode) {
            return;
        }
        mode = picked;
        apply(true);
        onModePicked.accept(picked);
    }

    private static InteractionMode other(InteractionMode current) {
        return current == InteractionMode.CLEAR ? InteractionMode.FLAG : InteractionMode.CLEAR;
    }

    private void apply(boolean animate) {
        boolean flagging = mode == InteractionMode.FLAG;
        setActive(clearLabel, !flagging);
        setActive(flagLabel, flagging);

        double target = flagging ? TRAVEL : 0.0;
        if (slide != null) {
            slide.stop();
        }
        if (!animate) {
            knob.setTranslateX(target);
            return;
        }
        slide = new TranslateTransition(SLIDE, knob);
        slide.setToX(target);
        slide.setInterpolator(Interpolator.EASE_BOTH);
        slide.play();
    }

    private static void setActive(Label label, boolean active) {
        label.getStyleClass().remove("active");
        if (active) {
            label.getStyleClass().add("active");
        }
    }
}
