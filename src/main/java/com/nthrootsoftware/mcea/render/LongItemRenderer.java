package com.nthrootsoftware.mcea.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LongItemRenderer extends WeaponItemRenderer
{
    @Override
    public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data)
    {
        Tessellator tess = Tessellator.instance;

        EntityLivingBase living = (EntityLivingBase) data[1];

        Item item = itemStack.getItem();
        IIcon[] icons = new IIcon[item.getRenderPasses(0)];

        for (int i = 0; i < icons.length; i++)
        {
            icons[i] = item.getIcon(itemStack, i);
        }


        GL11.glTranslatef(-0.5F, -0.5F, 0F);
        GL11.glScalef(2F, 2F, 1.4F);

        float t = 0.0625F;

        for (int i = 0; i < icons.length; i++)
        {
            renderItemIn2D(tess, icons[i].getMaxU(), icons[i].getMinV(), icons[i].getMinU(), icons[i].getMaxV(), icons[i].getIconWidth() * 16, icons[i].getIconHeight() * 16, t);
            renderEnchantEffect(tess, itemStack, 256, 256, t);
        }

    }
}
