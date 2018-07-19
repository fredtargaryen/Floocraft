package com.fredtargaryen.floocraft;

import net.minecraft.util.ResourceLocation;

/**
 * ===DESCRIPTION OF MESSAGE CHANNELS===
 * MessageAddFireplace: sends position and  unique name of fireplace to be added to server
 * MessageApproval: sends to client whether proposed fireplace name is unique
 * MessageApproveName: sends proposed fireplace name to be approved by server
 * MessageDoGreenFlash: Causes the green flash effect to appear on the client when you teleport
 * MessageFireplaceList: the lists of fireplace attributes: names; x coordinates; y coordinates; z coordinates; whether they can be teleported to
 * MessageFireplaceListRequest: empty packet. When this is received, server sends back MessageFireplaceList
 * MessageTeleportEntity: teleports player that sent this to (destX, destY, destZ) if possible
 * MessageTileEntityFireplaceFunction: lets the server know whether the sign at those coordinates is decorative or practical
 *
 * When changing version number, change in: DataReference, build.gradle, mcmod.info
 *
 * Guide to the numbers in fireplace algs:
 *        N
 *      _ _ _
 *     |_|2|_|
 *  W  |4|_|6|  E
 *     |_|8|_|
 *
 *        S
 */

public class DataReference
{
    //MAIN MOD DETAILS
    public static final String MODID = "ftfloocraft";
    public static final String MODNAME = "Floocraft";
    public static final String VERSION = "1.9.3";
    //PROXY PATHS
    public static final String CLIENTPROXYPATH = "com.fredtargaryen.floocraft.proxy.ClientProxy";
    public static final String SERVERPROXYPATH = "com.fredtargaryen.floocraft.proxy.ServerProxy";

    public static final int FLOO_FIRE_DETECTION_RANGE = 8;

    public static final ResourceLocation TP_BACKGROUND = new ResourceLocation(MODID+":textures/blocks/tp_background.png");
    //idk why the format of this ResourceLocation is different
    public static final ResourceLocation FLAMERL = new ResourceLocation(MODID+":particle/torchflame");
}
