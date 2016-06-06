package MCEntityAnimator;

import MCEntityAnimator.render.objRendering.EntityObj;
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


	public void registerItems()
	{
		GameRegistry.registerItem(MCEA_Main.Battleaxe, MCEA_Main.Battleaxe.getUnlocalizedName());
		GameRegistry.registerItem(MCEA_Main.Bayonet, MCEA_Main.Bayonet.getUnlocalizedName());
		GameRegistry.registerItem(MCEA_Main.Blowgun, MCEA_Main.Blowgun.getUnlocalizedName());
		GameRegistry.registerItem(MCEA_Main.Blunderbuss, MCEA_Main.Blunderbuss.getUnlocalizedName());
		GameRegistry.registerItem(MCEA_Main.Crossbow, MCEA_Main.Crossbow.getUnlocalizedName());
		GameRegistry.registerItem(MCEA_Main.Dagger, MCEA_Main.Dagger.getUnlocalizedName());
		GameRegistry.registerItem(MCEA_Main.Halberd, MCEA_Main.Halberd.getUnlocalizedName());
		GameRegistry.registerItem(MCEA_Main.Katana, MCEA_Main.Katana.getUnlocalizedName());
		GameRegistry.registerItem(MCEA_Main.Musket, MCEA_Main.Musket.getUnlocalizedName());
		GameRegistry.registerItem(MCEA_Main.Spear, MCEA_Main.Spear.getUnlocalizedName());
		
		LanguageRegistry.addName(MCEA_Main.Battleaxe, "Battleaxe");
		LanguageRegistry.addName(MCEA_Main.Bayonet, "Bayonet");
		LanguageRegistry.addName(MCEA_Main.Blowgun, "Blowgun");
		LanguageRegistry.addName(MCEA_Main.Blunderbuss, "Blunderbuss");
		LanguageRegistry.addName(MCEA_Main.Crossbow, "Crossbow");
		LanguageRegistry.addName(MCEA_Main.Dagger, "Dagger");
		LanguageRegistry.addName(MCEA_Main.Halberd, "Halberd");
		LanguageRegistry.addName(MCEA_Main.Katana, "Katana");
		LanguageRegistry.addName(MCEA_Main.Musket, "Musket");
		LanguageRegistry.addName(MCEA_Main.Spear, "Spear");
	}
}



