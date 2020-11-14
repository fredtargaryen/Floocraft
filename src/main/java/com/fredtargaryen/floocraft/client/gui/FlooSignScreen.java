package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.MessageApproveFireplace;
import com.fredtargaryen.floocraft.tileentity.FireplaceTileEntity;
import com.fredtargaryen.floocraft.tileentity.renderer.TileEntityFlooSignRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlooSignScreen extends Screen {
    private String namingStatus = "";

    /** Reference to the sign object. */
    private final FireplaceTileEntity fireplaceTE;

    /** Counts the number of screen updates. */
    private int updateCounter;

    /** The number of the line that is being edited. */
    public int editLine;
    private TextInputUtil textInputUtil;

    private Button decorButton;

    private final TileEntityFlooSignRenderer.FlooSignModel model = new TileEntityFlooSignRenderer.FlooSignModel();

    private boolean sendDefaultMessageOnClose;

    private static final ResourceLocation floosigntexloc = new ResourceLocation(DataReference.MODID, "textures/blocks/floosign.png");

    private static final TranslationTextComponent TITLE = new TranslationTextComponent("gui.floosign.title");
    private static final TranslationTextComponent DECOR_BUTTON = new TranslationTextComponent("gui.floosign.decoration");
    private static final TranslationTextComponent CONNECT_BUTTON = new TranslationTextComponent("gui.floosign.connect");

    public FlooSignScreen(FireplaceTileEntity par1FireplaceTileEntity) {
        super(TITLE);
        this.fireplaceTE = par1FireplaceTileEntity;
        this.sendDefaultMessageOnClose = true;
    }

    private void sendApprovalMessage(boolean attemptingToConnect) {
        MessageApproveFireplace maf = new MessageApproveFireplace();
        BlockPos pos = this.fireplaceTE.getPos();
        maf.x = pos.getX();
        maf.y = pos.getY();
        maf.z = pos.getZ();
        maf.attemptingToConnect = attemptingToConnect;
        maf.name = this.fireplaceTE.signText;
        MessageHandler.INSTANCE.sendToServer(maf);
        this.namingStatus = I18n.format("gui.floosign.approvalwait");
        this.sendDefaultMessageOnClose = false;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void init() {
        this.buttons.clear();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.decorButton = new Button(this.width / 2 - 100, this.height / 4 + 120, 98, 20, DECOR_BUTTON, button -> {
            FlooSignScreen.this.minecraft.displayGuiScreen(null);
        });
        this.addButton(this.decorButton);
        this.addButton(new Button(this.width / 2 + 2, this.height / 4 + 120, 98, 20, CONNECT_BUTTON, button -> {
            FlooSignScreen.this.sendApprovalMessage(true);
        }));
        this.textInputUtil = new TextInputUtil(
                () -> this.fireplaceTE.getString(this.editLine),
                stringToSet -> this.fireplaceTE.setString(this.editLine, stringToSet),
                TextInputUtil.getClipboardTextSupplier(this.minecraft),
                TextInputUtil.getClipboardTextSetter(this.minecraft),
                stringToLimit -> this.minecraft.fontRenderer.getStringWidth(stringToLimit) <= 90);
    }

    /**
     * Called when the screen is unloaded via the Esc button. Used to disable keyboard repeat events
     */
    @Override
    public void onClose() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        if(this.sendDefaultMessageOnClose) {
            FlooSignScreen.this.sendApprovalMessage(false);
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        ++this.updateCounter;
    }

    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        this.textInputUtil.putChar(p_charTyped_1_);
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.editLine = this.editLine - 1 & 3;
            this.textInputUtil.moveCursorToEnd();
            return true;
        } else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
            return this.textInputUtil.specialKeyPressed(keyCode) ? true : super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.editLine = this.editLine + 1 & 3;
            this.textInputUtil.moveCursorToEnd();
            return true;
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderHelper.setupGuiFlatDiffuseLighting();
        this.renderBackground(stack);
        drawCenteredString(
                stack,
                this.font,
                TITLE,
        		this.width / 2,
        		40,
        		16777215);
        drawCenteredString(
                stack,
                this.font,
            	this.namingStatus,
            	this.width / 2,
            	this.height / 4 + 100,
            	16777215);
        //Draw the sign
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.push();
        //Put the sign in the right place on the screen
        matrixstack.translate((double)(this.width / 2), 0.0D, 50.0D);
        float scale = 93.75F;
        matrixstack.scale(scale, -scale, scale);
        matrixstack.translate(0.0D, -1.625D, 0.0D);
        boolean showCursorThisFrame = this.updateCounter / 6 % 2 == 0;

        //Draw the board
        float scale2 = 0.6666667F;
        matrixstack.push();
        matrixstack.scale(scale2, -scale2, -scale2);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = this.minecraft.getRenderTypeBuffers().getBufferSource();
        RenderMaterial material = TileEntityFlooSignRenderer.FlooSignModel.MATERIAL;
        IVertexBuilder ivertexbuilder = material.getBuffer(irendertypebuffer$impl, this.model::getRenderType);
        this.model.board.render(matrixstack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);
        matrixstack.pop();

        //Transformations for text
        float f2 = 0.010416667F;
        matrixstack.translate(0.0D, (double)0.33333334F, (double)0.046666667F);
        matrixstack.scale(f2, -f2, f2);
        String[] astring = new String[4];
        for(int j = 0; j < astring.length; ++j) {
            astring[j] = this.fireplaceTE.getString(j);
        }

        Matrix4f matrix4f = matrixstack.getLast().getMatrix();
        int k = this.textInputUtil.getStartIndex();
        int l = this.textInputUtil.getEndIndex();
        int i1 = this.minecraft.fontRenderer.getBidiFlag() ? -1 : 1;
        int j1 = this.editLine * 10 - this.fireplaceTE.signText.length * 5;

        for(int k1 = 0; k1 < astring.length; ++k1) {
            String s = astring[k1];
            if (s != null) {
                float f3 = (float)(-this.minecraft.fontRenderer.getStringWidth(s) / 2);
                this.minecraft.fontRenderer.renderString(s, f3, (float)(k1 * 10 - this.fireplaceTE.signText.length * 5), 0, false, matrix4f, irendertypebuffer$impl, false, 0, 15728880);
                if (k1 == this.editLine && k >= 0 && showCursorThisFrame) {
                    int l1 = this.minecraft.fontRenderer.getStringWidth(s.substring(0, Math.max(Math.min(k, s.length()), 0)));
                    int i2 = (l1 - this.minecraft.fontRenderer.getStringWidth(s) / 2) * i1;
                    if (k >= s.length()) {
                        this.minecraft.fontRenderer.renderString("_", (float)i2, (float)j1, 0, false, matrix4f, irendertypebuffer$impl, false, 0, 15728880);
                    }
                }
            }
        }

        irendertypebuffer$impl.finish();

        for(int k3 = 0; k3 < astring.length; ++k3) {
            String s1 = astring[k3];
            if (s1 != null && k3 == this.editLine && k >= 0) {
                int l3 = this.minecraft.fontRenderer.getStringWidth(s1.substring(0, Math.max(Math.min(k, s1.length()), 0)));
                int i4 = (l3 - this.minecraft.fontRenderer.getStringWidth(s1) / 2) * i1;
                if (showCursorThisFrame && k < s1.length()) {
                    fill(stack, i4, j1 - 1, i4 + 1, j1 + 9, -16777216);
                }

                if (l != k) {
                    int j4 = Math.min(k, l);
                    int j2 = Math.max(k, l);
                    int k2 = (this.minecraft.fontRenderer.getStringWidth(s1.substring(0, j4)) - this.minecraft.fontRenderer.getStringWidth(s1) / 2) * i1;
                    int l2 = (this.minecraft.fontRenderer.getStringWidth(s1.substring(0, j2)) - this.minecraft.fontRenderer.getStringWidth(s1) / 2) * i1;
                    int i3 = Math.min(k2, l2);
                    int j3 = Math.max(k2, l2);
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuffer();
                    RenderSystem.disableTexture();
                    RenderSystem.enableColorLogicOp();
                    RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    bufferbuilder.pos(matrix4f, (float)i3, (float)(j1 + 9), 0.0F).color(0, 0, 255, 255).endVertex();
                    bufferbuilder.pos(matrix4f, (float)j3, (float)(j1 + 9), 0.0F).color(0, 0, 255, 255).endVertex();
                    bufferbuilder.pos(matrix4f, (float)j3, (float)j1, 0.0F).color(0, 0, 255, 255).endVertex();
                    bufferbuilder.pos(matrix4f, (float)i3, (float)j1, 0.0F).color(0, 0, 255, 255).endVertex();
                    bufferbuilder.finishDrawing();
                    WorldVertexBufferUploader.draw(bufferbuilder);
                    RenderSystem.disableColorLogicOp();
                    RenderSystem.enableTexture();
                }
            }
        }

        matrixstack.pop();
        RenderHelper.setupGui3DDiffuseLighting();
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    public void dealWithAnswer(boolean answer) {
        if(answer) {
            //Either the sign is for decoration, or it's for connecting and the name is valid
            this.namingStatus = "";
            this.fireplaceTE.markDirty();
            this.minecraft.displayGuiScreen(null);
        }
        else {
            //The sign is for connecting but the name has already been used.
            this.namingStatus = I18n.format("gui.floosign.nameinuse");
            this.sendDefaultMessageOnClose = true;
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}