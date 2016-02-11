package MCEntityAnimator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.animation.AnimationStance;

public class DataHandler implements IExtendedEntityProperties
{
	public final EntityPlayer player;

	public final static String EXT_PROP_NAME = "DataHandler";

	private final static File importFolder = new File(System.getProperty("user.dir"), "/Animation/Input");

	public DataHandler(EntityPlayer player)
	{
		this.player = player;
	}

	@Override
	public void init(Entity entity, World world)
	{

	}

	@Override
	public void saveNBTData(NBTTagCompound compound)
	{			
		AnimationData.saveData(compound);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{			
		AnimationData.loadData(compound);

		if(!importFolder.exists())
		{
			importFolder.mkdirs();
		}

		String[] entityFolders = importFolder.list();

		for(String entity : entityFolders)
		{
			File entityFolder = new File(importFolder, entity);
			String[] animationFiles = entityFolder.list();
			for(String animationName : animationFiles)
			{
				File animationFile = new File(entityFolder, animationName);
				try 
				{
					if(animationName.contains("Stance"))
					{
						NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(animationFile));
						AnimationStance stance = new AnimationStance();
						stance.loadData(nbt);
						AnimationData.addNewStance(entity, stance);
					}
					else
					{
						NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(animationFile));
						AnimationSequence sequence = new AnimationSequence("");
						sequence.loadData(entity, nbt);
						AnimationData.addNewSequence(entity, sequence);
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}

		}
	}




}