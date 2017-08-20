package obsidianAnimator.gui.timeline.swing.subsection;

import obsidianAnimator.gui.timeline.changes.ChangeMirror;
import obsidianAnimator.gui.timeline.changes.ChangeReverse;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class TimelineInputPanel extends JPanel
{
	
	JButton playPauseButton;
	public TimelineAnimationPanel animationPanel;
	private TimelinePartPanel partPanel;
	private TimelineMovementPanel movementPanel;
	private TimelineRenderPanel renderPanel;
	private TimelineItemPanel itemPanel;


	public TimelineInputPanel(TimelineInputController controller)
	{						
		animationPanel = controller.getAnimationPanel();
		partPanel = controller.getPartPanel();
		movementPanel = controller.getMovementPanel();
		renderPanel = controller.getRenderPanel();
		itemPanel = controller.getItemPanel();
		
		playPauseButton = new JButton("Play");
		playPauseButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(controller.getTime() >= controller.getCurrentAnimation().getTotalTime())
					controller.setTime(0);
				controller.setPlaying(!controller.isPlaying());	
				if(controller.isPlaying())
				{
					controller.setPlayStartTimeNano(System.nanoTime());
					controller.setPlayStartTimeFrame(controller.getTime());
				}

				controller.updatePlayPauseButton();
			}
		});

		JButton backButton = new JButton("Back");
		backButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				controller.close();
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
		c.insets = new Insets(2,5,10,5);
		c.gridy = 6;
		add(backButton,c);
	}
	
}
