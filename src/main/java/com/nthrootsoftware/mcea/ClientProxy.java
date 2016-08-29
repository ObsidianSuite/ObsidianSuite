package com.nthrootsoftware.mcea;

import java.util.List;

import com.nthrootsoftware.mcea.render.LongItemRenderer;
import com.nthrootsoftware.mcea.render.MCEAResourcePack;
import com.nthrootsoftware.mcea.render.objRendering.EntityObj;
import com.nthrootsoftware.mcea.render.objRendering.RenderObj;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy
{	
	
	public static final RenderObj renderObj = new RenderObj();

	public void registerRendering()
	{
		//Entities
        RenderingRegistry.registerEntityRenderingHandler(EntityObj.class, renderObj);

        //SpecialItems
        LongItemRenderer longrender = new LongItemRenderer();
        MinecraftForgeClient.registerItemRenderer(MCEA_Main.Halberd, longrender);
        MinecraftForgeClient.registerItemRenderer(MCEA_Main.Spear, longrender);
        
        List<IResourcePack> resourcePackList = ObfuscationReflectionHelper.getPrivateValue(FMLClientHandler.class, FMLClientHandler.instance(), "resourcePackList");
        //new File(MCEA_Main.modDir, "models/assets/models/skins").mkdirs();
       // IResourcePack pack = new FolderResourcePack(new File(MCEA_Main.modDir, "models"));
        
        resourcePackList.add(new MCEAResourcePack());
	}
}

