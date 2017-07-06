package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageApproveName;
import com.fredtargaryen.floocraft.network.messages.MessageTileEntityFireplaceFunction;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
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
        this.decorButton = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, 98, 20, "Use as decoration");
        this.buttonList.add(this.decorButton);
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, this.height / 4 + 120, 98, 20,   "Connect to Network"));
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        NetHandlerPlayClient netclienthandler = this.mc.getConnection();
        if (netclienthandler != null)
        {
            netclienthandler.sendPacket(new CPacketUpdateSign(this.fireplaceTE.getPos(), this.fireplaceTE.signText));
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
            BlockPos pos = this.fireplaceTE.getPos();
            m.x = pos.getX();
            m.y = pos.getY();
            m.z = pos.getZ();
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
    protected void keyTyped(char typedChar, int keyCode)
    {
        if (keyCode == 200)
        {
            this.editLine = this.editLine - 1 & 3;
        }

        if (keyCode == 208 || keyCode == 28 || keyCode == 156)
        {
            this.editLine = this.editLine + 1 & 3;
        }

        String s = this.fireplaceTE.signText[this.editLine].getUnformattedText();

        if (keyCode == 14 && !s.isEmpty())
        {
            s = s.substring(0, s.length() - 1);
        }

        if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.fontRenderer.getStringWidth(s + typedChar) <= 90)
        {
            s = s + typedChar;
        }

        this.fireplaceTE.signText[this.editLine] = new TextComponentString(s);

        if (keyCode == 1)
        {
            this.actionPerformed(this.decorButton);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
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

        TileEntityRendererDispatcher.instance.render(this.fireplaceTE, -0.5D, -0.75D, -0.5D, 0.0F);
        this.fireplaceTE.lineBeingEdited = -1;
        GL11.glPopMatrix();
        super.drawScreen(par1, par2, par3);
    }

    private static String nameAsLine(ITextComponent[] original)
    {
        return original[0].getUnformattedComponentText()+" "
                +original[1].getUnformattedComponentText()+" "
                +original[2].getUnformattedComponentText()+" "
                +original[3].getUnformattedComponentText();
    }

    public void dealWithAnswer(boolean answer)
    {
        if(answer)
        {
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