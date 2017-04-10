package obsidianAnimator.gui.timeline.swing.subsection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TimelinePartPanel extends JPanel
{
	
	protected JLabel partName, partX, partY, partZ;
	
	public TimelinePartPanel(TimelinePartController controller)
	{		
		partName = new JLabel();
		partX = new JLabel();
		partY = new JLabel();
		partZ = new JLabel();

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
	


}
