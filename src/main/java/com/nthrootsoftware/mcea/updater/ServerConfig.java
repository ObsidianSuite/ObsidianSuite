package com.nthrootsoftware.mcea.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ServerConfig 
{

	private final static String configURL = "http://nthrootsoftware.com/MCEA/updater.cfg";
	private static Map<String, Boolean> properties = new HashMap<String, Boolean>();

	public static void init()
	{
		try
		{
			URL url = new URL(configURL);

			InputStream html = url.openStream();

			int c = 0;
			StringBuffer buffer = new StringBuffer("");

			while(c != -1) 
			{
				c = html.read();
				buffer.append((char)c);
			}

			String configData = buffer.toString();

			String[] lines = configData.split("\n");
			for(int i = 0; i < lines.length - 1; i++)
			{
				String line = lines[i];
				if(!line.equals("") && !line.startsWith("#"))
				{
					try 
					{
						loadProperty(line);
					} 
					catch (PropertyException e) 
					{
						e.printStackTrace();
					}
				}
			}
			
			System.out.println("Loaded server config.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void loadProperty(String propertyLine) throws PropertyException
	{
		String[] split = propertyLine.split(":");
		if(split.length != 2)
			throw new PropertyException(propertyLine, PropertyException.FORMAT_ERROR);
		String property = split[0];
		String value = split[1];

		if(!value.equals("true") && !value.equals("false"))
			throw new PropertyException(propertyLine, PropertyException.VALUE_ERROR);

		boolean valueBool = Boolean.parseBoolean(split[1]);

		properties.put(property, valueBool);
	}

	private static boolean getProperty(String property) throws PropertyException
	{
		if(properties.containsKey(property))
			return properties.get(property);
		throw new PropertyException(property, PropertyException.PROP_NOT_FOUND_ERROR);
	}

	public static boolean modLive()
	{
		boolean flag = false;
		try 
		{
			flag = getProperty("mod_live");
		} 
		catch (PropertyException e) {e.printStackTrace();}
		return flag;
	}

	public static boolean updaterLive()
	{
		boolean flag = false;
		try 
		{
			flag = getProperty("updater_live");
		} 
		catch (PropertyException e) {e.printStackTrace();}
		return flag;
	}


}
