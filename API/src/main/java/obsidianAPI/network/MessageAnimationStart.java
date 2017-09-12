package obsidianAPI.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import obsidianAPI.EntityAnimationPropertiesClient;
import obsidianAPI.render.IRenderAnimated;
import obsidianAPI.render.ModelAnimated;

/**
 * Server -> Client
 * Server tells client that an animation has started.
 */
public class MessageAnimationStart implements IMessage {

	private int entityID;
	private String animationName;
	private boolean loopAnim;
	private float transitionTime;

	public MessageAnimationStart() {}

	public MessageAnimationStart(Entity entity, String animationName, boolean loopAnim, float transitionTime) {
		this.entityID = entity.getEntityId();
		this.animationName = animationName != null ? animationName : "null";
		this.loopAnim = loopAnim;
		this.transitionTime = transitionTime;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = buf.readInt();
		animationName = ByteBufUtils.readUTF8String(buf);
		loopAnim = buf.readBoolean();
		transitionTime = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
		ByteBufUtils.writeUTF8String(buf, animationName);
		buf.writeBoolean(loopAnim);
		buf.writeFloat(transitionTime);
	}

	public static class MessageAnimationStartHandler implements IMessageHandler<MessageAnimationStart, IMessage> {

		@Override
		public IMessage onMessage(MessageAnimationStart message, MessageContext ctx) {
			Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
			if(entity != null) {
				ModelAnimated model = ((IRenderAnimated) RenderManager.instance.getEntityRenderObject(entity)).getModel();
				EntityAnimationPropertiesClient.get(entity).setActiveAnimation(model, message.animationName, System.currentTimeMillis(), message.loopAnim, message.transitionTime);
			}
			return null;
		}

	}

}
