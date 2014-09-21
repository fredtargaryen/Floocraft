package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceListRequest;
import com.fredtargaryen.floocraft.network.messages.MessageTeleportEntity;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
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

    /** "Done" button for the GUI. */
    private GuiButton doneBtn;
    
    private int firstButton;
    private int lastButton;
    
    private List<String>placenamelist = new ArrayList<String>();
	private List<Integer>xcoordlist = new ArrayList<Integer>();
	private List<Integer>ycoordlist = new ArrayList<Integer>();
	private List<Integer>zcoordlist = new ArrayList<Integer>();
	private List<Boolean>enabledlist = new ArrayList<Boolean>();
	
	private boolean receivedLists;
    
	private int initX;
	private int initY;
	private int initZ;
	
    public GuiTeleport(int x, int y, int z)
    {
    	this.initX = x;
    	this.initY = y;
    	this.initZ = z;
        this.firstButton = 0;
        this.lastButton = 5;
        this.refresh();
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        GuiButton refreshButton = new GuiButton(-4, this.width - 100, 0, 98, 20, "Refresh");
        refreshButton.enabled = false;
        this.buttonList.add(this.doneBtn = new GuiButton(-1, this.width / 2 - 100, this.height / 4 + 144, "Done"));
        if(receivedLists)
        {
        	refreshButton.enabled = true;
	        for(int i = firstButton; i < this.placenamelist.size() && i < lastButton; i++)
	        {
	        	GuiButton b = new GuiButton(i,this.width / 2 - 100, this.height / 4 + (24 * (i-firstButton)), this.placenamelist.get(i));
	        	b.enabled = enabledlist.get(i);
	        	this.buttonList.add(b);
	        }
	        GuiButton upB = new GuiScrollButton(-2, this.width / 2 + 104, this.height / 4, "^");
	        GuiButton dnB = new GuiScrollButton(-3, this.width / 2 + 104, this.height / 4 + 96, "V");
	        upB.enabled = this.placenamelist.size() > 5 && firstButton > 0;
	        dnB.enabled = this.placenamelist.size() > 5 && lastButton < this.placenamelist.size();
	        this.buttonList.add(upB);
	        this.buttonList.add(dnB);
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
            if (par1GuiButton.id == -1)
            {
                ((ClientProxy) FloocraftBase.proxy).overrideTicker.start();
                this.mc.displayGuiScreen(null);
            }
            else if(par1GuiButton.id == -2)
            {
            	this.firstButton--;
            	this.lastButton--;
            	initGui();
            }
            else if(par1GuiButton.id == -3)
            {
            	this.firstButton++;
            	this.lastButton++;
            	initGui();
            }
            else if(par1GuiButton.id == -4)
            {
            	this.refresh();
            }
            else
            {
            	try
            	{
            		MessageTeleportEntity m = new MessageTeleportEntity();
                    m.initX = this.initX;
                    m.initY = this.initY;
                    m.initZ = this.initZ;
            		m.destX = xcoordlist.get(par1GuiButton.id);
            		m.destY = ycoordlist.get(par1GuiButton.id);
            		m.destZ = zcoordlist.get(par1GuiButton.id);
                    if(!(m.initX == m.destX && m.initY == m.destY && m.initZ == m.destZ))
                    {
                        PacketHandler.INSTANCE.sendToServer(m);
                    }
                    this.actionPerformed(this.doneBtn);
            	}
            	catch(Exception e)
            	{
            		e.printStackTrace();
            	}
            	this.actionPerformed(this.doneBtn);
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
            this.actionPerformed(this.doneBtn);
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
}
