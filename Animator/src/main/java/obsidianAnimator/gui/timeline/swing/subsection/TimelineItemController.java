package obsidianAnimator.gui.timeline.swing.subsection;

import obsidianAnimator.gui.timeline.TimelineController;
import obsidianAnimator.gui.timeline.swing.TimelineControllerSub;
import obsidianAnimator.render.entity.EntityObj;

public class TimelineItemController extends TimelineControllerSub
{
	
	public final TimelineItemPanel panel;
	
	public TimelineItemController(TimelineController controller)
	{
		super(controller);
		
		this.panel = new TimelineItemPanel(this);
	}


}
