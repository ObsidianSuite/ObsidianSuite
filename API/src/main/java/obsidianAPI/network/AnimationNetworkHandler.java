package obsidianAPI.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import obsidianAPI.network.MessageAnimationStart.MessageAnimationStartHandler;
import obsidianAPI.network.MessagePlayerLimbSwing.MessagePlayerLimbSwingHandler;
import obsidianAPI.network.MessageRequestEntityAnimation.MessageRequestEntityAnimationHandler;

public class AnimationNetworkHandler {

	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("ObsidianAnimations");
	
	public static void init() {
		registerMessages();
	}
	
	private static void registerMessages() {
		network.registerMessage(MessageAnimationStartHandler.class, MessageAnimationStart.class, 0, Side.CLIENT);
		network.registerMessage(MessagePlayerLimbSwingHandler.class, MessagePlayerLimbSwing.class, 1, Side.SERVER);
		network.registerMessage(MessageRequestEntityAnimationHandler.class, MessageRequestEntityAnimation.class, 2, Side.SERVER);
	}
	
}
