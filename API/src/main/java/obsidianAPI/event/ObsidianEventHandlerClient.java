package obsidianAPI.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import obsidianAPI.ObsidianAPIUtil;
import obsidianAPI.network.AnimationNetworkHandler;
import obsidianAPI.network.MessageRequestEntityAnimation;

public class ObsidianEventHandlerClient {

	private static final int ANIMATION_RANGE = 20;
	
	public static void handleOnEntityJoin(EntityJoinWorldEvent e) {
		Entity entity = e.entity;
		if(entity.worldObj.isRemote && ObsidianAPIUtil.isAnimatedEntity(entity) && entity.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) < ANIMATION_RANGE)
			AnimationNetworkHandler.network.sendToServer(new MessageRequestEntityAnimation((EntityLivingBase) e.entity));
	}
	
}
