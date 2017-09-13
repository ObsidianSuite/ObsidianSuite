package obsidianAPI;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import obsidianAPI.network.AnimationNetworkHandler;
import obsidianAPI.network.MessageRequestEntityAnimation;

public class ObsidianEventHandlerClient {

	private static final int ANIMATION_RANGE = 20;
	
	public static void handleOnEntityJoin(EntityJoinWorldEvent e) {
		Entity entity = e.getEntity();
		if(entity.world.isRemote && ObsidianAPIUtil.isAnimatedEntity(entity) && entity.getDistanceToEntity(Minecraft.getMinecraft().player) < ANIMATION_RANGE)
			AnimationNetworkHandler.network.sendToServer(new MessageRequestEntityAnimation((EntityLivingBase) e.getEntity()));
	}
	
}
