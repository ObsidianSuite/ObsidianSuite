package MCEntityAnimator.gui;

import MCEntityAnimator.gui.sequence.GuiAnimationTimelineWithFrames;
import MCEntityAnimator.render.objRendering.EntityObj;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class GuiInventoryChooseItem extends GuiInventory
{
    
	private EntityPlayer player;
	private String entityName;
	private GuiAnimationTimelineWithFrames parentGui;
	private EntityObj entity;

    public GuiInventoryChooseItem(GuiAnimationTimelineWithFrames parentGui, EntityObj entity)
    {
        super(Minecraft.getMinecraft().thePlayer);
        this.allowUserInput = true;
        this.parentGui = parentGui;
        this.entity = entity;
    }

    public void setItemStack(ItemStack par1ItemStack)
    {
    	this.entity.setCurrentItem(par1ItemStack);   	
    	this.mc.displayGuiScreen(this.parentGui);
    	this.parentGui.loadFrames();
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
           this.mc.displayGuiScreen(new GuiContainerChooseItem(this.mc.thePlayer, this));
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.mc.displayGuiScreen(new GuiContainerChooseItem(this.mc.thePlayer, this));
    }

}