package MCEntityAnimator.render.objRendering.parts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationParenting;
import MCEntityAnimator.render.MathHelper;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.RayTrace;
import MCEntityAnimator.render.objRendering.bend.Bend;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;

/**
 * One partObj for each 'part' of the model.
 * 
 */
public class PartObj extends Part
{
	private float[] rotationPoint;
	private float axisRotX, axisRotY, axisRotZ;
	private float prevAxisRotX, prevAxisRotY, prevAxisRotZ;
	private boolean showModel;

	//XXX
	private Map<Face, TextureCoordinate[]> defaultTextureCoords;

	private boolean visible;
	private Bend bend = null;
	public GroupObject groupObj;
	private String displayName;
	private FloatBuffer rotationMatrix;

	public PartObj(ModelObj modelObject, GroupObject groupObj) 
	{
		super(modelObject, (groupObj.name.contains("_") ? groupObj.name.substring(0, groupObj.name.indexOf("_")) : groupObj.name).toLowerCase());
		this.groupObj = groupObj;
		this.displayName = getName();
		defaultTextureCoords = new HashMap<Face, TextureCoordinate[]>();
		setDefaultTCsToCurrentTCs();
		visible = true;
		setupRotationMatrix();
		axisRotX = originalValues[0];
		axisRotY = originalValues[1];
		axisRotZ = originalValues[2];
		prevAxisRotX = axisRotX;
		prevAxisRotY = axisRotY;
		prevAxisRotZ = axisRotZ;
	}

	//------------------------------------------
	//              Basics
	//------------------------------------------

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public void setShowModel(boolean show)
	{
		showModel = show;
	}

	public boolean getShowModel()
	{
		return showModel;
	}

	public void setRotationPoint(float[] rot) 
	{
		rotationPoint = rot;
	}

	public float getRotationPoint(int i) 
	{
		return rotationPoint[i];
	}

	public float[] getRotationPoint()
	{
		return rotationPoint;
	}

	public void setVisible(boolean bool)
	{
		visible = bool;
	}

	public void setBend(Bend b)
	{
		bend = b;
	}

	public boolean hasBend()
	{
		return bend != null;
	}

	public void removeBend()
	{
		modelObj.removeBend(bend);
		bend.remove();
		bend = null;
	}

	private void setupRotationMatrix()
	{
		float[] rotationMatrixF = new float[]{1,0,0,0,
				0,1,0,0,
				0,0,1,0,
				0,0,0,1};
		ByteBuffer vbb = ByteBuffer.allocateDirect(rotationMatrixF.length*4);
		vbb.order(ByteOrder.nativeOrder());
		rotationMatrix = vbb.asFloatBuffer();
		rotationMatrix.put(rotationMatrixF);
		rotationMatrix.position(0);
	}
	
	public void setAxisRotation(int axis, float rot)
	{
		switch(axis)
		{
		case 0: axisRotX = rot; break;
		case 1: axisRotY = rot; break;
		case 2: axisRotZ = rot; break;
		}
	}
	
	//----------------------------------------------------------------
	// 							 Selection
	//----------------------------------------------------------------

	public void moveForAllParts()
	{
		AnimationParenting anipar = AnimationData.getAnipar(modelObj.getEntityType());
		List<PartObj> parts = new ArrayList<PartObj>();
		PartObj child = this;
		PartObj parent;
		parts.add(0, child);
		while((parent = anipar.getParent(child)) != null)
		{
			parts.add(0, parent);
			child = parent;
		}
		
		for(PartObj p : parts)
			p.move();
	}
	
	/**
	 * Test to see if a ray insects with this part.
	 * @param p0 - Point on ray.
	 * @param p1 - Another point on ray.
	 * @return - Minimum distance from p0 to part, null if no intersect exists.
	 */
	public Double testRay()
	{		
		GL11.glPushMatrix();
		moveForAllParts();
		Double min = null;
		for(Face f : groupObj.faces)
		{
			Double d = MathHelper.rayIntersectsFace(RayTrace.getRayTrace(), f);
			if(d != null && (min == null || d < min))
				min = d;
		}
		GL11.glPopMatrix();
		return min;	
	}

	//------------------------------------------
	//         Rendering and Rotating
	//------------------------------------------

	/**
	 * Stores the current texture coordinates in default texture coords.
	 * This is required in case a bend is removed, then the texture coords can be restored.
	 * XXX
	 */
	public void setDefaultTCsToCurrentTCs()
	{
		for(Face f : groupObj.faces)
		{
			if(f.textureCoordinates == null)
			{
				f.textureCoordinates = new TextureCoordinate[3];
				for(int i = 0; i < 3; i++)
				{
					f.textureCoordinates[i] = new TextureCoordinate(0, 0);
				}
			}   

			TextureCoordinate[] coordsToStore = new TextureCoordinate[3];
			for(int i = 0; i < 3; i++)
			{
				coordsToStore[i] = new TextureCoordinate(f.textureCoordinates[i].u, f.textureCoordinates[i].v);
			}

			defaultTextureCoords.put(f, coordsToStore);
		}
	}

	public void updateTextureCoordinates()
	{	
		updateTextureCoordinates(modelObj.isMainHighlight(this), modelObj.isPartHighlighted(this), true);
	}
	
	/**
	 * Change the texture coordinates and texture if the part is highlighted.
	 */
	public void updateTextureCoordinates(boolean mainHighlight, boolean otherHighlight, boolean bindTexture)
	{		
		boolean useHighlightCoords = true;
		ResourceLocation texture;
		TextureCoordinate[] highlightCoords = new TextureCoordinate[]{
				new TextureCoordinate(0.0F, 0.0F), 
				new TextureCoordinate(0.5F, 0.0F), 
				new TextureCoordinate(0.0F, 0.5F)};
		if(mainHighlight)
			texture = ModelObj.pinkResLoc;
		else if(otherHighlight)
			texture = ModelObj.whiteResLoc;
		else
		{
			texture = modelObj.getTexture();
			useHighlightCoords = false;
		}
		
		//System.out.println(this.getDisplayName() + " " + texture);

		if(bindTexture)
			Minecraft.getMinecraft().getTextureManager().bindTexture(texture);		
		
		for(Face f : groupObj.faces)
		{
			if(useHighlightCoords)
				f.textureCoordinates = highlightCoords;
			else
				f.textureCoordinates = defaultTextureCoords.get(f);
		}
	}

	public void render() 
	{
		GL11.glPushMatrix();
		move();
		updateTextureCoordinates();
		if(visible)
			groupObj.render();
		//Do for children - rotation for parent compensated for!
		List<PartObj> children = AnimationData.getAnipar(modelObj.getEntityType()).getChildren(this);
		if(children != null)
		{
			for(PartObj child : children)
				child.render();  
		}
		GL11.glPopMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(modelObj.getTexture());		
	}

	/**
	 * Setup rotation and translation for a GL11 matrix based on the rotation
	 * and rotation point of this part and all its parents.
	 * @param entityName - Name of the model. 
	 */
	@SideOnly(Side.CLIENT)
	public void postRender()
	{
		//Generate a list of parents: {topParent, topParent - 1,..., parent, this}/
		AnimationParenting anipar = AnimationData.getAnipar(modelObj.getEntityType());
		List<PartObj> parts = new ArrayList<PartObj>();
		PartObj child = this;
		PartObj parent;
		parts.add(0, child);
		while((parent = anipar.getParent(child)) != null)
		{
			parts.add(0, parent);
			child = parent;
		}

		//Translate and rotate all parts, starting with the top parent. 
		PartObj prevPart = null;
		for(PartObj p : parts)
		{
			//TODO Post render: Z formula, temporarily works for player though.
			//Not sure if the formulae will work for other entities.
			float[] currentRotPoint = p.getRotationPoint();
			//If prevPart is null, ie part is top parent, use 0,0,0.
			float[] prevRotPoint = prevPart != null ? prevPart.getRotationPoint() : new float[]{0.0F, 0.0F, 0.0F};
			prevPart = p;

			float[] trans = new float[3];
			float[] prevtrans = new float[3];

			for(int i = 0; i < 3; i++)
			{
				trans[i] = 0.0F;
				if(currentRotPoint[i] != 0.0f)
				{
					switch(i)
					{
					case 0: trans[0] = currentRotPoint[0]*-0.78125f; break;
					case 1: trans[1] = currentRotPoint[1]*0.77f + 1.2f; break;
					case 2: /* Z formula */  break;         
					}
				}

				prevtrans[i] = 0.0F;
				if(prevRotPoint[i] != 0.0f)
				{
					switch(i)
					{
					case 0: prevtrans[0] = prevRotPoint[0]*-0.78125f; break;
					case 1: prevtrans[1] = prevRotPoint[1]*0.77f + 1.2f; break;
					case 2: /* Z formula */  break;         
					}
				}
			}

			//Translate, compensating for the previous part translation.
			GL11.glTranslatef(trans[0] - prevtrans[0], trans[1] - prevtrans[1], trans[2] - prevtrans[2]);
			//Rotate
			p.applyRotation(); 
		}
	}

	public void move()
	{
		GL11.glTranslatef(-rotationPoint[0], -rotationPoint[1], -rotationPoint[2]);
		applyRotation();
		GL11.glTranslatef(rotationPoint[0], rotationPoint[1], rotationPoint[2]);    
	}

	public void applyRotation()
	{
		updateRotationMatrix();

		double absY = Math.asin(rotationMatrix.get(8));
		double absX = Math.asin(-rotationMatrix.get(9)/Math.cos(absY));
		double absZ = Math.asin(-rotationMatrix.get(4)/Math.cos(absY));

		double absXA = Math.PI - absX;
		double absYA = Math.PI - absY;
		double absZA = Math.PI - absZ;


		boolean exit = false;
		double x=0.0F,y=0.0F,z=0.0F;
		for(int i = 0; i < 2; i++)
		{
			x = i == 0 ? absX : absXA;
			for(int j = 0; j < 2; j++)
			{
				y = j == 0 ? absY : absYA;
				for(int k = 0; k < 2; k++)
				{
					z = k == 0 ? absZ : absZA;
					boolean pass = true;
					//System.out.println("Try new");
					for(int l = 0; l < 16; l++)
					{
						if(l < 11 && l != 3 && l != 7)
						{
							double t = 0;
							switch(l)
							{
							case 0: t = Math.cos(y)*Math.cos(z); break;
							case 1: t = Math.cos(x)*Math.sin(z)+Math.sin(x)*Math.sin(y)*Math.cos(z); break;
							case 2: t = Math.sin(x)*Math.sin(z)-Math.cos(x)*Math.sin(y)*Math.cos(z); break;
							case 4: t = -Math.cos(y)*Math.sin(z); break;
							case 5: t = Math.cos(x)*Math.cos(z)-Math.sin(x)*Math.sin(y)*Math.sin(z); break;
							case 6: t = Math.sin(x)*Math.cos(z)+Math.cos(x)*Math.sin(y)*Math.sin(z); break;
							case 8: t = Math.sin(y); break;
							case 9: t = -Math.cos(y)*Math.sin(x); break;
							case 10: t = Math.cos(y)*Math.cos(x); break;
							}
							if(Math.abs(t - rotationMatrix.get(l)) > 0.1)
							{
								pass = false;
								break;
							}

						}
					}
					if(pass)
					{
						exit = true;
						break;
					}
				}
				if(exit)
					break;
			}
			if(exit)
				break;
		}
		if(!exit)
		{
			System.out.println("No solution found..");
			System.out.println("   " + valueX + "," + valueY + "," + valueZ);
		}

		valueX = (float) x;
		valueY = (float) y;
		valueZ = (float) z;
		
		GL11.glRotatef((float) (valueX/Math.PI*180.0F), 1.0F, 0.0F, 0.0F);
		GL11.glRotatef((float) (valueY/Math.PI*180.0F), 0.0F, 1.0F, 0.0F);
		GL11.glRotatef((float) (valueZ/Math.PI*180.0F), 0.0F, 0.0F, 1.0F);
	}

	public void updateRotationMatrix()
	{
		float rotX = (float) ((axisRotX - prevAxisRotX)/Math.PI*180.0F);
		float rotY = (float) ((axisRotY - prevAxisRotY)/Math.PI*180.0F);
		float rotZ = (float) ((axisRotZ - prevAxisRotZ)/Math.PI*180.0F);

		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glMultMatrix(rotationMatrix);
		GL11.glRotated(rotX, 1.0F, 0.0F, 0.0F);
		GL11.glRotated(rotY, 0.0F, 1.0F, 0.0F);
		GL11.glRotated(rotZ, 0.0F, 0.0F, 1.0F);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, rotationMatrix);
		GL11.glPopMatrix();

		prevAxisRotX = axisRotX;
		prevAxisRotY = axisRotY;
		prevAxisRotZ = axisRotZ;

	}

	public void renderRotationAxis()
	{
		Vec3 origin = Vec3.createVectorHelper(0.0F, 0.0F, 0.0F);

		GL11.glPushMatrix();
		GL11.glTranslatef(-rotationPoint[0], -rotationPoint[1], -rotationPoint[2]);
		GL11.glMultMatrix(rotationMatrix);
		
		drawLine(Vec3.createVectorHelper(-0.05F, 0.0F, 0.0F), Vec3.createVectorHelper(0.05F, 0.0F, 0.0F), 0xFFFFFF);
		drawLine(Vec3.createVectorHelper(0.0F, -0.05F, 0.0F), Vec3.createVectorHelper(0.0F, 0.05F, 0.0F), 0xFFFFFF);
		drawLine(Vec3.createVectorHelper(0.0F, 0.0F, -0.05F), Vec3.createVectorHelper(0.0F, 0.0F, 0.05F), 0xFFFFFF);
		
		drawCircle(origin, 0.5F, 0, 1.0F, 0.0F, 0.0F);
		drawCircle(origin, 0.5F, 1, 0.0F, 1.0F, 0.0F);
		drawCircle(origin, 0.5F, 2, 0.0F, 0.0F, 1.0F);
		GL11.glPopMatrix();
	}

	/**
	 * Draw a circle in the x,y or z plane. 
	 * @param c - Centre of circle.
	 * @param r - Radius of circle.
	 * @param plane - 0,1,2 for x,y and z.
	 * @param red - red colour (0 - 1F)
	 * @param green - green colour (0 - 1F)
	 * @param blue - blue colour (0 - 1F)
	 */
	private void drawCircle(Vec3 c, double r, int plane, double red, double green, double blue)
	{		
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor3d(red, green, blue);
		GL11.glLineWidth(3.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
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
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
	
	//XXX
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
