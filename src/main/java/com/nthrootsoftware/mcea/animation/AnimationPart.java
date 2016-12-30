package com.nthrootsoftware.mcea.animation;

import java.text.DecimalFormat;

import org.lwjgl.util.vector.Quaternion;

import com.nthrootsoftware.mcea.render.MathHelper;
import com.nthrootsoftware.mcea.render.objRendering.parts.Part;
import com.nthrootsoftware.mcea.render.objRendering.parts.PartObj;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * A section of an animation for a specific part. 
 */
public class AnimationPart 
{
	private float startTime;
	private float endTime;
	private float[] startPosition;
	private float[] endPosition;
	private float[] movement = new float[3];
	private String partName;
	private DecimalFormat df = new DecimalFormat("##.##");
	private Quaternion startQuart, endQuart;

	public AnimationPart(NBTTagCompound compound) 
	{
		loadData(compound);
	}

	public AnimationPart(float startTime, float endTime, float[] startPos, float[] endPos, Part part)
	{
		this.startTime = startTime;
		this.endTime = endTime;
		this.startPosition = startPos;
		this.endPosition = endPos;
		this.partName = part.getName();

		this.startQuart = MathHelper.eulerToQuarternion(startPos[0]/180F*Math.PI, startPos[1]/180F*Math.PI, startPos[2]/180F*Math.PI);
		this.endQuart = MathHelper.eulerToQuarternion(endPos[0]/180F*Math.PI, endPos[1]/180F*Math.PI, endPos[2]/180F*Math.PI);

		for(int i = 0; i < 3; i++)
		{
			float dT = endTime - startTime;
			//This only happens when there is only one keyframe for a part and
			//it is at time zero.
			if(dT == 0)
				dT = 1;
			float dif = endPos[i] - startPos[i];
			if(part instanceof PartObj)
			{
				if(Math.abs(dif) > Math.PI)
				{
					if(dif < 0)
						dif += 2*Math.PI;
					else
						dif -= 2*Math.PI;
				}
			}
			this.movement[i] = dif/dT;
		}
	}

	public void animatePart(Part part, float time) 
	{		
		boolean useQuarternions = false;

		float[] values = new float[3];
		if(useQuarternions && part instanceof PartObj)		
		{
			PartObj partObj = (PartObj) part;
			float t = time/(endTime - startTime);

			//	System.out.println(endQuart);

			Quaternion interpolatedQ = MathHelper.slerp(startQuart, endQuart, t);
			values = MathHelper.quarternionToEuler(interpolatedQ);
			for(int i = 0; i < 3; i++)
			{
				//System.out.println(i + values[i]);
				values[i] = (float) (values[i]/Math.PI*180F);
			}
		}
		else
		{
			values[0] = startPosition[0] + time*movement[0];
			values[1] = startPosition[1] + time*movement[1];
			values[2] = startPosition[2] + time*movement[2];
		}

		for(int i = 0; i < 3; i++)
		{
			if(values[i] < -Math.PI)
				values[i] += 2*Math.PI;
			else if(values[i] > Math.PI)
				values[i] -= 2*Math.PI;	
		}


		part.setValues(values);
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
	 * Return true if the float array has the same values as the startPosition
	 */
	public boolean isStartPos(float[] rotation) 
	{
		for(int i = 0; i < 3; i++)
		{
			if(rotation[i] != startPosition[i])
				return false;
		}
		return true;
	}

	public boolean isEndPosDifferentToStartPos()
	{
		for(int i = 0; i < 3; i++)
		{
			if(startPosition[i] != endPosition[i])
				return true;
		}
		return false;
	}
	
	public String getPartName() 
	{
		return partName;
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
		animationData.setString("PartName", partName);
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
		this.partName = compound.getString("PartName");
	}


}
