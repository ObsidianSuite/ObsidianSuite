package com.nthrootsoftware.mcea.distribution.job;

import java.io.IOException;

import com.jcraft.jsch.JSchException;
import com.nthrootsoftware.mcea.distribution.ServerAccess;

public class JobPush extends Job
{
	
	private String localAddress;
	private String remoteAddress;

	public JobPush(String hrf, String localAddress, String remoteAddress)
	{
		super("Push " + hrf, new String[]{"Starting push", "Uploading " + localAddress, "Done"});
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
	}

	@Override
	public void run() 
	{
		super.run();
		incrementStage();
		push();
		incrementStage();
	}
	
	public void push()
	{
		try 
		{
			ServerAccess.sendFile(localAddress, remoteAddress, localAddress.contains("/home/shared/"));
		} 
		catch (IOException e1) {e1.printStackTrace();} 
		catch (JSchException e1) {e1.printStackTrace();}
	}
	
}
