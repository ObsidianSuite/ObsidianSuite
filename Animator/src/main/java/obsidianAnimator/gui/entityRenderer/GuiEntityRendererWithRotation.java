package obsidianAnimator.gui.entityRenderer;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;
import obsidianAPI.render.part.PartRotation;
import obsidianAPI.render.part.prop.PartPropRotation;
import obsidianAnimator.data.ModelHandler;
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
	private Vec3d rotationGuidePoint;
	//Vector for rotation of wheel when first clicked on.
	private Vec3d initialRotationGuidePoint;
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
	protected void mouseClicked(int x, int y, int button) throws IOException 
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
	public void mouseReleased(int x, int y, int which) 
	{
		super.mouseReleased(x, y, which);
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
		if(selectedPart != null && selectedPart instanceof PartRotation)
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
		Vec3d p = new Vec3d(0,0,0);
		for(int i = 0; i < 3; i++)
		{
			Vec3d n = null;
			switch(i)
			{
			case 0: n = new Vec3d(1, 0, 0); break;
			case 1: n = new Vec3d(0, 1, 0); break;
			case 2: n = new Vec3d(0, 0, 1); break;
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

	public Vec3d getMouseVectorInPlane(int dim)
	{
		Vec3d n = null;
		Vec3d p = new Vec3d(0,0,0);
		switch(dim)
		{
		case 0: n = new Vec3d(1, 0, 0); break;
		case 1: n = new Vec3d(0, 1, 0); break;
		case 2: n = new Vec3d(0, 0, 1); break; 
		}
		Vec3d v = MathHelper.getRayPlaneIntersection(RayTrace.getRayTrace(), p, n);
		return v;
	}

	private void processWheelRotation()
	{
		if(rotationGuidePoint != null && initialRotationGuidePoint != null)
		{
			Vec3d n = null;
			switch(rotationWheelPlane)
			{
			case 0: n = new Vec3d(1, 0, 0); break;
			case 1: n = new Vec3d(0, 1, 0); break;
			case 2: n = new Vec3d(0, 0, 1); break; 
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
		else if(selectedPart instanceof PartPropRotation) //Prop rotation
		{
			EnumHandSide handSide = selectedPart.getName().equals("prop_rot") ? EnumHandSide.RIGHT : EnumHandSide.LEFT;
			ModelHandler.modelRenderer.transformToHandAndRotate(handSide);
		}
	}

	/* ---------------------------------------------------- *
	 * 						Drawing							*
	 * ---------------------------------------------------- */	

	private void drawRotationWheel()
	{
		Vec3d origin = new Vec3d(0.0F, 0.0F, 0.0F);

		drawLine(new Vec3d(-0.05F, 0.0F, 0.0F), new Vec3d(0.05F, 0.0F, 0.0F), 0xFFFFFF, 1.0f, 1.0f);
		drawLine(new Vec3d(0.0F, -0.05F, 0.0F), new Vec3d(0.0F, 0.05F, 0.0F), 0xFFFFFF, 1.0f, 1.0f);
		drawLine(new Vec3d(0.0F, 0.0F, -0.05F), new Vec3d(0.0F, 0.0F, 0.05F), 0xFFFFFF, 1.0f, 1.0f);

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
	private void drawCircle(Vec3d c, double r, int plane, int colour, float width, float alpha)
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
			double x = c.x,y = c.y,z = c.z;
			switch(plane)
			{
			case 0:
				y = c.y + r*Math.sin(rad);
				z = c.z + r*Math.cos(rad);
				break;
			case 1:
				x = c.x + r*Math.sin(rad);
				z = c.z + r*Math.cos(rad);
				break;
			case 2:
				x = c.x + r*Math.sin(rad);
				y = c.y + r*Math.cos(rad);
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

}
