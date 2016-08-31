package com.nthrootsoftware.mcea.distribution.job;

import java.util.LinkedList;
import java.util.Queue;

import com.nthrootsoftware.mcea.gui.GuiHandler;

public class JobHandler 
{
	
	private Queue<Job> jobQueue;
	private JobThread jobThread;

	public JobHandler()
	{
		jobQueue = new LinkedList<Job>();
		jobThread = new JobThread();
		new Thread(jobThread).start();
	}
	
	public void queueJob(Job job)
	{
		jobQueue.add(job);
		synchronized (jobThread) 
		{
			jobThread.notify();
		}
	}
	
	public void dispose()
	{
		synchronized (jobThread)
		{
			jobThread.run = false;
			jobThread.notify();
		}
	}
	
	private class JobThread implements Runnable 
	{
		boolean run = true;
	    public void run() 
	    {
	        while(run)
	        {
	        	while(jobQueue.peek() != null)
	        	{
		        	Job currentJob = jobQueue.poll();
		     		GuiHandler.mainGui.jobPanel.updateJobNumberLabel(jobQueue.size() + 1);
	        		GuiHandler.mainGui.jobPanel.updateCurrentJobLabel(currentJob.getName());
	        		currentJob.run();
	        		currentJob.completeJob();
	        	}

	        	if(GuiHandler.mainGui != null)
	        	{
	        		GuiHandler.mainGui.jobPanel.updateJobNumberLabel(0);
	        		GuiHandler.mainGui.jobPanel.updateCurrentJobLabel("-");
	        		GuiHandler.mainGui.jobPanel.updateCurrentJobStatusLabel("-");
	        	}
	        	
	        	try
	        	{
	        		synchronized (jobThread) 
	        		{
	        			wait();
	        		}
				} 
	        	catch (InterruptedException e) 
	        	{
	        		e.printStackTrace();
	        	}
	        }
	    }
	}
	
}
