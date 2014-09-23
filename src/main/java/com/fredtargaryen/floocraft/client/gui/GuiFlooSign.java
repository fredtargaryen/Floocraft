package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageApproveName;
import com.fredtargaryen.floocraft.network.messages.MessageTileEntityFireplaceFunction;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiFlooSign extends GuiScreen
{
    private String sameNameError = "";

    /** Reference to the sign object. */
    public TileEntityFireplace fireplaceTE;

    /** Counts the number of screen updates. */
    private int updateCounter;

    /** The number of the line that is being edited. */
    private int editLine;

    public GuiFlooSign(TileEntityFireplace par1TileEntityFireplace)
    {
        this.fireplaceTE = par1TileEntityFireplace;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, 98, 20, "Use as decoration"));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, this.height / 4 + 120, 98, 20,   "Connect to Network"));
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        NetHandlerPlayClient netclienthandler = this.mc.getNetHandler();
        if (netclienthandler != null)
        {
            netclienthandler.addToSendQueue(new C12PacketUpdateSign(this.fireplaceTE.xCoord, this.fireplaceTE.yCoord, this.fireplaceTE.zCoord, this.fireplaceTE.signText));
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        ++this.updateCounter;
    }
    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            MessageTileEntityFireplaceFunction m = new MessageTileEntityFireplaceFunction();
            m.x = this.fireplaceTE.xCoord;
            m.y = this.fireplaceTE.yCoord;
            m.z = this.fireplaceTE.zCoord;
            m.isConnected = false;
            PacketHandler.INSTANCE.sendToServer(m);
            switch(par1GuiButton.id)
            {
                case 0:
                {
                    this.mc.displayGuiScreen(null);
                    break;
                }
                case 1:
                {
                    MessageApproveName man = new MessageApproveName();
                    man.name = nameAsLine(this.fireplaceTE.signText);
                    PacketHandler.INSTANCE.sendToServer(man);
                    break;
                }
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 200)
        {
            this.editLine = this.editLine - 1 & 3;
        }

        if (par2 == 208 || par2 == 28 || par2 == 156)
        {
            this.editLine = this.editLine + 1 & 3;
        }
        if (par2 == 14 && this.fireplaceTE.signText[this.editLine].length() > 0)
        {
            this.fireplaceTE.signText[this.editLine] = this.fireplaceTE.signText[this.editLine].substring(0, this.fireplaceTE.signText[this.editLine].length() - 1);
        }

        if (ChatAllowedCharacters.isAllowedCharacter(par1) && this.fireplaceTE.signText[this.editLine].length() < 15)
        {
            this.fireplaceTE.signText[this.editLine] = this.fireplaceTE.signText[this.editLine] + par1;
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
    	super.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj,
                "===Floo Network Setup Wizard===",
        		this.width / 2,
        		40,
        		16777215);
        this.drawCenteredString(this.fontRendererObj,
            	this.sameNameError,
            	this.width / 2,
            	this.height / 4 + 100,
            	16777215);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.width / 2), 0.0F, 50.0F);
        float f1 = 93.75F;
        GL11.glScalef(-f1, -f1, -f1);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);

            int k = this.fireplaceTE.getBlockMetadata();
            float f3 = 0.0F;

            if (k == 2)
            {
                f3 = 180.0F;
            }

            if (k == 4)
            {
                f3 = 90.0F;
            }

            if (k == 5)
            {
                f3 = -90.0F;
            }

            GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -1.0625F, 0.0F);

        if (this.updateCounter / 6 % 2 == 0)
        {
            this.fireplaceTE.lineBeingEdited = this.editLine;
        }

        TileEntityRendererDispatcher.instance.renderTileEntityAt(this.fireplaceTE, -0.5D, -0.75D, -0.5D, 0.0F);
        this.fireplaceTE.lineBeingEdited = -1;
        GL11.glPopMatrix();
        super.drawScreen(par1, par2, par3);
    }

    private static String nameAsLine(String[] original)
    {
        return original[0]+" "+original[1]+" "+original[2]+" "+original[3];
    }

    public void dealWithAnswer(boolean answer)
    {
        if(answer)
        {
            this.sameNameError = "";
            MessageTileEntityFireplaceFunction m = new MessageTileEntityFireplaceFunction();
            m.x = this.fireplaceTE.xCoord;
            m.y = this.fireplaceTE.yCoord;
            m.z = this.fireplaceTE.zCoord;
            m.isConnected = true;
            PacketHandler.INSTANCE.sendToServer(m);
            this.fireplaceTE.markDirty();
            this.fireplaceTE.addLocation(this.fireplaceTE.xCoord,
                    this.fireplaceTE.yCoord,
                    this.fireplaceTE.zCoord,
                    nameAsLine(this.fireplaceTE.signText),
                    this.fireplaceTE.getWorldObj());
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