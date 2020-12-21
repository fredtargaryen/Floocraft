package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.*;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class TeleportScreen extends Screen {
    /** The title string that is displayed in the top-centre of the screen. */
    private static final String screenTitle = I18n.format("gui.teleport.title");
    private String status;

    //"Peek..."
    private Button peekBtn;
    //"Go!"
    private Button goBtn;
    //"Cancel"
    private Button cancelBtn;

	private boolean[] enabledList;
	
	private boolean receivedLists;

    private PlaceScrollWindow scrollWindow;
    
	private final int initX;
    private final int initY;
    private final int initZ;

    //Every object in here is a String, so just cast
    private Object[] placeList;

    private int peekAttemptTimer;

    private static final TranslationTextComponent REFRESH = new TranslationTextComponent("gui.teleport.refresh");
    private static final TranslationTextComponent PEEK = new TranslationTextComponent("gui.teleport.peek");
    private static final TranslationTextComponent TELEPORT = new TranslationTextComponent("gui.teleport.go");
    private static final TranslationTextComponent CANCEL = new TranslationTextComponent("gui.teleport.cancel");
	
    public TeleportScreen(int x, int y, int z) {
        super(new StringTextComponent(screenTitle));
    	this.initX = x;
    	this.initY = y;
    	this.initZ = z;
        this.placeList = new Object[] {};
        this.enabledList = new boolean[] {};
        this.peekAttemptTimer = 0;
        this.refresh();
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void init() {
        this.buttons.clear();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        Button refreshButton = new Button(this.width - 100, 0, 98, 20, REFRESH, button -> {
            TeleportScreen.this.refresh();
            TeleportScreen.this.init();
        });
        refreshButton.active = false;
        this.addButton(this.peekBtn = new Button(this.width / 2 - 151, this.height - 40, 98, 20, PEEK, button -> {
            String dest = (String) TeleportScreen.this.placeList[TeleportScreen.this.scrollWindow.getSelected().id];
            try {
                MessagePeekRequest m = new MessagePeekRequest();
                m.initX = TeleportScreen.this.initX;
                m.initY = TeleportScreen.this.initY;
                m.initZ = TeleportScreen.this.initZ;
                m.dest = dest;
                MessageHandler.INSTANCE.sendToServer(m);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        this.peekBtn.active = false;
        this.addButton(this.goBtn = new Button(this.width / 2 - 49, this.height - 40, 98, 20, TELEPORT, button -> {
            int initX = TeleportScreen.this.initX;
            int initY = TeleportScreen.this.initY;
            int initZ = TeleportScreen.this.initZ;
            String dest = (String) TeleportScreen.this.placeList[TeleportScreen.this.scrollWindow.getSelected().id];
            try {
                MessageTeleportEntity m = new MessageTeleportEntity();
                m.initX = initX;
                m.initY = initY;
                m.initZ = initZ;
                m.dest = dest;
                MessageHandler.INSTANCE.sendToServer(m);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            TeleportScreen.this.cancelBtn.onClick(0.0, 0.0);
        }));
        this.goBtn.active = false;
        this.addButton(this.cancelBtn = new Button(this.width / 2 + 53, this.height - 40, 98, 20, CANCEL, button -> {
            ((ClientProxy) FloocraftBase.proxy).overrideTicker.start();
            TeleportScreen.this.minecraft.displayGuiScreen(null);
        }));
        if (receivedLists) {
            refreshButton.active = true;
            this.scrollWindow = new PlaceScrollWindow();
            this.children.add(this.scrollWindow);
        }
        this.addButton(refreshButton);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onClose() {
        ClientProxy proxy = (ClientProxy) FloocraftBase.proxy;
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        proxy.overrideTicker.start();
        super.onClose();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void tick() {
        super.tick();
        if (!this.receivedLists) {
            this.status = I18n.format("gui.teleport.loading");
        }
        else {//if the lists were received...
            //if they are empty...
            if (this.placeList.length == 0) {
                this.status = I18n.format("gui.teleport.empty");
            } else {
                if(this.peekAttemptTimer == 0) {
                    this.status = "";
                }
            }
        }
        if(this.peekAttemptTimer > 0) {
            --this.peekAttemptTimer;
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
    public void render(MatrixStack stack, int mousex, int mousey, float partialticks) {
        this.renderBackground(stack);
        this.drawCenteredString(stack, this.font,
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
            this.scrollWindow.render(stack, mousex, mousey, partialticks);
        }
        this.drawCenteredString(stack, this.font,
                screenTitle,
                this.width / 2,
                15,
                16777215);
        super.render(stack, mousex, mousey, partialticks);
    }
    
    private void refresh() {
        this.children.remove(this.scrollWindow);
        this.scrollWindow = null;
    	this.placeList = new Object[]{};
    	this.enabledList = new boolean[]{};
    	this.receivedLists = false;
        MessageHandler.INSTANCE.sendToServer(new MessageFireplaceListRequest());
    }
    
    public void onFireplaceList(MessageFireplaceList m) {
        try {
        	this.placeList = m.places;
        	this.enabledList = m.enabledList;
			this.receivedLists = true;
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
        this.init();
    }

    public void onStartPeek(MessageStartPeek msp) {
        this.minecraft.displayGuiScreen(
                new PeekScreen(
                        (String)this.placeList[this.scrollWindow.getSelected().id], msp.peekerUUID));
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        if(this.scrollWindow != null) {
            this.scrollWindow.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        }
        return true;
    }
    
    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    class GuiPlaceEntry extends ExtendedList.AbstractListEntry<GuiPlaceEntry> {
        private int id;

        GuiPlaceEntry(int id) {
            this.id = id;
        }

        @Override
        public void render(MatrixStack stack, int entryId, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean b, float partialTicks) {
            TeleportScreen.this.drawCenteredString(stack, TeleportScreen.this.font, (String) TeleportScreen.this.placeList[this.id], TeleportScreen.this.width / 2, top + 3, TeleportScreen.this.enabledList[this.id] ? 65280 : 16711680);
        }

        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            TeleportScreen.this.scrollWindow.setSelected(this);
            return true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class PlaceScrollWindow extends ExtendedList<GuiPlaceEntry> {
        PlaceScrollWindow() {
            super(TeleportScreen.this.minecraft, TeleportScreen.this.width, TeleportScreen.this.height, 32, TeleportScreen.this.height - 64, 18);
            this.setRenderSelection(true);
            for(int i = 0; i < TeleportScreen.this.placeList.length; ++i) {
                this.addEntry(new GuiPlaceEntry(i));
            }
            if(TeleportScreen.this.placeList.length > 0) this.setSelected(null);
        }

        @Override
        public int getWidth()
        {
            return 380;
        }

        /**
         * Called when the given entry is selected.
         */
        @Override
        public void setSelected(GuiPlaceEntry entry) {
            super.setSelected(entry);
            if(entry != null) {
                boolean enabled = TeleportScreen.this.enabledList[entry.id];
                TeleportScreen.this.goBtn.active = enabled;
                TeleportScreen.this.peekBtn.active = enabled;
            }
        }

        /**
         * Returns true if the element passed in is currently selected
         */
        protected boolean isSelected(int id)
        {
            return id == this.getSelected().id;
        }

        /**
         * Return the height of the content being scrolled
         */
        protected int getContentHeight() {
            return this.getHeight() * 18;
        }

        @Override
        protected void renderBackground(MatrixStack stack){}

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            int i = this.getScrollbarPosition();
            int j = i + 6;
            RenderSystem.disableLighting();
            RenderSystem.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int k = this.getRowLeft();
            int l = this.y0 + 4 - (int)this.getScrollAmount();

            this.renderList(stack, k, l, mouseX, mouseY, partialTicks);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ZERO.param, GlStateManager.DestFactor.ONE.param);
            RenderSystem.disableAlphaTest();
            RenderSystem.shadeModel(7425);
            RenderSystem.disableTexture();
            int i1 = 4;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)this.x0, (double)(this.y0 + 4), 0.0D).tex(0f, 1f).color(0, 0, 0, 0).endVertex();
            bufferbuilder.pos((double)this.x1, (double)(this.y0 + 4), 0.0D).tex(1f, 1f).color(0, 0, 0, 0).endVertex();
            bufferbuilder.pos((double)this.x1, (double)this.y0, 0.0D).tex(1f, 0f).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)this.x0, (double)this.y0, 0.0D).tex(0f, 0f).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos((double)this.x0, (double)this.y1, 0.0D).tex(0f, 1f).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)this.x1, (double)this.y1, 0.0D).tex(1f, 1f).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double)this.x1, (double)(this.y1 - 4), 0.0D).tex(1f, 0f).color(0, 0, 0, 0).endVertex();
            bufferbuilder.pos((double)this.x0, (double)(this.y1 - 4), 0.0D).tex(0f, 0f).color(0, 0, 0, 0).endVertex();
            tessellator.draw();
            int j1 = Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
            if (j1 > 0) {
                int k1 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
                k1 = MathHelper.clamp(k1, 32, this.y1 - this.y0 - 8);
                int l1 = (int)this.getScrollAmount() * (this.y1 - this.y0 - k1) / j1 + this.y0;
                if (l1 < this.y0) {
                    l1 = this.y0;
                }

                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos((double)i, (double)this.y1, 0.0D).tex(0f, 1f).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos((double)j, (double)this.y1, 0.0D).tex(1f, 1f).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos((double)j, (double)this.y0, 0.0D).tex(1f, 0f).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos((double)i, (double)this.y0, 0.0D).tex(0f, 0f).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos((double)i, (double)(l1 + k1), 0.0D).tex(0f, 1f).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos((double)j, (double)(l1 + k1), 0.0D).tex(1f, 1f).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos((double)j, (double)l1, 0.0D).tex(1f, 0f).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos((double)i, (double)l1, 0.0D).tex(0f, 0f).color(128, 128, 128, 255).endVertex();
                tessellator.draw();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos((double)i, (double)(l1 + k1 - 1), 0.0D).tex(0f, 1f).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos((double)(j - 1), (double)(l1 + k1 - 1), 0.0D).tex(1f, 1f).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos((double)(j - 1), (double)l1, 0.0D).tex(1f, 0f).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos((double)i, (double)l1, 0.0D).tex(0f, 0f).color(192, 192, 192, 255).endVertex();
                tessellator.draw();
            }

            this.renderDecorations(stack, mouseX, mouseY);
            RenderSystem.enableTexture();
            RenderSystem.shadeModel(7424);
            RenderSystem.enableAlphaTest();
            RenderSystem.disableBlend();
            this.flooverlayBackground(0, this.getTop());
            this.flooverlayBackground(this.getBottom(), this.height);
        }

        /**
         * Overlays the background to hide scrolled items
         */
        private void flooverlayBackground(int p_148136_1_, int p_148136_2_) {
            BufferBuilder wr = Tessellator.getInstance().getBuffer();
            TeleportScreen.this.minecraft.getTextureManager().bindTexture(DataReference.TP_BACKGROUND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            float f = 32.0F;
            wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            wr.pos(0.0, (double)p_148136_2_, 0.0D).tex(0f, (float)p_148136_2_ / f).endVertex();
            wr.pos(this.width, (double)p_148136_2_, 0.0D).tex((float)this.width / f, (float)p_148136_2_ / f).endVertex();
            wr.pos(this.width, (double) p_148136_1_, 0.0D).tex((float) this.width / f, (float)p_148136_1_ / f).endVertex();
            wr.pos(0.0, (double) p_148136_1_, 0.0D).tex(0f, (float)p_148136_1_ / f).endVertex();
            Tessellator.getInstance().draw();
        }
    }
}
