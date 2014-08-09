package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.network.messages.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(DataReference.MODID);

	public static void init()
	{
		INSTANCE.registerMessage(MessageFireplaceListRequest.class, MessageFireplaceListRequest.class, 0, Side.SERVER);
		INSTANCE.registerMessage(MessageTeleportEntity.class, MessageTeleportEntity.class, 1, Side.SERVER);
		INSTANCE.registerMessage(MessageAddFireplace.class, MessageAddFireplace.class, 2, Side.SERVER);
		INSTANCE.registerMessage(MessageFireplaceList.class, MessageFireplaceList.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(MessageApproveName.class, MessageApproveName.class, 4, Side.SERVER);
        INSTANCE.registerMessage(MessageApproval.class, MessageApproval.class, 5, Side.CLIENT);
        INSTANCE.registerMessage(MessageTileEntityFireplaceFunction.class, MessageTileEntityFireplaceFunction.class, 6, Side.SERVER);
	}
}
