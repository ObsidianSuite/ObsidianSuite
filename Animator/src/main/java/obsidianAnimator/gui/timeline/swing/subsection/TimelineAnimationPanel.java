package obsidianAnimator.gui.timeline.swing.subsection;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

		setBorder(BorderFactory.createTitledBorder("Animation"));
	}
	
}
