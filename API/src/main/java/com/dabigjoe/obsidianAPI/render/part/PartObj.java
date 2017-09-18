package com.dabigjoe.obsidianAPI.render.part;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.dabigjoe.obsidianAPI.render.ModelObj;
import com.dabigjoe.obsidianAPI.render.bend.Bend;
import com.dabigjoe.obsidianAPI.render.wavefront.Face;
import com.dabigjoe.obsidianAPI.render.wavefront.GroupObject;
import com.dabigjoe.obsidianAPI.render.wavefront.TextureCoordinate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * One partObj for each 'part' of the model.
 */
public class PartObj extends PartRotation
{
	private float[] rotationPoint;
	public GroupObject groupObj;
	private String displayName;

	protected Map<Face, TextureCoordinate[]> defaultTextureCoords = Maps.newHashMap();

	private PartObj parent;
	private Set<PartObj> children = Sets.newHashSet();
	private List<PartObj> mergedParts = new ArrayList<PartObj>();
	private boolean merged = false;
	private Bend bend = null;

	public PartObj(ModelObj modelObject, GroupObject groupObj)
	{
		super(modelObject, groupObj.name);
		this.groupObj = groupObj;
		this.displayName = getName();
		this.rotationPoint = new float[]{0,0,0};
		setDefaultTCsToCurrentTCs();
	}

	public void setParent(PartObj parent)
	{
		this.parent = parent;
	}

	public PartObj getParent()
	{
		return parent;
	}

	public boolean hasParent()
	{
        return parent != null;
    }

	public void addChild(PartObj child)
	{
		children.add(child);
	}

	public void removeChild(PartObj child)
	{
		children.remove(child);
	}

	public Set<PartObj> getChildren()
	{
		return children;
	}

	public void setBend(Bend bend)
	{
		this.bend = bend;
	}

	public boolean hasBend()
	{
		return bend != null;
	}

	public void removeBend()
	{
		if (bend != null)
		{
			modelObj.removeBend(bend);
			bend.remove();
			bend = null;
		}
	}
	
	public void addMergedPart(PartObj part) {
		mergedParts.add(part);
		part.setMerged(true);
	}

	public List<PartObj> getMergedParts() {
		return mergedParts;
	}
	
	public boolean isMerged() {
		return merged;
	}
	
	public void setMerged(boolean merged) {
		this.merged = merged;
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
		for (Face f : groupObj.faces)
		{
			if (f.textureCoordinates == null)
			{
				f.textureCoordinates = new TextureCoordinate[f.vertices.length];
				for (int i = 0; i < f.vertices.length; i++)
				{
					f.textureCoordinates[i] = new TextureCoordinate(0, 0);
				}
			}

			TextureCoordinate[] coordsToStore = new TextureCoordinate[f.textureCoordinates.length];
			for (int i = 0; i < f.textureCoordinates.length; i++)
			{
				coordsToStore[i] = new TextureCoordinate(f.textureCoordinates[i].u, f.textureCoordinates[i].v);
			}

			defaultTextureCoords.put(f, coordsToStore);
		}
	}

	public void updateTextureCoordinates(Entity entity)
	{
		updateTextureCoordinates(entity, false, false, true);
	}
	
	public void addFacesFromPart(PartObj part) {
		part.updateTextureCoordinates(null, false, false, false);
		groupObj.faces.addAll(part.groupObj.faces);
		for(Face f : part.groupObj.faces) {
			TextureCoordinate[] coordsToStore = new TextureCoordinate[f.textureCoordinates.length];
			for (int i = 0; i < f.textureCoordinates.length; i++)
			{
				coordsToStore[i] = new TextureCoordinate(f.textureCoordinates[i].u, f.textureCoordinates[i].v);
			}
			defaultTextureCoords.put(f, coordsToStore);
		}
	}

	/**
	 * Change the texture coordinates and texture if the part is highlighted.
	 */
	public void updateTextureCoordinates(Entity entity, boolean mainHighlight, boolean otherHighlight, boolean bindTexture)
	{
		if (bindTexture)
			Minecraft.getMinecraft().getTextureManager().bindTexture(modelObj.getTexture(entity));

		for (Face f : groupObj.faces)
		{
			f.textureCoordinates = defaultTextureCoords.get(f);
		}
	}

	public void render(Entity entity)
	{
		GL11.glPushMatrix();
		updateTextureCoordinates(entity);
		move();
		groupObj.render();
		
		//Do for children - rotation for parent compensated for!
        for (PartObj child : getChildren())
            child.render(entity);

        GL11.glPopMatrix();
		//Minecraft.getMinecraft().getTextureManager().bindTexture(modelObj.getTexture(entity));
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
		List<PartObj> parts = new ArrayList<PartObj>();
		PartObj child = this;
		PartObj parent;
		while((parent = child.getParent()) != null)
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

		float m0 = (float) (cy * cz);
		float m1 = (float) (sx * sy * cz - cx * sz);
		float m2 = (float) (cx * sy * cz + sx * sz);
		float m3 = (float) (cy * sz);
		float m4 = (float) (sx * sy * sz + cx * cz);
		float m5 = (float) (cx * sy * sz - sx * cz);
		float m6 = (float) -sy;
		float m7 = (float) (sx * cy);
		float m8 = (float) (cx * cy);

		return new float[] {m0, m1, m2, m3, m4, m5, m6, m7, m8};
	}

}
