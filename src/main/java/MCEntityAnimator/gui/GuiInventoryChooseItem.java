package MCEntityAnimator.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import MCEntityAnimator.render.objRendering.EntityObj;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInventoryChooseItem extends GuiInventory
{
    /**
     * x size of the inventory window in pixels. Defined as float, passed as int
     */
    private float xSize_lo;

    /**
     * y size of the inventory window in pixels. Defined as float, passed as int.
     */
    private float ySize_lo;

	private EntityPlayer player;
	private String entityName;
	private GuiScreen parentGui;
	private EntityObj entity;

    public GuiInventoryChooseItem(GuiScreen par1Gui, EntityObj par2Entity)
    {
        super(Minecraft.getMinecraft().thePlayer);
        this.allowUserInput = true;
        this.parentGui = par1Gui;
        this.entity = par2Entity;
    }

    public void setItemStack(ItemStack par1ItemStack)
    {
    	this.entity.setCurrentItem(par1ItemStack);   	
    	this.mc.displayGuiScreen(this.parentGui);
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        if (this.mc.playerController.isInCreativeMode())
        {
            this.mc.displayGuiScreen(new GuiContainerChooseItem(this.mc.thePlayer, this));
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();

        if (this.mc.playerController.isInCreativeMode())
        {
            this.mc.displayGuiScreen(new GuiContainerChooseItem(this.mc.thePlayer, this));
        }
        else
        {
            super.initGui();
        }
    }

}