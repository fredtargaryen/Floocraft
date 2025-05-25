package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.blockentity.FlooSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nonnull;

public class FlooSignBlock extends WallSignBlock {
    /**
     * Hack to avoid WallSignBlock's descriptionId stuff
     */
    private String descriptionIdCopy;

    public FlooSignBlock() {
        super(WoodType.OAK, Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, DataReference.getResourceLocation(FloocraftBlocks.FLOO_SIGN_RL)))
                .mapColor(MapColor.COLOR_GREEN)
                .forceSolidOn()
                .instrument(NoteBlockInstrument.BASS)
                .noCollission()
                .strength(1.0F)
                .ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        try {
            return new FlooSignBlockEntity(pos, state);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void openTextEdit(Player player, SignBlockEntity sbe, boolean ignored) {
    }
}