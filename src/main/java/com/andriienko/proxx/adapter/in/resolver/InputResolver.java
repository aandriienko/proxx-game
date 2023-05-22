package com.andriienko.proxx.adapter.in.resolver;

import com.andriienko.proxx.enums.PlayMode;

import java.util.function.Predicate;

public interface InputResolver {
    PlayMode getPlayMode();
    String getStringOrQuit();
    int getIntegerInput(Predicate<Integer> validatingPredicate, String promptMessage);
}
