package com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection;

import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.TimelineControllerSub;

public class TimelineAnimationController extends TimelineControllerSub
{

	public final TimelineAnimationPanel panel;
	private float timeMultiplier = 1.0F;
	private DecimalFormat df = new DecimalFormat("#.##");
	
	public TimelineAnimationController(TimelineController controller)
	{
		super(controller);
		this.panel = new TimelineAnimationPanel(this);
	}
	
	/**
	 * Ask the user for an FPS value, and set the aniamtion's FPS value to it 
	 * if an appropriate value is supplied.
	 */
	protected void getUserFPS()
	{
		Integer fps = null;			
		while(true)
		{
			String input = JOptionPane.showInputDialog(mainController.timelineFrame, "Set FPS (20-60)");
			if(input == null)
				break;
			fps = getFPSFromString(input);
			if(fps != null)
			{
				setFPS(fps);
				break;
			}
			else
				JOptionPane.showMessageDialog(mainController.timelineFrame, "Invalid input");
		}
	}

	/**
	 * Get an FPS value from a string.
	 * The FPS value must be between 20 and 60 (inclusive)
	 * @param input - String to parse.
	 * @return FPS value or null if string is invalid.
	 */
	protected Integer getFPSFromString(String input)
	{
		Integer fps = null;

		try
		{
			fps = Integer.parseInt(input);
			if(fps < 20 || fps > 60)
				fps = null;
		}
		catch(NumberFormatException e){}

		return fps;
	}
	
	public int getFPS()
	{
		return getCurrentAnimation().getFPS();
	}
	
	public float getTimeMultiplier()
	{
		return this.timeMultiplier;
	}
	
	public void setTimeMultiplier(float timeMultiplier)
	{
		this.timeMultiplier = timeMultiplier;
	}
	
	public void setFPS(int fps)
	{
		panel.fpsLabel.setText(fps + " FPS");
		mainController.updateAnimationFPS(fps);
	}
	
	public void onAnimationLengthChange()
	{
		int length_frames = getCurrentAnimation().getTotalTime();
		double length_time = length_frames/(double)getCurrentAnimation().getFPS();
		panel.lengthFrameLabel.setText(length_frames + " frames");
		panel.lengthSecondsLabel.setText(df.format(length_time) + " seconds");
	}
	
}
