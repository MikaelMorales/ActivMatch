package ch.unil.eda.activmatch.utils;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ActivMatchConstants {
    public static final double DURATION = 3.154 * Math.pow(10, 7);
    public static final double RANGE = 200;
    public static final List<Pair<String, Integer>> RANGES = new ArrayList<>();
    static {
        RANGES.add(new Pair<>("50 m", 50));
        RANGES.add(new Pair<>("100 m", 100));
        RANGES.add(new Pair<>("200 m", 200));
        RANGES.add(new Pair<>("500 m", 500));
        RANGES.add(new Pair<>("1 km", 1000));
        RANGES.add(new Pair<>("2 km", 2000));
        RANGES.add(new Pair<>("5 km", 5000));
        RANGES.add(new Pair<>("10 km", 10000));
        RANGES.add(new Pair<>("20 km", 20000));
    }
}
