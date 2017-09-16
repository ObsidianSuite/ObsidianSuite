package obsidianAnimator;

import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import obsidianAnimator.block.BlockBase;
import obsidianAnimator.block.BlockGrid;

@Mod(modid = ObsidianAnimator.MODID, name = ObsidianAnimator.MODNAME, version = ObsidianAnimator.VERSION)
public class ObsidianAnimator
{
	
    public static final String MODID = "obsidian_animator";
    public static final String MODNAME = "Obsidian Animator";
    public static final String VERSION = "0.4.0-Alpha";
    private static final String VERSION_LINK = "https://raw.githubusercontent.com/ObsidianSuite/ObsidianSuite/master/VERSION.json";
    
	@Mod.Instance
	public static ObsidianAnimator instance;

	@SidedProxy(serverSide = "obsidianAnimator.CommonProxy", clientSide = "obsidianAnimator.ClientProxy")
	public static CommonProxy proxy;
    
    public static final Block Base = new BlockBase();
    public static final Block Grid = new BlockGrid();
    
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{		
		FMLInterModComms.sendRuntimeMessage(MODID, "VersionChecker", "addVersionCheck", VERSION_LINK);
		
		instance = this;		
		proxy.init();
        
        EventHandler eventHandler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
		
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
            FMLCommonHandler.instance().bus().register(eventHandler);
        
        //Minecraft.getMinecraft().gameSettings.showInventoryAchievementHint = false;
        Minecraft.getMinecraft().gameSettings.saveOptions();
        
        //Stop space from activating buttons, allowing it to be used for adding keyframes in the timeline.
        InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
        im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
        im.put(KeyStroke.getKeyStroke("released SPACE"), "none");
	}
}
