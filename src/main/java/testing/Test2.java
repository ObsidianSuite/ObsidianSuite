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

public class Test2
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
			System.out.println(CompressedStreamTools.read(f));
		} 
		catch (FileNotFoundException e) {e.printStackTrace();} 
		catch (IOException e) {e.printStackTrace();}
	}

}
