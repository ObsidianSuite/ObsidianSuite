package obsidianAnimator.gui;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

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
		Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, 0,1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, 1,0);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, 0, 0);
        tessellator.draw();
	}
}