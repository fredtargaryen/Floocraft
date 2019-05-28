package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.*;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class GuiTeleport extends GuiScreen
{
    /** The title string that is displayed in the top-centre of the screen. */
    private static final String screenTitle = "===Choose a destination===";
    private String status;

    //"Peek..."
    private GuiButton peekBtn;
    //"Go!"
    private GuiButton goBtn;
    //"Cancel"
    private GuiButton cancelBtn;

	private boolean[] enabledList;
	
	private boolean receivedLists;

    private PlaceList scrollWindow;
    
	private final int initX;
    private final int initY;
    private final int initZ;

    //Every object in here is a String, so just cast
    private Object[] placeList;
	
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
        this.buttons.clear();
        this.mc.keyboardListener.enableRepeatEvents(true);
        GuiButton refreshButton = new GuiButton(-2, this.width - 100, 0, 98, 20, "Refresh") {
            @Override
            public void onClick(double mouseX, double mouseY) {
                GuiTeleport.this.refresh();
            }
        };
        refreshButton.enabled = false;
        this.buttons.add(this.peekBtn = new GuiButton(-4, this.width / 2 - 151, this.height - 40, 98, 20, "Peek...") {
            @Override
            public void onClick(double mouseX, double mouseY) {
                String dest = (String) GuiTeleport.this.placeList[id];
                try {
                    MessagePeekRequest m = new MessagePeekRequest();
                    m.initX = GuiTeleport.this.initX;
                    m.initY = GuiTeleport.this.initY;
                    m.initZ = GuiTeleport.this.initZ;
                    m.dest = dest;
                    PacketHandler.INSTANCE.sendToServer(m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.peekBtn.enabled = false;
        this.buttons.add(this.goBtn = new GuiButton(-3, this.width / 2 - 49, this.height - 40, 98, 20, "Go!") {
            @Override
            public void onClick(double mouseX, double mouseY) {
                int initX = GuiTeleport.this.initX;
                int initY = GuiTeleport.this.initY;
                int initZ = GuiTeleport.this.initZ;
                String dest = (String) GuiTeleport.this.placeList[id];
                try {
                    MessageTeleportEntity m = new MessageTeleportEntity();
                    m.initX = initX;
                    m.initY = initY;
                    m.initZ = initZ;
                    m.dest = dest;
                    PacketHandler.INSTANCE.sendToServer(m);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                GuiTeleport.this.cancelBtn.onClick(0.0, 0.0);
            }
        });
        this.goBtn.enabled = false;
        this.buttons.add(this.cancelBtn = new GuiButton(-1, this.width / 2 + 53, this.height - 40, 98, 20, "Cancel") {
            @Override
            public void onClick(double mouseX, double mouseY) {
                ((ClientProxy) FloocraftBase.proxy).overrideTicker.start();
                GuiTeleport.this.mc.displayGuiScreen(null);
            }
        });
        if (receivedLists)
        {
            refreshButton.enabled = true;
            this.scrollWindow = new PlaceList();
        }
        this.buttons.add(refreshButton);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        ClientProxy proxy = (ClientProxy) FloocraftBase.proxy;
        this.mc.keyboardListener.enableRepeatEvents(false);
        proxy.overrideTicker.start();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void tick() {
        super.tick();
        if (!this.receivedLists) {
            this.status = "Loading...";
        }
        else {//if the lists were received...
            //if they are empty...
            if (this.placeList.length == 0) {
                this.status = "No places found";
            } else {
                this.status = "";
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    public boolean charTyped(char par1, int par2) {
        if (par2 == 1) {
            this.cancelBtn.onClick(0.0, 0.0);
        }
        return true;
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(int mousex, int mousey, float partialticks)
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
        super.render(mousex, mousey, partialticks);
    }
    
    private void refresh()
    {
    	this.placeList = new Object[]{};
    	this.enabledList = new boolean[]{};
    	this.receivedLists = false;
    	this.initGui();
        PacketHandler.INSTANCE.sendToServer(new MessageFireplaceListRequest());
    }
    
    public void onFireplaceList(MessageFireplaceList m)
    {
        try
		{
        	this.placeList = m.places;
        	this.enabledList = m.enabledList;
			this.receivedLists = true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
        this.initGui();
    }

    public void onStartPeek(MessageStartPeek msp) {
        this.mc.displayGuiScreen(
                new GuiPeek(
                        (String)GuiTeleport.this.placeList[this.scrollWindow.getSelectedElement()], msp.peekerUUID));
    }
    
    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    //TODO Might not need these
//    @Override
//    public void handleMouseEvent() {
//        try
//        {
//            if(this.scrollWindow != null)
//            {
//                this.scrollWindow.handleMouseInput();
//            }
//            super.handleMouseInput();
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean mouseScrolled(double p_mouseScrolled_1_) {
//        return this.placeList.mouseScrolled(p_mouseScrolled_1_);
//    }

    @OnlyIn(Dist.CLIENT)
    class PlaceList extends GuiSlot
    {
        public PlaceList()
        {
            super(GuiTeleport.this.mc, GuiTeleport.this.width, GuiTeleport.this.height, 32, GuiTeleport.this.height - 64, 18);
            this.setShowSelectionBox(true);
        }

        @Override
        public int getListWidth()
        {
            return 380;
        }

        protected int getSize()
        {
            return GuiTeleport.this.placeList.length;
        }

        /**
         * The element in the slot that was clicked, boolean for whether it was double clicked or not
         */
        protected void elementClicked(int id, boolean isDoubleClick, int mousex, int mousey)
        {
            boolean enabled = GuiTeleport.this.enabledList[id];
            GuiTeleport.this.goBtn.enabled = enabled;
            GuiTeleport.this.peekBtn.enabled = enabled;
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
            GuiTeleport.this.drawCenteredString(GuiTeleport.this.fontRenderer, (String)GuiTeleport.this.placeList[id], this.width / 2, p_148126_3_ + 1, GuiTeleport.this.enabledList[id] ? 65280 : 16711680);
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
