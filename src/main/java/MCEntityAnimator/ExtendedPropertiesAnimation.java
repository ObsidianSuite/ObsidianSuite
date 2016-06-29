package MCEntityAnimator;

import MCEntityAnimator.distribution.DataHandler;
import MCEntityAnimator.distribution.SaveLoadHandler;
import MCEntityAnimator.distribution.ServerAccess;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPropertiesAnimation implements IExtendedEntityProperties
{
	public final static String extendedPropertiesName = "extendedPropertiesAnimation";
	private DataHandler dataHandler;

	@Override
	public void init(Entity entity, World world)
	{
	}

	@Override
	public void saveNBTData(NBTTagCompound nbtTagCompound)
	{
		if(ServerAccess.username != null && !ServerAccess.username.equals(""))
			SaveLoadHandler.upload();
	}

	@Override
	public void loadNBTData(NBTTagCompound nbtTagCompound)
	{
		//MCEA_Main.dataHandler.loadNBTData();
	}
}
