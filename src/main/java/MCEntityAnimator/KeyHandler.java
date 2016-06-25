package MCEntityAnimator;

import org.lwjgl.input.Keyboard;

import MCEntityAnimator.gui.animation.LoginGUI;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class KeyHandler
{	
	/** Key descriptions; use a language file to localize the description later */
	private static final String[] keyNames = {"Animation Gui"};
	
	/** Default key values */
	private static final int[] keyValues = {Keyboard.KEY_R};
	private final KeyBinding[] keys;
	
	public KeyHandler() 
	{
		keys = new KeyBinding[keyNames.length];
		for (int i = 0; i < keyNames.length; ++i) 
		{
			keys[i] = new KeyBinding(keyNames[i], keyValues[i], "MCEntity Animator");
			ClientRegistry.registerKeyBinding(keys[i]);
		}
	}
	
	/**
	 * KeyInputEvent is in the FML package, so we must register to the FML event bus
	 */
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event)
	{
		// 
		if (FMLClientHandler.instance().getClient().inGameHasFocus) 
		{
			Minecraft mc = Minecraft.getMinecraft();
			if (keys[0].isPressed()) 
			{
				new LoginGUI();
			}
		}
	}
}
