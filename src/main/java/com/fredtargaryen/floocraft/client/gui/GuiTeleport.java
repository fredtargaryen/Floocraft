package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceListRequest;
import com.fredtargaryen.floocraft.network.messages.MessageTeleportEntity;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiTeleport extends GuiScreen
{
    /** The title string that is displayed in the top-centre of the screen. */
    private static final String screenTitle = "===Choose a destination===";
    private String status;

    //"Go!".
    private GuiButton goBtn;
    //"Cancel"
    private GuiButton cancelBtn;
    
    private HashMap<String, int[]> placeList;
	private List<Boolean> enabledList = new ArrayList<>();
	
	private boolean receivedLists;

    private PlaceList scrollWindow;
    
	private final int initX;
    private final int initY;
    private final int initZ;

    //Every object in here is a String, so just cast
    private String[] placeListKeySet;
	
    public GuiTeleport(int x, int y, int z)
    {
    	this.initX = x;
    	this.initY = y;
    	this.initZ = z;
        this.refresh();
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        GuiButton refreshButton = new GuiButton(-2, this.width - 100, 0, 98, 20, "Refresh");
        refreshButton.enabled = false;
        this.buttonList.add(this.goBtn = new GuiButton(-3, this.width / 2 - 100, this.height / 4 + 144, 98, 20, "Go!"));
        this.goBtn.enabled = false;
        this.buttonList.add(this.cancelBtn = new GuiButton(-1, this.width / 2 + 2, this.height / 4 + 144, 98, 20, "Cancel"));
        if (receivedLists)
        {
            refreshButton.enabled = true;
            this.scrollWindow = new PlaceList();
        }
        this.buttonList.add(refreshButton);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        ClientProxy proxy = (ClientProxy) FloocraftBase.proxy;
        Keyboard.enableRepeatEvents(false);
        proxy.overrideTicker.start();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        if (!this.receivedLists)
        {
            this.status = "Loading...";
        }
        else //if the lists were received...
        {
            //if they are empty...
            if (this.placeList.size() == 0) {
                this.status = "No places found";
            } else {
                this.status = "";
            }
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            //Cancel
            if (par1GuiButton.id == -1)
            {
                ((ClientProxy) FloocraftBase.proxy).overrideTicker.start();
                this.mc.displayGuiScreen(null);
            }
            //Refresh
            else if(par1GuiButton.id == -2)
            {
            	this.refresh();
            }
            //Go!
            else if(par1GuiButton.id == -3)
            {
                int initX = this.initX;
                int initY = this.initY;
                int initZ = this.initZ;
                int[] destCoords = this.placeList.get(this.placeListKeySet[this.scrollWindow.getSelectedElement()]);
                try
                {
                    if(!(initX == destCoords[0] && initY == destCoords[1] && initZ == destCoords[2]))
                    {
                        MessageTeleportEntity m = new MessageTeleportEntity();
                        m.initX = initX;
                        m.initY = initY;
                        m.initZ = initZ;
                        m.destX = destCoords[0];
                        m.destY = destCoords[1];
                        m.destZ = destCoords[2];
                        PacketHandler.INSTANCE.sendToServer(m);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                this.actionPerformed(GuiTeleport.this.cancelBtn);
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1)
        {
            this.actionPerformed(this.cancelBtn);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void drawScreen(int mousex, int mousey, float partialticks)
    {
        this.drawCenteredString(this.fontRenderer,
                this.status,
                this.width / 2,
                this.height / 4 + 48,
                13158600);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (this.width / 2), 0.0F, 50.0F);
        float f1 = 93.75F;
        GL11.glScalef(-f1, -f1, -f1);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glPopMatrix();
        if (this.scrollWindow != null) {
            this.scrollWindow.drawScreen(mousex, mousey, partialticks);
        }
        this.drawCenteredString(this.fontRenderer,
                screenTitle,
                this.width / 2,
                15,
                16777215);
        super.drawScreen(mousex, mousey, partialticks);
    }
    
    private void refresh()
    {
    	this.placeList = new HashMap<>();
    	this.enabledList = new ArrayList<>();
    	this.receivedLists = false;
    	this.initGui();
        PacketHandler.INSTANCE.sendToServer(new MessageFireplaceListRequest());
    }
    
    public void onMessageReceived(MessageFireplaceList m)
    {
        try
		{
        	this.placeList = m.placeList;
        	this.enabledList = m.enabledList;
            this.placeListKeySet = (String[]) this.placeList.keySet().toArray();
			this.receivedLists = true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
        this.initGui();
    }
    
    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void handleMouseInput()
    {
        try
        {
            if(this.scrollWindow != null)
            {
                this.scrollWindow.handleMouseInput();
            }
            super.handleMouseInput();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    class PlaceList extends GuiSlot
    {
        public PlaceList()
        {
            super(GuiTeleport.this.mc, GuiTeleport.this.width, GuiTeleport.this.height, 32, GuiTeleport.this.height - 65 + 4, 18);
            this.setShowSelectionBox(true);
        }

        @Override
        public int getListWidth()
        {
            return 380;
        }

        protected int getSize()
        {
            return GuiTeleport.this.placeList.size();
        }

        /**
         * The element in the slot that was clicked, boolean for whether it was double clicked or not
         */
        protected void elementClicked(int id, boolean isDoubleClick, int mousex, int mousey)
        {
            GuiTeleport.this.goBtn.enabled = GuiTeleport.this.enabledList.get(id);
        }

        /**
         * Returns true if the element passed in is currently selected
         */
        protected boolean isSelected(int id)
        {
            return id == this.selectedElement;
        }

        /**
         * Return the height of the content being scrolled
         */
        @Override
        protected int getContentHeight()
        {
            return this.getSize() * 18;
        }

        @Override
        protected void drawBackground(){}

        @Override
        protected void drawSlot(int id, int p_148126_2_, int p_148126_3_, int p_148126_4_, int p_148126_6_, int p_148126_7_, float idk)
        {
            GuiTeleport.this.drawCenteredString(GuiTeleport.this.fontRenderer, (String)GuiTeleport.this.placeListKeySet[id], this.width / 2, p_148126_3_ + 1, GuiTeleport.this.enabledList.get(id) ? 65280 : 16711680);
        }

        @Override
        public void drawScreen(int i, int j, float f)
        {
            super.drawScreen(i, j, f);
            this.flooverlayBackground(0, this.top);
            this.flooverlayBackground(this.bottom, this.height);
        }
        /**
         * Overlays the background to hide scrolled items
         */
        private void flooverlayBackground(int p_148136_1_, int p_148136_2_)
        {
            BufferBuilder wr = Tessellator.getInstance().getBuffer();
            GuiTeleport.this.mc.getTextureManager().bindTexture(DataReference.TP_BACKGROUND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            float f = 32.0F;
            wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            wr.pos((double)this.left, (double)p_148136_2_, 0.0D).tex(0.0D, (double)((float)p_148136_2_ / f)).endVertex();
            wr.pos((double)(this.left + this.width), (double)p_148136_2_, 0.0D).tex((double)((float)this.width / f), (double)((float)p_148136_2_ / f)).endVertex();
            wr.pos((double) (this.left + this.width), (double) p_148136_1_, 0.0D).tex((double) ((float) this.width / f), (double) ((float)p_148136_1_ / f)).endVertex();
            wr.pos((double) this.left, (double) p_148136_1_, 0.0D).tex(0.0D, (double) ((float)p_148136_1_ / f)).endVertex();
            Tessellator.getInstance().draw();
        }

        @Override
        protected void drawContainerBackground(Tessellator tessellator)
        {
        }

        public int getSelectedElement()
        {
            return this.selectedElement;
        }
    }
}
