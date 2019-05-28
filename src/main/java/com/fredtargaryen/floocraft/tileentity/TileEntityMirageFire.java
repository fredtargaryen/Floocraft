//package com.fredtargaryen.floocraft.tileentity;
//
//import com.elytradev.mirage.event.GatherLightsEvent;
//import com.elytradev.mirage.lighting.ILightEventConsumer;
//import com.elytradev.mirage.lighting.Light;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraftforge.fml.common.Optional;
//
//@Optional.Interface(iface="com.elytradev.mirage.lighting.ILightEventConsumer", modid="mirage")
//public class TileEntityMirageFire extends TileEntity implements ILightEventConsumer
//{
//    private float radius;
//
//    public void setRadius(float r)
//    {
//        this.radius = r;
//    }
//
//    @Optional.Method(modid="mirage")
//    @Override
//    public void gatherLights(GatherLightsEvent evt)
//    {
//        evt.add(Light.builder()
//                .pos(this.pos)
//                //24 237 116; same colour as the flash
//                .color(0.09375F, 0.92578125F, 0.453125F)
//                .radius(this.radius)
//                .build());
//    }
//}
