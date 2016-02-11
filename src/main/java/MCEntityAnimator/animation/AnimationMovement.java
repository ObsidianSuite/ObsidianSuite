package MCEntityAnimator.animation;

import java.text.DecimalFormat;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class AnimationMovement 
{

	private float startTime;
	private float endTime;
	private float[] startPosition;
	private float[] endPosition;
	private float[] movement = new float[3];
	private DecimalFormat df = new DecimalFormat("##.##");


	public AnimationMovement() {}
	
	public AnimationMovement(float startT, float endT, float[] startPos, float[] endPos)
	{
		this.startTime = startT;
		this.endTime = endT;
		this.startPosition = startPos;
		this.endPosition = endPos;
		for(int i = 0; i < 3; i++)
		{
			this.movement[i] = (endPos[i] - startPos[i])/(endTime - startTime);
		}
	}

	public float getStartTime() 
	{
		return startTime;
	}

	public float getFinishTime() 
	{
		return endTime;
	}

	public void moveEntity(float time, Entity entity) 
	{
		entity.posX = startPosition[0] + time*movement[0];
		entity.posY = startPosition[1] + time*movement[1];
		entity.posZ = startPosition[2] + time*movement[2];
	}
	
	public float[] getStartPosition()
	{
		return startPosition;
	}
	
	public float[] getEndPosition()
	{
		return endPosition;
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
		return animationData;
	}

	public void loadData(NBTTagCompound compound) 
	{
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
	}
	
}
