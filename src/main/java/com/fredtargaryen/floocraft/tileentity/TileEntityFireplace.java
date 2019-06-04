package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.BlockFlooSign;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageAddFireplace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TileEntityFireplace extends TileEntitySign
{
	private EntityPlayer writer;
    private boolean isConnected;
    private int y;

	/**Sends packet containing:
	 *--xcoord
	 *--ycoord
	 *--zcoord
	 *--placename
	 */
	public void addLocation(BlockPos pos, String name, World par5World)
   	{
        if(par5World.isRemote)
        {
         	// We are on the client side.
            MessageAddFireplace m = new MessageAddFireplace();
            m.name = name;
            m.signPos = pos;
            BlockPos locationPos = iterateDownFromSign(par5World, pos);
            this.y = locationPos.getY();
            m.locationPos = locationPos;
        	PacketHandler.INSTANCE.sendToServer(m);
        }
   	}
	
	@Override
	public EntityPlayer getPlayer()
    {
		return this.writer;
    }
	
	@Override
	public void setPlayer(EntityPlayer par1EntityPlayer)
    {
		if(this.writer == null)
		{
			this.writer = par1EntityPlayer;
		}
    }

    public int getY(){return this.y;}

    /**
     * Gets the position of the block which, according to fireplace construction rules, forms the bottom of the fireplace.
     * Fireplaces only permit air, fire and green fire blocks inside them.
     */
    private static BlockPos iterateDownFromSign(World w, BlockPos pos)
    {
        //The block below the block at the top of the fireplace
        pos = pos.offset(w.getBlockState(pos).get(BlockFlooSign.FACING).getOpposite()).offset(EnumFacing.DOWN, 1);
        while((w.isAirBlock(pos) || w.getBlockState(pos).getBlock() == Blocks.FIRE || w.getBlockState(pos).getBlock() instanceof GreenFlamesBase) && pos.getY() > -1)
        {
            pos = pos.offset(EnumFacing.DOWN, 1);
        }
        return pos.offset(EnumFacing.UP, 1);
    }

    public void setConnected(boolean b)
    {
        this.isConnected = b;
    }

    public boolean getConnected()
    {
        return this.isConnected;
    }

    public NBTTagCompound write(NBTTagCompound par1) {
        par1 = super.write(par1);
        par1.setString("id", FloocraftBase.FIREPLACE_TYPE.getRegistryName().toString());
        par1.setBoolean("Connected",this.isConnected);
        par1.setInt("Y", this.y);
        return par1;
    }

    public void read(NBTTagCompound par1) {
        super.read(par1);
        this.isConnected = par1.getBoolean("Connected");
        this.y = par1.getInt("Y");
    }

    public void setY(int y){this.y = y;}
}
