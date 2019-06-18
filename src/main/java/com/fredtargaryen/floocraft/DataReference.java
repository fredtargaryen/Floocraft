package com.fredtargaryen.floocraft;

import net.minecraft.util.ResourceLocation;

/**
 * ===DESCRIPTION OF MESSAGE CHANNELS===
 * MessageAddFireplace: sends position and unique name of fireplace to be added to server
 * MessageApproval: sends to client whether proposed fireplace name is unique
 * MessageApproveName: sends proposed fireplace name to be approved by server
 * MessageDenyPeek: used to stop a peek from happening when Forge's forced chunk ticket limit has been reached
 * MessageDoGreenFlash: causes the green flash effect to appear on the client when you teleport
 * MessageEndPeek: sent when the "Mischief managed" button is pressed to stop the peek
 * MessageFireplaceList: the lists of fireplace attributes: names; x coordinates; y coordinates; z coordinates; whether they can be teleported to
 * MessageFireplaceListRequest: empty packet. When this is received, server sends back MessageFireplaceList
 * MessageFlooTorchTeleport: sent when a client player touches a Floo Torch and causes a random teleport. This can't be done via the server only; this just results in "Player moved too quickly!"
 * MessagePlayerID: sends the unique IDs of a Peeker and a player, so that the Peeker can have the texture of the player's head
 * MessagePlayerIDRequest: sent by the client to retrieve the ID of a Peeker's corresponding player
 * MessageStartPeek: sent by the client when initiating a peek
 * MessageTeleportEntity: teleports player that sent this to (destX, destY, destZ) if possible
 * MessageTileEntityFireplaceFunction: lets the server know whether the sign at those coordinates is decorative or practical
 *
 * When changing version number, change in: DataReference, build.gradle, mcmod.info
 */

public class DataReference {
    //MAIN MOD DETAILS
    public static final String MODID = "floocraftft";
    public static final String MODNAME = "Floocraft";
    public static final String VERSION = "1.9.6";
    //PROXY PATHS
    public static final String CLIENTPROXYPATH = "com.fredtargaryen.floocraft.proxy.ClientProxy";
    public static final String SERVERPROXYPATH = "com.fredtargaryen.floocraft.proxy.ServerProxy";

    public static final int FLOO_FIRE_DETECTION_RANGE = 8;

    public static final ResourceLocation TP_BACKGROUND = new ResourceLocation(MODID, "textures/blocks/tp_background.png");
    //idk why the format of this ResourceLocation is different
    public static final ResourceLocation FLAMERL = new ResourceLocation(MODID, "particle/torchflame");
}
