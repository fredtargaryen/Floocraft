package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.MessageFlooTorchTeleport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Random;

public class FlooTorchBlock extends TorchBlock {
    private static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(6.0D, 3.0D, 12.0D, 10.0D, 13.0D, 16.0D);
    private static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(6.0D, 3.0D, 0.0D, 10.0D, 13.0D, 4.0D);
    private static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(12.0D, 3.0D, 6.0D, 16.0D, 13.0D, 10.0D);
    private static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(0.0D, 3.0D, 6.0D, 4.0D, 13.0D, 10.0D);
    public static final DirectionProperty FACING_EXCEPT_DOWN = DirectionProperty.create("facing", (p_208125_0_) -> p_208125_0_ != Direction.DOWN);

	public FlooTorchBlock() {
		super(Block.Properties.create(Material.MISCELLANEOUS)
                .doesNotBlockMovement()
                .hardnessAndResistance(0F)
                .lightValue(14)
                .sound(SoundType.WOOD));
	}

	@Override
    @Nonnull
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch(state.get(FACING_EXCEPT_DOWN)) {
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case EAST:
                return EAST_SHAPE;
            default:
                return SHAPE;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING_EXCEPT_DOWN);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World worldIn, BlockPos pos, Random rand) {
        double d0 = (double)pos.getX() + 0.5D;
        double d1 = (double)pos.getY() + 0.7D;
        double d2 = (double)pos.getZ() + 0.5D;
        switch(state.get(FACING_EXCEPT_DOWN)) {
            case NORTH:
                d1 += 0.145D;
                d2 += 0.25D;
                break;
            case SOUTH:
                d1 += 0.145D;
                d2 -= 0.25D;
                break;
            case WEST:
                d1 += 0.145D;
                d0 += 0.25D;
                break;
            case EAST:
                d1 += 0.145D;
                d0 -= 0.25D;
                break;
        }
        worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        Minecraft.getInstance().particles.addParticle(FloocraftBase.GREEN_FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            if (entity instanceof PlayerEntity) {
                //Triggered by a player on the client side.
                MessageFlooTorchTeleport mftt = new MessageFlooTorchTeleport();
                mftt.torchX = pos.getX();
                mftt.torchY = pos.getY();
                mftt.torchZ = pos.getZ();
                MessageHandler.INSTANCE.sendToServer(mftt);
            }
        }
    }

    @Override
    @Nonnull
    public Item asItem() { return FloocraftBase.ITEM_FLOO_TORCH; }

    ////////////////////////
    //MIRAGE COMPATIBILITY//
    ////////////////////////
//    @Override
//    public boolean hasTileEntity(BlockState ibs)
//    {
//        return FloocraftBase.isMirageInstalled();
//    }
//
//    @Override
//    public TileEntity createTileEntity(World world, BlockState state)
//    {
//        TileEntityMirageFire temf = null;
//        if(FloocraftBase.isMirageInstalled())
//        {
//            temf = new TileEntityMirageFire();
//            temf.setRadius(4.0F);
//        }
//        return temf;
//    }
}