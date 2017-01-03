package obsidianAnimator.gui.sequence.timeline;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import obsidianAnimator.Util;
import obsidianAnimator.render.objRendering.parts.Part;

public class PartPanel extends JPanel
{
	private final GuiAnimationTimeline timeline;
	private DecimalFormat df = new DecimalFormat("#.##");
	JLabel partName, partX, partY, partZ;
	
	public PartPanel(GuiAnimationTimeline gui)
	{
		timeline = gui;
		
		partName = new JLabel();
		partX = new JLabel();
		partY = new JLabel();
		partZ = new JLabel();

		updatePartLabels();

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.anchor = c.CENTER;
		c.insets = new Insets(1,1,1,1);
		add(partName,c);
		c.gridx = 1;
		add(partX,c);
		c.gridx = 2;
		add(partY,c);
		c.gridx = 3;
		add(partZ,c);

		setBorder(BorderFactory.createTitledBorder("Part"));
	}
	
	void updatePartLabels()
	{
		String name = "No part selected";
		String x="-",y="-",z="-";
		if(timeline.currentPartName != null && !timeline.currentPartName.equals(""))
		{
			Part part = Util.getPartFromName(timeline.currentPartName, timeline.entityModel.parts);
			name = part.getDisplayName();
			x = df.format(part.getValue(0));
			y = df.format(part.getValue(1));
			z = df.format(part.getValue(2));
		}
		partName.setText(name);
		partX.setText("X: " + x);
		partY.setText("Y: " + y);
		partZ.setText("Z: " + z);
	}

}
