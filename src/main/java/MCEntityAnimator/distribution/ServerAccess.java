package MCEntityAnimator.distribution;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import MCEntityAnimator.MCEA_Main;

public class ServerAccess
{

	private static final String CrLf = "\r\n";
	private static final String baseURL = "http://users.ecs.soton.ac.uk/je5g15/MCEA/upload.php?folder=";
	public static DistributionGUI gui;

	public static List<String> uploadAll(File file, String path) throws IOException
	{
		List<String> failedFiles = new ArrayList<String>();
		String newPath = path.equals("") ? file.getName() : path + "/" + file.getName();
		if(file.isDirectory())
		{
			for(File f : file.listFiles())
			{
				failedFiles.addAll(uploadAll(f, newPath));
			}
		}
		else if(!uploadFile(file, path))
			failedFiles.add(newPath);
		return failedFiles;
	}	

	/**
	 * Upload a file to the server.
	 * @param file File to upload.
	 * @param path Destination path on server, will for example data/path, do not start with a '/'! 
	 * @throws IOException
	 */
	private static boolean uploadFile(File file, String path) throws IOException 
	{
		output("Uploading " + file.getName() + "...", false);

		URL	url = new URL(baseURL + path);

		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);

		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] fileData = new byte[fileInputStream.available()];
		fileInputStream.read(fileData);
		fileInputStream.close();

		String message1 = "";
		message1 += "-----------------------------4664151417711" + CrLf;
		message1 += "Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + file.getName() + "\"" + CrLf;
		message1 += "Content-Type: image/jpeg" + CrLf;
		message1 += CrLf;

		String message2 = "";
		message2 += CrLf + "-----------------------------4664151417711--" + CrLf;

		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------4664151417711");
		connection.setRequestProperty("Content-Length", String.valueOf((message1.length() + message2.length() + fileData.length)));

		OutputStream output = connection.getOutputStream();
		output.write(message1.getBytes());

		// SEND THE FILE
		int index = 0;
		int size = 1024;
		do 
		{
			if ((index + size) > fileData.length) 
				size = fileData.length - index;
			output.write(fileData, index, size);
			index += size;
		} while (index < fileData.length);

		output.write(message2.getBytes());
		output.flush();

		output("Processing file...", true);

		// READ RETURNING MESSAGE
		InputStream input = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));		
		String s;
		while((s = reader.readLine()) != null)
		{
			if(!s.equals("") && !s.equals(" "))
			{
				output(s, true);
				if(s.contains("There was an error uploading"))
					return false;
			}
		}

		output.close();
		input.close();
		return true;
	}

	public static void downloadData() throws IOException
	{
		URL url = new URL("http://users.ecs.soton.ac.uk/je5g15/MCEA/download.php");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream stream;
		if(connection.getResponseCode() == 200)
			stream = connection.getInputStream();
		else
			stream = connection.getErrorStream();

		if(stream != null)
		{
			File dataFolder = new File(MCEA_Main.animationPath + "/data");
			FileUtils.deleteDirectory(dataFolder);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String s;
			while((s = reader.readLine()) != null)
			{
				if(!s.equals(" "))
				{
					//* denotes folder
					if(s.contains("*"))
					{
						String folderName = s.substring(0, s.length() - 1);
						File file = new File(MCEA_Main.animationPath + "/" + folderName);
						file.mkdirs();
					}
					else
					{
						output("Downloading " + s + "...", false);

						File file = new File(MCEA_Main.animationPath + "/" + s);
						file.getParentFile().mkdirs();

						URL downloadURL = new URL("http://users.ecs.soton.ac.uk/je5g15/MCEA/" + s);
						URLConnection downloadConnection = downloadURL.openConnection();
						InputStream is = downloadConnection.getInputStream();

						long max = downloadConnection.getContentLength();
						BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(file));
						byte[] buffer = new byte[32 * 1024];
						int bytesRead = 0;
						int in = 0;
						while ((bytesRead = is.read(buffer)) != -1) 
						{
							in += bytesRead;
							fOut.write(buffer, 0, bytesRead);
						}
						output(s + " downloaded", false);
						fOut.flush();
						fOut.close();
						is.close();
					}
				}

			}
			stream.close();
		}
	}

	public static void output(String s, boolean bool)
	{
		if(gui != null)
			gui.updateOutput(s, false);
	}

	public static List<String> getErrors()
	{
		List<String> errorList = new ArrayList<String>();
		File srcFolder = new File(MCEA_Main.animationPath + "/data");
		for(File f : srcFolder.listFiles())
		{
			if(f.isDirectory())
				errorList.addAll(getEntityFolderErrors(f));
		}
		return errorList;
	}

	private static List<String> getEntityFolderErrors(File folder)
	{
		List<String> errorList = new ArrayList<String>();
		boolean animationFolderExists = false;
		boolean textureExists = false;
		boolean modelExists = false;
		boolean pxyExists = false;

		for(File f : folder.listFiles())
		{
			if(f.getName().equals("animation"))
				animationFolderExists = true;
			else if(f.getName().contains(".png"))
				textureExists = true;
			else if(f.getName().contains(".obj"))
				modelExists = true;
			else if(f.getName().contains(".pxy"))
				pxyExists = true;
		}

		if(!animationFolderExists)
			errorList.add("No animation folder for " + folder.getName() + ". Let Joe know about this.");
		if(!textureExists)
			errorList.add("No texture for " + folder.getName() + ".");
		if(!modelExists)
			errorList.add("No model for " + folder.getName() + ".");
		if(!pxyExists)
			errorList.add("No pxy file for " + folder.getName() + ".");

		return errorList;
	}


}

