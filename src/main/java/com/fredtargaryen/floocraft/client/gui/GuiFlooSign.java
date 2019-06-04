package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.block.BlockFlooSign;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageApproveName;
import com.fredtargaryen.floocraft.network.messages.MessageTileEntityFireplaceFunction;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class GuiFlooSign extends GuiScreen
{
    private String sameNameError = "";

    /** Reference to the sign object. */
    private final TileEntityFireplace fireplaceTE;

    /** Counts the number of screen updates. */
    private int updateCounter;

    /** The number of the line that is being edited. */
    private int editLine;

    private GuiButton decorButton;

    private static final ResourceLocation floosigntexloc = new ResourceLocation(DataReference.MODID, "textures/blocks/floosign.png");

    public GuiFlooSign(TileEntityFireplace par1TileEntityFireplace)
    {
        this.fireplaceTE = par1TileEntityFireplace;
    }

    private void sendDisconnectedMessage() {
        MessageTileEntityFireplaceFunction m = new MessageTileEntityFireplaceFunction();
        BlockPos pos = this.fireplaceTE.getPos();
        m.x = pos.getX();
        m.y = pos.getY();
        m.z = pos.getZ();
        m.isConnected = false;
        PacketHandler.INSTANCE.sendToServer(m);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        this.buttons.clear();
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.decorButton = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, 98, 20, "Use as decoration") {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                GuiFlooSign.this.sendDisconnectedMessage();
                GuiFlooSign.this.mc.displayGuiScreen(null);
            }
        };
        this.addButton(this.decorButton);
        this.addButton(new GuiButton(1, this.width / 2 + 2, this.height / 4 + 120, 98, 20,   "Connect to Network") {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                GuiFlooSign.this.sendDisconnectedMessage();
                MessageApproveName man = new MessageApproveName(nameAsLine(GuiFlooSign.this.fireplaceTE.signText));
                PacketHandler.INSTANCE.sendToServer(man);
            }
        });
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
        this.mc.keyboardListener.enableRepeatEvents(false);
        NetHandlerPlayClient netclienthandler = this.mc.getConnection();
        if (netclienthandler != null) {
            netclienthandler.sendPacket(new CPacketUpdateSign(this.fireplaceTE.getPos(),
                    this.fireplaceTE.signText[0],
                    this.fireplaceTE.signText[1],
                    this.fireplaceTE.signText[2],
                    this.fireplaceTE.signText[3]));
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
        String s = this.fireplaceTE.func_212366_a(this.editLine).getString();
        if (SharedConstants.isAllowedCharacter(p_charTyped_1_) && this.fontRenderer.getStringWidth(s + p_charTyped_1_) <= 90) {
            s = s + p_charTyped_1_;
        }

        this.fireplaceTE.func_212365_a(this.editLine, new TextComponentString(s));
        return true;
    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == 265) {
            this.editLine = this.editLine - 1 & 3;
            return true;
        } else if (p_keyPressed_1_ != 264 && p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
            if (p_keyPressed_1_ == 259) {
                String s = this.fireplaceTE.func_212366_a(this.editLine).getString();
                if (!s.isEmpty()) {
                    s = s.substring(0, s.length() - 1);
                    this.fireplaceTE.func_212365_a(this.editLine, new TextComponentString(s));
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
    	super.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer,
                "===Floo Network Setup Wizard===",
        		this.width / 2,
        		40,
        		16777215);
        this.drawCenteredString(this.fontRenderer,
            	this.sameNameError,
            	this.width / 2,
            	this.height / 4 + 100,
            	16777215);
        //Draw the sign
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bb = tessellator.getBuffer();
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(DataReference.MODID, "textures/blocks/floosign.png"));
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

            EnumFacing k = this.fireplaceTE.getBlockState().get(BlockFlooSign.FACING);
            float f3 = 0.0F;

            if (k == EnumFacing.NORTH) {
                f3 = 180.0F;
            }

            if (k == EnumFacing.WEST) {
                f3 = 90.0F;
            }

            if (k == EnumFacing.EAST) {
                f3 = -90.0F;
            }

            GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -1.0625F, 0.0F);

        if (this.updateCounter / 6 % 2 == 0) {
            this.fireplaceTE.lineBeingEdited = this.editLine;
        }

        TileEntityRendererDispatcher.instance.render(this.fireplaceTE, -0.5D, -0.75D, -0.5D, 0.0F);
        this.fireplaceTE.lineBeingEdited = -1;
        GL11.glPopMatrix();
        super.render(mouseX, mouseY, partialTicks);
    }

    private static String nameAsLine(ITextComponent[] original) {
        return original[0].getUnformattedComponentText()+" "
                +original[1].getUnformattedComponentText()+" "
                +original[2].getUnformattedComponentText()+" "
                +original[3].getUnformattedComponentText();
    }

    public void dealWithAnswer(boolean answer) {
        if(answer) {
            this.sameNameError = "";
            //Tells the server that this sign should be connected
            MessageTileEntityFireplaceFunction m = new MessageTileEntityFireplaceFunction();
            BlockPos pos = this.fireplaceTE.getPos();
            m.x = pos.getX();
            m.y = pos.getY();
            m.z = pos.getZ();
            m.isConnected = true;
            PacketHandler.INSTANCE.sendToServer(m);
            //Adds the location to the server
            this.fireplaceTE.addLocation(pos, nameAsLine(this.fireplaceTE.signText), this.fireplaceTE.getWorld());
            this.fireplaceTE.markDirty();
            this.mc.displayGuiScreen(null);
        }
        else
        {
            this.sameNameError = "There is already a fireplace with this name";
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}