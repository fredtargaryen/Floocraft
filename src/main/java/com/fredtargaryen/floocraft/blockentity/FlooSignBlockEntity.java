package com.fredtargaryen.floocraft.blockentity;

import com.fredtargaryen.floocraft.FloocraftBlockEntityTypes;
import com.fredtargaryen.floocraft.HelperFunctions;
import com.fredtargaryen.floocraft.block.FlooFlamesBlock;
import com.fredtargaryen.floocraft.block.FlooSignBlock;
import com.fredtargaryen.floocraft.block.entity.FlooSignText;
import com.fredtargaryen.floocraft.network.FloocraftLevelData;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FlooSignBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private FlooSignText signText;

    private Player writer;
    public static final int TEXT_LINE_HEIGHT = 10;
    public static final int MAX_TEXT_LINE_WIDTH = 90;

    public FlooSignBlockEntity(BlockPos pos, BlockState state) {
        super(FloocraftBlockEntityTypes.FLOO_SIGN.get(), pos, state);
        this.signText = new FlooSignText();
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("connected", this.isConnected);
        tag.putInt("y", this.y);
        DynamicOps<Tag> dynamicops = provider.createSerializationContext(NbtOps.INSTANCE);
        DataResult<Tag> result = FlooSignText.DIRECT_CODEC.encodeStart(dynamicops, this.signText);
        result.resultOrPartial(LOGGER::error).ifPresent(text -> tag.put("text", text));
    }

    @Override
    public void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.isConnected = tag.getBoolean("connected");
        this.y = tag.getInt("y");
        DynamicOps<Tag> dynamicops = provider.createSerializationContext(NbtOps.INSTANCE);
        DataResult<FlooSignText> result;
        if (tag.contains("text")) {
            result = FlooSignText.DIRECT_CODEC.parse(dynamicops, tag.getCompound("text"));
            result.resultOrPartial(LOGGER::error).ifPresent(text -> this.signText = this.loadLines(text));
        }
    }

    private FlooSignText loadLines(FlooSignText signText) {
        for (int line = 0; line < 4; ++line) {
            Component component = this.loadLine(signText.getMessage(line, false));
            Component component1 = this.loadLine(signText.getMessage(line, true));
            signText = signText.setMessage(line, component, component1);
        }
        return signText;
    }

    private Component loadLine(Component p_278307_) {
        Level level = this.level;
        if (level instanceof ServerLevel serverlevel) {
            try {
                return ComponentUtils.updateForEntity(createCommandSourceStack(null, serverlevel, this.worldPosition), p_278307_, null, 0);
            } catch (CommandSyntaxException ignored) {
            }
        }
        return p_278307_;
    }

    private static CommandSourceStack createCommandSourceStack(@Nullable Player p_279428_, Level p_279359_, BlockPos p_279430_) {
        String s = p_279428_ == null ? "Sign" : p_279428_.getName().getString();
        Component component = p_279428_ == null ? Component.literal("Sign") : p_279428_.getDisplayName();
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(p_279430_), Vec2.ZERO, (ServerLevel) p_279359_, 2, s, (Component) component, p_279359_.getServer(), p_279428_);
    }

    public FlooSignText getText() {
        return this.signText;
    }

    @Nonnull
    public Player getPlayer() {
        return this.writer;
    }

    public void setPlayer(@Nonnull Player par1PlayerEntity) {
        if (this.writer == null) {
            this.writer = par1PlayerEntity;
        }
    }

    public boolean playerIsTooFarAwayToEdit(UUID playerId) {
        assert this.level != null;
        Player player = this.level.getPlayerByUUID(playerId);
        return player == null || !player.canInteractWithBlock(this.getBlockPos(), 4.0);
    }

    public void markUpdate() {
        this.setChanged();
        assert this.level != null;
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    //////////////////
    //FIREPLACE INFO//
    //////////////////
    private boolean isConnected;
    private int y;

    public void addLocation(List<String> rawLocationName) {
        assert this.level != null;
        if (!this.level.isClientSide) {
            // We are on the server side.
            this.setNameOnSign(rawLocationName);
            Optional<BlockPos> locationPos = getFireplaceFirePos(this.level, this.getBlockPos());
            locationPos.ifPresent(
                    pos -> {
                        this.y = pos.getY();
                        FloocraftLevelData.getForLevel((ServerLevel) this.level).addLocation(HelperFunctions.convertArrayToLocationName(rawLocationName), pos);
                    });
        }
    }

    public void setNameOnSign(List<String> name) {
        for (int i = 0; i < 4; i++) {
            this.signText = this.signText.setMessage(i, Component.literal(name.get(i)));
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_333348_) {
        return this.saveCustomOnly(p_333348_);
    }

    /**
     * Get what should be the base position of the fire in the fireplace.
     * Uses similar rules to getTopBlockY in {@link FlooFlamesBlock}.
     *
     * @param level The level this Floo sign is in
     * @param pos   The position of this Floo sign
     * @return The position where the fire block should be.
     * Return Optional.empty() if the min build height is reached
     * without finding a solid block
     */
    private static Optional<BlockPos> getFireplaceFirePos(Level level, BlockPos pos) {
        int minY = level.getMinBuildHeight();
        //The block below the block at the top of the fireplace
        BlockPos nextPos = pos.relative(level.getBlockState(pos).getValue(FlooSignBlock.FACING).getOpposite())
                .below();
        int y = nextPos.getY();
        BlockState bs;
        while (y >= minY) {
            bs = level.getBlockState(nextPos);
            if (!bs.canBeReplaced()) return Optional.of(nextPos.above());
            nextPos = nextPos.below();
            y--;
        }
        return Optional.empty();
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean getConnected() {
        return this.isConnected;
    }

    public void setConnected(boolean b) {
        this.isConnected = b;
        this.setChanged();
    }

    public String getLocationName() {
        return HelperFunctions.convertArrayToLocationName(this.signText.getTextAsArray());
    }
}
