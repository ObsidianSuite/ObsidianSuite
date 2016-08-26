package MCEntityAnimator.distribution.job;

import java.io.IOException;

import com.jcraft.jsch.JSchException;

import MCEntityAnimator.distribution.ServerAccess;

public class JobPull extends Job
{
	
	private String localAddress;
	private String remoteAddress;
	
	public JobPull(String hrf, String localAddress, String remoteAddress)
	{
		super("Pull " + hrf, new String[]{"Starting pull", "Downloading " + remoteAddress, "Done"});
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
	}

	@Override
	public void run() 
	{
		super.run();
		incrementStage();
		pull();
		incrementStage();
	}
	
	public void pull()
	{
		try 
		{
			ServerAccess.getFile(localAddress, remoteAddress);
		} 
		catch (IOException e1) {e1.printStackTrace();} 
		catch (JSchException e1) {e1.printStackTrace();}
	}
	
}
