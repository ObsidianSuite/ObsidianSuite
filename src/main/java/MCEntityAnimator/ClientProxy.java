package MCEntityAnimator;

import MCEntityAnimator.render.LongItemRenderer;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.RenderObj;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.MinecraftForgeClient;

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
	}
}

