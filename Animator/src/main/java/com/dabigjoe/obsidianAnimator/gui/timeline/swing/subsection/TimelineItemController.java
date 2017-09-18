package com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection;

import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;
import com.dabigjoe.obsidianAnimator.gui.timeline.swing.TimelineControllerSub;
import com.dabigjoe.obsidianAnimator.render.entity.EntityObj;

public class TimelineItemController extends TimelineControllerSub
{
	
	public final TimelineItemPanel panel;
	
	public TimelineItemController(TimelineController controller)
	{
		super(controller);
		
		this.panel = new TimelineItemPanel(this);
	}


}
