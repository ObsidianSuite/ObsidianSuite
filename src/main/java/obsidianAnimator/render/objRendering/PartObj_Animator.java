package obsidianAnimator.render.objRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import obsidianAPI.render.PartObj;
import obsidianAPI.render.PartRotation;
import obsidianAnimator.animation.AnimationParenting;
import obsidianAnimator.render.MathHelper;

/**
 * One partObj for each 'part' of the model.
 * 
 */
public class PartObj_Animator extends PartObj
{
	private Map<Face, TextureCoordinate[]> defaultTextureCoords;

	public PartObj_Animator(ModelObj modelObject, GroupObject groupObj) 
	{
		super(modelObject, groupObj);
		defaultTextureCoords = new HashMap<Face, TextureCoordinate[]>();
		setDefaultTCsToCurrentTCs();
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
		AnimationParenting anipar = modelObj.parenting;
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

	@Override
	public void updateTextureCoordinates()
	{	
		updateTextureCoordinates(modelObj.isMainHighlight(this), modelObj.isPartHighlighted(this), true);
	}

	/**
	 * Change the texture coordinates and texture if the part is highlighted.
	 */
	private void updateTextureCoordinates(boolean mainHighlight, boolean otherHighlight, boolean bindTexture)
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
}
