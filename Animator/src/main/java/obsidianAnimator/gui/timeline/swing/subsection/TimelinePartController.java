package obsidianAnimator.gui.timeline.swing.subsection;

import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.timeline.TimelineController;
import obsidianAnimator.gui.timeline.swing.TimelineControllerSub;

public class TimelinePartController extends TimelineControllerSub
{
	public final TimelinePartPanel panel;

	public TimelinePartController(TimelineController controller)
	{
		super(controller);
		
		this.panel = new TimelinePartPanel(this);
		
		updatePartLabels();
	}
	
	public void updatePartLabels()
	{
		String name = "No part selected";
		double x=0,y=0,z=0;
		if(getSelectedPart() != null)
		{
			Part part = getSelectedPart();
			name = part.getDisplayName();
			x = part.getValue(0);
			y = part.getValue(1);
			z = part.getValue(2);
		}
		panel.partName.setText(name);
		panel.xSpinner.setValue(x);
		panel.ySpinner.setValue(y);
		panel.zSpinner.setValue(z);
	}

}
