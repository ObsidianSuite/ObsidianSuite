package obsidianAPI.render.part;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.bend.Bend;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private Bend bend = null;

	public PartObj(ModelObj modelObject, GroupObject groupObj)
	{
		super(modelObject, (groupObj.name.contains("_") ? groupObj.name.substring(0, groupObj.name.indexOf("_")) : groupObj.name).toLowerCase());
		this.groupObj = groupObj;
		this.displayName = getName();
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
				f.textureCoordinates = new TextureCoordinate[3];
				for (int i = 0; i < 3; i++)
				{
					f.textureCoordinates[i] = new TextureCoordinate(0, 0);
				}
			}

			TextureCoordinate[] coordsToStore = new TextureCoordinate[3];
			for (int i = 0; i < 3; i++)
			{
				coordsToStore[i] = new TextureCoordinate(f.textureCoordinates[i].u, f.textureCoordinates[i].v);
			}

			defaultTextureCoords.put(f, coordsToStore);
		}
	}

	public void updateTextureCoordinates()
	{
		updateTextureCoordinates(false, false, true);
	}

	/**
	 * Change the texture coordinates and texture if the part is highlighted.
	 */
	public void updateTextureCoordinates(boolean mainHighlight, boolean otherHighlight, boolean bindTexture)
	{
		ResourceLocation texture = modelObj.getTexture();

		if (bindTexture)
			Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

		for (Face f : groupObj.faces)
		{
			f.textureCoordinates = defaultTextureCoords.get(f);
		}
	}

	public void render()
	{
		GL11.glPushMatrix();
		move();
		updateTextureCoordinates();
		groupObj.render();

		//Do for children - rotation for parent compensated for!
        for (PartObj child : getChildren())
            child.render();

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
