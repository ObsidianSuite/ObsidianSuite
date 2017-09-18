package com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection;

import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.TimelineControllerSub;

public class TimelineRenderController extends TimelineControllerSub
{
	
	public final TimelineRenderPanel panel;
	
	private boolean looping = false;
	
	public TimelineRenderController(TimelineController controller)
	{
		super(controller);
		this.panel = new TimelineRenderPanel(this);
	}

	public boolean isLooping()
	{
		return looping;
	}
	
	public void setLooping(boolean looping)
	{
		this.looping = looping;
	}
	
	public boolean isBaseVisible()
	{
		return getTimelineGui().boolBase;
	}
	
	public void setBaseVisible(boolean base)
	{
		getTimelineGui().boolBase = base;
	}
	
	public boolean isGridVisible()
	{
		return getTimelineGui().boolGrid;
	}
	
	public void setGridVisible(boolean grid)
	{
		getTimelineGui().boolGrid = grid;
	}
}
