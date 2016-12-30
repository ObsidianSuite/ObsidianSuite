package com.nthrootsoftware.mcea;

import java.util.List;

import com.nthrootsoftware.mcea.distribution.ModelHandler;
import com.nthrootsoftware.mcea.render.MCEAResourcePack;
import com.nthrootsoftware.mcea.render.objRendering.EntityObj;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.resources.IResourcePack;

public class ClientProxy extends CommonProxy
{	
	
	public void registerRendering()
	{
		//Entities
        RenderingRegistry.registerEntityRenderingHandler(EntityObj.class, ModelHandler.modelRenderer);
        
        List<IResourcePack> resourcePackList = ObfuscationReflectionHelper.getPrivateValue(FMLClientHandler.class, FMLClientHandler.instance(), "resourcePackList");
        //new File(MCEA_Main.modDir, "models/assets/models/skins").mkdirs();
       // IResourcePack pack = new FolderResourcePack(new File(MCEA_Main.modDir, "models"));
        
        resourcePackList.add(new MCEAResourcePack());
	}
}

