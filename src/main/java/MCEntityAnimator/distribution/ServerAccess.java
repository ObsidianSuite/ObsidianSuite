package MCEntityAnimator.distribution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ServerAccess 
{

	private static final String host="192.241.128.45";
	private static Session session;
	
	public static boolean online = false;

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

	public static void login(String username, String password) throws JSchException, IOException
	{		
		Properties properties = new Properties(); 
		properties.put("StrictHostKeyChecking", "no");

		JSch jsch = new JSch();
		session=jsch.getSession(username, host, 22);
		session.setPassword(password);
		session.setConfig(properties);
		session.connect();
		System.out.println("Connected");
		
		executeCommand("/home/shared/animation/log.sh -l " + username);
		
		DataHandler.clearDataIfDifferentUser(username);
		
		DataHandler.writeUserData(username, password);
		
		online = true;
	}
	
	public static String getUser()
	{
		return session != null ? session.getUserName() : null;
	}
	
	

	public static void getFile(String localFileAddress, String remoteFileAddress) throws IOException, JSchException
	{		
		boolean exists = executeCommand("[ -e " + remoteFileAddress + " ] && echo \"true\" || echo \"false\"").replace("\n", "").replace("\r", "").equals("true");
		if(!exists)
		{
			return;
		}

		FileOutputStream fos=null;

		//Add separator if folder
		String prefix=null;
		if(new File(localFileAddress).isDirectory())
			prefix=localFileAddress+File.separator;

		//Execute 'scp -f remoteFileAddress' remotely
		String command="scp -f "+ remoteFileAddress;
		Channel channel= session.openChannel("exec");
		((ChannelExec)channel).setCommand(command);

		//Get I/O streams for remote scp
		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();
		channel.connect();

		//Send '\0'
		byte[] buf=new byte[1024];
		buf[0]=0;
		out.write(buf, 0, 1); 
		out.flush();

		while(true)
		{
			int c = checkConnection(in);
			if(c != 'C')
				break;

			//Read '0644'
			in.read(buf, 0, 5);
			long filesize=0L;

			while(true)
			{
				//Error
				if(in.read(buf, 0, 1) < 0)
					break; 
				if(buf[0] == ' ')
					break;
				filesize=filesize*10L+(long)(buf[0]-'0');
			}

			String file=null;
			for(int i=0; ;i++)
			{
				in.read(buf, i, 1);
				if(buf[i]==(byte)0x0a)
				{
					file=new String(buf, 0, i);
					break;
				}
			}

			//Send '\0'
			buf[0]=0;
			out.write(buf, 0, 1); 
			out.flush();

			//Write content to local file
			fos = new FileOutputStream(prefix==null ? localFileAddress : prefix + file);
			int foo;
			while(true)
			{
				if(buf.length < filesize) 
					foo = buf.length;
				else 
					foo = (int)filesize;
				foo = in.read(buf, 0, foo);
				if(foo<0)// error 
					break;
				fos.write(buf, 0, foo);
				filesize-=foo;
				if(filesize == 0L)
					break;
			}
			fos.close();
			fos=null;
			
			try 
			{
				File localFile = new File(localFileAddress);
				localFile.setLastModified(DataHandler.dateFormat.parse(getDateModified(remoteFileAddress)).getTime());
			} 
			catch (ParseException e) {e.printStackTrace();}
			
			if(checkConnection(in)!=0)
				return;

			//Send '\0'
			buf[0]=0; 
			out.write(buf, 0, 1); 
			out.flush();

		}
		
		System.out.println(executeCommand(String.format("/home/shared/animation/log.sh -q %s %s", session.getUserName(), remoteFileAddress)));

	}


	public static void sendFile(String localFileAddress, String remoteFileAddress, boolean userFolder) throws IOException, JSchException
	{		
		FileInputStream fis=null;

		//Execute 'scp -t remoteFileAddress' remotely
		String command="scp " + (userFolder ? "-p" :"") + " -t " + remoteFileAddress;
		Channel channel = session.openChannel("exec");
		((ChannelExec)channel).setCommand(command);

		//Get I/O streams for remote scp
		OutputStream out=channel.getOutputStream();
		InputStream in=channel.getInputStream();
		channel.connect();
		if(checkConnection(in) != 0)
			return;

		//Get local file
		File localFile = new File(localFileAddress);
		//TODO remove create new file.
		if(!localFile.exists())
			localFile.createNewFile();

		//If time stamp is to be preserved, read the time
		//details of the local file and send it over before the file data
		if(userFolder)
		{
			command="T" + (localFile.lastModified()/1000) + " 0";
			command+=(" " + (localFile.lastModified()/1000) + " 0\n"); 
			out.write(command.getBytes()); 
			out.flush();
			if(checkConnection(in) != 0)
				return;
		}

		//Send "C0644 filesize filename", where filename should not include '/'
		long filesize= localFile.length();
		command="C0644 " + filesize + " ";

		if(localFileAddress.lastIndexOf('/')>0)
			command+=localFileAddress.substring(localFileAddress.lastIndexOf('/')+1);
		else
			command+=localFileAddress;
		command+="\n";

		out.write(command.getBytes()); 
		out.flush();
		if(checkConnection(in) != 0)
			return;

		//Send contents of local file
		fis=new FileInputStream(localFile);
		byte[] buf=new byte[1024];
		while(true)
		{
			int len=fis.read(buf, 0, buf.length);
			if(len<=0) break;
			out.write(buf, 0, len);
		}
		fis.close();
		fis=null;

		//Send '\0'
		buf[0]=0; out.write(buf, 0, 1); 
		out.flush();

		if(checkConnection(in)!=0)
			return;

		out.close();

		channel.disconnect();
		
		if(!userFolder)
		{
			Date d = new Date(localFile.lastModified());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm.ss");
			String date = sdf.format(d);
			
			String remoteFileAddressAlt = remoteFileAddress + "b";
			
			String copyCommand = String.format("cp %s %s", remoteFileAddress, remoteFileAddressAlt);
			String removeCommand = String.format("rm %s", remoteFileAddress);
			String moveCommand = String.format("mv %s %s", remoteFileAddressAlt, remoteFileAddress);
			String touchCommand = String.format("touch -mt %s %s", date, remoteFileAddress);
			
			String completeCommand = String.format("%s && %s && %s && %s", copyCommand, removeCommand, moveCommand, touchCommand);
			executeCommand(completeCommand);
		}
		
		executeCommand(String.format("/home/shared/animation/log.sh -p %s %s", session.getUserName(), remoteFileAddress));
	}
		
	private static String getDateModified(String remoteFileAddress) throws JSchException, IOException
	{		
		//Execute 'stat remoteFileAddress' remotely
		String command = "stat " + remoteFileAddress;
		Channel channel = session.openChannel("exec");
		((ChannelExec)channel).setCommand(command);

		//Get I/O streams for remote stat
		OutputStream out=channel.getOutputStream();
		InputStream in=channel.getInputStream();
		channel.connect();


		//Get stat output
		String statOutput = "";
		byte[] tmp=new byte[1024];
		while(true)
		{
			while(in.available()>0)
			{
				int i=in.read(tmp, 0, 1024);
				if(i<0)break;
				statOutput += new String(tmp, 0, i);
			}
			if(channel.isClosed())
			{
				if(in.available()>0) 
					continue; 
				break;
			}
			try{Thread.sleep(1000);}catch(Exception ee){}
		}
		channel.disconnect();

		String lines[] = statOutput.split("\\r?\\n");

		for(String line : lines)
		{
			if(line.contains("Modify:"))
				return line.substring(8).substring(0, 19);
		}
		throw new JSchException("Unable to retrieve last modified date of " + remoteFileAddress + " from server.");
	}

	public static int checkConnection(InputStream in) throws IOException
	{
		int b=in.read();
		// b may be 0 for success,
		//          1 for error,
		//          2 for fatal error,
		//          -1

		//All good
		if(b==0) return b;
		if(b==-1) return b;

		//Not good, read issue and output.
		if(b==1 || b==2)
		{
			StringBuffer sb=new StringBuffer();
			int c;
			do 
			{
				c=in.read();
				if(c != '\n')
					sb.append((char)c);
			}
			while(c!='\n');
			throw new IOException("Error uploading: " + sb.toString());
		}
		return b;
	}

	public static String executeCommand(String command) throws JSchException, IOException
	{
		Channel channel = session.openChannel("exec");
		((ChannelExec)channel).setCommand(command);
		channel.setInputStream(null);
		((ChannelExec)channel).setErrStream(System.err);

		InputStream in = channel.getInputStream();
		channel.connect();

		String s = "";

		byte[] tmp=new byte[1024];
		while(true)
		{
			while(in.available()>0)
			{
				int i=in.read(tmp, 0, 1024);
				if(i<0)break;
				s += new String(tmp, 0, i);
			}
			if(channel.isClosed())
			{
				if(in.available()>0) 
					continue; 
				break;
			}
		}
		channel.disconnect();

		return s;
	}

}
