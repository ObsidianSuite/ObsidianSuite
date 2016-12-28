package com.nthrootsoftware.mcea.gui.frames;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class MCEAFrame 
{
	
	protected JFrame frame;
	protected JPanel mainPanel;
	protected GridBagConstraints c;
	
	public MCEAFrame(String title)
	{
		frame = new JFrame(title);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		
		addComponents();
		
		frame.setContentPane(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setAlwaysOnTop(true);
	}
	
	protected abstract void addComponents();
	
	public void display()
	{
		frame.setVisible(true);
	}

}
