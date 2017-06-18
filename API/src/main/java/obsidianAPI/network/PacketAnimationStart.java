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
import obsidianAPI.render.ModelAnimated;
import obsidianAPI.render.RenderAnimated;

public class PacketAnimationStart implements IMessage {

	private int entityID;
	private String animationName;
	private long animationStartTime;
	private boolean loopAnim;
	private float transitionTime;

	public PacketAnimationStart() {}

	public PacketAnimationStart(Entity entity, String animationName, long animationStartTime, boolean loopAnim, float transitionTime) {
		this.entityID = entity.getEntityId();
		this.animationName = animationName != null ? animationName : "null";
		this.animationStartTime = animationStartTime;
		this.loopAnim = loopAnim;
		this.transitionTime = transitionTime;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = buf.readInt();
		animationName = ByteBufUtils.readUTF8String(buf);
		animationStartTime = buf.readLong();
		loopAnim = buf.readBoolean();
		transitionTime = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
		ByteBufUtils.writeUTF8String(buf, animationName);
		buf.writeLong(animationStartTime);
		buf.writeBoolean(loopAnim);
		buf.writeFloat(transitionTime);
	}

	public static class PacketAnimationStartHandler implements IMessageHandler<PacketAnimationStart, IMessage> {

		@Override
		public IMessage onMessage(PacketAnimationStart message, MessageContext ctx) {
			Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
			if(entity != null) {
				System.out.println("Animating " + message.animationName + " " + message.animationStartTime + " " + message.loopAnim + " " + message.transitionTime);
				ModelAnimated model = ((RenderAnimated) RenderManager.instance.getEntityRenderObject(entity)).getModel();
				EntityAnimationPropertiesClient.get(entity).setActiveAnimation(model, message.animationName, message.animationStartTime, message.loopAnim, message.transitionTime);
			}
			return null;
		}

	}

}
