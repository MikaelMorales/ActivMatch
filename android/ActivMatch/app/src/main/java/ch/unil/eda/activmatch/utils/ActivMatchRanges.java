package ch.unil.eda.activmatch.utils;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ActivMatchRanges {
    public static final List<Pair<String, Integer>> RANGES = new ArrayList<>();
    static {
        RANGES.add(new Pair<>("500 m", 500));
        RANGES.add(new Pair<>("1 km", 1000));
        RANGES.add(new Pair<>("2 km", 2000));
        RANGES.add(new Pair<>("5 km", 5000));
        RANGES.add(new Pair<>("10 km", 10000));
        RANGES.add(new Pair<>("20 km", 20000));
        RANGES.add(new Pair<>("50 km", 50000));
        RANGES.add(new Pair<>("100 km", 100000));
    }
}
