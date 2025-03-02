package com.fredtargaryen.floocraft;

import java.util.List;

public class HelperFunctions {
    public static String convertArrayToLocationName(String[] array) {
        return String.format("%s %s %s %s",
                        array[0],
                        array[1],
                        array[2],
                        array[3]);
    }

    public static String convertArrayToLocationName(List<String> array) {
        return String.format("%s %s %s %s",
                array.get(0),
                array.get(1),
                array.get(2),
                array.get(3));
    }
}
