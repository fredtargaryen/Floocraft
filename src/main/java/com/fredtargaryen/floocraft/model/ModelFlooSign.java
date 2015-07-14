package com.fredtargaryen.floocraft.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSign;

public class ModelFlooSign extends ModelSign
{
	/** The board on a sign that has the writing on it. */
    private final ModelRenderer signBoard = new ModelRenderer(this, 0, 0);
    
    public ModelFlooSign()
    {
        this.signBoard.addBox(-12.0F, -14.0F, -1.0F, 24, 12, 2, 0.0F);
    }
    
    /**
     * Renders the sign model through TileEntitySignRenderer
     */
    public void renderSign()
    {
        this.signBoard.render(0.0625F);
    }
}
