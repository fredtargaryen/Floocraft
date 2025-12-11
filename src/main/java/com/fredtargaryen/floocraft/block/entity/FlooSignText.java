package com.fredtargaryen.floocraft.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FlooSignText {
    private static final Codec<Component[]> LINES_CODEC = ComponentSerialization.CODEC
            .listOf()
            .comapFlatMap(
                    p_337999_ -> Util.fixedSize((List<Component>) p_337999_, 4)
                            .map(p_277881_ -> new Component[]{p_277881_.get(0), p_277881_.get(1), p_277881_.get(2), p_277881_.get(3)}),
                    p_277460_ -> List.of(p_277460_[0], p_277460_[1], p_277460_[2], p_277460_[3])
            );
    public static final Codec<FlooSignText> DIRECT_CODEC = RecordCodecBuilder.create(
            p_338000_ -> p_338000_.group(
                            LINES_CODEC.fieldOf("messages").forGetter(p_277822_ -> p_277822_.messages),
                            LINES_CODEC.lenientOptionalFieldOf("filtered_messages").forGetter(FlooSignText::filteredMessages)
                    )
                    .apply(p_338000_, FlooSignText::load)
    );

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

    public boolean hasAnyClickCommands(Player player) {
        for (Component component : this.getMessages(player.isTextFilteringEnabled())) {
            Style style = component.getStyle();
            ClickEvent clickevent = style.getClickEvent();
            if (clickevent != null && clickevent.action() == ClickEvent.Action.RUN_COMMAND) {
                return true;
            }
        }

        return false;
    }

    public List<String> getTextAsArray() {
        return Arrays.stream(this.messages)
                .map(Component::getString)
                .toList();
    }
}
