package MCEntityAnimator.render.objRendering.parts;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.render.objRendering.Bend;
import MCEntityAnimator.render.objRendering.ModelObj;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class Part
{
	protected float valueX, valueY, valueZ;
	protected float[] originalValues;
	private String name;
	public ModelObj modelObj;

	public Part(ModelObj mObj, String name) 
	{
		modelObj = mObj;
		this.name = name.toLowerCase();
		valueX = 0.0F;
		valueY = 0.0F;
		valueZ = 0.0F;
		originalValues = new float[]{0.0F, 0.0F, 0.0F};
	}

	//------------------------------------------
	//  			Basics
	//------------------------------------------

	public String getName()
	{
		return name;
	}
	
	public String getDisplayName()
	{
		return getName();
	}

	public void setValues(float[] values)
	{
		valueX = values[0];
		valueY = values[1];
		valueZ = values[2];
	}

	public void setValue(int i, float f) 
	{
		switch(i)
		{
		case 0: valueX = f; break;
		case 1: valueY = f; break;
		case 2: valueZ = f; break;
		}
	}

	public float getValue(int i) 
	{
		switch(i)
		{
		case 0: return valueX;
		case 1: return valueY;
		case 2: return valueZ;
		}
		return 0.0F;
	}
	
	public float[] getValues()
	{
		return new float[]{valueX, valueY, valueZ};
	}
	
	public void setOriginalValues(float[] rot) 
	{
		originalValues = rot;
	}

	public float[] getOriginalValues() 
	{
		return originalValues;
	}
	
	public void setToOriginalValues() 
	{
		setValues(originalValues);
	}

	public abstract void move(Entity entity);

}
