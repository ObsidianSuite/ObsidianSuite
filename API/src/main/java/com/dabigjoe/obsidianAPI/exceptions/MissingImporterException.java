package com.dabigjoe.obsidianAPI.exceptions;

public class MissingImporterException extends Exception
{
	public MissingImporterException(String extension)
	{
		super("There is no importer for ths file type: ." + extension);
	}
}
