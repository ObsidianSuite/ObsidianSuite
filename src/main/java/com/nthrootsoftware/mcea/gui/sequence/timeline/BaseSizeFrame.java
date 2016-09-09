package com.nthrootsoftware.mcea.gui.sequence.timeline;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class BaseSizeFrame extends JFrame
{
	
	ValuePanel[] valuePanels;

	private JButton setSizeButton;
	
	public BaseSizeFrame(final RenderPanel renderPanel)
	{
		super("Base Size");
		
		valuePanels = new ValuePanel[]{
				new ValuePanel("Min X:", -1, false, renderPanel.timeline.gridMinX), 
				new ValuePanel("Max X:", 1, true, renderPanel.timeline.gridMaxX), 
				new ValuePanel("Min Z:", -1, false, renderPanel.timeline.gridMinZ), 
				new ValuePanel("Max Z:", 1, true, renderPanel.timeline.gridMaxZ)};
		
		JPanel mainPanel = new JPanel();

		setSizeButton = new JButton("Set Size");
		setSizeButton.setEnabled(checkValues());
		setSizeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				renderPanel.timeline.gridMinX = valuePanels[0].getValue();
				renderPanel.timeline.gridMaxX = valuePanels[1].getValue();
				renderPanel.timeline.gridMinZ = valuePanels[2].getValue();
				renderPanel.timeline.gridMaxZ = valuePanels[3].getValue();
				dispose();
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				dispose();
			}
		});
		
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(2,2,2,2);
		mainPanel.add(valuePanels[0],c);
		c.gridx = 1;
		mainPanel.add(valuePanels[1],c);
		c.gridx = 0;
		c.gridy = 1;
		mainPanel.add(valuePanels[2],c);
		c.gridx = 1;
		mainPanel.add(valuePanels[3],c);

		c.gridx = 0;
		c.gridy = 2;
		c.fill = c.BOTH;
		c.insets = new Insets(5,5,5,5);
		mainPanel.add(setSizeButton, c);
		c.gridx = 1;
		mainPanel.add(cancelButton, c);
		
		setAlwaysOnTop(true);
		setLocationRelativeTo(renderPanel);
		setResizable(false);
		setContentPane(mainPanel);
		pack();
		setVisible(true);
	}
	
	private void refresh()
	{
		setSizeButton.setEnabled(checkValues());
	}

	private boolean checkValues()
	{
		for(ValuePanel vp : valuePanels)
		{
			if(!vp.checkValue())
				return false;
		}
		return true;
	}

	private class ValuePanel extends JPanel
	{

		JLabel label, statusLabel;
		JTextField valueField;
		int limit;
		boolean greaterThan;
		
		private ValuePanel(String labelName, int limit, boolean greaterThan, int initialValue)
		{
			this.limit = limit;
			this.greaterThan = greaterThan;
			
			label = new JLabel(labelName);

			valueField = new JTextField();
			valueField.setText(Integer.toString(initialValue));
			valueField.setPreferredSize(new Dimension(30,20));
			valueField.setToolTipText(String.format("Value must be %s or equal to %d", greaterThan ? "greater than" : "less than", limit));
			valueField.getDocument().addDocumentListener(new DocumentListener() 
			{
				public void changedUpdate(DocumentEvent e) {process();}
				public void removeUpdate(DocumentEvent e) {process();}
				public void insertUpdate(DocumentEvent e) {process();}

				public void process() 
				{
					updateStatusLabel();
					BaseSizeFrame.this.refresh();
				}
			});

			statusLabel = new JLabel("OK");
			statusLabel.setPreferredSize(new Dimension(20,20));
			
			add(label);
			add(valueField);
			add(statusLabel);
			
			updateStatusLabel();
		}
		
		private void updateStatusLabel()
		{
			if(checkValue())
				statusLabel.setText("OK");
			else
				statusLabel.setText("X");
		}

		private boolean checkValue()
		{
			Integer value = getValue();
			if(value == null)
				return false;
			return isValueValid(value);
		}
		
		private boolean isValueValid(int value)
		{
			if(greaterThan)
				return value >= limit;
			return value <= limit;
		}
		
		private Integer getValue()
		{
			Integer value = null;
			try
			{
				value = Integer.parseInt(valueField.getText());
			}
			catch(NumberFormatException e){};

			return value;
		}
	}
	

}
