package MCEntityAnimator;

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
		dataHandler = new DataHandler();
	}

	@Override
	public void saveNBTData(NBTTagCompound nbtTagCompound)
	{
		dataHandler.saveNBTData();
	}

	@Override
	public void loadNBTData(NBTTagCompound nbtTagCompound)
	{
		dataHandler.loadNBTData();
	}
}
