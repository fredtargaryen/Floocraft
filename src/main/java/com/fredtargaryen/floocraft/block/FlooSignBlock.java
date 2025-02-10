package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.HelperFunctions;
import com.fredtargaryen.floocraft.blockentity.FlooSignBlockEntity;
import com.fredtargaryen.floocraft.client.gui.screens.inventory.FlooSignEditScreen;
import com.fredtargaryen.floocraft.network.FloocraftLevelData;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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
    public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (!level.isClientSide) {
            FlooSignBlockEntity fsbe = (FlooSignBlockEntity) level.getBlockEntity(pos);
            if (fsbe != null && fsbe.getConnected()) {
                FloocraftLevelData.getForLevel((ServerLevel) level).removeLocation(fsbe.getLocationName());
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    public void openTextEdit(Player player, FlooSignBlockEntity fsbe) {
        fsbe.setPlayer(player);
        Minecraft.getInstance().setScreen(new FlooSignEditScreen(fsbe));
    }

    @Override
    public String getDescriptionId() {
        if (this.descriptionIdCopy == null) {
            this.descriptionIdCopy = Util.makeDescriptionId("block", BuiltInRegistries.BLOCK.getKey(this));
        }
        return this.descriptionIdCopy;
    }
}