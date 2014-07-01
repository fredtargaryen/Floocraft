package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import net.minecraft.block.BlockFire;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.fredtargaryen.floocraft.FloocraftBase;

import java.util.Random;

public class GreenFlamesBusyHigher extends BlockFire
{
    private IIcon[] icons;

    public GreenFlamesBusyHigher()
     {
           super();
     }
	 
	@SideOnly(Side.CLIENT)
	public IIcon getFireIcon(int i0)
	 {
	     return this.icons[i0];
	 }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister i)
    {
        this.icons = new IIcon[] {i.registerIcon(DataReference.resPath(this.getUnlocalizedName()) + "_layer_0"),
                i.registerIcon(DataReference.resPath(this.getUnlocalizedName()) + "_layer_0")};
    }

	public void onBlockAdded(World par1World, int par2, int par3, int par4)
	{
		 if (par1World.getBlock(par2, par3 - 1, par4) == FloocraftBase.greenFlamesBusyLower)
         {
             par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
		 }
	 }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return this.icons[0];
    }

	public int tickRate(World par1World)
	 {
	     return 20;
	 }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5)
    {
        if(!(par1World.getBlock(par2, par3 - 1, par4) instanceof GreenFlamesBusyLower))
        {
            par1World.setBlockToAir(par2, par3, par4);
        }
    }
}