package obsidianAnimator.gui.frames;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import net.minecraft.client.Minecraft;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.gui.GuiPartSetup;

public class ModelListFrame extends BaseFrame
{
	
	private JList list;

	public ModelListFrame()
	{
		super("Model List");
		addComponents();
	}

	@Override
	protected void addComponents() 
	{
		list = new JList(ModelHandler.getModelList().toArray());
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(280, 150));
		
		JButton editButton = new JButton("Edit");
		editButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				editPressed();
			}
		});
		
		JButton backButton = new JButton("Back");
		backButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				backPressed();
			}
		});
	
		backButton.setPreferredSize(backButton.getPreferredSize());
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		c.weightx = 1;
		
		c.gridwidth = 2;
		mainPanel.add(listScroller,c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridy = 1;
		mainPanel.add(editButton, c);
		c.gridx = 1;
		mainPanel.add(backButton, c);
	}
	
	private void editPressed() 
	{
		if(list.getSelectedValue() != null)
		{
			String entityName = (String) list.getSelectedValue();
			ModelHandler.updateRenderer(entityName);
			frame.dispose();
			Minecraft.getMinecraft().displayGuiScreen(new GuiPartSetup(entityName));
		}
		else
			JOptionPane.showMessageDialog(frame, "Select a model first.");
			
	}
	
	private void backPressed()
	{
		frame.dispose();
		new HomeFrame().display();
	}

}
