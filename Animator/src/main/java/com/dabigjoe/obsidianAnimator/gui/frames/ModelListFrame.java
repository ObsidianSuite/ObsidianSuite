package com.dabigjoe.obsidianAnimator.gui.frames;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.dabigjoe.obsidianAPI.file.FileHandler;
import com.dabigjoe.obsidianAnimator.data.ModelHandler;
import com.dabigjoe.obsidianAnimator.file.FileChooser;
import com.dabigjoe.obsidianAnimator.file.FileNotChosenException;
import com.dabigjoe.obsidianAnimator.gui.entitySetup.EntitySetupController;
import com.dabigjoe.obsidianAnimator.gui.entitySetup.EntitySetupGui;

import net.minecraft.client.Minecraft;

public class ModelListFrame extends BaseFrame
{
	
	private JList list;
	private JButton editButton, exportButton;
	
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
				exportButton.setEnabled(((JList)e.getSource()).getSelectedValue() != null);
			}
		});

		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(280, 150));
		
		editButton = new JButton("Edit");
		editButton.setEnabled(false);
		editButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				editPressed();
			}
		});
		
		exportButton = new JButton("Export");
		exportButton.setEnabled(false);
		exportButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				exportPressed();
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
		
		c.gridwidth = 3;
		mainPanel.add(listScroller,c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridy = 1;
		mainPanel.add(editButton, c);
		c.gridx = 1;
		mainPanel.add(exportButton, c);
		c.gridx = 2;
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
	
	private void exportPressed() 
	{
		String entityName = (String) list.getSelectedValue();
		String suggestedFileName = entityName + "." + FileHandler.obsidianModelExtension;
		
		File copy;
		try {
			copy = FileChooser.getModelSaveLocation(frame, suggestedFileName);
		} catch (FileNotChosenException e) {
			return;
		}
		
		//Ensure extension is correct.
        String fileName = copy.getName();
        if(fileName.contains("."))
            fileName = fileName.substring(0, fileName.indexOf("."));
        fileName += "." + FileHandler.obsidianModelExtension;
        copy = new File(copy.getParentFile(), fileName);
		
		//Check if overwriting existing file
		if(copy.exists()) {
			//Prompt overwrite - exit method if don't want to overwrite.
			if(JOptionPane.showConfirmDialog(frame, "A file already exists with this name.\n Overwrite?", "Overwrite File", JOptionPane.YES_NO_OPTION) == 1)
				return;
		}
		
		File file = new File(FileHandler.modelFolder, suggestedFileName);
		try {
			org.apache.commons.io.FileUtils.copyFile(file, copy);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void backPressed()
	{
		frame.dispose();
		new HomeFrame().display();
	}

}
