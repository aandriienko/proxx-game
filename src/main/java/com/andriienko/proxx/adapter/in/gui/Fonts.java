package com.andriienko.proxx.adapter.in.gui;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Fonts {

    private static final List<String> PREFERRED = List.of("Menlo", "Consolas", "DejaVu Sans Mono");
    private static final String FALLBACK = "Monospaced";

    private static String resolved;

    public static synchronized String family() {
        if (resolved == null) {
            resolved = pickFamily(Font.getFamilies());
        }
        return resolved;
    }

    static String pickFamily(Collection<String> installedFamilies) {
        Set<String> installed = new HashSet<>(installedFamilies);
        return PREFERRED.stream()
                .filter(installed::contains)
                .findFirst()
                .orElse(FALLBACK);
    }

    public static Font regular(double size) {
        return Font.font(family(), size);
    }

    public static Font bold(double size) {
        return Font.font(family(), FontWeight.BOLD, size);
    }
}
