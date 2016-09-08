package com.nthrootsoftware.mcea.gui.sequence.timeline;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AnimationPanel extends JPanel
{
	
	private final GuiAnimationTimeline timeline;
	JLabel lengthFrameLabel, lengthSecondsLabel, fpsLabel;

	public AnimationPanel(GuiAnimationTimeline gui)
	{
		timeline = gui;
		
		lengthFrameLabel = new JLabel();
		lengthSecondsLabel = new JLabel();
		fpsLabel = new JLabel(timeline.currentAnimation.getFPS() + " FPS");

		JButton fpsButton = new JButton("Set FPS");
		fpsButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				getUserFPS();
			}
		});

		final JLabel valueLabel = new JLabel();
		valueLabel.setPreferredSize(new Dimension(30, 16));
		valueLabel.setText("100%");

		final JSlider slider = new JSlider(0, 200, 100);
		slider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				valueLabel.setText(slider.getValue() + "%");
				timeline.timeMultiplier = slider.getValue()/100F;
			}
		});
		slider.setPreferredSize(new Dimension(100,20));

		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				slider.setValue(100);
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.anchor = c.CENTER;
		c.insets = new Insets(2,2,2,2);
		c.gridwidth = 1;
		add(lengthFrameLabel, c);
		c.gridx = 1;
		add(lengthSecondsLabel, c);

		c.gridx = 0;
		c.gridy = 1;
		add(fpsLabel, c);
		c.gridx = 1;
		add(fpsButton, c);

		c.gridx = 0;
		c.gridy = 2;
		add(new JLabel("Play speed"), c);

		c.gridx = 1;
		add(slider, c);

		c.gridx = 0;
		c.gridy = 3;
		add(valueLabel,c);
		c.gridx = 1;
		add(resetButton,c);

		setBorder(BorderFactory.createTitledBorder("Animation"));
	}
	
	/**
	 * Ask the user for an FPS value, and set the aniamtion's FPS value to it 
	 * if an appropriate value is supplied.
	 */
	private void getUserFPS()
	{
		Integer fps = null;			
		while(true)
		{
			String input = JOptionPane.showInputDialog(timeline.timelineFrame, "Set FPS (20-60)");
			if(input == null)
				break;
			fps = getFPSFromString(input);
			if(fps != null)
			{
				timeline.onFPSChange(fps);
				break;
			}
			else
				JOptionPane.showMessageDialog(timeline.timelineFrame, "Invalid input");
		}
	}

	/**
	 * Get an FPS value from a string.
	 * The FPS value must be between 20 and 60 (inclusive)
	 * @param input - String to parse.
	 * @return FPS value or null if string is invalid.
	 */
	private Integer getFPSFromString(String input)
	{
		Integer fps = null;

		try
		{
			fps = Integer.parseInt(input);
			if(fps < 20 || fps > 60)
				fps = null;
		}
		catch(NumberFormatException e){}

		return fps;
	}
}
