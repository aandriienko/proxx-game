package com.andriienko.proxx.adapter.in.gui;

import com.andriienko.proxx.adapter.in.gui.game.GameScreen;
import com.andriienko.proxx.adapter.in.gui.intro.GameSettings;
import com.andriienko.proxx.adapter.in.gui.intro.IntroScreen;
import com.andriienko.proxx.adapter.in.gui.render.NebulaCanvas;
import com.andriienko.proxx.adapter.in.gui.render.Palette;
import com.andriienko.proxx.adapter.in.gui.render.TextureAtlas;
import com.andriienko.proxx.application.port.in.PlayGameUseCase;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class ProxxFxApp extends Application {

    private static PlayGameUseCase useCase;

    private final NebulaCanvas nebula = new NebulaCanvas();
    private final StackPane root = new StackPane();

    private GameScreen gameScreen;

    public static void setUseCase(PlayGameUseCase playGameUseCase) {
        useCase = playGameUseCase;
    }

    @Override
    public void start(Stage stage) {
        root.getChildren().add(nebula);
        Scene scene = new Scene(root, 900, 760);
        scene.getStylesheets().add(getClass().getResource("/css/proxx.css").toExternalForm());

        stage.setTitle("PROXX");
        stage.setMinWidth(520);
        stage.setMinHeight(560);
        stage.setScene(scene);

        nebula.start();
        navigator().showIntro();

        stage.show();
        warmTextures();
    }

    private void warmTextures() {
        PauseTransition warm = new PauseTransition(Duration.millis(60));
        warm.setOnFinished(event -> TextureAtlas.shared(Screen.getPrimary().getOutputScaleX()));
        warm.play();
    }

    @Override
    public void stop() {
        disposeGameScreen();
        nebula.stop();
    }

    private Navigator navigator() {
        return new Navigator() {
            @Override
            public void showIntro() {
                disposeGameScreen();
                nebula.setColors(Palette.NEBULA_SAFE_LIGHT, Palette.NEBULA_SAFE_DARK, true);
                setScreen(new IntroScreen(this).getRoot());
            }

            @Override
            public void showGame(GameSettings settings) {
                disposeGameScreen();
                gameScreen = new GameScreen(useCase, this, nebula, settings);
                setScreen(gameScreen.getRoot());
            }
        };
    }

    private void setScreen(Parent screen) {
        if (root.getChildren().size() > 1) {
            root.getChildren().remove(1, root.getChildren().size());
        }
        root.getChildren().add(screen);
    }

    private void disposeGameScreen() {
        if (gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }
    }
}
