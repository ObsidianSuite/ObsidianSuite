package com.dabigjoe.obsidianAPI.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {
	
	public static void addEntryToExistingZip(File zipFile, File fileToAdd) throws IOException {
		addEntryToExistingZip(zipFile, fileToAdd.getName(), new FileInputStream(fileToAdd));
	}
	
	public static void addEntryToExistingZip(File zipFile, String entryName, InputStream stream) throws IOException {

		//Get a temp file and delete it so zip file can be renamed to it.
		File tempFile = File.createTempFile(zipFile.getName(), null);
		tempFile.delete();

		//Rename zip file to temp file
		boolean renameOk = zipFile.renameTo(tempFile);
		if (!renameOk)
		{
			throw new IOException();
		}

		byte[] buf = new byte[1024];
		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

		//Write files in zip to output (only if they aren't the same as file being added)
		ZipEntry entry = zin.getNextEntry();
		while (entry != null) 
		{
			String name = entry.getName();
			if (!entryName.equals(name)) 
			{
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(name));
				// Transfer bytes from the ZIP file to the output file
				int len;
				while ((len = zin.read(buf)) > 0) 
				{
					out.write(buf, 0, len);
				}
			}
			entry = zin.getNextEntry();
		}       
		zin.close();

		//Write new file to zip
		out.putNextEntry(new ZipEntry(entryName));
		int len;
		while ((len = stream.read(buf)) > 0) 
		{
			out.write(buf, 0, len);
		}
		out.closeEntry();
		stream.close();

		// Complete the ZIP file
		out.close();
		tempFile.delete();
	}
	
}
