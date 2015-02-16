package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceListRequest;
import com.fredtargaryen.floocraft.network.messages.MessageTeleportEntity;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import com.fredtargaryen.floocraft.DataReference;
import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiTeleport extends GuiScreen
{
    /** The title string that is displayed in the top-centre of the screen. */
    protected String screenTitle = "===Choose a destination===";
    private String status;

    //"Go!" button for the GUI.
    private GuiButton goBtn;
    //"Cancel"
    private GuiButton cancelBtn;
    
    private List<String> placenamelist = new ArrayList<String>();
	private List<Integer> xcoordlist = new ArrayList<Integer>();
	private List<Integer> ycoordlist = new ArrayList<Integer>();
	private List<Integer> zcoordlist = new ArrayList<Integer>();
	private List<Boolean> enabledlist = new ArrayList<Boolean>();
	
	private boolean receivedLists;

    private PlaceList scrollWindow;
    
	private int initX, initY, initZ, idSelected;
	
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
        if(receivedLists)
        {
        	refreshButton.enabled = true;
            if(this.placenamelist.size() > 0)
            {
                this.scrollWindow = new PlaceList();
            }
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
    	if(!this.receivedLists)
        {
        	this.status = "Loading...";
        }
        else //if the lists were received...
        {
        	//if they are empty...
        	if(this.placenamelist.size() == 0)
        	{
        		this.status = "No places found";
        	}
        	else
        	{
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
            //Go! (id -3)
            else
            {
                int initX = this.initX;
                int initY = this.initY;
                int initZ = this.initZ;
                int destX = xcoordlist.get(this.idSelected);
                int destY = ycoordlist.get(this.idSelected);
                int destZ = zcoordlist.get(this.idSelected);
                try
                {
                    if(!(initX == destX && initY == destY && initZ == destZ))
                    {
                        MessageTeleportEntity m = new MessageTeleportEntity();
                        m.initX = initX;
                        m.initY = initY;
                        m.initZ = initZ;
                        m.destX = destX;
                        m.destY = destY;
                        m.destZ = destZ;
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
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj,
        		this.screenTitle,
        		this.width / 2,
        		40,
        		16777215);
        this.drawCenteredString(this.fontRendererObj,
        		this.status,
        		this.width / 2,
        		this.height / 4 + 48,
        		16777215);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.width / 2), 0.0F, 50.0F);
        float f1 = 93.75F;
        GL11.glScalef(-f1, -f1, -f1);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glPopMatrix();
        if(this.scrollWindow != null) {
            this.scrollWindow.drawScreen(par1, par2, par3);
        }
        super.drawScreen(par1, par2, par3);
    }
    
    private void refresh()
    {
    	this.placenamelist.clear();
    	this.xcoordlist.clear();
    	this.ycoordlist.clear();
    	this.zcoordlist.clear();
    	this.enabledlist.clear();
    	this.receivedLists = false;
    	this.initGui();
        PacketHandler.INSTANCE.sendToServer(new MessageFireplaceListRequest());
    }
    
    public void onMessageReceived(MessageFireplaceList m)
    {
        try
		{
        	this.placenamelist = m.placenamelist;
        	this.xcoordlist = m.xcoordlist;
        	this.ycoordlist = m.ycoordlist;
        	this.zcoordlist = m.zcoordlist;
        	this.enabledlist = m.enabledlist;
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

    @SideOnly(Side.CLIENT)
    class PlaceList extends GuiSlot
    {
        private final java.util.List l = Lists.newArrayList();
        private static final String __OBFID = "CL_00000699";

        public PlaceList()
        {
            super(GuiTeleport.this.mc, GuiTeleport.this.width, GuiTeleport.this.height, 32, GuiTeleport.this.height - 65 + 4, 18);
            this.setShowSelectionBox(true);
        }

        protected int getSize()
        {
            return GuiTeleport.this.placenamelist.size();
        }

        /**
         * The element in the slot that was clicked, boolean for whether it was double clicked or not
         */
        protected void elementClicked(int id, boolean p_148144_2_, int p_148144_3_, int p_148144_4_)
        {
            GuiTeleport.this.idSelected = id;
            GuiTeleport.this.goBtn.enabled = true;
        }

        /**
         * Returns true if the element passed in is currently selected
         */
        protected boolean isSelected(int p_148131_1_)
        {
            return false;
        }

        /**
         * Return the height of the content being scrolled
         */
        protected int getContentHeight()
        {
            return this.getSize() * 18;
        }

        protected void drawBackground(){}

        protected void drawSlot(int id, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator p_148126_5_, int p_148126_6_, int p_148126_7_)
        {
            GuiTeleport.this.drawCenteredString(GuiTeleport.this.fontRendererObj, GuiTeleport.this.placenamelist.get(id), this.width / 2, p_148126_3_ + 1, 16777215);
        }
    }
}
