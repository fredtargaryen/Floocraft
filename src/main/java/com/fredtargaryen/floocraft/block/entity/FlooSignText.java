package com.fredtargaryen.floocraft.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FlooSignText {
    private static final Codec<Component[]> LINES_CODEC;
    public static final Codec<FlooSignText> DIRECT_CODEC;
    public static final int LINES = 4;
    private final Component[] messages;
    private final Component[] filteredMessages;
    @Nullable
    private FormattedCharSequence[] renderMessages;
    private boolean renderMessagedFiltered;

    public FlooSignText() {
        this(emptyMessages(), emptyMessages());
    }

    public FlooSignText(Component[] p_277506_, Component[] p_277908_) {
        this.messages = p_277506_;
        this.filteredMessages = p_277908_;
    }

    private static Component[] emptyMessages() {
        return new Component[]{CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY};
    }

    private static FlooSignText load(Component[] messages, Optional<Component[]> filteredMessages) {
        return new FlooSignText(messages, filteredMessages.orElse(Arrays.copyOf(messages, messages.length)));
    }

    public Component getMessage(int line, boolean filtered) {
        return this.getMessages(filtered)[line];
    }

    public FlooSignText setMessage(int line, Component component) {
        return this.setMessage(line, component, component);
    }

    public FlooSignText setMessage(int line, Component newUnfilteredMessage, Component newFilteredMessage) {
        Component[] messages = Arrays.copyOf(this.messages, this.messages.length);
        Component[] filteredMessages = Arrays.copyOf(this.filteredMessages, this.filteredMessages.length);
        messages[line] = newUnfilteredMessage;
        filteredMessages[line] = newFilteredMessage;
        return new FlooSignText(messages, filteredMessages);
    }

    public boolean hasMessage(Player player) {
        return Arrays.stream(this.getMessages(player.isTextFilteringEnabled())).anyMatch((p_277499_) -> {
            return !p_277499_.getString().isEmpty();
        });
    }

    public Component[] getMessages(boolean filtered) {
        return filtered ? this.filteredMessages : this.messages;
    }

    public FormattedCharSequence[] getRenderMessages(boolean p_277336_, Function<Component, FormattedCharSequence> p_277538_) {
        if (this.renderMessages == null || this.renderMessagedFiltered != p_277336_) {
            this.renderMessagedFiltered = p_277336_;
            this.renderMessages = new FormattedCharSequence[4];

            for (int $$2 = 0; $$2 < 4; ++$$2) {
                this.renderMessages[$$2] = (FormattedCharSequence) p_277538_.apply(this.getMessage($$2, p_277336_));
            }
        }

        return this.renderMessages;
    }

    private Optional<Component[]> filteredMessages() {
        for (int $$0 = 0; $$0 < 4; ++$$0) {
            if (!this.filteredMessages[$$0].equals(this.messages[$$0])) {
                return Optional.of(this.filteredMessages);
            }
        }

        return Optional.empty();
    }

    public boolean hasAnyClickCommands(Player p_277865_) {
        Component[] var2 = this.getMessages(p_277865_.isTextFilteringEnabled());
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Component $$1 = var2[var4];
            Style $$2 = $$1.getStyle();
            ClickEvent $$3 = $$2.getClickEvent();
            if ($$3 != null && $$3.getAction() == Action.RUN_COMMAND) {
                return true;
            }
        }

        return false;
    }

    public String[] getTextAsArray() {
        return Arrays.stream(this.messages)
                .map(Component::getString)
                .toArray(String[]::new);
    }

    static {
        LINES_CODEC = ComponentSerialization.FLAT_CODEC.listOf().comapFlatMap((p_327312_) -> {
            return Util.fixedSize(p_327312_, 4).map((p_277881_) -> {
                return new Component[]{(Component) p_277881_.get(0), (Component) p_277881_.get(1), (Component) p_277881_.get(2), (Component) p_277881_.get(3)};
            });
        }, (p_277460_) -> {
            return List.of(p_277460_[0], p_277460_[1], p_277460_[2], p_277460_[3]);
        });
        DIRECT_CODEC = RecordCodecBuilder.create((builder) -> builder
                .group(LINES_CODEC.fieldOf("messages")
                                .forGetter(flooSignText -> flooSignText.messages),
                        LINES_CODEC.lenientOptionalFieldOf("filtered_messages")
                                .forGetter(FlooSignText::filteredMessages)
                ).apply(builder, FlooSignText::load));
    }
}
