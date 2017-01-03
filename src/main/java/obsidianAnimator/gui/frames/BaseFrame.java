package obsidianAnimator.gui.frames;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.lwjgl.opengl.Display;

public abstract class BaseFrame 
{
	
	protected JFrame frame;
	protected JPanel mainPanel;
	
	public BaseFrame(String title)
	{
		frame = new JFrame(title);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setPreferredSize(new Dimension(300, 200));
				
		frame.setContentPane(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setLocation(Display.getX() + Display.getWidth()/2 - frame.getWidth()/2, Display.getY() + Display.getHeight()/2 - frame.getHeight()/2);
	}
	
	protected abstract void addComponents();
	
	public void display()
	{
		frame.setVisible(true);
	}

}
