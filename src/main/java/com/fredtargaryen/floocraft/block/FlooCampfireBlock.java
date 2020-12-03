package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static net.minecraft.block.CampfireBlock.LIT;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FlooCampfireBlock extends FlooMainTeleporterBase implements IWaterLoggable {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D);
   protected static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final DirectionProperty FACING = HORIZONTAL_FACING;

   public FlooCampfireBlock(int lightLevel) {
      super(AbstractBlock.Properties.create(Material.WOOD).setLightLevel(state -> lightLevel));
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to randomTick and needsRandomTick, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      int randomInt = rand.nextInt(10);
      if(randomInt < 2)
      {
         spawnSmokeParticles(worldIn, pos);
      }
      if (randomInt == 0) {
         worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
      }
   }

   public static void extinguish(IWorld world, BlockPos pos, BlockState state) {
      if (world.isRemote()) {
         for(int i = 0; i < 20; ++i) {
            spawnSmokeParticles((World)world, pos);
         }
      }
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
      if (fluidStateIn.getFluid() == Fluids.WATER) {
         if (!worldIn.isRemote()) {
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }

         extinguish(worldIn, pos, state);

         BlockState campfireBlockState = state.getBlock() == FloocraftBase.FLOO_SOUL_CAMPFIRE.get() ? Blocks.SOUL_CAMPFIRE.getDefaultState() : Blocks.CAMPFIRE.getDefaultState();
         worldIn.setBlockState(pos, campfireBlockState
            .with(LIT, Boolean.valueOf(false))
            .with(WATERLOGGED, Boolean.valueOf(true))
            .with(CampfireBlock.FACING, state.get(HORIZONTAL_FACING)), 3);
         worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
         return true;
      } else {
         return false;
      }
   }

   public static void spawnSmokeParticles(World worldIn, BlockPos pos) {
      Random random = worldIn.getRandom();
      BasicParticleType basicparticletype = ParticleTypes.CAMPFIRE_COSY_SMOKE;
      worldIn.addOptionalParticle(basicparticletype, true, (double)pos.getX() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + random.nextDouble() + random.nextDouble(), (double)pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated Implementing/overriding is fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      super.fillStateContainer(builder);
      builder.add(FACING, WATERLOGGED);
   }

   @Override
   @Nonnull
   public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }
}
