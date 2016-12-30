package testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import javax.swing.JFileChooser;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.client.model.obj.WavefrontObject;

public class Test 
{

	public static void main(String[] args)
	{
		toFiles();
	}

	public static void toFiles()
	{
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("C:\\Users\\Joseph\\Desktop\\Minecraft\\MCEntityAnimator\\Animations\\NewSetup\\entities"));
		fc.showOpenDialog(null);

		File f = fc.getSelectedFile();

		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(f));
			
			String setupString = "";
			String objString = "";
			String partString = "";

			int targetString = 0;
			String currentLine = "";
			while((currentLine = reader.readLine()) != null)
			{				
				if(currentLine.contains("# Model #"))
					targetString = 1;
				else if(currentLine.contains("# Part #"))
					targetString = 2;
				else
				{
					switch(targetString)
					{
					case 0: setupString += currentLine + "\n"; break;
					case 1: objString += currentLine + "\n"; break;
					case 2: partString += currentLine + "\n"; break;
					}
				}
			}

			new WavefrontObject("Test file", new ByteArrayInputStream(objString.getBytes("UTF-8")));

			File tmp = new File("tmp");
			if(!tmp.exists())
				tmp.createNewFile();
			
			FileWriter fw = new FileWriter(tmp);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(setupString);
			bw.close();
			fw.close();
				
			System.out.println(CompressedStreamTools.read(tmp));
			
			reader.close();
			tmp.delete();
		} 
		catch (FileNotFoundException e) {e.printStackTrace();} 
		catch (IOException e) {e.printStackTrace();}
	}

}
