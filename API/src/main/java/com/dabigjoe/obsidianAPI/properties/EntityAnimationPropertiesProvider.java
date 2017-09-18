package com.dabigjoe.obsidianAPI.properties;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;

public class EntityAnimationPropertiesProvider {

	private static Map<Entity, EntityAnimationProperties> serverStore = new HashMap<Entity, EntityAnimationProperties>();
	private static Map<Entity, EntityAnimationPropertiesClient> clientStore = new HashMap<Entity, EntityAnimationPropertiesClient>();

	public static void register(Entity entity, Side side) {
		if(side == Side.CLIENT) {
			EntityAnimationPropertiesClient animProps = new EntityAnimationPropertiesClient();
			animProps.init(entity);
			clientStore.put(entity, animProps);
		}
		else {
			EntityAnimationProperties animProps = new EntityAnimationProperties();
			animProps.init(entity);
			serverStore.put(entity, animProps);
		}
	}
	
	public static IAnimationProperties get(Entity entity, Side side) {
		return side == Side.CLIENT ? clientStore.get(entity) : serverStore.get(entity);
	}
	
	
}
