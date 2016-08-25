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
public class PartObj extends PartRotation
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

	//----------------------------------------------------------------
	// 							 Selection
	//----------------------------------------------------------------

	/**
	 * Test to see if a ray insects with this part.
	 * @param p0 - Point on ray.
	 * @param p1 - Another point on ray.
	 * @return - Minimum distance from p0 to part, null if no intersect exists.
	 */
	public Double testRay()
	{		
		GL11.glPushMatrix();

		//Get all parents that need compensating for.
		AnimationParenting anipar = AnimationData.getAnipar(modelObj.getEntityType());
		List<PartObj> parents = new ArrayList<PartObj>();
		PartObj p = this;
		parents.add(p);
		while(anipar.hasParent(p))
		{
			p = anipar.getParent(p);
			parents.add(0, p);
		}

		//Compensate for all parents. TODO remove compensate Part rotation method
		for(PartObj q : parents)
			q.move();
		
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

	public void postRenderItem()
	{
		//Adjust initial position.
		GL11.glRotatef(180F, 1, 0, 0);
		GL11.glTranslatef(0, -1.6F, 0);
		
		//Actually do the post rendering here.
		postRenderAll();
		
		//Re-adjust positon to align with hand in resting position.
		GL11.glRotatef(180F, 1, 0, 0);
		GL11.glTranslatef(-0.06F,0.05F,0.1F);
	}

	/**
	 * Complete post render - parent post render, translation for this part, then rotation for this part.
	 */
	public void postRenderAll()
	{
		postRenderAllTrans();
		rotate();
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

			p.rotate();

		}
		return totalTranslation;
	}

	public void move()
	{
		GL11.glTranslatef(-rotationPoint[0], -rotationPoint[1], -rotationPoint[2]);
		rotate();
		GL11.glTranslatef(rotationPoint[0], rotationPoint[1], rotationPoint[2]);    
	}

	public float[] createRotationMatrixFromAngles()
	{
		double sx = Math.sin(-valueX);
		double sy = Math.sin(-valueY);
		double sz = Math.sin(-valueZ);
		double cx = Math.cos(-valueX);
		double cy = Math.cos(-valueY);
		double cz = Math.cos(-valueZ);

		float m0 = (float) (cy*cz);
		float m1 = (float) (sx*sy*cz-cx*sz);
		float m2 = (float) (cx*sy*cz+sx*sz);
		float m3 = (float) (cy*sz);
		float m4 = (float) (sx*sy*sz+cx*cz);
		float m5 = (float) (cx*sy*sz-sx*cz);
		float m6 = (float) -sy;
		float m7 = (float) (sx*cy);
		float m8 = (float) (cx*cy);

		return new float[]{m0,m1,m2,m3,m4,m5,m6,m7,m8};
	}
}
