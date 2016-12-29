package com.nthrootsoftware.mcea.gui.frames;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;

import com.nthrootsoftware.mcea.animation.AnimationSequence;
import com.nthrootsoftware.mcea.distribution.FileChooser;
import com.nthrootsoftware.mcea.distribution.FileHandler;
import com.nthrootsoftware.mcea.gui.GuiBlack;
import com.nthrootsoftware.mcea.gui.sequence.timeline.GuiAnimationTimeline;

import net.minecraft.client.Minecraft;

public class HomeFrame extends MCEAFrame
{
		
	public HomeFrame()
	{
		super("Home");
		addComponents();
	}
	
	@Override
	protected void addComponents() 
	{
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
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				closePressed();
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		mainPanel.add(newAnimationButton,c);
		c.gridy = 1;
		mainPanel.add(openAnimationButton,c);
		c.gridy = 2;
		mainPanel.add(openEntityButton,c);
		c.gridy = 3;
		mainPanel.add(importEntityButton,c);		
		c.gridy = 4;
		mainPanel.add(closeButton,c);		
	}
	
	private void newAnimationPressed()
	{
		frame.dispose();
		new AnimationNewFrame().display();;
	}

	private void openAnimationPressed()
	{
		File animationFile = FileChooser.loadAnimationFile(frame);
		AnimationSequence sequence = FileHandler.getAnimationFromFile(animationFile);
		frame.dispose();
		Minecraft.getMinecraft().displayGuiScreen(new GuiAnimationTimeline(animationFile, "player", sequence));
	}
	
	private void openEntityPressed()
	{
		
	}
	
	private void importEntityPressed()
	{
		
	}

	private void closePressed()
	{
		frame.dispose();
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen instanceof GuiBlack)
			((GuiBlack) mc.currentScreen).initateClose();
	}
	
}
