package obsidianAnimator;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import obsidianAnimator.block.BlockBase;
import obsidianAnimator.block.BlockGrid;
import obsidianAnimator.data.ModelHandler;

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
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new BlockBase());
        event.getRegistry().register(new BlockGrid());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(ModBlocks.base).setRegistryName(ModBlocks.base.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.grid).setRegistryName(ModBlocks.grid.getRegistryName()));
    }
}



