package com.nthrootsoftware.mcea.gui.sequence.timeline;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nthrootsoftware.mcea.gui.sequence.EntityAutoMove.Direction;

public class MovementPanel extends JPanel
{
	
	private final GuiAnimationTimeline timeline;
	
	public MovementPanel(GuiAnimationTimeline gui)
	{		
		timeline = gui;
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(2,2,2,2);
		c.anchor = c.CENTER;
		add(new JComboBox(Direction.values()), c);
		
		c.gridx = 1;
		add(new JLabel("Active"), c);
		
		c.gridx = 2;
		add(new JCheckBox(), c);
		
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		add(new JLabel("4.3 blocks/sec"), c);
		
		c.gridx = 1;
		c.gridwidth = 2;
		add(new JButton("Set"), c);
		
		setBorder(BorderFactory.createTitledBorder("Movement"));
	}

}
