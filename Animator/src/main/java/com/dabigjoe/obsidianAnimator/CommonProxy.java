package com.dabigjoe.obsidianAnimator;

import com.dabigjoe.obsidianAnimator.data.ModelHandler;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonProxy
{	
	public void init() 
	{	
		ModEntities.init();
		registerRendering();
		registerModels();
	}

	public void registerModels() 
	{
		ModelHandler.loadModelFromResource("ObsidianPlayer");
		ModelHandler.loadModelFromResource("ObsidianCow");
	}

	public void registerRendering() {}


	@SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {}

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {}
    
}



