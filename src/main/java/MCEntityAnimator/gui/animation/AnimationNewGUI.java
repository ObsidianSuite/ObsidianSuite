package MCEntityAnimator.gui.animation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.lwjgl.opengl.Display;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.distribution.DataHandler;
import MCEntityAnimator.gui.GuiHandler;
import MCEntityAnimator.gui.sequence.GuiAnimationTimeline;
import net.minecraft.client.Minecraft;

public class AnimationNewGUI extends JFrame
{

	private static final long serialVersionUID = 679735101553823485L;

	private static final Insets narrowInsets = new Insets(2,10,2,10);
	private static final Insets wideInsets = new Insets(2,25,2,10);
	private static final Insets deepInsets = new Insets(2,10,4,10);
	
	private static final String[] entites = DataHandler.getEntities().toArray(new String[0]);
	
	public AnimationNewGUI()
	{
		super("New Animation");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		mainPanel.setPreferredSize(new Dimension(300, 170));
		
		final JComboBox<String> entityDropDown = new JComboBox<String>(entites);
		entityDropDown.setPreferredSize(new Dimension(100,25));
		
	    final JTextField nameTextField = new JTextField();
		
		JButton create = new JButton("Create");
		create.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String animationName = nameTextField.getText();
				String entityName = (String) entityDropDown.getSelectedItem();
				if(!animationName.equals(""))
				{
					if(!AnimationData.sequenceExists(entityName, animationName))
					{
						AnimationSequence sequence = new AnimationSequence(animationName);
						AnimationData.addSequence(entityName, sequence);
						dispose();
						Minecraft.getMinecraft().displayGuiScreen(new GuiAnimationTimeline(entityName, sequence));
					}
					else
						JOptionPane.showMessageDialog(AnimationNewGUI.this, "An animation with that name already exists.");
				}
			}
		});
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				dispose();
				GuiHandler.mainGui = new MainGUI();
			}
		});
		
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

		setContentPane(mainPanel);
		pack();
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setLocation(Display.getX() + Display.getWidth()/2 - this.getWidth()/2, Display.getY() + Display.getHeight()/2 - this.getHeight()/2);

	}
	
}
