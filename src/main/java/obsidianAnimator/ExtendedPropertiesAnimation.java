package obsidianAnimator;

import javax.activation.DataHandler;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import obsidianAnimator.data.Persistence;

public class ExtendedPropertiesAnimation implements IExtendedEntityProperties
{

	@Override
	public void init(Entity entity, World world){}

	@Override
	public void saveNBTData(NBTTagCompound nbtTagCompound)
	{
		Persistence.save();
	}

	@Override
	public void loadNBTData(NBTTagCompound nbtTagCompound)
	{
		Persistence.load();
	}
}
