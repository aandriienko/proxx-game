package com.andriienko.proxx.adapter.in.gui.render;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Easings {

    public static double deg2rad(double degrees) {
        return degrees / 180.0 * Math.PI;
    }

    public static double easeInQuad(double t) {
        return t * t;
    }

    public static double easeOutQuad(double t) {
        return -1.0 * t * (t - 2.0);
    }

    public static double easeInOutQuad(double t) {
        double v = t * 2.0;
        if (v < 1.0) {
            return 0.5 * v * v;
        }
        v -= 1.0;
        return -0.5 * (v * (v - 2.0) - 1.0);
    }

    public static double easeInCubic(double t) {
        return t * t * t;
    }

    public static double easeOutCubic(double t) {
        double v = t - 1.0;
        return v * v * v + 1.0;
    }

    public static double easeInOutCubic(double t) {
        double v = t * 2.0;
        if (v < 1.0) {
            return 0.5 * v * v * v;
        }
        v -= 2.0;
        return 0.5 * (v * v * v + 2.0);
    }

    public static double easeInExpo(double t) {
        return t == 0.0 ? 0.0 : Math.pow(2.0, 10.0 * (t - 1.0));
    }

    public static double easeOutExpo(double t) {
        return t == 1.0 ? 1.0 : -Math.pow(2.0, -10.0 * t) + 1.0;
    }

    public static double easeInOutExpo(double t) {
        if (t == 0.0) {
            return 0.0;
        }
        if (t == 1.0) {
            return 1.0;
        }
        double v = t * 2.0;
        if (v < 1.0) {
            return 0.5 * Math.pow(2.0, 10.0 * (v - 1.0));
        }
        v -= 1.0;
        return 0.5 * (-Math.pow(2.0, -10.0 * v) + 2.0);
    }

    public static double clamp(double v) {
        return Math.min(1.0, Math.max(0.0, v));
    }

    public static double remap(double minIn, double maxIn, double minOut, double maxOut, double v) {
        return (v - minIn) / (maxIn - minIn) * (maxOut - minOut) + minOut;
    }

    public static double clampedRemap(double minIn, double maxIn, double minOut, double maxOut, double v) {
        return clamp(remap(minIn, maxIn, minOut, maxOut, v));
    }

    /**
     * A smooth 0 -> 1 -> 0 pulse. Rises over [startIn, endIn] and falls over [startOut, endOut].
     * <p>
     * The original eases the product of the two ramps rather than subtracting two eased
     * ramps; the forms agree, but this one keeps the port literal.
     */
    public static double smoothpulse(double startIn, double endIn, double startOut, double endOut, double t) {
        return easeInOutCubic(clampedRemap(startIn, endIn, 0.0, 1.0, t)
                * (1.0 - clampedRemap(startOut, endOut, 0.0, 1.0, t)));
    }
}
