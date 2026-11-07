package com.andriienko.proxx.adapter.in.gui.intro;

import com.andriienko.proxx.adapter.in.gui.Fonts;
import com.andriienko.proxx.adapter.in.gui.Navigator;
import com.andriienko.proxx.enums.PlayMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

public final class IntroScreen {

    private static final String CUSTOM_LABEL = "Custom";

    private final Navigator navigator;
    private final ComboBox<String> difficulty = new ComboBox<>();
    private final Spinner<Integer> width = new Spinner<>();
    private final Spinner<Integer> height = new Spinner<>();
    private final Spinner<Integer> blackHoles = new Spinner<>();
    private final Button start = new Button("START");
    private final VBox root = new VBox();

    private boolean syncing;

    public IntroScreen(Navigator navigator) {
        this.navigator = navigator;
        buildFields();
        buildLayout();
        selectDifficulty(PlayMode.EASY);
    }

    public Parent getRoot() {
        return root;
    }

    private void buildFields() {
        difficulty.getItems().addAll("Easy", "Medium", "Hard", CUSTOM_LABEL);
        difficulty.getStyleClass().add("field");
        difficulty.setMaxWidth(Double.MAX_VALUE);
        difficulty.valueProperty().addListener((o, previous, selected) -> onDifficultySelected(selected));

        configureSpinner(width, GameSettings.MIN_SIDE, GameSettings.MAX_SIDE, 8);
        configureSpinner(height, GameSettings.MIN_SIDE, GameSettings.MAX_SIDE, 8);
        configureSpinner(blackHoles, 1, GameSettings.maxBlackHoles(8, 8), 10);

        width.valueProperty().addListener((o, a, b) -> onDimensionChanged());
        height.valueProperty().addListener((o, a, b) -> onDimensionChanged());
        blackHoles.valueProperty().addListener((o, a, b) -> onFieldEdited());

        start.getStyleClass().add("start-button");
        start.setMaxWidth(Double.MAX_VALUE);
        start.setDefaultButton(true);
        start.setOnAction(event -> navigator.showGame(currentSettings()));
    }

    private void configureSpinner(Spinner<Integer> spinner, int min, int max, int initial) {
        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initial);
        factory.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return value == null ? "" : Integer.toString(value);
            }

            @Override
            public Integer fromString(String text) {
                return parseOr(text, factory);
            }
        });
        spinner.setValueFactory(factory);
        spinner.setEditable(true);
        spinner.getStyleClass().add("spinner");
        spinner.setMaxWidth(Double.MAX_VALUE);
        spinner.focusedProperty().addListener((observable, hadFocus, hasFocus) -> {
            if (!hasFocus) {
                commitEditor(spinner);
            }
        });
    }

    private void commitEditor(Spinner<Integer> spinner) {
        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                (SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory();
        factory.setValue(parseOr(spinner.getEditor().getText(), factory));
        spinner.getEditor().setText(Integer.toString(factory.getValue()));
    }

    private static int parseOr(String text, SpinnerValueFactory.IntegerSpinnerValueFactory factory) {
        try {
            int typed = Integer.parseInt(text == null ? "" : text.trim());
            return Math.max(factory.getMin(), Math.min(factory.getMax(), typed));
        } catch (NumberFormatException notANumber) {
            return factory.getValue();
        }
    }

    private void buildLayout() {
        Text title = new Text("PROXX");
        title.getStyleClass().add("title");
        title.setFont(Fonts.bold(64));

        VBox form = new VBox(10,
                labelled("Difficulty", difficulty),
                new HBox(10, grow(labelled("Width", width)), grow(labelled("Height", height))),
                labelled("Black holes", blackHoles),
                start);
        form.setMaxWidth(420);
        form.setFillWidth(true);

        root.getStyleClass().add("screen");
        root.setAlignment(Pos.CENTER);
        root.setSpacing(36);
        root.setPadding(new Insets(24));
        root.getChildren().addAll(title, form);
    }

    private VBox labelled(String text, Node field) {
        Label label = new Label(text);
        label.getStyleClass().add("label-text");
        VBox box = new VBox(4, label, field);
        box.setFillWidth(true);
        return box;
    }

    private VBox grow(VBox box) {
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private void onDifficultySelected(String selected) {
        if (syncing || selected == null || CUSTOM_LABEL.equals(selected)) {
            return;
        }
        selectDifficulty(switch (selected) {
            case "Easy" -> PlayMode.EASY;
            case "Medium" -> PlayMode.MEDIUM;
            default -> PlayMode.EXPERT;
        });
    }

    private void selectDifficulty(PlayMode mode) {
        GameSettings settings = GameSettings.of(mode);
        syncing = true;
        difficulty.setValue(switch (mode) {
            case EASY -> "Easy";
            case MEDIUM -> "Medium";
            default -> "Hard";
        });
        width.getValueFactory().setValue(settings.columns());
        height.getValueFactory().setValue(settings.rows());
        retargetBlackHoleRange(settings.rows(), settings.columns(), settings.blackHoles());
        syncing = false;
        revalidate();
    }

    private void onDimensionChanged() {
        if (syncing) {
            return;
        }
        int rows = height.getValue();
        int columns = width.getValue();
        int max = GameSettings.maxBlackHoles(rows, columns);
        retargetBlackHoleRange(rows, columns, Math.min(blackHoles.getValue(), max));
        onFieldEdited();
    }

    private void retargetBlackHoleRange(int rows, int columns, int value) {
        int max = GameSettings.maxBlackHoles(rows, columns);
        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                (SpinnerValueFactory.IntegerSpinnerValueFactory) blackHoles.getValueFactory();
        factory.setMax(max);
        factory.setValue(Math.max(1, Math.min(value, max)));
    }

    private void onFieldEdited() {
        if (!syncing) {
            syncing = true;
            difficulty.setValue(CUSTOM_LABEL);
            syncing = false;
        }
        revalidate();
    }

    private void revalidate() {
        start.setDisable(!GameSettings.isValid(height.getValue(), width.getValue(), blackHoles.getValue()));
    }

    private GameSettings currentSettings() {
        return new GameSettings(height.getValue(), width.getValue(), blackHoles.getValue());
    }
}
