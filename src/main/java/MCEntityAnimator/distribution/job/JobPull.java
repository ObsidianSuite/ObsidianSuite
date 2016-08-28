package MCEntityAnimator.distribution.job;

import java.io.File;
import java.io.IOException;

import com.jcraft.jsch.JSchException;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.distribution.DataHandler;
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
			if(localAddress.contains("user"))
			{
				String s = localAddress.substring(0,localAddress.lastIndexOf("/"));
				String entityName = s.substring(s.lastIndexOf("/") + 1);
				String animationName = localAddress.substring(localAddress.lastIndexOf("/") + 1, localAddress.lastIndexOf("."));
				DataHandler.loadEntityAnimation(entityName, animationName);
			}
		} 
		catch (IOException e1) {e1.printStackTrace();} 
		catch (JSchException e1) {e1.printStackTrace();}
	}
	
}
