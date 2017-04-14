package obsidianAnimator.gui.timeline;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.ExternalFrame;
import obsidianAnimator.gui.entityRenderer.GuiEntityRendererWithTranslation;

public class TimelineGui extends GuiEntityRendererWithTranslation implements ExternalFrame
{

	public TimelineController controller;
			
	public TimelineGui(TimelineController controller)
	{
		super(controller.currentAnimation.getEntityName());
		this.controller = controller;
	}

	public void drawScreen(int par1, int par2, float par3)
	{				
		controller.onGuiDraw();
		updateExternalFrameFromDisplay();
		super.drawScreen(par1, par2, par3);
	}

	@Override
	public void updatePart(Part newPart)
	{
		super.updatePart(newPart);
		controller.setExceptionPart(newPart);
		controller.refresh();
	}	

	@Override
	public void processRay()
	{
		GL11.glPushMatrix();
		super.processRay();
		GL11.glPopMatrix();
		controller.checkFramePartHighlighting();
	}

	@Override
	protected void keyTyped(char par1, int par2)
	{
		controller.handleMinecraftKey(par2);
		if(par2 != Keyboard.KEY_ESCAPE)
			super.keyTyped(par1, par2);
	}

	@Override
	protected void onControllerDrag()
	{
		super.onControllerDrag();
		controller.setExceptionPart(selectedPart);
	}

	@Override	
	protected void onControllerRelease()
	{
		super.onControllerRelease();
		if(controller.keyframeController.keyframeExists())
			controller.keyframeController.addKeyframe();
	}

	@Override
	public void updateExternalFrameFromDisplay() 
	{
		controller.timelineFrame.setAlwaysOnTop(Display.isActive());
	}

}
