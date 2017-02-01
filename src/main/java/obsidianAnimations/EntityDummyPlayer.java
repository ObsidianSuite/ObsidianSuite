package obsidianAnimations;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;

public class EntityDummyPlayer extends EntityCreature
{

	public EntityDummyPlayer() 
	{
		super(Minecraft.getMinecraft().theWorld);
	}
	
	public EntityDummyPlayer(World world) 
	{
		super(world);
	}

}
