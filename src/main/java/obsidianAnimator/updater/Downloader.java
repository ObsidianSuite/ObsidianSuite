package obsidianAnimator.updater;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;

public class Downloader
{

	private final static String dowloadURL = "http://nthrootsoftware.com/MCEA/MCEA_Updater.zip";
	private Thread worker;
	private Updater gui;
	
	public Downloader(Updater gui) 
	{
		this.gui = gui;
		gui.setOutputText("Contacting Download Server...");
		download();
	}

	private void download()
	{
		worker = new Thread(new Runnable()
		{
			public void run()
			{
				try 
				{
					downloadZip(dowloadURL);
					unzip();
					cleanup();
					gui.setOutputText("Updater download finished!");
				} 
				catch (Exception ex) 
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "An error occured while installing the updater");
				}
			}
		});
		worker.start();
	}
	
	private void downloadZip(String link) throws MalformedURLException, IOException
	{
		URL url = new URL(link);
		URLConnection conn = url.openConnection();
		InputStream is = conn.getInputStream();
		long max = conn.getContentLength();
		gui.setOutputText("Downloding file...");
		BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(new File("update.zip")));
		byte[] buffer = new byte[32 * 1024];
		int bytesRead = 0;
		int in = 0;
		while ((bytesRead = is.read(buffer)) != -1) {
			in += bytesRead;
			fOut.write(buffer, 0, bytesRead);
		}
		fOut.flush();
		fOut.close();
		is.close();
		gui.setOutputText("Download Complete!");

	}

	private void unzip() throws IOException
	{
		int BUFFER = 2048;
		BufferedOutputStream dest = null;
		BufferedInputStream is = null;
		ZipEntry entry;
		ZipFile zipfile = new ZipFile("update.zip");
		Enumeration<? extends ZipEntry> e = zipfile.entries();
		
		while(e.hasMoreElements()) 
		{
			entry = (ZipEntry) e.nextElement();
			gui.setOutputText("Extracting: " +entry);
			if(entry.isDirectory())
				(new File(entry.getName())).mkdir();
			else{
				(new File(entry.getName())).createNewFile();
				is = new BufferedInputStream(zipfile.getInputStream(entry));
				int count;
				byte data[] = new byte[BUFFER];
				FileOutputStream fos = new
						FileOutputStream(entry.getName());
				dest = new
						BufferedOutputStream(fos, BUFFER);
				while ((count = is.read(data, 0, BUFFER))
						!= -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();
			}
		}
		zipfile.close();
	}
	
	private void cleanup()
	{
		gui.setOutputText("Preforming clean up...");
		File f = new File("update.zip");
		try 
		{
			Files.delete(f.toPath());
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
