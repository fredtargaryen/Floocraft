package com.fredtargaryen.floocraft.network;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.messages.MessageAddFireplace;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceListRequest;
import com.fredtargaryen.floocraft.network.messages.MessageRemoveFireplace;
import com.fredtargaryen.floocraft.network.messages.MessageTeleportEntity;

public class PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("ftfloocraft");

	public static void init()
	{
		INSTANCE.registerMessage(MessageFireplaceListRequest.class, MessageFireplaceListRequest.class, 0, Side.SERVER);
		INSTANCE.registerMessage(MessageTeleportEntity.class, MessageTeleportEntity.class, 1, Side.SERVER);
		INSTANCE.registerMessage(MessageAddFireplace.class, MessageAddFireplace.class, 2, Side.SERVER);
		INSTANCE.registerMessage(MessageRemoveFireplace.class, MessageRemoveFireplace.class, 3, Side.SERVER);
		INSTANCE.registerMessage(MessageFireplaceList.class, MessageFireplaceList.class, 4, Side.CLIENT);
	}
}
