package com.fredtargaryen.floocraft;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

/**
 * Version number located in gradle.properties
 */
public class DataReference {
    public static final String MODID = "floocraftft";

    public static ResourceLocation getResourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    // Set in client setup event by ClientModEvents
    public static Material SIGN_MATERIAL = null;

    public static final int FLOO_FIRE_DETECTION_RANGE = 8;

    public static final int POT_MIN_H_RANGE = 0;
    public static final int POT_MAX_H_RANGE = 5;
    public static final int POT_MIN_V_RANGE = 0;
    public static final int POT_MAX_V_RANGE = 5;

    public static final ResourceLocation VALID_ARRIVAL_BLOCKS = getResourceLocation("valid_arrival_blocks");

    public static final ResourceLocation GREENED_RL = getResourceLocation("greened");
    public static final ResourceLocation TP_RL = getResourceLocation("tp");
    public static final ResourceLocation FLICK_RL = getResourceLocation("flick");

    public static final int FLOO_GREEN_COLOUR = 1633652;
    public static final int FLOO_SOUL_COLOUR = 15474135;
    public static final int RED_COLOUR = 16711680;
    public static final int BLACK_COLOUR = 0;
}
