package obsidianAnimator.gui.entitySetup;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.lwjgl.opengl.Display;

public class EntitySetupFrame extends JFrame
{


	
	private EntitySetupController controller;

	public EntitySetupFrame(EntitySetupController controller)
	{
		super("Entity Setup");
		
		this.controller = controller;
		
		if(Display.isVisible())
			setLocation(Display.getX() + 10, Display.getY() + 440);
		else
		{
			setLocationRelativeTo(null);
			setLocation(50, 420);
		}

		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				controller.close();
			}

		});
		
		JButton doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				//mc.displayGuiScreen(new GuiAnimationParenting(entityName)); 
			}
		});
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Part Setup", new EntitySetupPartPanel(controller));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);

		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(tabbedPane,c);
		
		c.gridx = 0;
		c.gridy = 1;
		mainPanel.add(doneButton, c);
		
		setContentPane(mainPanel);
		pack();
		setResizable(false);
		setAlwaysOnTop(true);
	}

	
}

