package obsidianAnimator.gui.timeline.swing.subsection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TimelinePartPanel extends JPanel
{
	
	protected JLabel partName;
	protected JButton partX, partY, partZ;
	
	public TimelinePartPanel(TimelinePartController controller)
	{		
		partName = new JLabel();
		partX = new JButton();
		partY = new JButton();
		partZ = new JButton();
		
		partX.addActionListener(new PartLabelListener());
		partY.addActionListener(new PartLabelListener());
		partZ.addActionListener(new PartLabelListener());

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
	
	private class PartLabelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(TimelinePartPanel.this, "Test");			
		}

		
	}


}
