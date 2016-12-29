package testing;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import com.nthrootsoftware.mcea.gui.frames.AnimationNewFrame;

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
		
		File folder = f.getParentFile();
				
		String name = f.getName();
		name = name.substring(0, name.indexOf("."));
				
		File modelFile = new File(folder, name + ".obj");
		File partFile = new File(folder, name + ".pxy");
		File setupFile = new File(folder, name + ".data");
		
		try 
		{
			modelFile.createNewFile();
			partFile.createNewFile();
			setupFile.createNewFile();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
	}

}
