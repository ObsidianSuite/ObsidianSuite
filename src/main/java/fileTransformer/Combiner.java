package fileTransformer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class Combiner 
{

	public static void main(String[] args)
	{
		try {
			new Combiner().combine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void combine() throws IOException
	{
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("C:/Users/Joseph/Desktop/Minecraft/MCEntityAnimator/Animations/NewSetup/entities"));
		fc.showSaveDialog(null);

		File f = fc.getSelectedFile();
		PrintWriter writer = new PrintWriter(f);
		writer.print("");
		writer.close();
		
		File folder = f.getParentFile();

		String name = f.getName();
		name = name.substring(0, name.indexOf("."));

		File modelFile = new File(folder, name + ".obj");
		File partFile = new File(folder, name + ".pxy");
		File setupFile = new File(folder, name + ".data");

		String modelStr = readFile(modelFile);
		String partStr = readFile(partFile);

		FileWriter fw = new FileWriter(f,true);
		BufferedWriter bw = new BufferedWriter(fw);

		CompressedStreamTools.write(readCompressed(setupFile), f);
		bw.write("\n# Model #\r\n");
		bw.write(modelStr);
		bw.write("# Part #\r\n");
		bw.write(partStr);
		
		bw.close();
		fw.close();
	}

	private String readFile(File f) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String str = "";

		String currentLine = "";
		while((currentLine = reader.readLine()) != null)
		{				
			str += currentLine + "\n";
		}
		reader.close();
		return str;
	}

	private NBTTagCompound readCompressed(File f) throws FileNotFoundException, IOException
	{
		return CompressedStreamTools.readCompressed(new FileInputStream(f));
	}
	
}
