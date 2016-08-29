package com.nthrootsoftware.updater;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UpdaterGUI extends JFrame
{

	private JPanel mainPanel;
	private JLabel output = new JLabel("Would you like to update now?");
	private JProgressBar progressBar;
	private JButton updateButton;
	private JButton cancelButton;

	public UpdaterGUI() 
	{
		super("MCEA Updater");

		createAndDrawGUI();
		
		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
	}
	
	private void createAndDrawGUI()
	{
		mainPanel = new JPanel();

		
		updateButton = new JButton("Update");
		updateButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				update();
			}
		});

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				dispose();
			}
		});

		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();


		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		JLabel label = new JLabel("MCEA Updater");
		label.setFont(label.getFont().deriveFont(20F));
		mainPanel.add(label, c);

		c.gridx = 0;
		c.gridy = 1;
		mainPanel.add(createLogScrollPane(), c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		mainPanel.add(output, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(updateButton, c);

		c.gridx = 1;
		mainPanel.add(cancelButton, c);

		setContentPane(mainPanel);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private JScrollPane createLogScrollPane()
	{
		JTextArea textArea = new JTextArea(5, 30);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setText(Updater.getLog());
		textArea.setEditable(false);
		textArea.setOpaque(false);
		textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		return scrollPane;
	}
	
	private void update()
	{
		removeButtons();
		new Downloader(this);
	}

	public void setOutputText(String outputText)
	{
		output.setText(outputText);
		revalidate();
		repaint();
		pack();
	}

	public void removeButtons()
	{
		mainPanel.remove(updateButton);
		mainPanel.remove(cancelButton);
		addProgressBar();
		revalidate();
		repaint();
		pack();
	}
	
	private void addProgressBar()
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		mainPanel.add(progressBar, c);
	}

	public void setProgressBarMax(int max)
	{
		progressBar.setMaximum(max);
	}

	public void setProgressBarValue(int value)
	{
		progressBar.setValue(value);
	}
	
	public void shutDown()
	{
		new ShutdownThread().start();
	}

	private class ShutdownThread extends Thread
	{
		@Override
		public void run() 
		{
			try 
			{
				Thread.sleep(1000);
				UpdaterGUI.this.setOutputText("Closing");
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			dispose();
		}
	}
}
