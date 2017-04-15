package obsidianAnimator.gui.timeline.swing.subsection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import obsidianAnimator.gui.entityRenderer.EntityAutoMove;
import obsidianAnimator.gui.entityRenderer.EntityAutoMove.Direction;
import obsidianAnimator.gui.timeline.TimelineGui;

public class TimelineMovementPanel extends JPanel
{
		
	protected JComboBox<Direction> directionBox;
	private JCheckBox activeCheckbox;
	private JLabel speedLabel;
	private TimelineMovementController controller;
	
	public TimelineMovementPanel(TimelineMovementController controller)
	{				
		this.controller = controller;
		
		directionBox = new JComboBox<Direction>(Direction.values());
		directionBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(directionBox.getSelectedItem().equals(Direction.None))
				{
					controller.setEntityMovement(null);
					controller.setMovementActive(false);
					activeCheckbox.setSelected(false);
				}
				else
				{
					controller.updateEntityMovement();
					controller.setMovementActive(true);
					activeCheckbox.setSelected(true);
				}
			}
		});
		
		activeCheckbox = new JCheckBox();
		activeCheckbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				controller.setMovementActive(abstractButton.getModel().isSelected());
				if(controller.isMovementActive())
					controller.updateEntityMovement();
				else
					controller.setEntityMovement(null);
			}
		});
		activeCheckbox.setSelected(controller.isMovementActive());
		
		speedLabel = new JLabel();
		updateSpeedLabel();
		
		JButton setButton = new JButton("Set");
		setButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				controller.getUserSpeed();
			}
		});
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(2,2,2,2);
		c.anchor = GridBagConstraints.CENTER;
		add(directionBox, c);
		
		c.gridx = 1;
		add(new JLabel("Active"), c);
		
		c.gridx = 2;
		add(activeCheckbox, c);
		
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		add(speedLabel, c);
		
		c.gridx = 1;
		c.gridwidth = 2;
		add(setButton, c);
		
		setBorder(BorderFactory.createTitledBorder("Movement"));
	}
	
	void updateSpeedLabel()
	{
		speedLabel.setText(controller.getSpeed() + " block/sec");
	}

}
