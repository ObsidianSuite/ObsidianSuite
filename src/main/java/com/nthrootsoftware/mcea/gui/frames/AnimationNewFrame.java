package com.nthrootsoftware.mcea.gui.frames;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.lwjgl.opengl.Display;

import com.nthrootsoftware.mcea.animation.AnimationData;
import com.nthrootsoftware.mcea.animation.AnimationSequence;
import com.nthrootsoftware.mcea.distribution.DataHandler;
import com.nthrootsoftware.mcea.gui.GuiHandler;
import com.nthrootsoftware.mcea.gui.sequence.timeline.GuiAnimationTimeline;

import net.minecraft.client.Minecraft;

public class AnimationNewFrame extends MCEAFrame
{

	private Insets narrowInsets = new Insets(2,10,2,10);
	private Insets wideInsets = new Insets(2,25,2,10);
	private Insets deepInsets = new Insets(2,10,4,10);
	
	private JComboBox<String> entityDropDown;
	private JTextField nameTextField;
	
	private String[] entites = DataHandler.getEntities().toArray(new String[0]);

	public AnimationNewFrame()
	{
		super("New Animation");
		entityDropDown = new JComboBox<String>(entites);
		nameTextField = new JTextField();
		addComponents();
	}

	@Override
	protected void addComponents()
	{
		entityDropDown.setPreferredSize(new Dimension(100,25));

		JButton create = new JButton("Create");
		create.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				createPressed();
			}
		});

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				cancelPressed();
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = narrowInsets;
		mainPanel.add(new JLabel("Entity"),c);
		c.gridy = 1;
		c.insets = wideInsets;
		mainPanel.add(entityDropDown, c);
		c.gridy = 2;
		c.insets = narrowInsets;
		mainPanel.add(new JLabel("Animation Name"),c);
		c.gridy = 3;
		c.insets = wideInsets;
		mainPanel.add(nameTextField, c);
		c.gridy = 4;
		c.insets = narrowInsets;
		mainPanel.add(create,c);
		c.gridy = 5;
		c.insets = deepInsets;
		mainPanel.add(cancel,c);

	}

	private void createPressed()
	{
		String animationName = nameTextField.getText();
		String entityName = (String) entityDropDown.getSelectedItem();
		if(!animationName.equals(""))
		{
			if(!AnimationData.sequenceExists(entityName, animationName))
			{
				AnimationSequence sequence = new AnimationSequence(entityName, animationName);
				AnimationData.addSequence(entityName, sequence);
				frame.dispose();
				Minecraft.getMinecraft().displayGuiScreen(new GuiAnimationTimeline(entityName, sequence));
			}
			else
				JOptionPane.showMessageDialog(frame, "An animation with that name already exists.");
		}
	}

	private void cancelPressed()
	{
		frame.dispose();
		new HomeFrame().display();
	}

}
