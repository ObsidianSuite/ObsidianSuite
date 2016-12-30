package com.nthrootsoftware.mcea;

import java.io.File;

import com.nthrootsoftware.mcea.block.BlockBase;
import com.nthrootsoftware.mcea.block.BlockGrid;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "MCEA")
public class MCEA_Main
{
	
	public static final String homePath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
	public static final String animationPath = homePath + "/animation";
	public static final String version = "4.5";
	
	@Mod.Instance("MCEA")
	public static MCEA_Main instance;

	@SidedProxy(serverSide = "com.nthrootsoftware.mcea.CommonProxy", clientSide = "com.nthrootsoftware.mcea.ClientProxy")
	public static CommonProxy proxy;
    
    public static final Block Base = new BlockBase();
    public static final Block Grid = new BlockGrid();
    		
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{		
		instance = this;		
		proxy.init();
		proxy.registerBlocks();
		proxy.registerItems();
        
        EventHandler eventHandler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
		
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            FMLCommonHandler.instance().bus().register(eventHandler);
        }
        
        Minecraft.getMinecraft().gameSettings.showInventoryAchievementHint = false;
        Minecraft.getMinecraft().gameSettings.saveOptions();
	}
	
	public static File getEntityAnimationFolder(String entityName)
	{
		File folder = new File(animationPath + "/" + entityName);
		folder.mkdirs();
		return folder;
	}

}
