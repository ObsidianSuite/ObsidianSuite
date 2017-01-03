package com.nthrootsoftware.mcea.gui.frames;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;

import com.nthrootsoftware.mcea.animation.AnimationSequence;
import com.nthrootsoftware.mcea.data.ModelHandler;
import com.nthrootsoftware.mcea.file.FileChooser;
import com.nthrootsoftware.mcea.file.FileHandler;
import com.nthrootsoftware.mcea.file.FileNotChosenException;
import com.nthrootsoftware.mcea.gui.GuiBlack;
import com.nthrootsoftware.mcea.gui.GuiPartSetup;
import com.nthrootsoftware.mcea.gui.sequence.timeline.GuiAnimationTimeline;
import com.nthrootsoftware.mcea.render.objRendering.ModelObj;

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

		JButton modelListButton = new JButton("Model List");
		modelListButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				modelListPressed();
			}
		});		
		
		JButton importEntityButton = new JButton("Import Model");
		importEntityButton.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				importModelPressed();
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
		mainPanel.add(modelListButton,c);	
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
		try
		{
			File animationFile = FileChooser.loadAnimationFile(frame);
			AnimationSequence sequence = FileHandler.getAnimationFromFile(animationFile);
			frame.dispose();
			Minecraft.getMinecraft().displayGuiScreen(new GuiAnimationTimeline(animationFile, "player", sequence));
		}
		catch(FileNotChosenException e){}
	}

	private void modelListPressed()
	{
		frame.dispose();
		new ModelListFrame().display();
	}
	
	private void importModelPressed()
	{
		frame.dispose();
		new ImportModelFrame().display();
	}

	private void closePressed()
	{
		frame.dispose();
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen instanceof GuiBlack)
			((GuiBlack) mc.currentScreen).initateClose();
	}

}
