package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.FlooSignBlock;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FireplaceTileEntity extends TileEntity {

    /////////////
    //SIGN INFO//
    /////////////
    public final String[] signText = new String[]{"", "", "", ""};
    private int lineBeingEdited = -1;
	private PlayerEntity writer;

    public FireplaceTileEntity() {
        super(FloocraftBase.FIREPLACE_TYPE);
    }

    @Override
    @Nonnull
    public CompoundNBT write(CompoundNBT par1) {
        par1 = super.write(par1);
        par1.putBoolean("Connected",this.isConnected);
        par1.putInt("Y", this.y);
        for(int i = 0; i < 4; ++i) par1.putString(String.valueOf(i), this.getString(i));
        return par1;
    }

    public void read(CompoundNBT par1) {
        super.read(par1);
        this.isConnected = par1.getBoolean("Connected");
        this.y = par1.getInt("Y");
        for(int i = 0; i < 4; ++i) this.setString(i, par1.getString(String.valueOf(i)));
    }

    public static String getSignTextAsLine(String[] signText) {
        return signText[0] + " " + signText[1] + " " + signText[2] + " " + signText[3];
    }

    public String getString(int index) {
        return(signText[index]);
    }

    public void setString(int index, String s) {
        this.signText[index] = s;
    }

    @Nonnull
    public PlayerEntity getPlayer()
    {
        return this.writer;
    }

    public void setPlayer(@Nonnull PlayerEntity par1PlayerEntity) {
        if(this.writer == null)
        {
            this.writer = par1PlayerEntity;
        }
    }

    public int getLineBeingEdited() { return this.lineBeingEdited; }

    public void setLineBeingEdited(int index) {
        this.lineBeingEdited = index;
    }

    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, -1, this.getUpdateTag());
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     *
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    //////////////////
    //FIREPLACE INFO//
    //////////////////
    private boolean isConnected;
    private int y;

    public void addLocation() {
        if(!this.world.isRemote) {
            // We are on the server side.
            BlockPos locationPos = iterateDownFromSign(this.world, this.pos);
            this.y = locationPos.getY();
            FloocraftWorldData.forWorld(this.world).addLocation(getSignTextAsLine(this.signText), locationPos);
            this.markDirty();
        }
    }

    /**
     * Gets the position of the block which, according to fireplace construction rules, forms the bottom of the fireplace.
     * Fireplaces only permit air, fire and green fire blocks inside them.
     */
    private static BlockPos iterateDownFromSign(World w, BlockPos pos)
    {
        //The block below the block at the top of the fireplace
        pos = pos.offset(w.getBlockState(pos).get(FlooSignBlock.FACING).getOpposite()).offset(Direction.DOWN, 1);
        while((w.isAirBlock(pos) || w.getBlockState(pos).getBlock() == Blocks.FIRE || w.getBlockState(pos).getBlock() instanceof GreenFlamesBase) && pos.getY() > -1)
        {
            pos = pos.offset(Direction.DOWN, 1);
        }
        return pos.offset(Direction.UP, 1);
    }

    public int getY(){return this.y;}

    public void setY(int y){this.y = y;}

    public boolean getConnected() {
        return this.isConnected;
    }

    public void setConnected(boolean b)
    {
        this.isConnected = b;
    }
}
