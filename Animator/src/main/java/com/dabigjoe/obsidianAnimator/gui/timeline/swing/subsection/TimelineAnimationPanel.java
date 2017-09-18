package com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dabigjoe.obsidianAnimator.gui.timeline.changes.ChangeMirror;
import com.dabigjoe.obsidianAnimator.gui.timeline.changes.ChangeReverse;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimelineAnimationPanel extends JPanel
{

	public JLabel lengthFrameLabel;
	public JLabel lengthSecondsLabel;
	public JLabel fpsLabel;

	public TimelineAnimationPanel(TimelineAnimationController controller)
	{
		lengthFrameLabel = new JLabel();
		lengthSecondsLabel = new JLabel();
		fpsLabel = new JLabel(controller.getFPS() + " FPS");

		JButton fpsButton = new JButton("Set FPS");
		fpsButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.getUserFPS();
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
				controller.setTimeMultiplier(slider.getValue()/100F);
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

		JButton reverseButton = new JButton("Reverse");
		reverseButton.addActionListener(e -> controller.mainController.versionController.applyChange(new ChangeReverse()));

		JButton mirrorButton = new JButton("Mirror");
		mirrorButton.addActionListener(e -> controller.mainController.versionController.applyChange(new ChangeMirror()));

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
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

        c.gridy = 4;
        c.gridx = 0;
        add(reverseButton, c);
        c.gridx = 1;
        add(mirrorButton, c);

		setBorder(BorderFactory.createTitledBorder("Animation"));
	}

}
