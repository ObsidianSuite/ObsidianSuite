package obsidianAnimator.gui.entitySetup;

import org.lwjgl.input.Keyboard;

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
	protected void keyTyped(char par1, int par2)
	{
		if(par2 == Keyboard.KEY_ESCAPE)
			controller.close();
		else
			super.keyTyped(par1, par2);
	}
}
