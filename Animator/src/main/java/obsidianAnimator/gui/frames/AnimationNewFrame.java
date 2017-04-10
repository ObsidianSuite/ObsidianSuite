package obsidianAnimator.gui.frames;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import obsidianAPI.animation.AnimationSequence;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.file.FileChooser;
import obsidianAnimator.file.FileHandler;
import obsidianAnimator.file.FileNotChosenException;
import obsidianAnimator.gui.timeline.GuiAnimationTimeline;
import obsidianAnimator.gui.timeline.TimelineController;

public class AnimationNewFrame extends BaseFrame
{

	private JComboBox<String> entityDropDown;
	private JTextField nameTextField;
	private JLabel locationLabel;

	private String[] entites = ModelHandler.getModelList().toArray(new String[0]);

	private File animationFolder;

	public AnimationNewFrame()
	{
		super("New Animation");
		addComponents();
	}

	@Override
	protected void addComponents()
	{
		entityDropDown = new JComboBox<String>(entites);
		nameTextField = new JTextField();
		locationLabel = new JLabel("No location set");

		entityDropDown.setPreferredSize(new Dimension(100,25));
		locationLabel.setPreferredSize(new Dimension(100,25));

		JButton chooseFolder = new JButton("Choose folder");
		chooseFolder.addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				chooseFolderPressed();
			}
		});

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

		create.setPreferredSize(chooseFolder.getPreferredSize());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2,5,2,5);

		//Entity
		mainPanel.add(new JLabel("Entity"),c);
		c.gridy = 1;
		mainPanel.add(entityDropDown, c);

		//Folder location
		c.gridy = 2;
		mainPanel.add(new JLabel("Location"), c);
		c.gridwidth = 1;
		c.gridy = 3;
		mainPanel.add(locationLabel, c);
		c.gridx = 1;
		mainPanel.add(chooseFolder, c);

		//Animation name
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		mainPanel.add(new JLabel("Name"),c);
		c.gridy = 5;
		mainPanel.add(nameTextField, c);

		//Buttons
		c.gridwidth = 1;
		c.gridy = 6;
		mainPanel.add(create,c);
		c.gridx = 1;
		mainPanel.add(cancel,c);

	}

	private void chooseFolderPressed()
	{
		try
		{
			animationFolder = FileChooser.chooseAnimationFolder(frame);
			String path = animationFolder.getAbsolutePath();
			locationLabel.setText(path);
			locationLabel.setToolTipText(path);
			frame.revalidate();
			frame.repaint();
		}
		catch(FileNotChosenException e){}
	}

	private void createPressed()
	{
		String animationName = nameTextField.getText();
		String entityName = (String) entityDropDown.getSelectedItem();
		if(!animationName.equals(""))
		{
			File animationFile = new File(animationFolder, animationName + "." + FileHandler.animationExtension);
			if(!animationFile.exists())
			{
				AnimationSequence sequence = new AnimationSequence(entityName, animationName);
				frame.dispose();
				new TimelineController(animationFile, sequence).display();
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
