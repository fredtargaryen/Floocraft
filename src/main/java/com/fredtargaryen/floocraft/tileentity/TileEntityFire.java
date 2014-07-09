package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.block.GreenFlamesBusyHigher;
import com.fredtargaryen.floocraft.block.GreenFlamesBusyLower;
import com.fredtargaryen.floocraft.block.GreenFlamesIdle;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityFire extends TileEntity
{
    public void arePlayersNearby(int par1, int par2, int par3)
    {
        EntityPlayer ep = this.worldObj.getClosestPlayer((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE);
        System.out.println(ep);
        //if(this.worldObj.getClosestPlayer((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE) == null)
        if(ep == null)
        {
            if(this.worldObj.getBlock(par1, par2, par3) instanceof GreenFlamesBusyLower)
            {
                this.worldObj.setBlock(par1, par2, par3, new GreenFlamesIdle());
            }
        }
        else
        {
            if(this.worldObj.getBlock(par1, par2, par3) instanceof GreenFlamesIdle)
            {
                this.worldObj.setBlock(par1, par2, par3, new GreenFlamesBusyLower());
                if(this.worldObj.getBlock(par1, par2 + 1, par3) instanceof BlockAir)
                {
                    this.worldObj.setBlock(par1, par2 + 1, par3, new GreenFlamesBusyHigher());
                }
            }
        }
    }
}
