package com.nthrootsoftware.mcea;

import com.nthrootsoftware.mcea.render.objRendering.EntityObj;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy
{	
	public void init() 
	{	
		EntityRegistry.registerGlobalEntityID(EntityObj.class, "Obj", EntityRegistry.findGlobalUniqueEntityId(), 0, 0);
		LanguageRegistry.instance().addStringLocalization("entity.Obj.name", "Obj");
		registerRendering();
	}

	public void registerRendering() {}


	public void registerBlocks()
	{
		GameRegistry.registerBlock(MCEA_Main.Base, "Base");
		GameRegistry.registerBlock(MCEA_Main.Grid, "Grid");
	}
	
	public void registerItems(){}
}



