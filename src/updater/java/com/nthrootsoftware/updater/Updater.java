package com.nthrootsoftware.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Updater 
{

	private final static String logURL = "http://nthrootsoftware.com/MCEA/mcea.log";

	public static void main(String[] args)
	{
		try
		{
			new UpdaterGUI();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String getLog()
	{
		String log = "MISSING_LOG";
		try
		{
			String rawData = Updater.getData(logURL);
			log = rawData.substring(rawData.indexOf("[log]") + 5, rawData.indexOf("[/log]"));
			log = log.replaceFirst("\n", "");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return log;
	}

	private static String getData(String address) throws IOException
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
}
