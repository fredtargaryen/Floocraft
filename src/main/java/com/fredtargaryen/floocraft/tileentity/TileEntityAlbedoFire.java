package com.fredtargaryen.floocraft.tileentity;

import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface="elucent.albedo.lighting.ILightProvider", modid="albedo")
public class TileEntityAlbedoFire extends TileEntity implements ILightProvider
{
    private float radius;

    public void setRadius(float r)
    {
        this.radius = r;
    }

    @Optional.Method(modid="albedo")
    @Override
    public Light provideLight()
    {
        return Light.builder()
                .pos(this.pos)
                //24 237 116; same colour as the flash
                .color(0.09375F, 0.92578125F, 0.453125F)
                .radius(this.radius)
                .build();
    }
}
