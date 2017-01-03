package com.nthrootsoftware.mcea;

import java.util.ArrayList;
import java.util.List;

import com.nthrootsoftware.mcea.render.objRendering.parts.Part;
import com.nthrootsoftware.mcea.render.objRendering.parts.PartObj;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;

public class Util 
{
	
	/**
	 * Used for rendering custom Gui sizes, args: posX, posY, width, height, zLevel
	 **/
	public static void drawCustomGui(double x, double y, double width, double height, double zLevel)
	{
		Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, 0,1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, 1,0);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, 0, 0);
        tessellator.draw();
	}
	
	public static String getDisplayName(String partName, ArrayList<Part> parts) 
	{
		Part p = getPartFromName(partName, parts);
		if(p != null)
			return p.getDisplayName();
		return partName;
	}

	public static Part getPartFromName(String name, ArrayList<Part> parts) 
	{
		for(Part part : parts)
		{
			if(part.getName().equals(name))
			{
				return part;
			}
		}
		throw new RuntimeException("No part found for '" + name + "'");
	}
	
	public static PartObj getPartObjFromName(String name, ArrayList<Part> parts) 
	{
		for(Part p : parts)
		{
			if(p instanceof PartObj)
			{
				PartObj part = (PartObj) p;
				if(part.getName().equals(name) || part.getDisplayName().equals(name))
				{
					return part;
				}
			}
		}
		throw new RuntimeException("No part obj found for " + name + ".");
	}
	
	/**
	 * Calculate which frame an animation is on based on the time that it started at, which frame it started at, and its FPS.
	 * @param startTimeNano - Nano time the aniamtion starting being played on.
	 * @param startTimeFrame - Frame the animation started being played on.
	 * @param fps - FPS the animation is running at. 
	 * @param multiplier - Speed multiplier so the animation is rendered slower or faster
	 * @return Frame time.
	 */
	public static float getAnimationFrameTime(long startTimeNano, float startTimeFrame, int fps, float multiplier)
	{
		return (System.nanoTime() - startTimeNano)/1000000000F*fps*multiplier + startTimeFrame;
	}
	
}
