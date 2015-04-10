package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceListRequest;
import com.fredtargaryen.floocraft.network.messages.MessageTeleportEntity;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
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
    //private boolean isPeeking;

    //Peek
    //private GuiButton peekBtn;

    //"Go!".
    private GuiButton goBtn;
    //"Cancel"
    private GuiButton cancelBtn;

    //"Done" (when peeking)
    //private GuiButton doneBtn;
    
    private List<String> placenamelist = new ArrayList<String>();
	private List<Integer> xcoordlist = new ArrayList<Integer>();
	private List<Integer> ycoordlist = new ArrayList<Integer>();
	private List<Integer> zcoordlist = new ArrayList<Integer>();
	private List<Boolean> enabledlist = new ArrayList<Boolean>();
	
	private boolean receivedLists;

    private EntityLivingBase prevCameraman;

    private PlaceList scrollWindow;
    
	private int initX, initY, initZ, idSelected;
	
    public GuiTeleport(int x, int y, int z)
    {
    	this.initX = x;
    	this.initY = y;
    	this.initZ = z;
        this.idSelected = -4;
        //this.isPeeking = false;
        this.refresh();
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        //if(this.isPeeking)
        //{
            //this.buttonList.clear();
            //Keyboard.enableRepeatEvents(true);
            //this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 144, 200, 20, "Done");
            //this.buttonList.add(doneBtn);
        //}
        //else {
            this.buttonList.clear();
            Keyboard.enableRepeatEvents(true);
            GuiButton refreshButton = new GuiButton(-2, this.width - 100, 0, 98, 20, "Refresh");
            refreshButton.enabled = false;
            //this.buttonList.add(this.peekBtn = new GuiButton(-4, this.width / 2 - 100, this.height / 4 + 144, 64, 20, "Peek"));
            //this.peekBtn.enabled = false;
            //this.buttonList.add(this.goBtn = new GuiButton(-3, this.width / 2 - 32, this.height / 4 + 144, 64, 20, "Go!"));
        this.buttonList.add(this.goBtn = new GuiButton(-3, this.width / 2 - 100, this.height / 4 + 144, 98, 20, "Go!"));
            this.goBtn.enabled = false;
            //this.buttonList.add(this.cancelBtn = new GuiButton(-1, this.width / 2 + 36, this.height / 4 + 144, 64, 20, "Cancel"));
        this.buttonList.add(this.cancelBtn = new GuiButton(-1, this.width / 2 + 2, this.height / 4 + 144, 98, 20, "Cancel"));
            if (receivedLists) {
                refreshButton.enabled = true;
                if (this.placenamelist.size() > 0) {
                    this.scrollWindow = new PlaceList();
                }
            }
            this.buttonList.add(refreshButton);
        }
    //}

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
    public void updateScreen() {
        //if (this.isPeeking) {
            //this.status = "";
        //} else {
            super.updateScreen();
            if (!this.receivedLists) {
                this.status = "Loading...";
            } else //if the lists were received...
            {
                //if they are empty...
                if (this.placenamelist.size() == 0) {
                    this.status = "No places found";
                } else {
                    this.status = "";
                }
            }
        }
    //}

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
       //if(this.isPeeking){
           // if(par1GuiButton.enabled){
                //if(par1GuiButton.id == 0)
                //{
                   //this.isPeeking = false;
                   // this.mc.renderViewEntity = this.prevCameraman;
                   // this.refresh();
                //}
            //}
        //}else{
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
            //Peek (id -4)
            //else
            //{
                //int initX = this.initX;
//                int initY = this.initY;
//                int initZ = this.initZ;
//                int destX = xcoordlist.get(this.idSelected);
//                int destY = ycoordlist.get(this.idSelected);
//                int destZ = zcoordlist.get(this.idSelected);
//                if(!(initX == destX && initY == destY && initZ == destZ))
//                {
//                    this.isPeeking = true;
//                    this.prevCameraman = this.mc.thePlayer;
//                    EntityFaceInFire efif = new EntityFaceInFire(this.mc.theWorld);
//                    //Change these lines later
//                    efif.prevRotationPitch = this.mc.thePlayer.prevRotationPitch;
//                    efif.prevRotationYaw = this.mc.thePlayer.prevRotationYaw;
//                    efif.rotationYawHead = this.mc.thePlayer.rotationYawHead;
//
//                    efif.prevPosX = destX;
//                    efif.prevPosY = destY;
//                    efif.prevPosZ = destZ;
//                    efif.setPositionAndUpdate((double)destX + 0.5, (double)destY + 0.5, (double)destZ + 0.5);
//                    this.mc.renderViewEntity = efif;
//                }
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
        //if(!this.isPeeking) {
            this.drawCenteredString(this.fontRendererObj,
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
                this.scrollWindow.drawScreen(par1, par2, par3);
            }
            this.drawCenteredString(this.fontRendererObj,
                    this.screenTitle,
                    this.width / 2,
                    15,
                    16777215);
        //}
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
            GuiTeleport.this.goBtn.enabled = GuiTeleport.this.enabledlist.get(GuiTeleport.this.idSelected);
            //GuiTeleport.this.goBtn.enabled = GuiTeleport.this.peekBtn.enabled = GuiTeleport.this.enabledlist.get(GuiTeleport.this.idSelected);
        }

        /**
         * Returns true if the element passed in is currently selected
         */
        protected boolean isSelected(int p_148131_1_)
        {
            return p_148131_1_ == GuiTeleport.this.idSelected && GuiTeleport.this.enabledlist.get(idSelected);
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
        protected void drawSlot(int id, int p_148126_2_, int p_148126_3_, int p_148126_4_, int p_148126_6_, int p_148126_7_)
        {
            GuiTeleport.this.drawCenteredString(GuiTeleport.this.fontRendererObj, GuiTeleport.this.placenamelist.get(id), this.width / 2, p_148126_3_ + 1, GuiTeleport.this.enabledlist.get(id) ? 65280 : 16711680);
        }

        @Override
        public void drawScreen(int i, int j, float f)
        {
            super.drawScreen(i, j, f);
            this.flooverlayBackground(0, this.top, 255, 255);
            this.flooverlayBackground(this.bottom, this.height, 255, 255);
        }
        /**
         * Overlays the background to hide scrolled items
         */
        private void flooverlayBackground(int p_148136_1_, int p_148136_2_, int p_148136_3_, int p_148136_4_)
        {
            WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
            GuiTeleport.this.mc.getTextureManager().bindTexture(DataReference.TP_BACKGROUND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            float f = 32.0F;
            wr.startDrawingQuads();
            //wr.setColorRGBA_I(4210752, p_148136_4_);
            wr.addVertexWithUV((double)this.left, (double)p_148136_2_, 0.0D, 0.0D, (double)((float)p_148136_2_ / f));
            wr.addVertexWithUV((double)(this.left + this.width), (double)p_148136_2_, 0.0D, (double)((float)this.width / f), (double)((float)p_148136_2_ / f));
            //wr.setColorRGBA_I(4210752, p_148136_3_);
            wr.addVertexWithUV((double)(this.left + this.width), (double)p_148136_1_, 0.0D, (double)((float)this.width / f), (double)((float)p_148136_1_ / f));
            wr.addVertexWithUV((double)this.left, (double)p_148136_1_, 0.0D, 0.0D, (double)((float)p_148136_1_ / f));
            Tessellator.getInstance().draw();
        }

        @Override
        protected void drawContainerBackground(Tessellator tessellator)
        {
        }
    }
}
