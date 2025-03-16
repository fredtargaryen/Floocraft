package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.network.messages.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = DataReference.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MessageHandler {
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1")
                .executesOn(HandlerThread.NETWORK);

        registrar.playToServer(
                FireplaceListRequestMessage.TYPE,
                FireplaceListRequestMessage.STREAM_CODEC,
                FireplaceListRequestMessage::handle
        );

        registrar.playToClient(
                FireplaceListResponseMessage.TYPE,
                FireplaceListResponseMessage.STREAM_CODEC,
                FireplaceListResponseMessage::handle
        );

        registrar.playToServer(
                FlooSignNameRequestMessage.TYPE,
                FlooSignNameRequestMessage.STREAM_CODEC,
                FlooSignNameRequestMessage::handle
        );

        registrar.playToClient(
                FlooSignNameResponseMessage.TYPE,
                FlooSignNameResponseMessage.STREAM_CODEC,
                FlooSignNameResponseMessage::handle
        );

        registrar.playToServer(
                FloowerPotSettingsUpdateMessage.TYPE,
                FloowerPotSettingsUpdateMessage.STREAM_CODEC,
                FloowerPotSettingsUpdateMessage::handle
        );

        registrar.playToClient(
                OpenFlooSignEditScreenMessage.TYPE,
                OpenFlooSignEditScreenMessage.STREAM_CODEC,
                OpenFlooSignEditScreenMessage::handle
        );

        registrar.playToServer(
                EndPeekMessage.TYPE,
                EndPeekMessage.STREAM_CODEC,
                EndPeekMessage::handle
        );

        registrar.playToServer(
                PeekerInfoRequestMessage.TYPE,
                PeekerInfoRequestMessage.STREAM_CODEC,
                PeekerInfoRequestMessage::handle
        );

        registrar.playToServer(
                PeekerInfoResponseMessage.TYPE,
                PeekerInfoResponseMessage.STREAM_CODEC,
                PeekerInfoResponseMessage::handle
        );

        registrar.playToServer(
                StartPeekRequestMessage.TYPE,
                StartPeekRequestMessage.STREAM_CODEC,
                StartPeekRequestMessage::handle
        );

        registrar.playToClient(
                StartPeekResponseMessage.TYPE,
                StartPeekResponseMessage.STREAM_CODEC,
                StartPeekResponseMessage::handle
        );

        registrar.playToServer(
                TeleportByTorchMessage.TYPE,
                TeleportByTorchMessage.STREAM_CODEC,
                TeleportByTorchMessage::handle
        );

        registrar.playToClient(
                TeleportResponseMessage.TYPE,
                TeleportResponseMessage.STREAM_CODEC,
                TeleportResponseMessage::handle
        );

        registrar.playToServer(
                TeleportRequestMessage.TYPE,
                TeleportRequestMessage.STREAM_CODEC,
                TeleportRequestMessage::handle
        );
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.sendToServer(message);
    }

    public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }
}
