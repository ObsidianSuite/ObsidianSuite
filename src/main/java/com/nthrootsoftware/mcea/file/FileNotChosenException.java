package com.nthrootsoftware.mcea.file;

public class FileNotChosenException extends Exception
{

	public FileNotChosenException()
	{
		super("User did not choose a file...");
	}
	
}
