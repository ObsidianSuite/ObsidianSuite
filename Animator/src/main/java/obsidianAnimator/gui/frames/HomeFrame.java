package obsidianAnimator.gui.frames;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import net.minecraft.client.Minecraft;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.file.FileHandler;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.file.FileChooser;
import obsidianAnimator.file.FileNotChosenException;
import obsidianAnimator.gui.GuiBlack;
import obsidianAnimator.gui.timeline.TimelineGui;
import obsidianAnimator.gui.timeline.TimelineController;

public class HomeFrame extends BaseFrame
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
		new AnimationNewFrame().display();
	}

	private void openAnimationPressed()
	{
		try
		{
			File animationFile = FileChooser.loadAnimationFile(frame);
			AnimationSequence sequence = FileHandler.getAnimationFromFile(animationFile);
			if(ModelHandler.isModelImported(sequence.getEntityName()))
			{
				frame.dispose();
				new TimelineController(animationFile, sequence).display();
			}
			else
				JOptionPane.showMessageDialog(frame, "You must import the " + sequence.getEntityName() + " model first.");
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
