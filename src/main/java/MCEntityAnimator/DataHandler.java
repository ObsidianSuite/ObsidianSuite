package MCEntityAnimator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.swing.JOptionPane;

import com.google.common.io.Files;

import MCEntityAnimator.animation.AnimationData;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class DataHandler
{


	private final static File resourceFolder = MCEA_Main.resourceFolder;
	private final static File saveFile = new File(resourceFolder, "AnimationData.data");

	public void saveNBTData()
	{			
		NBTTagCompound compoundToSave = new NBTTagCompound();
		AnimationData.saveData(compoundToSave);
		try 
		{
			CompressedStreamTools.writeCompressed(compoundToSave, new FileOutputStream(saveFile));
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void loadNBTData()
	{	
		try
		{
			//Create file if it doesn't exist, or create backup if it does.
			if(!saveFile.exists())
			{
				if(!saveFile.exists())
					resourceFolder.mkdirs();
				saveFile.createNewFile();
			}
			else
			{

				//File backup = new File(resourceFolder, "AnimationDataBackup.data");

				//			if(!backup.exists())
				//				backup.createNewFile();

				//			Runtime.getRuntime().exec("attrib +H Animation/AnimationDataBackup.data");
				//			Files.copy(saveFile, backup);

				try 
				{
					AnimationData.loadData(CompressedStreamTools.readCompressed(new FileInputStream(saveFile)));
				} 
				catch (Exception e) 
				{
					//Files.copy(backup, saveFile);
					//JOptionPane.showMessageDialog(null, "Error when loading data. Restored from backup. Restart game to reload.", "Loading error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				} 
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}

}