package com.dabigjoe.obsidianAnimator;

import com.dabigjoe.obsidianAnimator.data.ModelHandler;
import com.dabigjoe.obsidianAnimator.render.entity.EntityObj;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModEntities {

	public static void init() {
        int id = 1;
        EntityRegistry.registerModEntity(new ResourceLocation(ObsidianAnimator.MODID, "Obj"), EntityObj.class, "Obj", id++, ObsidianAnimator.instance, 64, 3, true, 0x996600, 0x00ff00);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
    	RenderingRegistry.registerEntityRenderingHandler(EntityObj.class, ModelHandler.modelRenderer);
    }
	
}
