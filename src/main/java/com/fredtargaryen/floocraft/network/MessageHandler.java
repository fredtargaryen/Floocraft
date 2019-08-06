package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.network.messages.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MessageHandler {
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(DataReference.MODID, "channel"),
			() -> "1.0", //version that will be offered to the server
			(String s) -> s.equals("1.0"), //client accepted versions
			(String s) -> s.equals("1.0"));//server accepted versions

	public static void init() {
		INSTANCE.registerMessage(0, MessageFireplaceListRequest.class, 		MessageFireplaceListRequest::toBytes, 			MessageFireplaceListRequest::new, 			MessageFireplaceListRequest::onMessage);
		INSTANCE.registerMessage(1, MessageTeleportEntity.class, 				MessageTeleportEntity::toBytes, 				MessageTeleportEntity::new, 				MessageTeleportEntity::onMessage);
		INSTANCE.registerMessage(2, MessageFireplaceList.class, 				MessageFireplaceList::toBytes, 					MessageFireplaceList::new, 					MessageFireplaceList::onMessage);
		INSTANCE.registerMessage(3, MessageApproveFireplace.class,			MessageApproveFireplace::toBytes,				MessageApproveFireplace::new,				MessageApproveFireplace::onMessage);
		INSTANCE.registerMessage(4, MessageApproval.class, 					MessageApproval::toBytes, 						MessageApproval::new, 						MessageApproval::onMessage);
		INSTANCE.registerMessage(5, MessageDoGreenFlash.class, 				MessageDoGreenFlash::toBytes, 					MessageDoGreenFlash::new, 					MessageDoGreenFlash::onMessage);
		INSTANCE.registerMessage(6, MessageFlooTorchTeleport.class, 			MessageFlooTorchTeleport::toBytes, 				MessageFlooTorchTeleport::new, 				MessageFlooTorchTeleport::onMessage);
		INSTANCE.registerMessage(7, MessagePeekRequest.class, 				MessagePeekRequest::toBytes, 					MessagePeekRequest::new, 					MessagePeekRequest::onMessage);
		INSTANCE.registerMessage(8, MessageStartPeek.class, 					MessageStartPeek::toBytes, 						MessageStartPeek::new, 						MessageStartPeek::onMessage);
		INSTANCE.registerMessage(9, MessageEndPeek.class, 					MessageEndPeek::toBytes, 						MessageEndPeek::new, 						MessageEndPeek::onMessage);
		INSTANCE.registerMessage(10, MessagePlayerIDRequest.class, 			MessagePlayerIDRequest::toBytes, 				MessagePlayerIDRequest::new, 				MessagePlayerIDRequest::onMessage);
		INSTANCE.registerMessage(11, MessagePlayerID.class, 					MessagePlayerID::toBytes, 						MessagePlayerID::new,						MessagePlayerID::onMessage);
	}
}
