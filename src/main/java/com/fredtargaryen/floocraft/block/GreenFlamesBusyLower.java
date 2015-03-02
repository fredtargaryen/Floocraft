package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesBusyLower extends GreenFlamesLowerBase
{
    private int renderID;

	public GreenFlamesBusyLower(int id)
    {
		super();
        this.renderID = id;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
    }

    @Override
    public void onBlockAdded(World par1World, int x, int y, int z)
    {
        par1World.scheduleBlockUpdate(x, y, z, this, this.tickRate(par1World));
    }

	@Override
    public void updateTick(World w, int x, int y, int z, Random par5Random)
    {
        if(w.getClosestPlayer((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE) == null)
        {
            w.setBlock(x, y, z, FloocraftBase.greenFlamesIdle, w.getBlockMetadata(x, y, z), 2);
        }
        super.updateTick(w, x, y, z, par5Random);
    }

    @Override
    public int getRenderType(){return this.renderID;}

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister i)
    {
        this.icons = new IIcon[] {i.registerIcon(DataReference.MODID + ":greenflamesbusylower"),
                i.registerIcon(DataReference.MODID + ":greenflamesbusyhigher")};
    }
}