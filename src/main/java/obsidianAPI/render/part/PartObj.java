package obsidianAPI.render.part;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.model.obj.GroupObject;
import obsidianAPI.animation.AnimationParenting;
import obsidianAPI.render.ModelObj;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * One partObj for each 'part' of the model.
 *
 */
public class PartObj extends PartRotation
{
	private float[] rotationPoint;
	public GroupObject groupObj;
	private String displayName;

	private PartObj parent;
	private Set<PartObj> children = Sets.newHashSet();

	public PartObj(ModelObj modelObject, GroupObject groupObj)
	{
		super(modelObject, (groupObj.name.contains("_") ? groupObj.name.substring(0, groupObj.name.indexOf("_")) : groupObj.name).toLowerCase());
		this.groupObj = groupObj;
		this.displayName = getName();
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

	public void updateTextureCoordinates(){}

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
}
