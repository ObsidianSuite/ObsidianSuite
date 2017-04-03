package obsidianAnimator.gui.sequence;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import obsidianAPI.Util;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;
import obsidianAPI.render.part.PartRotation;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.gui.GuiEntityRenderer;
import obsidianAnimator.render.MathHelper;
import obsidianAnimator.render.RayTrace;

public class GuiEntityRendererWithRotation extends GuiEntityRenderer
{

	//True if mouse is hovering over the rotation wheel.
	private boolean rotationWheelMouseOver = false;
	//True if mouse was clicked while hovering over the rotation wheel and is still down.
	private boolean rotationWheelDrag = false;
	//The current plane selection of the rotation wheel (0-x,1-y,2-z)
	private Integer rotationWheelPlane;

	//Vector for current rotation of wheel while dragging.
	private Vec3 rotationGuidePoint;
	//Vector for rotation of wheel when first clicked on.
	private Vec3 initialRotationGuidePoint;
	private double prevRotationWheelDelta = 0.0F;
	
	//Used for storing the values of the part before rotation 
	private float[] preRotationPartValues = new float[3];

	public GuiEntityRendererWithRotation(String entityName)
	{
		super(entityName);
	}

	/* ---------------------------------------------------- *
	 * 		     	   Part Manipulation					*
	 * ---------------------------------------------------- */

	protected void updatePartValue(double value, int dim)
	{
		Part part = selectedPart;
		if(part instanceof PartRotation)
			((PartRotation) part).rotateLocal((float) value, dim);
		else
			part.setValue((float) (part.getValue(dim) + value), dim);
	}

	/* ---------------------------------------------------- *
	 * 						Input							*
	 * ---------------------------------------------------- */

	@Override
	protected void mouseClicked(int x, int y, int button) 
	{
		//If mouse over and lmb clicked, begin drag.
		if(rotationWheelMouseOver && button == 0)
		{
			rotationWheelDrag = true;
			storePreRotationPartValues();
		}
		else
		{
			rotationWheelDrag = false;
			super.mouseClicked(x, y, button);
		}
	}

	@Override
	public void mouseMovedOrUp(int x, int y, int which) 
	{
		super.mouseMovedOrUp(x, y, which);
		//If lmb lifted, reset drag, guide and delta.
		if(which == 0)
		{
			if(rotationWheelDrag)
				onControllerRelease();
			rotationGuidePoint = null;
			rotationWheelDrag = false;
			prevRotationWheelDelta = 0.0F;
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
			if(!rotationWheelDrag)
			{
				updateWheelMouseOver();
				if(!rotationWheelMouseOver)
					super.processRay();
			}
			else
				processRotation();

			GL11.glPushMatrix();
			applyRotationTransform();
			drawRotationWheel();
			GL11.glPopMatrix();
		}
		else
			super.processRay();
	}

	/**
	 * Update the rotation wheel states based on the current part.
	 */
	private void updateWheelMouseOver()
	{
		GL11.glPushMatrix();

		applyRotationTransform();

		Integer dim = testRotationRay();
		if(dim != null)
		{
			rotationWheelMouseOver = true;
			rotationWheelPlane = dim;
			hoveredPart = null;
		}
		else
		{
			rotationWheelMouseOver = false;
			rotationWheelPlane = null;
		}

		GL11.glPopMatrix();
	}

	private void processRotation()
	{		
		GL11.glPushMatrix();

		//Rotation calculation must be done relative to the state before dragging begins.
		//Makes sure that the new rotation input doesn't come into effect. 
		applyPreRotationTransform();

		rotationGuidePoint = getMouseVectorInPlane(rotationWheelPlane);
		if(initialRotationGuidePoint == null)
			initialRotationGuidePoint = rotationGuidePoint;

		onControllerDrag();

		GL11.glPopMatrix();
	}

	/**
	 * Test to see if the ray intersects with the rotation wheel at all.
	 * @return Dimension of wheel that intersection is with. (0-x,1-y,2-z).
	 */
	private Integer testRotationRay()
	{
		Double min = null;
		Integer dim = null;
		Vec3 p = Vec3.createVectorHelper(0,0,0);
		for(int i = 0; i < 3; i++)
		{
			Vec3 n = null;
			switch(i)
			{
			case 0: n = Vec3.createVectorHelper(1, 0, 0); break;
			case 1: n = Vec3.createVectorHelper(0, 1, 0); break;
			case 2: n = Vec3.createVectorHelper(0, 0, 1); break;
			}
			Double d = MathHelper.rayIntersectsRotationWheel(RayTrace.getRayTrace(), p, n);
			if(d != null && (min == null || d < min))
			{
				min = d;
				dim = i;
			}
		}
		return dim;	
	}

	public Vec3 getMouseVectorInPlane(int dim)
	{
		Vec3 n = null;
		Vec3 p = Vec3.createVectorHelper(0,0,0);
		switch(dim)
		{
		case 0: n = Vec3.createVectorHelper(1, 0, 0); break;
		case 1: n = Vec3.createVectorHelper(0, 1, 0); break;
		case 2: n = Vec3.createVectorHelper(0, 0, 1); break; 
		}
		Vec3 v = MathHelper.getRayPlaneIntersection(RayTrace.getRayTrace(), p, n);
		return v;
	}

	private void processWheelRotation()
	{
		if(rotationGuidePoint != null && initialRotationGuidePoint != null)
		{
			Vec3 n = null;
			switch(rotationWheelPlane)
			{
			case 0: n = Vec3.createVectorHelper(1, 0, 0); break;
			case 1: n = Vec3.createVectorHelper(0, 1, 0); break;
			case 2: n = Vec3.createVectorHelper(0, 0, 1); break; 
			}
			double rotationWheelDelta = MathHelper.getAngleBetweenVectors(rotationGuidePoint, initialRotationGuidePoint, n);
			double d = rotationWheelDelta - prevRotationWheelDelta;
			if(!Double.isNaN(d))
			{
				updatePartValue(-d/Math.PI*180F, rotationWheelPlane);
				prevRotationWheelDelta = rotationWheelDelta;
			}
		}
	}

	protected void onControllerDrag()
	{
		rotationGuidePoint = getMouseVectorInPlane(rotationWheelPlane);
		processWheelRotation();
	}

	protected void onControllerRelease()
	{
		initialRotationGuidePoint = null;
	}

	/* ---------------------------------------------------- *
	 * 					Transformations				     	*
	 * ---------------------------------------------------- */	

	private void storePreRotationPartValues()
	{
		if(selectedPart != null)
			preRotationPartValues = selectedPart.getValues();
	}
	
	private void applyPreRotationTransform()
	{
		if(selectedPart != null)
		{
			float[] currentValues = selectedPart.getValues();
			selectedPart.setValues(preRotationPartValues);
			applyRotationTransform();
			selectedPart.setValues(currentValues);
		}
	}
	
	private void applyRotationTransform()
	{
		PartObj partObj = null;
		if(selectedPart instanceof PartObj)
		{
			partObj = (PartObj) selectedPart;
			partObj.postRenderAll();
		}
		else if(selectedPart instanceof PartRotation) //Prop rotation
		{
			ItemStack itemstack = entityToRender.getHeldItem();
			ModelHandler.modelRenderer.transformToItemCentreAndRotate(itemstack);
		}
	}

	/* ---------------------------------------------------- *
	 * 						Drawing							*
	 * ---------------------------------------------------- */	

	private void drawRotationWheel()
	{
		Vec3 origin = Vec3.createVectorHelper(0.0F, 0.0F, 0.0F);

		drawLine(Vec3.createVectorHelper(-0.05F, 0.0F, 0.0F), Vec3.createVectorHelper(0.05F, 0.0F, 0.0F), 0xFFFFFF);
		drawLine(Vec3.createVectorHelper(0.0F, -0.05F, 0.0F), Vec3.createVectorHelper(0.0F, 0.05F, 0.0F), 0xFFFFFF);
		drawLine(Vec3.createVectorHelper(0.0F, 0.0F, -0.05F), Vec3.createVectorHelper(0.0F, 0.0F, 0.05F), 0xFFFFFF);

		int colour = 0xFFFFFF;
		for(int i = 0; i < 3; i++)
		{
			switch(i)
			{
			case 0: colour = 0xFF0000; break;
			case 1: colour = 0x00FF00; break;
			case 2: colour = 0x0000FF; break;
			}
			if(rotationWheelPlane != null && rotationWheelPlane == i)
				drawCircle(origin, MathHelper.rotationWheelRadius, i, colour, 4.0F, 1.0F);
			else
				drawCircle(origin, MathHelper.rotationWheelRadius, i, colour, 3.0F, 0.4F);
		}	
	}

	/**
	 * Draw a circle in the x,y or z plane. 
	 * @param c - Centre of circle.
	 * @param r - Radius of circle.
	 * @param plane - 0,1,2 for x,y and z.
	 */
	private void drawCircle(Vec3 c, double r, int plane, int colour, float width, float alpha)
	{		
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);		
		float[] rgb = MathHelper.intToRGB(colour);
		GL11.glColor4f(rgb[0], rgb[1], rgb[2], alpha);
		GL11.glLineWidth(width);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		for(int i = 0; i < 360; i++)
		{
			double rad = i/180F*Math.PI;
			double x = c.xCoord,y = c.yCoord,z = c.zCoord;
			switch(plane)
			{
			case 0:
				y = c.yCoord + r*Math.sin(rad);
				z = c.zCoord + r*Math.cos(rad);
				break;
			case 1:
				x = c.xCoord + r*Math.sin(rad);
				z = c.zCoord + r*Math.cos(rad);
				break;
			case 2:
				x = c.xCoord + r*Math.sin(rad);
				y = c.yCoord + r*Math.cos(rad);
				break;
			}
			GL11.glVertex3d(x,y,z);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	private void drawLine(Vec3 p1, Vec3 p2, int color)
	{
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);

		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawing(1);
		tessellator.setColorOpaque_I(color);
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
