package obsidianAnimator.gui.entitySetup;

import org.lwjgl.input.Keyboard;

import obsidianAPI.render.part.PartObj;
import obsidianAnimator.gui.entityRenderer.GuiEntityRenderer;

public class EntitySetupGui extends GuiEntityRenderer
{

	private EntitySetupController controller;

	public EntitySetupGui(String entityName, EntitySetupController controller) 
	{
		super(entityName);		
		this.controller = controller;
	}
	
	@Override 
	public void processRay()
	{
		if(controller.hoverCheckRequired())
			super.processRay();
	}


	@Override
	protected void keyTyped(char par1, int par2)
	{
		if(par2 == Keyboard.KEY_ESCAPE)
			controller.close();
		else
			super.keyTyped(par1, par2);
	}

	@Override
	protected void mouseClicked(int x, int y, int i) 
	{
		super.mouseClicked(x, y, i);
		if(i == 1 && hoveredPart != null) {
			PartObj selectedPartObj = (PartObj) selectedPart;
			PartObj hoveredPartObj = (PartObj) hoveredPart;			
			if(isShiftKeyDown()) {
				controller.getEntityModel().addMerge(selectedPartObj, hoveredPartObj);
				controller.getEntityModel().runMerge();
				controller.refresh();
			}
			else 
				controller.attemptParent(selectedPartObj, hoveredPartObj);
		}
	}
}
