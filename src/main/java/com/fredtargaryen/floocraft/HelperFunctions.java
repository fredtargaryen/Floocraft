package com.fredtargaryen.floocraft;

import java.util.List;

public class HelperFunctions {
    public static String convertArrayToLocationName(List<String> array) {
        String joined = String.join(" ", array);
        return joined.substring(0, Math.min(joined.length(), 160)).trim();
    }

    public static float getElapsedPartialTicks(float oldPt, float newPt) {
        if (newPt > oldPt) return newPt - oldPt;
        if (newPt == oldPt) return 0f;
        return (1f - oldPt) + newPt;
    }
}
