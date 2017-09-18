package com.dabigjoe.obsidianAPI.file;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.dabigjoe.obsidianAPI.render.ModelObj;
import com.dabigjoe.obsidianAPI.render.part.PartObj;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Class for handling extra part data for models.
 * Rotation point and original rotation values.
 */
public class PartData {

	private static final String ROTPX = "rotationPointX";
	private static final String ROTPY = "rotationPointY";
	private static final String ROTPZ = "rotationPointZ";
	private static final String ORIGINALX = "originalX";
	private static final String ORIGINALY = "originalY";
	private static final String ORIGINALZ = "originalZ";
	
	public static void load(InputStream stream, ModelObj model)
	{
		try 
		{
			NBTTagCompound nbt = CompressedStreamTools.read(new DataInputStream(stream));
			fromNBT(nbt, model);
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	public static void fromNBT(NBTTagCompound nbt, ModelObj model)
	{		
		for(PartObj p : model.getPartObjs())
		{					
			NBTTagCompound base = nbt.getCompoundTag(p.getName());
			
			float[] rotationPoint = new float[3];
			rotationPoint[0] = base.getFloat(ROTPX);
			rotationPoint[1] = base.getFloat(ROTPY);
			rotationPoint[2] = base.getFloat(ROTPZ);
			p.setRotationPoint(rotationPoint);

			float[] originalValues = new float[3];
			originalValues[0] = base.getFloat(ORIGINALX);
			originalValues[1] = base.getFloat(ORIGINALY);
			originalValues[2] = base.getFloat(ORIGINALZ);
			
			p.setOriginalValues(originalValues);
			p.setValues(originalValues);
		}
	}
	
	public static NBTTagCompound toNBT(ModelObj model)
	{
		NBTTagCompound mainNBT = new NBTTagCompound();
		
		for(PartObj p : model.getPartObjs())
		{					
			NBTTagCompound partNBT = new NBTTagCompound();
			
			partNBT.setFloat(ROTPX, p.getRotationPoint(0));
			partNBT.setFloat(ROTPY, p.getRotationPoint(1));
			partNBT.setFloat(ROTPZ, p.getRotationPoint(2));
			
			float[] originalValues = p.getOriginalValues();
			partNBT.setFloat(ORIGINALX, originalValues[0]);
			partNBT.setFloat(ORIGINALY, originalValues[1]);
			partNBT.setFloat(ORIGINALZ, originalValues[2]);

			mainNBT.setTag(p.getName(), partNBT);
		}
		return mainNBT;
	}
	
	
}
