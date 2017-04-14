package obsidianAnimator.gui.timeline.swing.subsection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TimelineRenderPanel extends JPanel
{
	
	private JCheckBox loopCB, baseCB, gridCB; 
	private BaseSizeFrame baseSizeFrame;
	
	public TimelineRenderPanel(TimelineRenderController controller)
	{
		
		setLayout(new GridBagLayout());
		
		loopCB = new JCheckBox();
		loopCB.setSelected(controller.isLooping());
		loopCB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent actionEvent) 
			{
				AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				controller.setLooping(abstractButton.getModel().isSelected());
			}
		});
		
		baseCB = new JCheckBox();
		baseCB.setSelected(controller.isBaseVisible());
		baseCB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent actionEvent) 
			{
				AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				controller.setBaseVisible(abstractButton.getModel().isSelected());
			}
		});
		
		gridCB = new JCheckBox();
		gridCB.setSelected(controller.isGridVisible());
		gridCB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent actionEvent) 
			{
				AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				controller.setGridVisible(abstractButton.getModel().isSelected());
			}
		});
		
		JButton setGridSizeButton = new JButton("Set Base Size");
		setGridSizeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(baseSizeFrame != null)
					baseSizeFrame.dispose();
				baseSizeFrame = new BaseSizeFrame(TimelineRenderPanel.this, controller);
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		
		add(new JLabel("Loop"),c);
		c.gridx = 1;
		add(loopCB, c);
		
		c.gridx = 2;
		add(new JLabel("Base"),c);
		c.gridx = 3;
		add(baseCB, c);
		
		c.gridx = 4;
		add(new JLabel("Grid"),c);
		c.gridx = 5;
		add(gridCB, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 6;
		add(setGridSizeButton,c);
		
		
		setBorder(BorderFactory.createTitledBorder("Render"));
	}

}
