package com.fredtargaryen.floocraft;

public class HelperFunctions {
    public static String convertArrayToLocationName(String[] array) {
        return String.format("%s %s %s %s",
                        array[0],
                        array[1],
                        array[2],
                        array[3]);
    }
}
