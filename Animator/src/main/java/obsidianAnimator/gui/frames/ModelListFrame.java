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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.minecraft.client.Minecraft;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.gui.entitySetup.EntitySetupController;
import obsidianAnimator.gui.entitySetup.EntitySetupGui;

public class ModelListFrame extends BaseFrame
{
	
	private JList list;
	private JButton editButton;
	
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
		list.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				editButton.setEnabled(((JList)e.getSource()).getSelectedValue() != null);
			}
		});

		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(280, 150));
		
		editButton = new JButton("Edit model");
		editButton.setEnabled(false);
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
			new EntitySetupController(entityName).display();
		}	
	}
	
	private void backPressed()
	{
		frame.dispose();
		new HomeFrame().display();
	}

}
