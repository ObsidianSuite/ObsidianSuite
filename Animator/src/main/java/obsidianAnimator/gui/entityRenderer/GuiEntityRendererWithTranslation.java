package obsidianAnimator.gui.entityRenderer;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartEntityPos;
import obsidianAPI.render.part.PartObj;
import obsidianAPI.render.part.PartRotation;
import obsidianAPI.render.part.prop.PartPropScale;
import obsidianAPI.render.part.prop.PartPropTranslation;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.render.MathHelper;
import obsidianAnimator.render.RayTrace;

public class GuiEntityRendererWithTranslation extends GuiEntityRendererWithRotation
{

	private boolean translationAxisMouseOver = false;
	private boolean translationAxisDrag = false;
	private Integer translationAxisPlane;

	//Vector for current rotation of wheel while dragging.
	private Vec3d translationGuidePoint;
	//Vector for rotation of wheel when first clicked on.
	private Vec3d initialTranslationGuidePoint;
	private double prevTranslationDelta = 0.0F;

	public GuiEntityRendererWithTranslation(String entityName) 
	{
		super(entityName);
	}

	/* ---------------------------------------------------- *
	 * 						Input							*
	 * ---------------------------------------------------- */

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException 
	{
		//If mouse over and lmb clicked, begin drag.
		if(translationAxisMouseOver && button == 0)
			translationAxisDrag = true;
		else
			super.mouseClicked(x, y, button);
	}

	@Override
	public void mouseReleased(int x, int y, int which) 
	{
		super.mouseReleased(x, y, which);
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
		if(selectedPart != null)
		{
			GL11.glPushMatrix();

			if(selectedPart instanceof PartRotation)
			{
				GL11.glPopMatrix();
				super.processRay();
				return;
			} else if (selectedPart instanceof PartPropTranslation || selectedPart instanceof PartPropScale)
			{
				ItemStack itemstack = entityToRender.getHeldItem(EnumHand.MAIN_HAND);
				if (selectedPart.getName().equals("prop_trans") || selectedPart.getName().equals("prop_scale"))
				{
					ModelHandler.modelRenderer.transformToItemCentreRight(itemstack);
				} else
				{
					ModelHandler.modelRenderer.transformToItemCentreLeft(ModelHandler.modelRenderer.getLeftItem());
				}
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
					hoveredPart = null;
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
			GL11.glPushMatrix();
			Part part = selectedPart;
			if(part instanceof PartEntityPos)
				GL11.glTranslated(-entityToRender.posX, -entityToRender.posY, -entityToRender.posZ);
			translationAxisMouseOver = true;
			translationAxisPlane = dim;
			int i = translationAxisPlane == 0 ? 2 : translationAxisPlane - 1;
			translationGuidePoint = getMouseVectorInPlane(i);
			initialTranslationGuidePoint = translationGuidePoint;
			Vec3d v = null;
			switch(translationAxisPlane)
			{
			case 0: v = new Vec3d(1, 0, 0); break;
			case 1: v = new Vec3d(0, 1, 0); break;
			case 2: v = new Vec3d(0, 0, 1); break; 
			}
			if(translationGuidePoint != null)
				prevTranslationDelta = MathHelper.getLineScalarForClosestPoint(new Vec3d(0, 0, 0), v, translationGuidePoint);
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
			Vec3d p = null;
			Vec3d n = null;
			switch(i)
			{
			case 0: 
				p = new Vec3d(MathHelper.rotationWheelRadius, 0, 0); 
				n = new Vec3d(0, 1, 0); 
				break;
			case 1: 
				p = new Vec3d(0, MathHelper.rotationWheelRadius, 0); 
				n = new Vec3d(0, 0, 1); 
				break;
			case 2: 
				p = new Vec3d(0, 0, MathHelper.rotationWheelRadius); 
				n = new Vec3d(1, 0, 0); 
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
			Vec3d v = null;
			switch(translationAxisPlane)
			{
			case 0: v = new Vec3d(1, 0, 0); break;
			case 1: v = new Vec3d(0, 1, 0); break;
			case 2: v = new Vec3d(0, 0, 1); break; 
			}

			double translationDelta = MathHelper.getLineScalarForClosestPoint(new Vec3d(0, 0, 0), v, translationGuidePoint);
			double d = translationDelta - prevTranslationDelta;
			if(!Double.isNaN(d))
			{
				updatePartValue(d, translationAxisPlane);
				if(selectedPart instanceof PartEntityPos || selectedPart instanceof PartPropScale)
					prevTranslationDelta = translationDelta;
				else
					prevTranslationDelta = translationDelta - d;
			}
		}
	}

	@Override
	protected void onControllerDrag()
	{
		Part part = selectedPart;
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
		Vec3d origin = new Vec3d(0.0F, 0.0F, 0.0F);
		int colour = 0xFFFFFF;
		Vec3d v = null;
		for(int i = 0; i < 3; i++)
		{
			switch(i)
			{
			case 0: 
				colour = 0xFF0000; 
				v = new Vec3d(MathHelper.rotationWheelRadius, 0.0F, 0.0F);
				break;
			case 1: 
				colour = 0x00FF00; 
				v = new Vec3d(0.0F, MathHelper.rotationWheelRadius, 0.0F);
				break;
			case 2: 
				colour = 0x0000FF; 
				v = new Vec3d(0.0F, 0.0F, MathHelper.rotationWheelRadius);
				break;
			}
			if(translationAxisPlane != null && translationAxisPlane == i)
				drawLine(origin, v, colour, 1.0F, 3.0F);
			else
				drawLine(origin, v, colour, 0.4F, 2.0F);
		}	
	}


}
