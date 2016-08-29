package com.nthrootsoftware.mcea.distribution.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.JSchException;
import com.nthrootsoftware.mcea.distribution.ServerAccess;
import com.nthrootsoftware.mcea.gui.GuiHandler;


public abstract class Job
{
	
	//Name of the job, eg "push player animation - test"
	private String jobName;
	//Various statuses the job progress through.
	private String[] statuses;
	//The progress of the job (which status it is on).
	private int stage;
	
	public Job(String jobName, String[] statuses)
	{
		this.jobName = jobName;
		this.statuses = statuses;
	}
	
	public String getName()
	{
		return jobName;
	}
	
	public void run()
	{
		stage = 0;		
		postStatus();
	}
	
	public void incrementStage()
	{
		stage ++;
		postStatus();
	}
	
	public void postStatus()
	{
		String output = "";
		if(stage >= statuses.length)
			output = "STATUS_MISSING";
		else
			output = statuses[stage];
		GuiHandler.mainGui.jobPanel.updateCurrentJobStatusLabel(output);
	}
	
	public void completeJob()
	{
		if(GuiHandler.mainGui != null)
			GuiHandler.mainGui.refreshTable();
	}
	
}


