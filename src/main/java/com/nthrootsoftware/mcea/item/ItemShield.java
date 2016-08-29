package com.nthrootsoftware.mcea.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemShield extends Item
{
    public ItemShield()
    {
        super();
    }

	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("mod_mcea:" + this.getUnlocalizedName().substring(5, this.getUnlocalizedName().length()));
    }



}
