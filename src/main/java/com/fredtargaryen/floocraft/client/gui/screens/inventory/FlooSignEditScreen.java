package com.fredtargaryen.floocraft.client.gui.screens.inventory;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.blockentity.FlooSignBlockEntity;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.FlooSignNameRequestMessage;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GUI for setting the text on a Floo Sign.
 * Most of the code here is adapted from @link{SignEditScreen} and @link{AbstractSignEditScreen},
 * which at the time of writing seems better for performance than extending them and duplicating
 * the private variables introduced in the parent classes
 */
@OnlyIn(Dist.CLIENT)
public class FlooSignEditScreen extends Screen {
    private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);

    @Nullable
    private Model signModel;
    private int frame;
    private int line;
    @Nullable
    private TextFieldHelper signField;

    private List<String> signCandidateName;

    private StringWidget status;
    private static final Component WAITING_STATUS = Component.translatable("gui.floo_sign.approval_wait");
    private static final Component REJECTED_STATUS = Component.translatable("gui.floo_sign.name_in_use");

    private final FlooSignBlockEntity sign;

    private Button decorButton;
    private static final Component DECOR_BUTTON = Component.translatable("gui.floo_sign.decoration");

    private Button connectButton;
    private static final Component CONNECT_BUTTON = Component.translatable("gui.floo_sign.connect");

    private static final int BUTTON_WIDTH = 110;
    private static final int BUTTON_HEIGHT = 20;

    public FlooSignEditScreen(FlooSignBlockEntity blockEntity) {
        super(Component.translatable("gui.floo_sign.title"));
        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;
        this.sign = blockEntity;
        this.signModel = SignRenderer.createSignModel(this.minecraft.getEntityModels(), WoodType.OAK, false);
        this.signCandidateName = new ArrayList<>(Arrays.asList("", "", "", ""));
    }

    @Override
    protected void init() {
        this.status = new StringWidget(Component.empty(), this.font);
        this.status.setY(this.height / 4 + 100);
        this.addRenderableWidget(this.status);

        this.decorButton = this.addRenderableWidget(
                Button.builder(DECOR_BUTTON, button -> {
                            this.onClose();
                        })
                        .bounds(this.width / 2 - BUTTON_WIDTH - 4,
                                this.height / 4 + 144,
                                BUTTON_WIDTH,
                                BUTTON_HEIGHT)
                        .build());

        this.connectButton = this.addRenderableWidget(
                Button.builder(CONNECT_BUTTON, button -> {
                            this.setStatusAndCentre(WAITING_STATUS);
                            FlooSignNameRequestMessage fsnrm = new FlooSignNameRequestMessage(
                                    this.sign.getBlockPos(),
                                    true,
                                    this.signCandidateName);
                            MessageHandler.sendToServer(fsnrm);
                        })
                        .bounds(this.width / 2 + 4,
                                this.height / 4 + 144,
                                BUTTON_WIDTH,
                                BUTTON_HEIGHT)
                        .build());

        assert this.minecraft != null;
        this.signField = new TextFieldHelper(
                () -> this.signCandidateName.get(this.line),
                newString -> this.signCandidateName.set(this.line, newString),
                TextFieldHelper.createClipboardGetter(this.minecraft),
                TextFieldHelper.createClipboardSetter(this.minecraft),
                stringToLimit -> this.font.width(stringToLimit) <= 90
        );
    }

    @Override
    public void tick() {
        ++this.frame;
        if (!this.isValid()) {
            this.onClose();
        }
    }

    @Override
    public boolean keyPressed(int p_252300_, int p_250424_, int p_250697_) {
        if (p_252300_ == 265) {
            this.line = this.line - 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        } else if (p_252300_ != 264 && p_252300_ != 257 && p_252300_ != 335) {
            return this.signField.keyPressed(p_252300_) ? true : super.keyPressed(p_252300_, p_250424_, p_250697_);
        } else {
            this.line = this.line + 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        }
    }

    @Override
    public boolean charTyped(char p_252008_, int p_251178_) {
        this.signField.charTyped(p_252008_);
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float partialTicks) {
        super.render(graphics, x, y, partialTicks);
        Lighting.setupForFlatItems();
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 16777215);
        this.renderSign(graphics);
        Lighting.setupFor3DItems();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, float partialTicks) {
        this.renderTransparentBackground(graphics);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.sendMessageAsDecoration();
        super.onClose();
    }

    protected void offsetSign(GuiGraphics graphics) {
        graphics.pose().translate((float) this.width / 2.0F, 90.0F, 50.0F);
    }

    protected Vector3f getSignTextScale() {
        return TEXT_SCALE;
    }

    private boolean isValid() {
        return this.minecraft != null && this.minecraft.player != null && !this.sign.isRemoved() && !this.sign.playerIsTooFarAwayToEdit(this.minecraft.player.getUUID());
    }

    private void sendMessageAsDecoration() {
        if (!this.sign.getConnected()) {
            FlooSignNameRequestMessage fsnrm = new FlooSignNameRequestMessage(
                    this.sign.getBlockPos(),
                    false,
                    this.signCandidateName);
            MessageHandler.sendToServer(fsnrm);
        }
    }

    private void renderSign(GuiGraphics graphics) {
        graphics.pose().pushPose();
        this.offsetSign(graphics);
        graphics.pose().pushPose();
        this.renderSignBackground(graphics);
        graphics.pose().popPose();
        this.renderSignText(graphics);
        graphics.pose().popPose();
    }

    protected void renderSignBackground(GuiGraphics graphics) {
        if (this.signModel != null) {
            graphics.pose().translate(0.0F, 31.0F, 0.0F);
            graphics.pose().scale(62.500004F, 62.500004F, -62.500004F);
            graphics.drawSpecial(multiBufferSource -> {
                VertexConsumer consumer = DataReference.SIGN_MATERIAL.buffer(multiBufferSource, this.signModel::renderType);
                this.signModel.renderToBuffer(graphics.pose(), consumer, 15728880, OverlayTexture.NO_OVERLAY);
            });
        }
    }

    private void renderSignText(GuiGraphics graphics) {
        graphics.pose().translate(0.0F, 0.0F, 4.0F);
        Vector3f $$1 = this.getSignTextScale();
        graphics.pose().scale($$1.x(), $$1.y(), $$1.z());
        int blackColour = 0;
        boolean cursorShowing = this.frame / 6 % 2 == 0;
        int cursorPos = this.signField.getCursorPos();
        int selectionPos = this.signField.getSelectionPos();
        int $$6 = 4 * FlooSignBlockEntity.TEXT_LINE_HEIGHT / 2;
        int $$7 = this.line * FlooSignBlockEntity.TEXT_LINE_HEIGHT - $$6;

        int lineNumber;
        String $$14;
        int $$15;
        int $$16;
        int $$17;
        for (lineNumber = 0; lineNumber < 4; ++lineNumber) {
            $$14 = this.signCandidateName.get(lineNumber);
            if ($$14 != null) {
                if (this.font.isBidirectional()) {
                    $$14 = this.font.bidirectionalShaping($$14);
                }

                $$15 = -this.font.width($$14) / 2;
                graphics.drawString(this.font, $$14, $$15, lineNumber * FlooSignBlockEntity.TEXT_LINE_HEIGHT - $$6, blackColour, false);
                if (lineNumber == this.line && cursorPos >= 0 && cursorShowing) {
                    $$16 = this.font.width($$14.substring(0, Math.max(Math.min(cursorPos, $$14.length()), 0)));
                    $$17 = $$16 - this.font.width($$14) / 2;
                    if (cursorPos >= $$14.length()) {
                        graphics.drawString(this.font, "_", $$17, $$7, blackColour, false);
                    }
                }
            }
        }

        for (lineNumber = 0; lineNumber < 4; ++lineNumber) {
            $$14 = this.signCandidateName.get(lineNumber);
            if ($$14 != null && lineNumber == this.line && cursorPos >= 0) {
                $$15 = this.font.width($$14.substring(0, Math.max(Math.min(cursorPos, $$14.length()), 0)));
                $$16 = $$15 - this.font.width($$14) / 2;
                if (cursorShowing && cursorPos < $$14.length()) {
                    graphics.fill($$16, $$7 - 1, $$16 + 1, $$7 + FlooSignBlockEntity.TEXT_LINE_HEIGHT, -16777216 | blackColour);
                }

                if (selectionPos != cursorPos) {
                    $$17 = Math.min(cursorPos, selectionPos);
                    int $$18 = Math.max(cursorPos, selectionPos);
                    int $$19 = this.font.width($$14.substring(0, $$17)) - this.font.width($$14) / 2;
                    int $$20 = this.font.width($$14.substring(0, $$18)) - this.font.width($$14) / 2;
                    int $$21 = Math.min($$19, $$20);
                    int $$22 = Math.max($$19, $$20);
                    graphics.fill(RenderType.guiTextHighlight(), $$21, $$7, $$22, $$7 + FlooSignBlockEntity.TEXT_LINE_HEIGHT, -16776961);
                }
            }
        }
    }

    public void handleResponse(boolean response) {
        if (response) {
            this.onClose();
        } else {
            this.setStatusAndCentre(REJECTED_STATUS);
        }
    }

    private void setStatusAndCentre(Component newStatus) {
        this.status.setMessage(newStatus);
        int width = this.font.width(newStatus.getVisualOrderText());
        this.status.setWidth(width);
        this.status.setX(this.width / 2 - width / 2);
    }
}
