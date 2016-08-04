package MCEntityAnimator;

import org.lwjgl.input.Keyboard;

import MCEntityAnimator.gui.GuiBlack;
import MCEntityAnimator.gui.GuiEntityRenderer;
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
	private static final String[] keyNames = {"Animation Gui","Test GUI"};
	
	/** Default key values */
	private static final int[] keyValues = {Keyboard.KEY_R,Keyboard.KEY_T};
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
		if (FMLClientHandler.instance().getClient().inGameHasFocus) 
		{
			if (keys[0].isPressed()) 
			{
				Minecraft.getMinecraft().displayGuiScreen(new GuiBlack());
				new LoginGUI();
			}
		}
		if (keys[1].isPressed()) 
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiEntityRenderer("player"));
		}
	}
}
