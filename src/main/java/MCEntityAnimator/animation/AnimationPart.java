package MCEntityAnimator.animation;

import java.text.DecimalFormat;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import MCEntityAnimator.Util;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.PartObj;
import MCEntityAnimator.render.objRendering.RenderObj;

public class AnimationPart 
{
	private float startTime;
	private float endTime;
	private float[] startPosition;
	private float[] endPosition;
	private float[] movement = new float[3];
	private DecimalFormat df = new DecimalFormat("##.##");
	private PartObj part;

	public AnimationPart() {}

	public AnimationPart(float startTime, float endTime, float[] startPos, float[] endPos, PartObj part)
	{
		this.startTime = startTime;
		this.endTime = endTime;
		this.startPosition = startPos;
		this.endPosition = endPos;
		for(int i = 0; i < 3; i++)
		{
			float dT = endTime - startTime;
			//This only happens when there is only one keyframe for a part and
			//it is at time zero.
			if(dT == 0)
				dT = 1;
			this.movement[i] = (endPos[i] - startPos[i])/dT;
		}
		this.part = part;
	}

	public void animatePart(float time) 
	{	
		float x = startPosition[0] + time*movement[0];
		float y = startPosition[1] + time*movement[1];
		float z = startPosition[2] + time*movement[2];
		
		part.setRotation(new float[]{x, y, z});
	}

	public PartObj getPart()
	{
		return part;
	}
	
	public float[] getStartPosition() 
	{
		return startPosition;
	}

	public float[] getEndPosition() 
	{
		return endPosition;
	}

	public float getStartTime()
	{
		return startTime;
	}
	
	public float getEndTime()
	{
		return endTime;
	}

	/**
	 * Return true is the float array has the same values as the startPosition
	 */
	public boolean compareRotation(float[] defaults) 
	{
		for(int i = 0; i < 3; i++)
		{
			if(defaults[i] != startPosition[i])
				return false;
		}
		return true;
	}

	public NBTBase getSaveData() 
	{
		NBTTagCompound animationData = new NBTTagCompound();		
		animationData.setFloat("XStart", Float.parseFloat(df.format(this.startPosition[0])));
		animationData.setFloat("YStart", Float.parseFloat(df.format(this.startPosition[1])));
		animationData.setFloat("ZStart", Float.parseFloat(df.format(this.startPosition[2])));
		animationData.setFloat("XEnd", Float.parseFloat(df.format(this.endPosition[0])));
		animationData.setFloat("YEnd", Float.parseFloat(df.format(this.endPosition[1])));
		animationData.setFloat("ZEnd", Float.parseFloat(df.format(this.endPosition[2])));
		animationData.setFloat("StartTime", Float.parseFloat(df.format(this.startTime)));
		animationData.setFloat("FinishTime", Float.parseFloat(df.format(this.endTime)));
		animationData.setString("Part", this.part.getName());
		return animationData;
	}

	public void loadData(String entityName, NBTTagCompound compound) 
	{
		EntityObj entity = new EntityObj(Minecraft.getMinecraft().theWorld, entityName);
		ArrayList<PartObj> parts = ((RenderObj) RenderManager.instance.getEntityRenderObject(entity)).getModel(entityName).parts;
		this.startPosition = new float[3];
		this.endPosition = new float[3];
		this.startPosition[0] = compound.getFloat("XStart");
		this.startPosition[1] = compound.getFloat("YStart");
		this.startPosition[2] = compound.getFloat("ZStart");
		this.endPosition[0] = compound.getFloat("XEnd");
		this.endPosition[1] = compound.getFloat("YEnd");
		this.endPosition[2] = compound.getFloat("ZEnd");
		this.startTime = compound.getFloat("StartTime");
		this.endTime = compound.getFloat("FinishTime");
		this.part = Util.getPartFromName(compound.getString("Part"), parts);
	}


}
