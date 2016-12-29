package com.nthrootsoftware.mcea.distribution;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.nthrootsoftware.mcea.animation.AnimationSequence;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class FileIO 
{

	public static AnimationSequence loadAnimationSequence(Component parentComponent)
	{
		File animationFile = FileChooser.loadAnimationFile(parentComponent);

		try 
		{
			return new AnimationSequence("", CompressedStreamTools.readCompressed(new FileInputStream(animationFile)));
		} 
		catch (FileNotFoundException e) {throw new RuntimeException(e);}
		catch (IOException e) {throw new RuntimeException(e);}
	}

}
