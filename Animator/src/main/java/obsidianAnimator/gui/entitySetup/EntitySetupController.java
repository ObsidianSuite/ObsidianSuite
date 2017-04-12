package obsidianAnimator.gui.entitySetup;

import net.minecraft.client.Minecraft;
import obsidianAPI.animation.PartGroups;
import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.GuiBlack;
import obsidianAnimator.gui.frames.HomeFrame;
import obsidianAnimator.render.entity.ModelObj_Animator;

public class EntitySetupController 
{
	
	private PartGroups partGroups;
	private EntitySetupGui gui;
	private EntitySetupFrame frame;
	
	public EntitySetupController(String entityName)
	{
		gui = new EntitySetupGui(entityName, this);
		this.partGroups = gui.entityModel.partGroups;
		
		frame = new EntitySetupFrame(this);
	}
	
	public void display()
	{
		Minecraft.getMinecraft().displayGuiScreen(gui);
		frame.setVisible(true);
	}
	
	public void close()
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiBlack());
		frame.dispose();
		new HomeFrame().display();
	}
	
	public ModelObj_Animator getEntityModel()
	{
		return gui.entityModel;
	}
	
	public void setSelectedPart(Part part)
	{
		gui.selectedPart = part;
	}
	
	public PartGroups getPartGroups()
	{
		return partGroups;
	}
	
}
