package MCEntityAnimator.gui.sequence;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.Util;
import MCEntityAnimator.render.MathHelper;
import MCEntityAnimator.render.objRendering.RayTrace;
import MCEntityAnimator.render.objRendering.parts.Part;
import MCEntityAnimator.render.objRendering.parts.PartEntityPos;
import MCEntityAnimator.render.objRendering.parts.PartObj;
import MCEntityAnimator.render.objRendering.parts.PartRotation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

public class GuiEntityRendererWithTranslation extends GuiEntityRendererWithRotation
{

	private boolean translationAxisMouseOver = false;
	private boolean translationAxisDrag = false;
	private Integer translationAxisPlane;

	//Vector for current rotation of wheel while dragging.
	private Vec3 translationGuidePoint;
	//Vector for rotation of wheel when first clicked on.
	private Vec3 initialTranslationGuidePoint;
	private double prevTranslationDelta = 0.0F;

	public GuiEntityRendererWithTranslation(String entityName) 
	{
		super(entityName);
	}

	/* ---------------------------------------------------- *
	 * 						Input							*
	 * ---------------------------------------------------- */

	@Override
	protected void mouseClicked(int x, int y, int button) 
	{
		//If mouse over and lmb clicked, begin drag.
		if(translationAxisMouseOver && button == 0)
			translationAxisDrag = true;
		else
			super.mouseClicked(x, y, button);
	}

	@Override
	public void mouseMovedOrUp(int x, int y, int which) 
	{
		super.mouseMovedOrUp(x, y, which);
		//If lmb lifted, reset drag, guide and delta.
		if(which == 0)
		{
			if(translationAxisDrag)
				onControllerRelease();
			translationGuidePoint = null;
			translationAxisDrag = false;
			prevTranslationDelta = 0.0F;
		}
	}

	/* ---------------------------------------------------- *
	 * 					  Ray Trace							*
	 * ---------------------------------------------------- */

	@Override
	public void processRay()
	{
		if(currentPartName != null && !currentPartName.equals(""))
		{
			GL11.glPushMatrix();

			Part part = Util.getPartFromName(currentPartName, entityModel.parts);
			if(part instanceof PartObj || part instanceof PartRotation)
			{
				GL11.glPopMatrix();
				super.processRay();
				return;
			}
			else if(!(part instanceof PartEntityPos))
			{	
				PartObj partObj = (PartObj) Util.getPartFromName("cube.008", entityModel.parts);
				partObj.postRenderAll();
				GL11.glTranslatef(0,-0.17F,0);
			}


			drawTranslationAxis();

			translationAxisMouseOver = false;
			if(!translationAxisDrag)
			{
				updateAxisMouseOver();
				//If it isn't being moused over, hand over ray processing to GuiEntityRenderer, which will test for hovering over parts.
				if(!translationAxisMouseOver)
				{
					GL11.glPopMatrix();
					super.processRay();
					return;
				}
				//If it is being hovered over, ensure there is no part highlighted for selection.
				else
					additionalHighlightPartName = null;
			}
			else
				onControllerDrag();

			GL11.glPopMatrix();
		}
		else
			super.processRay();
	}

	private void updateAxisMouseOver()
	{
		Integer dim = testAxisRay();
		if(dim != null)
		{
			translationAxisMouseOver = true;
			translationAxisPlane = dim;
			int i = translationAxisPlane == 0 ? 2 : translationAxisPlane - 1;
			translationGuidePoint = getMouseVectorInPlane(i);
			initialTranslationGuidePoint = translationGuidePoint;
			Vec3 v = null;
			switch(translationAxisPlane)
			{
			case 0: v = Vec3.createVectorHelper(1, 0, 0); break;
			case 1: v = Vec3.createVectorHelper(0, 1, 0); break;
			case 2: v = Vec3.createVectorHelper(0, 0, 1); break; 
			}
			GL11.glPushMatrix();
			Part part = Util.getPartFromName(currentPartName, entityModel.parts);
			if(part instanceof PartEntityPos)
				GL11.glTranslated(-entityToRender.posX, -entityToRender.posY, -entityToRender.posZ);
			prevTranslationDelta = MathHelper.getLineScalarForClosestPoint(Vec3.createVectorHelper(0, 0, 0), v, initialTranslationGuidePoint);
			GL11.glPopMatrix();

		}
		else
			translationAxisPlane = null;
	}

	public Integer testAxisRay()
	{
		Double min = null;
		Integer dim = null;
		for(int i = 0; i < 3; i++)
		{
			Vec3 p = null;
			Vec3 n = null;
			switch(i)
			{
			case 0: 
				p = Vec3.createVectorHelper(MathHelper.rotationWheelRadius, 0, 0); 
				n = Vec3.createVectorHelper(0, 1, 0); 
				break;
			case 1: 
				p = Vec3.createVectorHelper(0, MathHelper.rotationWheelRadius, 0); 
				n = Vec3.createVectorHelper(0, 0, 1); 
				break;
			case 2: 
				p = Vec3.createVectorHelper(0, 0, MathHelper.rotationWheelRadius); 
				n = Vec3.createVectorHelper(1, 0, 0); 
				break;
			}
			Double d = MathHelper.rayIntersectsAxisSlider(RayTrace.getRayTrace(), p, n);
			if(d != null && (min == null || d < min))
			{
				min = d;
				dim = i;
			}
		}
		return dim;	
	}

	private void processAxisDrag()
	{
		if(translationGuidePoint != null && initialTranslationGuidePoint != null)
		{
			Vec3 v = null;
			switch(translationAxisPlane)
			{
			case 0: v = Vec3.createVectorHelper(1, 0, 0); break;
			case 1: v = Vec3.createVectorHelper(0, 1, 0); break;
			case 2: v = Vec3.createVectorHelper(0, 0, 1); break; 
			}

			double translationDelta = MathHelper.getLineScalarForClosestPoint(Vec3.createVectorHelper(0, 0, 0), v, translationGuidePoint);

			double d = translationDelta - prevTranslationDelta;


			System.out.println(translationGuidePoint + " " + initialTranslationGuidePoint);
			if(!Double.isNaN(d))
			{
				Part part = Util.getPartFromName(currentPartName, entityModel.parts);
				if(part instanceof PartEntityPos)
					d *= -1;
				updatePartValue(-d, translationAxisPlane);
				prevTranslationDelta = translationDelta;
			}
		}
	}

	@Override
	protected void onControllerDrag()
	{
		Part part = Util.getPartFromName(currentPartName, entityModel.parts);
		if(part instanceof PartObj || part instanceof PartRotation)
			super.onControllerDrag();
		else
		{
			GL11.glPushMatrix();
			if(part instanceof PartEntityPos)
				GL11.glTranslated(-entityToRender.posX, -entityToRender.posY, -entityToRender.posZ);
			int i = translationAxisPlane == 0 ? 2 : translationAxisPlane - 1;
			translationGuidePoint = getMouseVectorInPlane(i);
			processAxisDrag();
			GL11.glPopMatrix();
		}
	}

	@Override
	protected void onControllerRelease()
	{
		super.onControllerRelease();
	}

	/* ---------------------------------------------------- *
	 * 						Render							*
	 * ---------------------------------------------------- */	

	private void drawTranslationAxis()
	{
		Vec3 origin = Vec3.createVectorHelper(0.0F, 0.0F, 0.0F);
		int colour = 0xFFFFFF;
		Vec3 v = null;
		for(int i = 0; i < 3; i++)
		{
			switch(i)
			{
			case 0: 
				colour = 0xFF0000; 
				v = Vec3.createVectorHelper(MathHelper.rotationWheelRadius, 0.0F, 0.0F);
				break;
			case 1: 
				colour = 0x00FF00; 
				v = Vec3.createVectorHelper(0.0F, MathHelper.rotationWheelRadius, 0.0F);
				break;
			case 2: 
				colour = 0x0000FF; 
				v = Vec3.createVectorHelper(0.0F, 0.0F, MathHelper.rotationWheelRadius);
				break;
			}
			if(translationAxisPlane != null && translationAxisPlane == i)
				drawLine(origin, v, colour, 3.0F, 1.0F);
			else
				drawLine(origin, v, colour, 2.0F, 0.4F);
		}	
	}

	private void drawLine(Vec3 p1, Vec3 p2, int color, float width, float alpha)
	{
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
		GL11.glLineWidth(width);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);

		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawing(1);
		tessellator.setColorRGBA_I(color, (int) (alpha*255));
		tessellator.addVertex(p1.xCoord,p1.yCoord,p1.zCoord);
		tessellator.addVertex(p2.xCoord,p2.yCoord,p2.zCoord);
		tessellator.draw();

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

}
