package MCEntityAnimator;

import net.minecraftforge.client.MinecraftForgeClient;
import MCEntityAnimator.block.TestRenderer;
import MCEntityAnimator.block.TileEntityTest;
import MCEntityAnimator.render.LongItemRenderer;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.RenderObj;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{	

	public void registerRendering()
	{
		//Entities
        RenderingRegistry.registerEntityRenderingHandler(EntityObj.class, new RenderObj());

        //SpecialItems
        LongItemRenderer longrender = new LongItemRenderer();
        MinecraftForgeClient.registerItemRenderer(MCEA_Main.Halberd, longrender);
        MinecraftForgeClient.registerItemRenderer(MCEA_Main.Spear, longrender);
        
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTest.class, new TestRenderer());

	}
}

