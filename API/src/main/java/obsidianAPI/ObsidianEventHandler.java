package obsidianAPI;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import obsidianAPI.network.AnimationNetworkHandler;
import obsidianAPI.network.MessagePlayerLimbSwing;
import obsidianAPI.registry.AnimationRegistry;

public class ObsidianEventHandler 
{

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing e)
	{
		Entity entity = e.entity;
		if(AnimationRegistry.isRegisteredClass(entity.getClass())) {
			entity.registerExtendedProperties(EntityAnimationProperties.EXT_PROP_NAME, new EntityAnimationProperties());
			if(FMLCommonHandler.instance().getEffectiveSide().isClient())
				entity.registerExtendedProperties(EntityAnimationPropertiesClient.EXT_PROP_NAME, new EntityAnimationPropertiesClient());
		}
	}

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent e) {
		EntityLivingBase entity = e.entityLiving;
//		if(entity instanceof EntityPlayer) {
//			System.out.println(entity.getClass() + " " + entity.limbSwing + " " + entity.limbSwingAmount );
//		}
		if(!entity.worldObj.isRemote) {
			EntityAnimationProperties animationProps = EntityAnimationProperties.get(entity);
			if(animationProps != null) {	
				animationProps.updateActiveAnimation();
				animationProps.runAnimationTick();
			}
		}
		else if(entity instanceof EntityPlayer)
			AnimationNetworkHandler.network.sendToServer(new MessagePlayerLimbSwing(entity));	
	}

}
