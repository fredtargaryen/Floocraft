package com.fredtargaryen.floocraft.tileentity;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class TileEntityFire extends TileEntity
{
	private int fireplaceSpeed;
	
	public List<EntityPlayerMP> players  = new ArrayList<EntityPlayerMP>();
	
	public void addEntity(EntityPlayerMP e)
	{
		if(!this.players.contains(e))
		{
			this.players.add(e);
		}
	}
	
	public void moveAllEntities(int x, int y, int z)
	{
		if(this.players.size() > 0)
    	{
    		for(int p = 0; p < this.players.size(); p++)
    		{
    			EntityPlayerMP e = this.players.get(p);
    			if(e.isRiding())
    			{
    				e.mountEntity((Entity)null);
    			}
    			e.playerNetServerHandler.setPlayerLocation(x, y, z, e.rotationYaw, e.rotationPitch);
        		e.fallDistance = 0.0F;
    		}
    		this.players.clear();
    	}
	}
	
	public int getFireplaceSpeed()
	{
		return fireplaceSpeed;
	}
	
	public void setFireplaceSpeed(int s)
	{
		this.fireplaceSpeed = s;
	}
}
