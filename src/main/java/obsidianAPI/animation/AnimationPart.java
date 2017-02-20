package obsidianAPI.animation;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import obsidianAPI.Util;
import org.lwjgl.util.vector.Quaternion;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;

/**
 * A section of an animation for a specific part. 
 */
public class AnimationPart 
{
	private int startTime;
	private int endTime;
	private float[] startPosition;
	private float[] endPosition;
	private float[] movement = new float[3];
	private String partName;
	private DecimalFormat df = new DecimalFormat("##.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	private Quaternion startQuart, endQuart;

	public AnimationPart(NBTTagCompound compound) 
	{
		loadData(compound);
	}

	public AnimationPart(int startTime, int endTime, float[] startPosition, float[] endPosition, Part part)
	{
		this.startTime = startTime;
		this.endTime = endTime;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.partName = part.getName();
		init();
	}
	
	private void init()
	{
		this.startQuart = Util.eulerToQuarternion(startPosition[0] / 180F * Math.PI, startPosition[1] / 180F * Math.PI, startPosition[2] / 180F * Math.PI);
		this.endQuart = Util.eulerToQuarternion(endPosition[0] / 180F * Math.PI, endPosition[1] / 180F * Math.PI, endPosition[2] / 180F * Math.PI);

		for(int i = 0; i < 3; i++)
		{
			float dT = endTime - startTime;
			//This only happens when there is only one keyframe for a part and
			//it is at time zero.
			if(dT == 0)
				dT = 1;
			float dif = endPosition[i] - startPosition[i];
			//Keep rotation within range.
			//TODO Find a way to re-implement this without needed the part instance. 
//			if(part instanceof PartObj)
//			{
//				if(Math.abs(dif) > Math.PI)
//				{
//					if(dif < 0)
//						dif += 2*Math.PI;
//					else
//						dif -= 2*Math.PI;
//				}
//			}
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

			Quaternion interpolatedQ = Util.slerp(startQuart, endQuart, t);
			values = Util.quarternionToEuler(interpolatedQ);
			for(int i = 0; i < 3; i++)
			{
				//System.out.println(i + values[i]);
				values[i] = (float) (values[i]/Math.PI*180F);
			}
		}
		else
			values = getPartRotationAtTime(time);
		part.setValues(values);
	}
	
	public float[] getPartRotationAtTime(float time)
	{
		float[] values = new float[3];
		for(int i = 0; i < 3; i++)
		{
			values[i] = startPosition[i] + time*movement[i];
			if(values[i] < -Math.PI)
				values[i] += 2*Math.PI;
			else if(values[i] > Math.PI)
				values[i] -= 2*Math.PI;	
		}
		return values;
	}

	public float[] getStartPosition() 
	{
		return startPosition;
	}

	public float[] getEndPosition() 
	{
		return endPosition;
	}

	public int getStartTime()
	{
		return startTime;
	}

	public int getEndTime()
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
		animationData.setFloat("StartTime", this.startTime);
		animationData.setFloat("FinishTime", this.endTime);
		animationData.setString("Part", partName);
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
		this.startTime = compound.getInteger("StartTime");
		this.endTime = compound.getInteger("FinishTime");
		this.partName = compound.getString("Part");
		init();
	}


}
