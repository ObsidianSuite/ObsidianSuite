package com.nthrootsoftware.mcea.gui.sequence.timeline;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RenderPanel extends JPanel
{
	
	private final GuiAnimationTimeline timeline;
	
	public RenderPanel(GuiAnimationTimeline gui)
	{
		timeline = gui;
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 0;
		for(int i = 0; i < 4; i++)
		{	
			c.gridx = i%2*2;
			c.gridy = i/2;
			System.out.println(i%2 + " " + i/2);
			c.anchor = GridBagConstraints.EAST;
			JCheckBox cb = new JCheckBox();
			cb.setHorizontalAlignment(JCheckBox.RIGHT);
			add(cb, c);
			String s = "";
			switch(i)
			{
			case 0: 
				s = "Loop"; 
				cb.setSelected(timeline.boolLoop);
				cb.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent actionEvent) 
					{
						AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
						timeline.boolLoop = abstractButton.getModel().isSelected();
					}
				});
				break;
			case 1: s = "Shield"; break; //TODO shield??
			case 2: 
				s = "Base";
				cb.setSelected(timeline.boolBase);
				cb.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent actionEvent) 
					{
						AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
						timeline.boolBase = abstractButton.getModel().isSelected();
					}
				});
				break;
			case 3:
				s = "Grid";
				cb.setSelected(timeline.boolGrid);
				cb.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent actionEvent) 
					{
						AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
						timeline.boolGrid = abstractButton.getModel().isSelected();
					}
				});
				break;
			}
			c.gridx = i%2*2 + 1;
			c.anchor = GridBagConstraints.WEST;
			add(new JLabel(s),c);
		}
		setBorder(BorderFactory.createTitledBorder("Render"));
	}

}
