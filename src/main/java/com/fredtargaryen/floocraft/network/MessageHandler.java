package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.network.messages.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class MessageHandler {
	private static final int PROTOCOL_VERSION = 1;

	public static final SimpleChannel INSTANCE = ChannelBuilder
			.named(new ResourceLocation(DataReference.MODID, "main"))
			.clientAcceptedVersions((status, version) -> version == PROTOCOL_VERSION)
			.serverAcceptedVersions((status, version) -> version == PROTOCOL_VERSION)
			.networkProtocolVersion(PROTOCOL_VERSION)
			.simpleChannel();

	public static void init() {
		INSTANCE.messageBuilder(FireplaceListRequestMessage.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(FireplaceListRequestMessage::encode)
				.decoder(FireplaceListRequestMessage::new)
				.consumerMainThread(FireplaceListRequestMessage::handle)
				.add();

		INSTANCE.messageBuilder(FireplaceListResponseMessage.class, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(FireplaceListResponseMessage::encode)
				.decoder(FireplaceListResponseMessage::new)
				.consumerMainThread(FireplaceListResponseMessage::handle)
				.add();

		INSTANCE.messageBuilder(FlooSignNameRequestMessage.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(FlooSignNameRequestMessage::encode)
				.decoder(FlooSignNameRequestMessage::new)
				.consumerMainThread(FlooSignNameRequestMessage::handle)
				.add();

		INSTANCE.messageBuilder(FlooSignNameResponseMessage.class, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(FlooSignNameResponseMessage::encode)
				.decoder(FlooSignNameResponseMessage::new)
				.consumerMainThread(FlooSignNameResponseMessage::handle)
				.add();

		INSTANCE.messageBuilder(FloowerPotSettingsUpdateMessage.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(FloowerPotSettingsUpdateMessage::encode)
				.decoder(FloowerPotSettingsUpdateMessage::new)
				.consumerMainThread(FloowerPotSettingsUpdateMessage::handle)
				.add();

		// TODO: Understand the difference between this and PeekStartMessage
		INSTANCE.messageBuilder(MessageStartPeek.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(MessageStartPeek::encode)
				.decoder(MessageStartPeek::new)
				.consumerMainThread(MessageStartPeek::handle)
				.add();

		INSTANCE.messageBuilder(OpenFlooSignEditScreenMessage.class, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(OpenFlooSignEditScreenMessage::encode)
				.decoder(OpenFlooSignEditScreenMessage::new)
				.consumerMainThread(OpenFlooSignEditScreenMessage::handle)
				.add();

		INSTANCE.messageBuilder(PeekEndMessage.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(PeekEndMessage::encode)
				.decoder(PeekEndMessage::new)
				.consumerMainThread(PeekEndMessage::handle)
				.add();

		INSTANCE.messageBuilder(PeekerInfoRequestMessage.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(PeekerInfoRequestMessage::encode)
				.decoder(PeekerInfoRequestMessage::new)
				.consumerMainThread(PeekerInfoRequestMessage::handle)
				.add();

		INSTANCE.messageBuilder(PeekerInfoResponseMessage.class, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(PeekerInfoResponseMessage::encode)
				.decoder(PeekerInfoResponseMessage::new)
				.consumerMainThread(PeekerInfoResponseMessage::handle)
				.add();

		INSTANCE.messageBuilder(PeekStartMessage.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(PeekStartMessage::encode)
				.decoder(PeekStartMessage::new)
				.consumerMainThread(PeekStartMessage::handle)
				.add();

		INSTANCE.messageBuilder(TeleportByTorchMessage.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(TeleportByTorchMessage::encode)
				.decoder(TeleportByTorchMessage::new)
				.consumerMainThread(TeleportByTorchMessage::handle)
				.add();

		INSTANCE.messageBuilder(TeleportFlashMessage.class, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(TeleportFlashMessage::encode)
				.decoder(TeleportFlashMessage::new)
				.consumerMainThread(TeleportFlashMessage::handle)
				.add();

		INSTANCE.messageBuilder(TeleportMessage.class, NetworkDirection.PLAY_TO_SERVER)
				.encoder(TeleportMessage::encode)
				.decoder(TeleportMessage::new)
				.consumerMainThread(TeleportMessage::handle)
				.add();
	}

	public static void sendToServer(Object message) {
		INSTANCE.send(message, PacketDistributor.SERVER.noArg());
	}

	public static void sendToPlayer(Object message, ServerPlayer player) {
		INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
	}
}
