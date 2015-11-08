package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.entity.EntityGreenFlame;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.*;

public class BlockFlooTorch extends BlockTorch
{
	public BlockFlooTorch()
	{
		super();
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister i)
	{
		this.blockIcon = i.registerIcon(DataReference.resPath(this.getUnlocalizedName()));
	}
	
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int l = par1World.getBlockMetadata(par2, par3, par4);
        double d0 = (double)((float)par2 + 0.5F);
        double d1 = (double)((float)par3 + 0.7F);
        double d2 = (double)((float)par4 + 0.5F);
        double d3 = 0.2199999988079071D;
        double d4 = 0.27000001072883606D;
        if (l == 1)
        {
            par1World.spawnParticle("smoke", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(par1World, d0 - d4, d1 + d3, d2));
        }
        else if (l == 2)
        {
            par1World.spawnParticle("smoke", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(par1World, d0 + d4, d1 + d3, d2));
        }
        else if (l == 3)
        {
            par1World.spawnParticle("smoke", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(par1World, d0, d1 + d3, d2 - d4));
        }
        else if (l == 4)
        {
            par1World.spawnParticle("smoke", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(par1World, d0, d1 + d3, d2 + d4));
        }
        else
        {
            par1World.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(par1World, d0, d1, d2));
        }
    }
	
	public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    @Override
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
        if (!par1World.isRemote) {
            if (par5Entity instanceof EntityPlayer) {
                Random r = new Random();
                int minx = par2 - 3;
                int maxx = par2 + 3;
                int minz = par4 - 3;
                int maxz = par4 + 3;
                List<List<Integer>> coords = new ArrayList<List<Integer>>();
                for (int x = minx; x <= maxx; x++) {
                    for (int z = minz; z <= maxz; z++) {
                        if (par1World.isAirBlock(x, par3, z) && par1World.isAirBlock(x, par3 + 1, z)) {
                            List<Integer> nextCoord = new ArrayList<Integer>();
                            nextCoord.add(x);
                            nextCoord.add(z);
                            coords.add(nextCoord);
                        }
                    }
                }
                if(coords.size() > 0) {
                    List<Integer> chosenCoord = coords.get(r.nextInt(coords.size()));
                    ((EntityPlayer) par5Entity).setPositionAndUpdate(chosenCoord.get(0) + 0.5, par3, chosenCoord.get(1) + 0.5);
                    par1World.playSoundEffect((double) par2, (double) par3, (double) par4, DataReference.MODID + ":flick", 1.0F, 1.0F);
                }
            }
        }
    }
}
