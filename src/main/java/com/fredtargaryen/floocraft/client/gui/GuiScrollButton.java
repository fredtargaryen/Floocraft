/**
 * This class will be removed once I make a scrollbar for GUITeleport
 */

package com.fredtargaryen.floocraft.client.gui;

import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.gui.GuiButton;

@SideOnly(Side.CLIENT)
public class GuiScrollButton extends GuiButton
{
	public GuiScrollButton(int par1, int par2, int par3, String par4Str)
    {
        super(par1, par2, par3, 20, 20, par4Str);
    }
}
