package obsidianAnimator.gui.timeline.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.lwjgl.opengl.Display;

import obsidianAnimator.gui.timeline.TimelineController;
import obsidianAnimator.gui.timeline.swing.component.CopyLabel;
import obsidianAnimator.gui.timeline.swing.subsection.ActionPointsPanel;

/* ---------------------------------------------------- *
 * 				   	Timeline Frame						*
 * ---------------------------------------------------- */

public class TimelineFrame extends JFrame
{
	
	private TimelineController controller;
	
	
	JPanel mainPanel;
	public ActionPointsPanel actionsPanel;

	public TimelineFrame(TimelineController controller)
	{
		super("Timeline");
		this.controller = controller;
		this.actionsPanel = new ActionPointsPanel(controller);
		
		mainPanel = new JPanel();
		mainPanel.add(controller.inputController.panel);
		mainPanel.add(createRightPane());

		setContentPane(mainPanel);
		pack();
		setAlwaysOnTop(true);
		
		if(Display.isVisible())
			setLocation(Display.getX() + 50, Display.getY() + 500);
		else
		{
			setLocationRelativeTo(null);
			setLocation(50, 520);
		}

		setResizable(false);

		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				controller.close();
			}

		});
		
		controller.keyframeController.panel.refresthLineColours();
	}
	
	private JPanel createRightPane()
	{
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		rightPanel.add(createKeyframeScrollPane(),c);
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		rightPanel.add(actionsPanel, c);
		
		return rightPanel;
	}
	
	private JScrollPane createKeyframeScrollPane()
	{
		JScrollPane scrollPane = new JScrollPane(controller.keyframeController.panel);
		scrollPane.setPreferredSize(new Dimension(700,400));
		scrollPane.setWheelScrollingEnabled(false);
		return scrollPane;
	}

	
}
