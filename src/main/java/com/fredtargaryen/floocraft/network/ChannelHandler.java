package com.fredtargaryen.floocraft.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<IPacket>
{
    public ChannelHandler()
    {
    	
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, IPacket packet, ByteBuf data) throws Exception
    {
        packet.writeBytes(data);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, IPacket packet)
    {
    	packet.readBytes(data);
        switch(FMLCommonHandler.instance().getEffectiveSide())
        {
            case CLIENT:
                packet.executeClient(Minecraft.getMinecraft().thePlayer);
                break;
            case SERVER:
                INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
                packet.executeServer(((NetHandlerPlayServer) netHandler).playerEntity);
                break;
        }
    }
}
