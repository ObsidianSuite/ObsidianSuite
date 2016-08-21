package MCEntityAnimator.distribution;

import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ServerAccess 
{

	private static final String host="192.241.128.45";
	private static Session session;
	
	public static boolean testConnection()
	{
		Properties properties = new Properties(); 
		properties.put("StrictHostKeyChecking", "no");
		
		JSch jsch = new JSch();
		try 
		{
			Session s = jsch.getSession(host);
			s.setConfig(properties);
			s.connect();
		} 
		catch (JSchException e) 
		{
			if(e.getMessage().equals("Auth fail"))
				return true;
			return false;
		}
		return true;
	}
	
	public static void login(String username, String password) throws JSchException
	{
		Properties properties = new Properties(); 
		properties.put("StrictHostKeyChecking", "no");

		JSch jsch = new JSch();
		session=jsch.getSession(username, host, 22);
		session.setPassword(password);
		session.setConfig(properties);
		session.connect();
		System.out.println("Connected");
	}

}
