package com.nthrootsoftware.mcea.gui;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Method;

@SideOnly(Side.CLIENT)
public class GuiContainerChooseItem extends GuiContainerCreative
{

	private GuiInventoryChooseItem gui;

	public GuiContainerChooseItem(EntityPlayer par1EntityPlayer, GuiInventoryChooseItem guiInventoryChooseItem)
	{
		super(par1EntityPlayer);
		this.gui = guiInventoryChooseItem;
	}

	@Override
	protected void handleMouseClick(Slot par1Slot, int par2, int par3, int par4)
	{
		ItemStack itemstack;
		InventoryPlayer inventoryplayer;

		if(par4 == 0 && par1Slot != null && par1Slot.getHasStack())
		{
			this.gui.setItemStack(par1Slot.getStack());
		}
	}

	@Override
	public void initGui()
	{
		super.initGui();
		Method setCurrentCreativeTab = ReflectionHelper.findMethod(GuiContainerCreative.class, this, new String[] { "setCurrentCreativeTab", "func_147050_b"}, CreativeTabs.class);
		GuiTextField searchField = ObfuscationReflectionHelper.getPrivateValue(GuiContainerCreative.class, this, "searchField", "field_147062_A");
		Method updateCreativeSearch = ReflectionHelper.findMethod(GuiContainerCreative.class, this, new String[] { "updateCreativeSearch", "func_147053_i"});
	}
}