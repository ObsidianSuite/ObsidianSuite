package obsidianAnimator.updater;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.minecraft.client.Minecraft;

public class Updater 
{

	private final static String logURL = "http://nthrootsoftware.com/MCEA/mcea.log";

	private String modVersion;
	private String serverVersion;

	private UpdaterFrame updaterFrame;

	public void checkForUpdate(String currentVersion) throws IOException
	{
		modVersion = currentVersion;
		serverVersion = getLatestVersion();
		updaterFrame = new UpdaterFrame();
		if(ServerConfig.updaterLive())
			installUpdater();
		if(ServerConfig.modLive() || Minecraft.getMinecraft().getSession().getUsername().equals("DaBigJoe"))
		{
			System.out.println("Mod live");
			if(!outOfDate(currentVersion, getLatestVersion()))
				updaterFrame.dispose();	
		}
		else
			updaterFrame.dispose();	
	}

	public String getLatestVersion() throws IOException
	{
		String data = getData(logURL);
		for(String line : data.split("\n"))
		{
			if(line.startsWith("Version"))
				return line.substring(9).trim();
		}
		throw new RuntimeException("Unable to get latest version number from server.");
	}

	public String getData(String address) throws IOException
	{
		URL url = new URL(address);

		InputStream html = url.openStream();

		int c = 0;
		StringBuffer buffer = new StringBuffer("");

		while(c != -1) 
		{
			c = html.read();
			buffer.append((char)c);
		}

		return buffer.toString();
	}

	private boolean outOfDate(String modVersion, String serverVersion)
	{
		String[] modVerSplit = modVersion.split("\\.");
		String[] serverVerSplit = serverVersion.split("\\.");

		for(int i = 0; i < serverVerSplit.length; i++)
		{
			int s = Integer.parseInt(serverVerSplit[i]);
			if(modVerSplit.length > i)
			{
				int m = Integer.parseInt(modVerSplit[i]);
				if(s > m)
					return true;
			}
			else
			{
				return true;
			}
		}
		return false;
	}

	public void setOutputText(String s)
	{
		updaterFrame.output.setText(s);
	}

	private class UpdaterFrame extends JFrame
	{

		private JPanel mainPanel;
		private JLabel output = new JLabel("Would you like to update now?");
		private JButton updateButton, cancelButton;

		private UpdaterFrame()
		{
			super("MCEA Update");
			mainPanel = new JPanel();

			updateButton = new JButton("Update");
			updateButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					removeButtons();
					beginUpdate();
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
			c.gridwidth = 2;
			c.insets = new Insets(10, 10, 10, 10);
			mainPanel.add(new JLabel("A new version of MCEA is available."), c);

			c.gridy = 1;
			c.gridwidth = 1;
			mainPanel.add(new JLabel("Current version: v" + modVersion), c);

			c.gridx = 1;
			c.gridy = 1;
			mainPanel.add(new JLabel("New version: v" + serverVersion), c);

			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 2;
			mainPanel.add(output, c);

			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 1;
			mainPanel.add(updateButton, c);

			c.gridx = 1;
			mainPanel.add(cancelButton, c);

			setContentPane(mainPanel);
			pack();
			setLocationRelativeTo(null);
			setResizable(false);
			setVisible(true);
			setAlwaysOnTop(true);
		}


		private void removeButtons()
		{
			mainPanel.remove(updateButton);
			mainPanel.remove(cancelButton);
			revalidate();
			repaint();
			pack();
		}

	}

	private void installUpdater()
	{
		setOutputText("Updater not found, installing...");
		updaterFrame.revalidate();
		updaterFrame.repaint();
		updaterFrame.pack();
		new Downloader(this);
	}

	public void beginUpdate()
	{
		setOutputText("Stopping Minecraft and loading updater...");
		updaterFrame.revalidate();
		updaterFrame.repaint();
		updaterFrame.pack();
		ShutdownThread t = new ShutdownThread();
		t.start();
	}

	private class ShutdownThread extends Thread
	{

		@Override
		public void run() 
		{
			try 
			{
				Thread.sleep(2000);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			try 
			{
				Runtime.getRuntime().exec("java -jar MCEA_Updater.jar");
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			Minecraft.getMinecraft().shutdown();
		}

	}
}
