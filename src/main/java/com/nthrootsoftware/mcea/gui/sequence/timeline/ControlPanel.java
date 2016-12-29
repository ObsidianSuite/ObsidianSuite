package com.nthrootsoftware.mcea.gui.sequence.timeline;

import java.awt.Dimension;
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
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.nthrootsoftware.mcea.Util;
import com.nthrootsoftware.mcea.animation.AnimationData;
import com.nthrootsoftware.mcea.animation.AnimationSequence;
import com.nthrootsoftware.mcea.gui.GuiInventoryChooseItem;
import com.nthrootsoftware.mcea.gui.sequence.EntityAutoMove.Direction;
import com.nthrootsoftware.mcea.render.objRendering.EntityObj;
import com.nthrootsoftware.mcea.render.objRendering.parts.Part;

import net.minecraft.client.Minecraft;

public class ControlPanel extends JPanel
{

	private final GuiAnimationTimeline timeline;
	
	JButton playPauseButton;
	AnimationPanel animationPanel;
	PartPanel partPanel;
	MovementPanel movementPanel;
	RenderPanel renderPanel;
	ItemPanel itemPanel;


	public ControlPanel(GuiAnimationTimeline gui)
	{				
		timeline = gui;
		
		animationPanel = new AnimationPanel(timeline);
		partPanel = new PartPanel(timeline);
		movementPanel = new MovementPanel(timeline);
		renderPanel = new RenderPanel(timeline);
		itemPanel = new ItemPanel(timeline);
		
		playPauseButton = new JButton("Play");
		playPauseButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(timeline.time >= timeline.currentAnimation.getTotalTime())
					timeline.time = 0;
				timeline.boolPlay = !timeline.boolPlay; 		
				if(timeline.boolPlay)
				{
					timeline.playStartTimeNano = System.nanoTime();
					timeline.playStartTimeFrame = timeline.time;
				}

				updatePlayPauseButton();
			}
		});

		JButton backButton = new JButton("Back");
		backButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				timeline.close();
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2,5,2,5);
		add(playPauseButton,c);
		c.insets = new Insets(0,2,0,2);
		c.gridy = 1;
		add(animationPanel,c);
		c.gridy = 2;
		add(partPanel,c);
		c.gridy = 3;
		add(movementPanel,c);
		c.gridy = 4;
		add(renderPanel,c);
		c.gridy = 5;
		add(itemPanel,c);
		c.gridy = 6;
		c.insets = new Insets(2,5,10,5);
		add(backButton,c);
	}

	protected void updatePlayPauseButton()
	{
		playPauseButton.setText(timeline.boolPlay ? "Pause" : "Play");
	}
		
}
