package com.nthrootsoftware.mcea.gui.frames;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HomeFrame 
{
	
	JFrame frame;
	
	public HomeFrame()
	{
		frame = new JFrame();
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		JLabel titleLabel = new JLabel("MCEA Home");
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		
		JButton newAnimationButton = new JButton("New Animation");
		newAnimationButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				newAnimationPressed();
			}
		});
		
		JButton openAnimationButton = new JButton("Open Animation");
		openAnimationButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				openAnimationPressed();
			}
		});
		
		JButton openEntityButton = new JButton("Open Entity");
		openEntityButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				openEntityPressed();
			}
		});
		
		JButton importEntityButton = new JButton("Import Entity");
		importEntityButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				importEntityPressed();
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		mainPanel.add(titleLabel,c);
		c.gridy = 1;
		mainPanel.add(newAnimationButton,c);
		c.gridy = 2;
		mainPanel.add(openAnimationButton,c);
		c.gridy = 3;
		mainPanel.add(openEntityButton,c);
		c.gridy = 4;
		mainPanel.add(importEntityButton,c);
		
		frame.setContentPane(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	public void display()
	{
		frame.setVisible(true);
	}
	
	private void newAnimationPressed()
	{
		
	}

	private void openAnimationPressed()
	{
		
	}
	
	private void openEntityPressed()
	{
		
	}
	
	private void importEntityPressed()
	{
		
	}
	
}
