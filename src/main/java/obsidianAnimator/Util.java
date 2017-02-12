package obsidianAnimator;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;

public class Util 
{


	
	
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
