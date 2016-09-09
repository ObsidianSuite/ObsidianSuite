package com.nthrootsoftware.mcea.gui.sequence.timeline;

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

import com.nthrootsoftware.mcea.gui.sequence.EntityAutoMove;
import com.nthrootsoftware.mcea.gui.sequence.EntityAutoMove.Direction;

public class MovementPanel extends JPanel
{
	
	private final GuiAnimationTimeline timeline;
	
	private float speed = 0.0F; 
	private JComboBox directionBox;
	private JCheckBox activeCheckbox;
	private JLabel speedLabel;
	
	public MovementPanel(GuiAnimationTimeline gui)
	{		
		timeline = gui;
		
		directionBox = new JComboBox(Direction.values());
		directionBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				updateEntityMovement();
				if(directionBox.getSelectedItem().equals(Direction.None))
				{
					timeline.boolMovementActive = false;
					activeCheckbox.setSelected(false);
				}
				else
				{
					timeline.boolMovementActive = true;
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
				timeline.boolMovementActive = abstractButton.getModel().isSelected();				
				if(timeline.boolMovementActive)
					updateEntityMovement();
				else
					timeline.entityMovement = null;
			}
		});
		activeCheckbox.setSelected(timeline.boolMovementActive);
		
		speedLabel = new JLabel();
		updateSpeedLabel();
		
		JButton setButton = new JButton("Set");
		setButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				getUserSpeed();
			}
		});
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(2,2,2,2);
		c.anchor = c.CENTER;
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
	
	private void updateSpeedLabel()
	{
		speedLabel.setText(speed + " block/sec");
	}
	
	private void updateEntityMovement()
	{
		updateEntityMovement(timeline.currentAnimation.getFPS());
	}
	
	public void updateEntityMovement(int fps)
	{
		timeline.entityMovement = new EntityAutoMove(speed, (Direction) directionBox.getSelectedItem(), fps);
	}
	
	private void getUserSpeed()
	{
		Float speed = null;			
		while(true)
		{
			String input = JOptionPane.showInputDialog(timeline.timelineFrame, "Set Speed (greater than 0)");
			if(input == null)
				break;
			speed = getSpeedFromString(input);
			if(speed != null)
			{
				this.speed = speed;
				updateSpeedLabel();
				updateEntityMovement();
				break;
			}
			else
				JOptionPane.showMessageDialog(timeline.timelineFrame, "Invalid input");
		}
	}


	private Float getSpeedFromString(String input)
	{
		Float speed = null;

		try
		{
			speed = Float.parseFloat(input);
			if(speed <= 0.0F)
				speed = null;
		}
		catch(NumberFormatException e){}

		return speed;
	}

}
