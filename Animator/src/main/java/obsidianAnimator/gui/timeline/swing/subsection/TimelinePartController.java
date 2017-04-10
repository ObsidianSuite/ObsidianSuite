package obsidianAnimator.gui.timeline.swing.subsection;

import java.text.DecimalFormat;

import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.timeline.TimelineController;
import obsidianAnimator.gui.timeline.swing.TimelineControllerSub;

public class TimelinePartController extends TimelineControllerSub
{
	
	public final TimelinePartPanel panel;
	
	private DecimalFormat df = new DecimalFormat("#.##");
	
	public TimelinePartController(TimelineController controller)
	{
		super(controller);
		
		this.panel = new TimelinePartPanel(this);
		
		updatePartLabels();
	}
	
	public void updatePartLabels()
	{
		String name = "No part selected";
		String x="-",y="-",z="-";
		if(getSelectedPart() != null)
		{
			Part part = getSelectedPart();
			name = part.getDisplayName();
			x = df.format(part.getValue(0));
			y = df.format(part.getValue(1));
			z = df.format(part.getValue(2));
		}
		panel.partName.setText(name);
		panel.partX.setText("X: " + x);
		panel.partY.setText("Y: " + y);
		panel.partZ.setText("Z: " + z);
	}

}
