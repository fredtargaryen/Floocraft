package com.fredtargaryen.floocraft;

/**
 * ===DESCRIPTION OF CHANNELS===
 * MessageAddFireplace: sends position and  unique name of fireplace to be added to server
 * MessageApproval: sends to client whether proposed fireplace name is unique
 * MessageApprovalName: sends proposed fireplace name to be approved by server
 * MessageFireplaceList: the lists of fireplace attributes: names; x coordinates; y coordinates; z coordinates; whether they can be teleported to
 * MessageFireplaceListRequest: empty packet. When this is received, server sends back MessageFireplaceList
 * MessageTeleportEntity: teleports player that sent this to (destX, destY, destZ) if possible
 * MessageTileEntityFireplaceFunction: lets the server know whether the sign at those coordinates is decorative or practical
 *
 * When changing version number, change in: DataReference, build.gradle, mcmod.info
 */

public class DataReference
{
    //MAIN MOD DETAILS
    public static final String MODID = "ftfloocraft";
    public static final String MODNAME = "Floocraft";
    public static final String VERSION = "1.7.10-1.2.5";
    //PROXY PATHS
    public static final String CLIENTPROXYPATH = "com.fredtargaryen.floocraft.proxy.ClientProxy";
    public static final String SERVERPROXYPATH = "com.fredtargaryen.floocraft.proxy.ServerProxy";

    public static final int FLOO_FIRE_DETECTION_RANGE = 8;

    public static String resPath(String un)
    {
        return MODID+":"+un.substring(5);
    }
}
