package MCEntityAnimator.render.objRendering.parts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.sun.org.apache.xpath.internal.operations.Variable;

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
	private boolean showModel;

	//XXX
	private Map<Face, TextureCoordinate[]> defaultTextureCoords;

	private boolean visible;
	private Bend bend = null;
	public GroupObject groupObj;
	private String displayName;

	public PartObj(ModelObj modelObject, GroupObject groupObj) 
	{
		super(modelObject, (groupObj.name.contains("_") ? groupObj.name.substring(0, groupObj.name.indexOf("_")) : groupObj.name).toLowerCase());
		this.groupObj = groupObj;
		this.displayName = getName();
		defaultTextureCoords = new HashMap<Face, TextureCoordinate[]>();
		setDefaultTCsToCurrentTCs();
		visible = true;
		setupRotationMatrix();
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
		//		float[] rotationMatrixF = new 
		//				float[]{1,0,0,0,
		//						0,1,0,0,
		//						0,0,1,0,
		//						0,0,0,1};
		//		ByteBuffer vbb = ByteBuffer.allocateDirect(rotationMatrixF.length*4);
		//		vbb.order(ByteOrder.nativeOrder());
		//		rotationMatrix = vbb.asFloatBuffer();
		//		rotationMatrix.put(rotationMatrixF);
		//		rotationMatrix.position(0);
	}

	//----------------------------------------------------------------
	// 							 Selection
	//----------------------------------------------------------------

	public void moveForAllParts()
	{
		//		AnimationParenting anipar = AnimationData.getAnipar(modelObj.getEntityType());
		//		List<PartObj> parts = new ArrayList<PartObj>();
		//		PartObj child = this;
		//		PartObj parent;
		//		parts.add(0, child);
		//		while((parent = anipar.getParent(child)) != null)
		//		{
		//			parts.add(0, parent);
		//			child = parent;
		//		}
		//
		//		for(PartObj p : parts)
		//			p.move();
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
			//System.out.println(groupObj.faces.get(0).vertices[0].x + ", " + groupObj.faces.get(0).vertices[0].y + ", " + groupObj.faces.get(0).vertices[0].z);
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
			p.move(); 
		}
	}

	/**
	 * Complete post render - parent post render, translation for this part, then rotation for this part.
	 */
	public void postRenderAll()
	{
		postRenderAllTrans();
		GL11.glMultMatrix(createRotationMatrixFromAngles());
	}

	/**
	 * Complete post render except rotation of this part.
	 */
	public void postRenderAllTrans()
	{
		float[] totalTranslation = postRenderParent();
		GL11.glTranslated(-getRotationPoint(0)-totalTranslation[0], -getRotationPoint(1)-totalTranslation[1], -getRotationPoint(2)-totalTranslation[2]);
	}

	/**
	 * Adjust GL11 Matrix for all parents of this part.
	 */
	//TODO could this be done recursively?
	public float[] postRenderParent()
	{
		//Generate a list of parents: {topParent, topParent - 1,..., parent}
		AnimationParenting anipar = AnimationData.getAnipar(modelObj.getEntityType());
		List<PartObj> parts = new ArrayList<PartObj>();
		PartObj child = this;
		PartObj parent;
		while((parent = anipar.getParent(child)) != null)
		{
			parts.add(0, parent);
			child = parent;
		}

		float[] totalTranslation = new float[]{0,0,0};
		for(PartObj p : parts)
		{			
			GL11.glTranslated(-p.getRotationPoint(0)-totalTranslation[0], -p.getRotationPoint(1)-totalTranslation[1], -p.getRotationPoint(2)-totalTranslation[2]);
			for(int i = 0; i < 3; i++)
				totalTranslation[i] = -p.getRotationPoint(i);

			GL11.glMultMatrix(p.createRotationMatrixFromAngles());

		}
		return totalTranslation;
	}

	public void move()
	{
		GL11.glTranslatef(-rotationPoint[0], -rotationPoint[1], -rotationPoint[2]);
		GL11.glMultMatrix(createRotationMatrixFromAngles());
		GL11.glTranslatef(rotationPoint[0], rotationPoint[1], rotationPoint[2]);    

	}

	public void rotateLocal(float delta, int dim)
	{
		//System.out.println("Detla: " + delta);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glMultMatrix(createRotationMatrixFromAngles());

		//GL11.glTranslatef(-rotationPoint[0], -rotationPoint[1], -rotationPoint[2]);
		//		GL11.glRotatef(-valueZ,0,0,1);
		//		GL11.glRotatef(-valueY,0,1,0);
		//		GL11.glRotatef(-valueX,1,0,0);
		switch(dim)
		{
		case 0: GL11.glRotated(delta,1,0,0); break;
		case 1: GL11.glRotated(delta,0,1,0); break;
		case 2: GL11.glRotated(delta,0,0,1); break;
		}
		//		GL11.glRotatef(valueZ,0,0,1);
		//		GL11.glRotatef(valueY,0,1,0);
		//		GL11.glRotatef(valueX,1,0,0);

		float[] rotationMatrixF = new float[16];
		ByteBuffer vbb = ByteBuffer.allocateDirect(rotationMatrixF.length*4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer rotationMatrix = vbb.asFloatBuffer();
		rotationMatrix.put(rotationMatrixF);
		rotationMatrix.position(0);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, rotationMatrix);
		updateRotationAngles(rotationMatrix);
		GL11.glPopMatrix();
	}

	private void updateRotationAngles(FloatBuffer rotationMatrix)
	{
		//		System.out.println("Matrix proper:");
		//		System.out.println(String.format("%f %f %f", rotationMatrix.get(0),rotationMatrix.get(1),rotationMatrix.get(2)));
		//		System.out.println(String.format("%f %f %f", rotationMatrix.get(4),rotationMatrix.get(5),rotationMatrix.get(6)));
		//		System.out.println(String.format("%f %f %f", rotationMatrix.get(8),rotationMatrix.get(9),rotationMatrix.get(10)));


		double x,y,z;
		float r8 = rotationMatrix.get(8);
		if(Math.abs(r8) != 1)
		{
			y = -Math.asin(r8);

			//Find x value
			float r9 = rotationMatrix.get(9);
			float r10 = rotationMatrix.get(10);
			x = Math.atan2(r9/Math.cos(y), r10/Math.cos(y));

			//Find z value
			float r0 = rotationMatrix.get(0);
			float r4 = rotationMatrix.get(4);
			z = Math.atan2(r4/Math.cos(y), r0/Math.cos(y));
		}
		else
		{
			//Gimbal lock case - infinite solutions, set z to zero.
			z = 0.0F;
			float r1 = rotationMatrix.get(1);
			float r2 = rotationMatrix.get(2);
			if(r8 == -1)
			{
				y = Math.PI/2;
				x = z + Math.atan2(r1, r2);
			}
			else
			{
				y = -Math.PI/2;
				x = z + Math.atan2(-r1,-r2);
			}
		}

		//		System.out.println("Matrix reconstructed:");
		//		
		//		double m0 = Math.cos(y)*Math.cos(z);
		//		double m1 = Math.sin(x)*Math.sin(y)*Math.cos(z)-Math.cos(x)*Math.sin(z);
		//		double m2 = Math.cos(x)*Math.sin(y)*Math.cos(z)+Math.sin(x)*Math.sin(z);
		//		double m3 = Math.cos(y)*Math.sin(z);
		//		double m4 = Math.sin(x)*Math.sin(y)*Math.sin(z)+Math.cos(x)*Math.cos(z);
		//		double m5 = Math.cos(x)*Math.sin(y)*Math.sin(z)-Math.sin(x)*Math.cos(z);
		//		double m6 = -Math.sin(y);
		//		double m7 = Math.sin(x)*Math.cos(y);
		//		double m8 = Math.cos(x)*Math.cos(y);
		//		
		//		System.out.println(String.format("%f %f %f",m0,m1,m2));
		//		System.out.println(String.format("%f %f %f",m3,m4,m5));
		//		System.out.println(String.format("%f %f %f",m6,m7,m8));


		valueX = (float) (x/Math.PI*180F);
		valueY = (float) (y/Math.PI*180F);
		valueZ = (float) (z/Math.PI*180F);
	}

	public FloatBuffer createRotationMatrixFromAngles()
	{
		double x = valueX/180F*Math.PI;
		double y = valueY/180F*Math.PI;
		double z = valueZ/180F*Math.PI;

		float m0 = (float) (Math.cos(y)*Math.cos(z));
		float m1 = (float) (Math.sin(x)*Math.sin(y)*Math.cos(z)-Math.cos(x)*Math.sin(z));
		float m2 = (float) (Math.cos(x)*Math.sin(y)*Math.cos(z)+Math.sin(x)*Math.sin(z));
		float m3 = (float) (Math.cos(y)*Math.sin(z));
		float m4 = (float) (Math.sin(x)*Math.sin(y)*Math.sin(z)+Math.cos(x)*Math.cos(z));
		float m5 = (float) (Math.cos(x)*Math.sin(y)*Math.sin(z)-Math.sin(x)*Math.cos(z));
		float m6 = (float) -Math.sin(y);
		float m7 = (float) (Math.sin(x)*Math.cos(y));
		float m8 = (float) (Math.cos(x)*Math.cos(y));

		float[] rotationMatrixF = new 
				float[]{m0,m1,m2,0,
						m3,m4,m5,0,
						m6,m7,m8,0,
						0,0,0,1};
		ByteBuffer vbb = ByteBuffer.allocateDirect(rotationMatrixF.length*4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer m = vbb.asFloatBuffer();
		m.put(rotationMatrixF);
		m.position(0);
		return m;
	}
}
