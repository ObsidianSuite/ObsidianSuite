package obsidianAPI;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import obsidianAPI.registry.AnimationRegistry;

public class ObsidianEventHandler 
{

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing e)
	{
		Entity entity = e.entity;
		if(AnimationRegistry.isRegisteredClass(entity.getClass())) {
			if(FMLCommonHandler.instance().getEffectiveSide().isServer())
				entity.registerExtendedProperties(EntityAnimationProperties.EXT_PROP_NAME, new EntityAnimationProperties());
			else
				entity.registerExtendedProperties(EntityAnimationPropertiesClient.EXT_PROP_NAME, new EntityAnimationPropertiesClient());
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public void onEntityUpdate(LivingUpdateEvent e) {
		EntityLivingBase entity = e.entityLiving;
		EntityAnimationProperties animationProps = EntityAnimationProperties.get(entity);
		if(animationProps != null && animationProps.getEntityName().equals("saiga")) {	
			animationProps.updateActiveAnimation();
			animationProps.runAnimationTick();
		}
	}

}
