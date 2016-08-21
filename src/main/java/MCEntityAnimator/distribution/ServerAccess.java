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

import MCEntityAnimator.MCEA_Main;
import MCEntityAnimator.gui.animation.MainGUI;

public class ServerAccess
{

	private static final String CrLf = "\r\n";
	private static final String baseURL = "http://nthrootsoftware.com/MCEA/";
	private static final String uploadURL = baseURL + "upload.php?folder=";
	private static final String downloadURL = baseURL + "download.php?user=";

	public static MainGUI gui;
	public static String username;

	public static List<String> uploadAll() throws IOException
	{
		File file;
		
		if(!canConnect())
			return new ArrayList<String>();
		
		if(username.equals("root"))
			file = new File(MCEA_Main.animationPath + "/data/shared");
		else
			file = new File(MCEA_Main.animationPath + "/data/" + username);
		return uploadFolder(file);
	}	

	private static List<String> uploadFolder(File folder) throws IOException
	{
		List<String> failedFiles = new ArrayList<String>();
		for(File f : folder.listFiles())
		{
			if(f.isDirectory())
				failedFiles.addAll(uploadFolder(f));
			else
			{
				if(!uploadFile(f))
					failedFiles.add(f.getName());
			}
				
		}
		return failedFiles;
	}

	/**
	 * Upload a file to the server.
	 * @param file File to upload.
	 * @param path Destination path on server, will for example data/path, do not start with a '/'! 
	 * @throws IOException
	 */
	private static boolean uploadFile(File file) throws IOException 
	{
		output("Uploading " + file.getName() + "...", false);

		String parentPath = file.getParentFile().getPath().replace("\\", "/");
		parentPath = parentPath.substring(parentPath.indexOf("/data") + 1, parentPath.length());	
		URL	url = new URL(uploadURL + parentPath);

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

	public static void downloadAll() throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection) new URL(downloadURL  + username).openConnection();
		InputStream stream;
		
		if(!canConnect())
			return;
		
		if(connection.getResponseCode() == 200)
			stream = connection.getInputStream();
		else
			stream = connection.getErrorStream();

		if(stream != null)
		{
			File homeDir = new File(MCEA_Main.animationPath);
			if(homeDir.exists())
				deleteDirectory(homeDir);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String s;
			while((s = reader.readLine()) != null)
			{
				if(!s.equals(" "))
				{
					if(s.contains("No folder found for"))
						output(s, true);
					else
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

							URL downloadURL = new URL(baseURL + s);
							URLConnection downloadConnection = downloadURL.openConnection();
							InputStream is = downloadConnection.getInputStream();

							BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(file));
							byte[] buffer = new byte[32 * 1024];
							int bytesRead = 0;
							while ((bytesRead = is.read(buffer)) != -1) 
							{
								fOut.write(buffer, 0, bytesRead);
							}
							output(s + " downloaded", false);
							fOut.flush();
							fOut.close();
							is.close();
						}
					}
				}

			}
			stream.close();
		}
	}


	public static void output(String output, boolean server)
	{
		if(gui != null)
			gui.updateOutput(output, server);
//		else
//			System.out.println("[" + (server ? "SERVER" : "CLIENT") + "] " + output);
	}

	public static List<String> getErrors()
	{
		List<String> errorList = new ArrayList<String>();
		File sharedFolder = new File(MCEA_Main.animationPath + "/data/shared");
		for(File f : sharedFolder.listFiles())
		{
			if(f.isDirectory())
				errorList.addAll(getEntityFolderErrors(f));
		}
		return errorList;
	}

	private static List<String> getEntityFolderErrors(File folder)
	{
		List<String> errorList = new ArrayList<String>();
		boolean dataExists = false;
		boolean textureExists = false;
		boolean modelExists = false;
		boolean pxyExists = false;

		for(File f : folder.listFiles())
		{
			if(f.getName().contains(".data"))
				dataExists = true;
			else if(f.getName().contains(".png"))
				textureExists = true;
			else if(f.getName().contains(".obj"))
				modelExists = true;
			else if(f.getName().contains(".pxy"))
				pxyExists = true;
		}

		if(!dataExists)
			errorList.add("No data file for " + folder.getName() + ".");
		if(!textureExists)
			errorList.add("No texture for " + folder.getName() + ".");
		if(!modelExists)
			errorList.add("No model for " + folder.getName() + ".");
		if(!pxyExists)
			errorList.add("No pxy file for " + folder.getName() + ".");

		return errorList;
	}

	private static void deleteDirectory(File dir)
	{
		for(File f : dir.listFiles())
		{
			if(f.isDirectory())
				deleteDirectory(f);
			else
				f.delete();
		}
		dir.delete();	
	}

	public static boolean canConnect()
	{
		try
		{
			HttpURLConnection connection = (HttpURLConnection) new URL(baseURL).openConnection();
			connection.connect();
		}
		catch(IOException e)
		{
			System.out.println("Unable to connect to animation server.");
			return false;
		}		
		return true;
	}

}

