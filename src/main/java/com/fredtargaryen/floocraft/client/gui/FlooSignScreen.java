package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.block.FlooSignBlock;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.MessageApproveFireplace;
import com.fredtargaryen.floocraft.tileentity.FireplaceTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class FlooSignScreen extends Screen {
    private String sameNameError = "";

    /** Reference to the sign object. */
    private final FireplaceTileEntity fireplaceTE;

    /** Counts the number of screen updates. */
    private int updateCounter;

    /** The number of the line that is being edited. */
    public int editLine;

    private Button decorButton;

    private static final ResourceLocation floosigntexloc = new ResourceLocation(DataReference.MODID, "textures/blocks/floosign.png");

    public FlooSignScreen(FireplaceTileEntity par1FireplaceTileEntity) {
        super(new StringTextComponent(I18n.format("gui.floosign.title")));
        this.fireplaceTE = par1FireplaceTileEntity;
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
        this.sameNameError = I18n.format("Waiting for approval...");
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void init() {
        this.buttons.clear();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.decorButton = new Button(this.width / 2 - 100, this.height / 4 + 120, 98, 20, I18n.format("gui.floosign.decoration"), button -> {
            FlooSignScreen.this.sendApprovalMessage(false);
            FlooSignScreen.this.minecraft.displayGuiScreen(null);
        });
        this.addButton(this.decorButton);
        this.addButton(new Button(this.width / 2 + 2, this.height / 4 + 120, 98, 20, I18n.format("gui.floosign.connect"), button -> {
            FlooSignScreen.this.sendApprovalMessage(true);
        }));
    }

    /**
     * Called when the screen is unloaded via the Esc button. Used to disable keyboard repeat events
     */
    @Override
    public void onClose() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        this.decorButton.onPress();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void tick()
    {
        ++this.updateCounter;
    }

    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        String s = this.fireplaceTE.getString(this.editLine);
        if (SharedConstants.isAllowedCharacter(p_charTyped_1_) && this.font.getStringWidth(s + p_charTyped_1_) <= 90) {
            s = s + p_charTyped_1_;
        }

        this.fireplaceTE.setString(this.editLine, s);
        return true;
    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == 265) {
            this.editLine = this.editLine - 1 & 3;
            return true;
        } else if (p_keyPressed_1_ != 264 && p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
            if (p_keyPressed_1_ == 259) {
                String s = this.fireplaceTE.getString(this.editLine);
                if (!s.isEmpty()) {
                    s = s.substring(0, s.length() - 1);
                    this.fireplaceTE.setString(this.editLine, s);
                }

                return true;
            } else {
                return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
            }
        } else {
            this.editLine = this.editLine + 1 & 3;
            return true;
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
    	super.renderBackground();
        this.drawCenteredString(this.font,
                I18n.format("gui.floosign.title"),
        		this.width / 2,
        		40,
        		16777215);
        this.drawCenteredString(this.font,
            	this.sameNameError,
            	this.width / 2,
            	this.height / 4 + 100,
            	16777215);
        //Draw the sign
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bb = tessellator.getBuffer();
        Minecraft.getInstance().getTextureManager().bindTexture(floosigntexloc);
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        int centreX = this.width / 2;
        int centreY = 121;
        bb.pos(centreX + 50, centreY + 25, 0.0).tex(0.375, 0.21875).endVertex();
        bb.pos(centreX + 50, centreY - 25, 0.0).tex(0.375, 0.03125).endVertex();
        bb.pos(centreX - 50, centreY - 25, 0.0).tex(0.03125, 0.03125).endVertex();
        bb.pos(centreX - 50, centreY + 25, 0.0).tex(0.03125, 0.21875).endVertex();
        tessellator.draw();

        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.width / 2), 0.0F, 50.0F);
        float f1 = 93.75F;
        GL11.glScalef(-f1, -f1, -f1);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);

            Direction k = this.fireplaceTE.getBlockState().get(FlooSignBlock.FACING);
            float f3 = 0.0F;

            if (k == Direction.NORTH) {
                f3 = 180.0F;
            }

            if (k == Direction.WEST) {
                f3 = 90.0F;
            }

            if (k == Direction.EAST) {
                f3 = -90.0F;
            }

            GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -1.0625F, 0.0F);

        if (this.updateCounter / 6 % 2 == 0) {
            this.fireplaceTE.setLineBeingEdited(this.editLine);
        }

        TileEntityRendererDispatcher.instance.render(this.fireplaceTE, -0.5D, -0.75D, -0.5D, 0.0F);
        this.fireplaceTE.setLineBeingEdited(-1);
        GL11.glPopMatrix();
        super.render(mouseX, mouseY, partialTicks);
    }

    public void dealWithAnswer(boolean answer) {
        if(answer) {
            //Either the sign is for decoration, or it's for connecting and the name is valid
            this.sameNameError = "";
            this.fireplaceTE.markDirty();
            this.minecraft.displayGuiScreen(null);
        }
        else {
            //The sign is for connecting but the name has already been used.
            this.sameNameError = I18n.format("gui.floosign.nameinuse");
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