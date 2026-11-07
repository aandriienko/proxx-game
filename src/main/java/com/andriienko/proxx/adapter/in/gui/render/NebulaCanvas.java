package com.andriienko.proxx.adapter.in.gui.render;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

import java.util.Random;

public final class NebulaCanvas extends Canvas {

    private static final int NOISE_SIZE = 96;
    private static final double NOISE_ALPHA = 0.35;
    private static final double DRIFT_PIXELS_PER_SECOND = 6.0;
    private static final Duration TWEEN = Duration.millis(300);

    private static final double[][] CIRCLES = {
            {0.55, -0.45, 0.34, 0.2},
            {-0.52, 0.18, 0.46, -0.2},
            {0.12, 0.62, 0.24, -1.0},
    };

    private static final double CIRCLE_PERIOD_SECONDS = 24.0;
    private static final double CIRCLE_SWING_RADIANS = Math.PI / 4.0;

    private final ObjectProperty<Color> light = new SimpleObjectProperty<>(Palette.NEBULA_SAFE_LIGHT);
    private final ObjectProperty<Color> dark = new SimpleObjectProperty<>(Palette.NEBULA_SAFE_DARK);
    private final WritableImage noise = generateNoise(new Random(20190508L));

    private AnimationTimer timer;
    private Timeline tween;
    private long startNanos;

    public NebulaCanvas() {
        widthProperty().addListener((o, a, b) -> render(0.0));
        heightProperty().addListener((o, a, b) -> render(0.0));
        light.addListener((o, a, b) -> render(elapsedSeconds()));
        dark.addListener((o, a, b) -> render(elapsedSeconds()));
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    @Override
    public void resize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    @Override
    public double minWidth(double height) {
        return 0;
    }

    @Override
    public double minHeight(double width) {
        return 0;
    }

    @Override
    public double maxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    public double maxHeight(double width) {
        return Double.MAX_VALUE;
    }

    public void setColors(Color newLight, Color newDark, boolean animate) {
        if (tween != null) {
            tween.stop();
        }
        if (!animate) {
            light.set(newLight);
            dark.set(newDark);
            return;
        }
        tween = new Timeline(new KeyFrame(TWEEN,
                new KeyValue(light, newLight, Interpolator.EASE_BOTH),
                new KeyValue(dark, newDark, Interpolator.EASE_BOTH)));
        tween.play();
    }

    public void start() {
        if (timer != null) {
            return;
        }
        startNanos = System.nanoTime();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                render((now - startNanos) / 1_000_000_000.0);
            }
        };
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        if (tween != null) {
            tween.stop();
            tween = null;
        }
    }

    private double elapsedSeconds() {
        return startNanos == 0L ? 0.0 : (System.nanoTime() - startNanos) / 1_000_000_000.0;
    }

    private void render(double seconds) {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0.0 || h <= 0.0) {
            return;
        }
        GraphicsContext g = getGraphicsContext2D();
        g.setGlobalAlpha(1.0);
        g.setGlobalBlendMode(BlendMode.SRC_OVER);
        g.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, light.get()), new Stop(1.0, dark.get())));
        g.fillRect(0, 0, w, h);

        double drift = seconds * DRIFT_PIXELS_PER_SECOND;
        double spread = Math.max(w, h) * 1.6;
        double offsetX = -((drift * 0.6) % spread);
        double offsetY = -((drift * 0.25) % spread);

        g.setGlobalAlpha(NOISE_ALPHA);
        g.setGlobalBlendMode(BlendMode.SCREEN);
        g.drawImage(noise, offsetX, offsetY, spread, spread);
        g.drawImage(noise, offsetX + spread, offsetY, spread, spread);
        g.drawImage(noise, offsetX, offsetY + spread, spread, spread);
        g.drawImage(noise, offsetX + spread, offsetY + spread, spread, spread);

        drawCircles(g, w, h, seconds);

        g.setGlobalAlpha(1.0);
        g.setGlobalBlendMode(BlendMode.SRC_OVER);
    }

    private void drawCircles(GraphicsContext g, double w, double h, double seconds) {
        double unit = Math.min(w, h);
        double centreX = w / 2.0;
        double centreY = h / 2.0;
        double t = seconds / CIRCLE_PERIOD_SECONDS * 2.0 * Math.PI;
        Color tint = light.get();

        g.setGlobalBlendMode(BlendMode.SCREEN);
        g.setGlobalAlpha(1.0);
        for (double[] circle : CIRCLES) {
            double angle = Math.sin(t + circle[3]) * CIRCLE_SWING_RADIANS;
            double x = circle[0] * w / 2.0;
            double y = circle[1] * h / 2.0;
            double cx = centreX + x * Math.cos(angle) - y * Math.sin(angle);
            double cy = centreY + x * Math.sin(angle) + y * Math.cos(angle);
            double radius = circle[2] * unit;

            g.setFill(new RadialGradient(0, 0, cx, cy, radius, false, CycleMethod.NO_CYCLE,
                    new Stop(0.0, tint.deriveColor(0, 0.85, 1.6, 0.42)),
                    new Stop(0.55, tint.deriveColor(0, 0.9, 1.25, 0.16)),
                    new Stop(1.0, Color.TRANSPARENT)));
            g.fillOval(cx - radius, cy - radius, radius * 2.0, radius * 2.0);
        }
    }

    private static WritableImage generateNoise(Random random) {
        double[][] field = new double[NOISE_SIZE][NOISE_SIZE];
        double amplitude = 1.0;
        double total = 0.0;

        for (int octave = 0; octave < 4; octave++) {
            int period = NOISE_SIZE >> octave;
            if (period < 2) {
                break;
            }
            double[][] lattice = new double[NOISE_SIZE / period + 2][NOISE_SIZE / period + 2];
            for (int y = 0; y < lattice.length; y++) {
                for (int x = 0; x < lattice[y].length; x++) {
                    lattice[y][x] = random.nextDouble();
                }
            }
            for (int y = 0; y < NOISE_SIZE; y++) {
                for (int x = 0; x < NOISE_SIZE; x++) {
                    int gx = x / period;
                    int gy = y / period;
                    double fx = smoothstep((x % period) / (double) period);
                    double fy = smoothstep((y % period) / (double) period);
                    double top = lerp(lattice[gy][gx], lattice[gy][gx + 1], fx);
                    double bottom = lerp(lattice[gy + 1][gx], lattice[gy + 1][gx + 1], fx);
                    field[y][x] += lerp(top, bottom, fy) * amplitude;
                }
            }
            total += amplitude;
            amplitude *= 0.5;
        }

        WritableImage image = new WritableImage(NOISE_SIZE, NOISE_SIZE);
        PixelWriter writer = image.getPixelWriter();
        for (int y = 0; y < NOISE_SIZE; y++) {
            for (int x = 0; x < NOISE_SIZE; x++) {
                double v = field[y][x] / total;
                double alpha = Math.pow(Easings.clamp(v), 2.2);
                writer.setColor(x, y, Color.color(1.0, 1.0, 1.0, alpha));
            }
        }
        return image;
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double smoothstep(double t) {
        return t * t * (3.0 - 2.0 * t);
    }
}
