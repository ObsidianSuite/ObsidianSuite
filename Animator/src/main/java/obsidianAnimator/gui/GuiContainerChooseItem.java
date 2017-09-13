package obsidianAnimator.gui;

import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerChooseItem extends GuiContainerCreative
{

	private GuiInventoryChooseItem gui;
	private final ResourceLocation texture = new ResourceLocation("mod_obsidian_animator:gui/gui_black.png");
	
	public GuiContainerChooseItem(EntityPlayer par1EntityPlayer, GuiInventoryChooseItem guiInventoryChooseItem)
	{
		super(par1EntityPlayer);
		this.gui = guiInventoryChooseItem;
	}

	@Override
	protected void handleMouseClick(Slot par1Slot, int par2, int par3, ClickType clickType)
	{
		ItemStack itemstack;
		InventoryPlayer inventoryplayer;

		if(par1Slot != null && par1Slot.getHasStack())
		{
			this.gui.setItemStack(par1Slot.getStack());
		}
	}

	@Override
	public void initGui()
	{
		super.initGui();
		Method setCurrentCreativeTab = ReflectionHelper.findMethod(GuiContainerCreative.class, "setCurrentCreativeTab", "func_147050_b", CreativeTabs.class);
		GuiTextField searchField = ObfuscationReflectionHelper.getPrivateValue(GuiContainerCreative.class, this, "searchField", "field_147062_A");
		Method updateCreativeSearch = ReflectionHelper.findMethod(GuiContainerCreative.class, "updateCreativeSearch", "func_147053_i");
	}

	@Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(texture);		
		drawCustomGui(0, 0, width, height, 0);	
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
    }
	
	private void drawCustomGui(double x, double y, double width, double height, double zLevel)
	{
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x + 0, y + height, zLevel).endVertex();
        bufferbuilder.pos(x + width, y + height, zLevel).endVertex();
        bufferbuilder.pos(x + width, y + 0, zLevel).endVertex();
        bufferbuilder.pos(x + 0, y + 0, zLevel).endVertex();
        tessellator.draw();
	}
}