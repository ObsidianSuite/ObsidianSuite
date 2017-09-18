package com.dabigjoe.obsidianAPI.exceptions;

public class UnregisteredEntityException extends RuntimeException
{
	public UnregisteredEntityException(String entityType)
	{
		super("Attempted to register an animation for the unregistered entity " + entityType);
	}
}
