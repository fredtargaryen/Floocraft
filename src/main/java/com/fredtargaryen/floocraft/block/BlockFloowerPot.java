package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.entity.EntityDroppedFlooPowder;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFloowerPot extends BlockContainer
{
    public BlockFloowerPot()
    {
        super(Material.circuits);
        this.setBlockBoundsForItemRender();
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IIconRegister)
    {
        this.blockIcon = par1IIconRegister.registerIcon(DataReference.resPath(this.getUnlocalizedName()));
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileEntityFloowerPot();
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float f = 0.375F;
        float f1 = f / 2.0F;
        this.setBlockBounds(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, f, 0.5F + f1);
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        TileEntity tileEntity = w.getTileEntity(x, y, z);
        if (tileEntity == null || player.isSneaking())
        {
            return false;
        }
        player.openGui(FloocraftBase.instance, 0, w, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
    {
        this.dropItems(world, x, y, z);
        super.breakBlock(world, x, y, z, par5, par6);
    }

    private void dropItems(World world, int x, int y, int z){
        Random rand = new Random();

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof IInventory)) {
            return;
        }
        IInventory inventory = (IInventory) tileEntity;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            if (item != null && item.stackSize > 0) {
                float rx = rand.nextFloat() * 0.8F + 0.1F;
                float ry = rand.nextFloat() * 0.8F + 0.1F;
                float rz = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityItem = new EntityItem(world,
                        x + rx, y + ry, z + rz,
                        new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

                if (item.hasTagCompound()) {
                    entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                }

                float factor = 0.05F;
                entityItem.motionX = rand.nextGaussian() * factor;
                entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                entityItem.motionZ = rand.nextGaussian() * factor;
                world.spawnEntityInWorld(entityItem);
                item.stackSize = 0;
            }
        }
    }

    @Override
    public int tickRate(World par1World)
    {
        return 60;
    }

    @Override
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if(!par1World.isRemote)
        {
            TileEntityFloowerPot pot = (TileEntityFloowerPot) par1World.getTileEntity(par2, par3, par4);
            ItemStack stack = pot.getStackInSlot(0);
            if (stack != null && stack.stackSize > 0)
            {
                for (int x = par2 - 5; x < par2 + 6; x++)
                {
                    for (int y = par3 - 5; y < par3 + 6; y++)
                    {
                        for (int z = par4 - 5; z < par4 + 6; z++)
                        {
                            if (par1World.getBlock(x, y, z) == Blocks.fire)
                            {
                                par1World.setBlock(x, y, z, FloocraftBase.greenFlamesTemp);
                                GreenFlamesIdleTemp gfit = (GreenFlamesIdleTemp) par1World.getBlock(x, y, z);
                                boolean shouldPut = gfit.approveOrDenyTeleport(par1World, x, y, z);
                                par1World.setBlock(x, y, z, Blocks.fire);
                                if (shouldPut)
                                {
                                    Item i = stack.getItem();
                                    par1World.setBlock(x, y, z, FloocraftBase.greenFlamesBusyLower, ((ItemFlooPowder)i).getConcentration(), 2);
                                    stack.stackSize--;
                                    pot.setInventorySlotContents(0, stack.stackSize == 0 ? null : stack.splitStack(stack.stackSize));
                                }
                            }
                        }
                    }
                }
            }
        }
        par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World) + par5Random.nextInt(20));
    }

    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
    }
}
