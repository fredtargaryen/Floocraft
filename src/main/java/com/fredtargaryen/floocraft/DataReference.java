package com.fredtargaryen.floocraft;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

/**
 * ===DESCRIPTION OF MESSAGE CHANNELS===
 * MessageEndPeek: sent when the "Mischief managed" button is pressed to stop the peek
 * MessagePeekRequest: sent to the server when the player presses the "Peek..." button
 * MessagePlayerID: sends the unique IDs of a Peeker and a player, so that the Peeker can have the texture of the player's head
 * MessagePlayerIDRequest: sent by the client to retrieve the ID of a Peeker's corresponding player
 * MessageStartPeek: sent by the client when initiating a peek
 * When changing version number, change in: build.gradle, mods.toml
 */
public class DataReference {
    public static final String MODID = "floocraftft";

    public static ResourceLocation getResourceLocation(String path) {
        return new ResourceLocation(MODID, path);
    }

    // Set in client setup event by ClientModEvents
    public static Material SIGN_MATERIAL = null;

    public static final int FLOO_FIRE_DETECTION_RANGE = 8;

    public static final int POT_MIN_H_RANGE = 2;
    public static final int POT_MAX_H_RANGE = 5;
    public static final int POT_MIN_V_RANGE = 2;
    public static final int POT_MAX_V_RANGE = 5;

    public static final ResourceLocation VALID_ARRIVAL_BLOCKS = getResourceLocation("valid_arrival_blocks");

    public static final ResourceLocation FLOO_TORCH_PARTICLE_RL = getResourceLocation("floo_torch_flame");

    public static final ResourceLocation GREENED_RL = getResourceLocation("greened");
    public static final ResourceLocation TP_RL = getResourceLocation("tp");
    public static final ResourceLocation FLICK_RL = getResourceLocation("flick");

    public static final int FLOO_GREEN_COLOUR = 1633652;
    public static final int FLOO_SOUL_COLOUR = 15474135;
    public static final int RED_COLOUR = 16711680;
    public static final int BLACK_COLOUR = 0;
}
