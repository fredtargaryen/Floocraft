package com.fredtargaryen.floocraft;

/**
 * ===DESCRIPTION OF CHANNELS===
 * !MessageFireplaceListRequest: sends empty packet. When this is received, server sends back MessageFireplaceList
 * !MessageTeleportEntity: teleports player that sent this to (destX, destY, destZ)
 * !MessageAddFireplace: sends position and name of fireplace to be added to server
 * !MessageRemoveFireplace: sends position of fireplace to be removed to server
 * !MessageFireplaceList: sends lists (place names, xcoords, ycoords and zcoords) to client that sent MessageFireplaceListRequest
 */

public class DataReference
{
    public static final String MODID = "ftfloocraft";
    public static final String MODNAME = "Floocraft";
    public static final String VERSION = "1.7.2-0.1";

    public static final String CLIENTPROXYPATH = "com.fredtargaryen.floocraft.proxy.ClientProxy";
    public static final String SERVERPROXYPATH = "com.fredtargaryen.floocraft.proxy.CommonProxy";
}
