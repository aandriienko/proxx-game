package com.andriienko.proxx.adapter.in.gui.render;

import com.andriienko.proxx.adapter.in.gui.Fonts;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.LinkedHashMap;
import java.util.Map;


public final class TextureAtlas {

    private static final int CACHE_SIZE = 4;

    private static final Map<Double, TextureAtlas> CACHE =
            new LinkedHashMap<>(CACHE_SIZE + 1, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Double, TextureAtlas> eldest) {
                    return size() > CACHE_SIZE;
                }
            };

    public static final int OUTLINE = CellGeometry.IDLE_ANIMATION_FRAMES;
    public static final int INNER_CIRCLE = OUTLINE + 1;
    public static final int DOT = INNER_CIRCLE + 1;
    public static final int MINE = DOT + 1;
    public static final int FOCUS = MINE + 1;
    public static final int NUMBER_BASE = FOCUS + 1;
    public static final int SPRITE_COUNT = NUMBER_BASE + 8;

    private final Image image;
    private final double texSize;
    private final int columns;

    private TextureAtlas(Image image, double texSize, int columns) {
        this.image = image;
        this.texSize = texSize;
        this.columns = columns;
    }

    public static int number(int adjacentCount) {
        if (adjacentCount < 1 || adjacentCount > 8) {
            throw new IllegalArgumentException("A cell can only touch 1..8 black holes, got " + adjacentCount);
        }
        return NUMBER_BASE + adjacentCount - 1;
    }

    public static int idleFrame(long elapsedMs, long rippleOffsetMs) {
        long total = elapsedMs + rippleOffsetMs;
        int frames = CellGeometry.IDLE_ANIMATION_FRAMES;
        int frame = (int) (Math.floorMod(total, CellGeometry.IDLE_ANIMATION_LENGTH_MS)
                * (long) frames / CellGeometry.IDLE_ANIMATION_LENGTH_MS);
        return Math.min(frame, frames - 1);
    }

    public Image image() {
        return image;
    }

    public void draw(GraphicsContext g, int spriteIndex, double dx, double dy, double size) {
        double sx = (spriteIndex % columns) * texSize;
        double sy = (spriteIndex / columns) * texSize;
        g.drawImage(image, sx, sy, texSize, texSize, dx, dy, size, size);
    }

    private static void roundRectPath(GraphicsContext g, double x, double y, double w, double h, double r) {
        double radius = Math.min(r, Math.min(w, h) / 2.0);
        g.beginPath();
        g.moveTo(x + radius, y);
        g.lineTo(x + w - radius, y);
        g.arcTo(x + w, y, x + w, y + radius, radius);
        g.lineTo(x + w, y + h - radius);
        g.arcTo(x + w, y + h, x + w - radius, y + h, radius);
        g.lineTo(x + radius, y + h);
        g.arcTo(x, y + h, x, y + h - radius, radius);
        g.lineTo(x, y + radius);
        g.arcTo(x, y, x + radius, y, radius);
        g.closePath();
    }

    private static void strokeRoundRectCentred(GraphicsContext g, double side, double radius) {
        roundRectPath(g, -side / 2.0, -side / 2.0, side, side, radius);
        g.stroke();
    }

    private static void drawIdleFrame(GraphicsContext g, double size, double ts) {
        double magnification = CellGeometry.magnification(Easings.smoothpulse(0.0, 0.5, 0.5, 1.0, ts));

        g.save();
        g.setStroke(Palette.ARTWORK);
        g.setLineWidth(size * CellGeometry.THIN_LINE);

        for (int i = 0; i < CellGeometry.NUM_INNER_RECTS; i++) {
            double subsize = CellGeometry.idleRectSize(i, size, magnification);
            g.save();
            g.rotate(CellGeometry.idleRectAngleDegrees(i, ts));
            strokeRoundRectCentred(g, subsize, subsize * CellGeometry.BORDER_RADIUS);
            g.restore();
        }
        g.restore();
    }

    private static void drawOutline(GraphicsContext g, double size) {
        g.setStroke(Palette.ARTWORK);
        g.setLineWidth(size * CellGeometry.THICK_LINE);
        strokeRoundRectCentred(g, size, size * CellGeometry.BORDER_RADIUS);
    }

    private static void drawInnerCircle(GraphicsContext g, double size) {
        double r = size * CellGeometry.INNER_CIRCLE_RADIUS;
        g.setStroke(Palette.ARTWORK);
        g.setLineWidth(size * CellGeometry.THIN_LINE);
        g.strokeOval(-r, -r, r * 2.0, r * 2.0);
    }

    private static void drawDot(GraphicsContext g, double size) {
        double r = size * CellGeometry.FLAG_CIRCLE_RADIUS;
        g.setFill(Palette.ARTWORK);
        g.setStroke(Palette.ARTWORK);
        g.setLineWidth(size * CellGeometry.THICK_LINE);
        g.fillOval(-r, -r, r * 2.0, r * 2.0);
        g.strokeOval(-r, -r, r * 2.0, r * 2.0);
    }

    private static void drawNumber(GraphicsContext g, double size, int count, Font font) {
        double r = size / 2.0 * CellGeometry.NUMBER_CIRCLE_RADIUS;
        g.setStroke(Palette.ARTWORK);
        g.setLineWidth(size * CellGeometry.THICK_LINE);
        g.strokeOval(-r, -r, r * 2.0, r * 2.0);

        g.setFill(Palette.ARTWORK);
        g.setFont(font);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(Integer.toString(count), 0.0, size * CellGeometry.NUMBER_FONT_TOP_SHIFT_FACTOR);
    }

    private static void drawMine(GraphicsContext g, double size) {
        double r = size / 2.0 * CellGeometry.SAFETY_BUFFER_FACTOR;

        double outer = r * CellGeometry.BLACK_HOLE_OUTER_RADIUS;
        g.setFill(Palette.BLACK_HOLE_OUTER);
        g.fillOval(-outer, -outer, outer * 2.0, outer * 2.0);

        double inner = r * CellGeometry.BLACK_HOLE_INNER_RADIUS;
        g.setFill(Color.BLACK);
        g.fillOval(-inner, -inner, inner * 2.0, inner * 2.0);

        g.setFill(new RadialGradient(0, 0, 0, 0, inner, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Palette.BLACK_HOLE_INNER.deriveColor(0, 1, 1, 0.5)),
                new Stop(1.0, Palette.BLACK_HOLE_INNER.deriveColor(0, 1, 1, 0.1))));
        g.fillOval(-inner, -inner, inner * 2.0, inner * 2.0);

        double hole = r * CellGeometry.BLACK_HOLE_RADIUS;
        g.setFill(Color.BLACK);
        g.fillOval(-hole, -hole, hole * 2.0, hole * 2.0);
    }

    private static void drawFocus(GraphicsContext g, double size) {
        g.save();
        g.setGlobalAlpha(0.3);
        g.setFill(Palette.FOCUS_RING);
        roundRectPath(g, -size / 2.0, -size / 2.0, size, size, size * CellGeometry.BORDER_RADIUS);
        g.fill();
        g.restore();
    }

    public static TextureAtlas shared(double outputScale) {
        return build(CellGeometry.drawableCellSize(CellGeometry.MAX_CELL_SIZE), outputScale);
    }

    private static TextureAtlas build(double cellSizePx, double outputScale) {
        double texSize = Math.ceil(cellSizePx * outputScale);
        TextureAtlas cached = CACHE.get(texSize);
        if (cached != null) {
            return cached;
        }
        TextureAtlas atlas = render(texSize);
        CACHE.put(texSize, atlas);
        return atlas;
    }

    private static TextureAtlas render(double texSize) {
        int columns = (int) Math.ceil(Math.sqrt(SPRITE_COUNT));
        int rows = (int) Math.ceil((double) SPRITE_COUNT / columns);

        Canvas canvas = new Canvas(columns * texSize, rows * texSize);
        GraphicsContext g = canvas.getGraphicsContext2D();
        double blur = texSize * CellGeometry.GLOW_FACTOR;

        GaussianBlur glowBlur = new GaussianBlur(blur);
        Font numberFont = Fonts.bold(texSize * CellGeometry.NUMBER_FONT_SIZE_FACTOR);

        for (int index = 0; index < SPRITE_COUNT; index++) {
            double tileX = (index % columns) * texSize;
            double tileY = (index / columns) * texSize;
            double cx = tileX + texSize / 2.0;
            double cy = tileY + texSize / 2.0;

            boolean glows = index != MINE && index != FOCUS;
            int passes = glows ? 2 : 1;

            for (int pass = 0; pass < passes; pass++) {
                boolean glowPass = glows && pass == 0;
                g.save();
                g.beginPath();
                g.rect(tileX, tileY, texSize, texSize);
                g.clip();
                g.translate(cx, cy);
                if (glowPass) {
                    g.setEffect(glowBlur);
                    g.setGlobalAlpha(CellGeometry.GLOW_ALPHA);
                }
                paintSprite(g, index, texSize, numberFont);
                g.restore();
                g.setEffect(null);
                g.setGlobalAlpha(1.0);
            }
        }

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image image = canvas.snapshot(params, null);
        return new TextureAtlas(image, texSize, columns);
    }

    private static void paintSprite(GraphicsContext g, int index, double texSize, Font numberFont) {
        if (index < CellGeometry.IDLE_ANIMATION_FRAMES) {
            drawIdleFrame(g, texSize, (double) index / CellGeometry.IDLE_ANIMATION_FRAMES);
        } else if (index == OUTLINE) {
            drawOutline(g, texSize);
        } else if (index == INNER_CIRCLE) {
            drawInnerCircle(g, texSize);
        } else if (index == DOT) {
            drawDot(g, texSize);
        } else if (index == MINE) {
            drawMine(g, texSize);
        } else if (index == FOCUS) {
            drawFocus(g, texSize);
        } else {
            drawNumber(g, texSize, index - NUMBER_BASE + 1, numberFont);
        }
    }
}
