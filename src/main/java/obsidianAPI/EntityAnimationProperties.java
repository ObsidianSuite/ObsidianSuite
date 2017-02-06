package obsidianAPI;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.registry.AnimationRegistry;

public class EntityAnimationProperties implements IExtendedEntityProperties
{
	
	private AnimationSequence activeAnimation;
	private long animationStartTime;
	
	@Override
	public void init(Entity entity, World world) 
	{
		
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound) 
	{
		
	}
	@Override
	public void loadNBTData(NBTTagCompound compound) 
	{
		
	}
	
	public AnimationSequence getActiveAnimation()
	{
		//return activeAnimation;
		return AnimationRegistry.getAnimation("player", "WalkF");
	}
	
	public long getCurrentFrame()
	{
		return animationStartTime;
	}


}
