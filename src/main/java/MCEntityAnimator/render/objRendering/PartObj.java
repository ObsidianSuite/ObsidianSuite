package MCEntityAnimator.render.objRendering;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PartObj 
{
	private float[] rotationPoint;
	private float[] originalRotation;
	private float rotationX;
	private float rotationY;
	private float rotationZ;
	private boolean showModel;

	private ArrayList<PartObj> children;

	private HashMap<Face, TextureCoordinate[]> defaultTextureCoords;

	private String name;

	ModelObj modelObj;
	GroupObject groupObj;
	
	private boolean visible;
	private Bend bend = null;

	public PartObj(ModelObj mObj, GroupObject gObj) 
	{
		modelObj = mObj;
		groupObj = gObj;
		name = groupObj.name.contains("_") ? groupObj.name.substring(0, groupObj.name.indexOf("_")) : groupObj.name;
		name = name.toLowerCase();
		rotationX = 0.0F;
		rotationY = 0.0F;
		rotationZ = 0.0F;
		children = new ArrayList<PartObj>();
		defaultTextureCoords = new HashMap<Face, TextureCoordinate[]>();
		updateDefaultTextureCoordinates();
		visible = true;
	}

	//------------------------------------------
	//  			Basics
	//------------------------------------------

	public String getName()
	{
		return name;
	}

	public void setShowModel(boolean show)
	{
		showModel = show;
	}

	public boolean getShowModel()
	{
		return showModel;
	}

	public void setOriginalRotation(float[] rot) 
	{
		originalRotation = rot;
	}


	public float[] getOriginalRotation() 
	{
		return originalRotation;
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

	public void setRotation(float[] rot)
	{
		rotationX = rot[0];
		rotationY = rot[1];
		rotationZ = rot[2];
	}

	public void setRotation(int i, float f) 
	{
		switch(i)
		{
		case 0: rotationX = f; break;
		case 1: rotationY = f; break;
		case 2: rotationZ = f; break;
		}
	}

	public float getRotation(int i) 
	{
		switch(i)
		{
		case 0: return rotationX;
		case 1: return rotationY;
		case 2: return rotationZ;
		}
		return 0.0F;
	}
	
	public float[] getRotation()
	{
		return new float[]{rotationX, rotationY, rotationZ};
	}


	public void setToOriginalRotation() 
	{
		rotationX = originalRotation[0];
		rotationY = originalRotation[1];
		rotationZ = originalRotation[2];
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


	//------------------------------------------
	//              Parenting
	//------------------------------------------


	public void addChild(PartObj child) 
	{
		this.children.add(child);
	}

	public void removeChild(PartObj child) 
	{
		this.children.remove(child);
		child.setToOriginalRotation();
	}

	public void clearChildModels() 
	{
		this.children.clear();
	}

	//------------------------------------------
	//  	   Rendering and Rotating
	//------------------------------------------

	public void updateDefaultTextureCoordinates()
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
	
	public void updateTextureCoordinates(boolean highlight, boolean main)
	{
		float u = 0.0F;
		float v = 0.0F;
		if(highlight)
		{
			if(main)
			{
				u = 0.0F;
				v = 0.75F;
			}
			else
			{
				u = 0.75F;
				v = 0.0F;
			}
		}
		
		
		if(!modelObj.renderWithTexture)
		{
			for(Face f : groupObj.faces)
			{
				TextureCoordinate texCo = new TextureCoordinate(u, v);
				f.textureCoordinates = new TextureCoordinate[]{texCo, texCo, texCo};
			}
		}
		else
		{
			if(highlight)
			{
				for(Face f : groupObj.faces)
				{
					TextureCoordinate texCo = new TextureCoordinate(0.0F, 0.0F);
					f.textureCoordinates = new TextureCoordinate[]{texCo, texCo, texCo};
				}
			}
			else
			{
				for(Face f : groupObj.faces)
				{
					f.textureCoordinates = defaultTextureCoords.get(f);
				}
			}			
		}
	}
	
	public void render(boolean highlight, boolean main) 
	{
		updateTextureCoordinates(highlight, main);
		
		GL11.glPushMatrix();
		rotate();
		if(visible)
		{
			groupObj.render();
		}
		GL11.glPopMatrix();
	}

	/**
	 * Allows the changing of Angles after a box has been rendered
	 */
	@SideOnly(Side.CLIENT)
	public void postRender(float p_78794_1_)
	{
		if (this.rotationX == 0.0F && this.rotationY == 0.0F && this.rotationZ == 0.0F)
		{
			if (this.rotationPoint[0] != 0.0F || this.rotationPoint[1] != 0.0F || this.rotationPoint[2] != 0.0F)
			{
				GL11.glTranslatef(this.rotationPoint[0] * p_78794_1_, this.rotationPoint[1] * p_78794_1_, this.rotationPoint[2] * p_78794_1_);
			}
		}
		else
		{
			GL11.glTranslatef(this.rotationPoint[0] * p_78794_1_, this.rotationPoint[1] * p_78794_1_, this.rotationPoint[2] * p_78794_1_);

			if (this.rotationZ != 0.0F)
			{
				GL11.glRotatef(this.rotationZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
			}

			if (this.rotationY != 0.0F)
			{
				GL11.glRotatef(this.rotationY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
			}

			if (this.rotationX != 0.0F)
			{
				GL11.glRotatef(this.rotationX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
			}
		}
	}

	/**
	 * To be called inside a GL11 matrix, will move the part to (0,0,0), rotate it and then move it back. 
	 */
	private void rotate()
	{
		//Translate part to centre
		GL11.glTranslatef(-rotationPoint[0], -rotationPoint[1], -rotationPoint[2]);

		//Rotate
		GL11.glRotated((rotationX - originalRotation[0])/Math.PI*180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotated((rotationY - originalRotation[1])/Math.PI*180.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotated((rotationZ - originalRotation[2])/Math.PI*180.0F, 0.0F, 0.0F, 1.0F);

		//Translate to original position
		GL11.glTranslatef(rotationPoint[0], rotationPoint[1], rotationPoint[2]);	

		//Do for children - rotation for parent compensated for!
		for(PartObj child : this.children)
		{
			child.render(modelObj.isPartHighlighted(child), modelObj.isMainHighlight(child));
		}
	}


}
