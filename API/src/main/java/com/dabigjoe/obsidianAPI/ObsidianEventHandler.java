package com.dabigjoe.obsidianAPI;

import com.dabigjoe.obsidianAPI.network.AnimationNetworkHandler;
import com.dabigjoe.obsidianAPI.network.MessagePlayerLimbSwing;
import com.dabigjoe.obsidianAPI.properties.EntityAnimationProperties;
import com.dabigjoe.obsidianAPI.properties.EntityAnimationPropertiesProvider;
import com.dabigjoe.obsidianAPI.registry.AnimationRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ObsidianEventHandler 
{

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
		Entity entity = e.getEntity();
		if(entity.world.isRemote)
			ObsidianEventHandlerClient.handleOnEntityJoin(e);

		if(AnimationRegistry.isRegisteredClass(entity.getClass())) {
			if(FMLCommonHandler.instance().getEffectiveSide().isClient())
				EntityAnimationPropertiesProvider.register(entity, Side.CLIENT);
			else
				EntityAnimationPropertiesProvider.register(entity, Side.SERVER);
		}
	}

}
