package obsidianAPI;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.registry.AnimationRegistry;

public class EntityAnimationProperties implements IExtendedEntityProperties
{

	private String entityName;
	private AnimationSequence activeAnimation;
	private long animationStartTime;
	
	@Override
	public void init(Entity entity, World world) 
	{
		entityName = AnimationRegistry.getEntityName(entity.getClass());
		setActiveAnimation("WalkF");
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound) 
	{
		
	}
	@Override
	public void loadNBTData(NBTTagCompound compound) 
	{
		
	}
	
	public void setActiveAnimation(String binding)
	{
		activeAnimation = AnimationRegistry.getAnimation(entityName, binding);
		animationStartTime = System.nanoTime();
	}
	
	public AnimationSequence getActiveAnimation()
	{
		return activeAnimation;
	}
	
	public long getAnimationStartTime()
	{
		return System.nanoTime();
	}


}
