package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.tileentity.FireplaceTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class FlooSignBlock extends WallSignBlock {
	public FlooSignBlock() {
		super(Properties.create(Material.WOOD)
                .doesNotBlockMovement()
                .hardnessAndResistance(1.0F)
                .sound(SoundType.WOOD), WoodType.OAK);
		this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
	}

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        try {
            return new FireplaceTileEntity();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    @Nonnull
    public Item asItem() { return FloocraftBase.ITEM_FLOO_SIGN; }

    @Override
    @Nonnull
    public void onReplaced(BlockState state, World w, BlockPos pos, BlockState newState, boolean isMoving) {
        if(!w.isRemote) {
            FireplaceTileEntity tef = (FireplaceTileEntity) w.getTileEntity(pos);
            if (tef.getConnected()) {
                //Finds the fireplace position from the sign position and rotation
                //The block below the block at the top of the fireplace
                BlockPos locationPos = pos.offset(state.get(FACING).getOpposite());
                FloocraftWorldData.forWorld(w).removeLocation(locationPos.getX(), tef.getY(), locationPos.getZ());
            }
        }
        super.onReplaced(state, w, pos, newState, isMoving);
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call via {@link BlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
     */
    @Override
    @Nonnull
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}