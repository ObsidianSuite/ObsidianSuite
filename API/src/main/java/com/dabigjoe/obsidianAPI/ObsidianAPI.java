package com.dabigjoe.obsidianAPI;

import com.dabigjoe.obsidianAPI.event.ObsidianEventBus;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = ObsidianAPI.MODID, name = ObsidianAPI.MODNAME, version = ObsidianAPI.VERSION)
public class ObsidianAPI {
	
    public static final String MODID = "obsidian_api";
    public static final String MODNAME = "Obsidian API";
    public static final String VERSION = "1.0.0";
    public static final ObsidianEventBus EVENT_BUS = new ObsidianEventBus();
	
}
