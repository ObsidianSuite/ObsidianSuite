package obsidianAPI.render.part;

import obsidianAPI.render.ModelObj;

/**
 * An abstract object for tracking information about a part (limb, position of model etc).
 */
public class Part
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

	public void setValue(float f, int i) 
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

}
