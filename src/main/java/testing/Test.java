//package testing;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//import javax.swing.JFileChooser;
//
//import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
//import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
//
//import net.minecraftforge.client.model.obj.WavefrontObject;
//
//public class Test 
//{
//	
//	public static void main(String[] args)
//	{
//		toFiles();
//	}
//	
//	public static void toFiles()
//	{
//		JFileChooser fc = new JFileChooser();
//		fc.setCurrentDirectory(new File("C:\\Users\\Joseph\\Desktop\\Minecraft\\MCEntityAnimator\\Animations\\NewSetup\\entities"));
//		fc.showOpenDialog(null);
//		
//		File f = fc.getSelectedFile();
//		
////		File folder = f.getParentFile();
////				
////		String name = f.getName();
////		name = name.substring(0, name.indexOf("."));
////				
////		File modelFile = new File(folder, name + ".obj");
////		File partFile = new File(folder, name + ".pxy");
////		File setupFile = new File(folder, name + ".data");
//		
//		
//		
//		try 
//		{
//			FileInputStream fis = new FileInputStream(f);
//			ByteInputStream bis = new ByteInputStream();
//			
//			int b;
//			String currentLine = "";
//			while((b = fis.read()) != -1)
//			{
//				if(currentLine.contains("# Part #"))
//					break;
//				if(b == '\n')
//				{
//					for(char c : currentLine.toCharArray())
//						bis.write(c);
//					currentLine = "";
//				}
//				else
//					currentLine += (char) b;
//			}
//			
//			new WavefrontObject("Test file", inputStream)
//			
//		} 
//		catch (FileNotFoundException e) {e.printStackTrace();} 
//		catch (IOException e) {e.printStackTrace();}
//	}
//
//}
