package obsidianAPI;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import obsidianAPI.network.AnimationNetworkHandler;
import obsidianAPI.network.MessagePlayerLimbSwing;
import obsidianAPI.properties.EntityAnimationProperties;
import obsidianAPI.properties.EntityAnimationPropertiesClient;
import obsidianAPI.properties.EntityAnimationPropertiesProvider;
import obsidianAPI.registry.AnimationRegistry;

public class ObsidianEventHandler 
{

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing e)
	{
		Entity entity = e.getEntity();
		if(AnimationRegistry.isRegisteredClass(entity.getClass())) {
			EntityAnimationPropertiesProvider.register(entity, Side.SERVER);
			if(FMLCommonHandler.instance().getEffectiveSide().isClient())
				EntityAnimationPropertiesProvider.register(entity, Side.CLIENT);
		}
	}

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent e) {
		EntityLivingBase entity = e.getEntityLiving();
		if(!entity.world.isRemote && ObsidianAPIUtil.isAnimatedEntity(entity)) {
			EntityAnimationProperties animationProps = EntityAnimationProperties.get(entity);
			if(animationProps != null) {	
				animationProps.updateActiveAnimation();
				animationProps.runAnimationTick();
			}
		}
		
		if(entity.world.isRemote && entity instanceof EntityPlayer)
			AnimationNetworkHandler.network.sendToServer(new MessagePlayerLimbSwing(entity));	
	}

	@SubscribeEvent
	public void onEntityJoin(EntityJoinWorldEvent e) {
		if(e.getEntity().world.isRemote)
			ObsidianEventHandlerClient.handleOnEntityJoin(e);
	}

}
