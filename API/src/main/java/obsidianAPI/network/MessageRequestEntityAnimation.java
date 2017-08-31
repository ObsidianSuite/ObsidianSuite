package obsidianAPI.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.ObsidianAPIUtil;
import obsidianAPI.animation.wrapper.IEntityAnimated;

/**
 * Client -> Server
 * Contains player limb swing amount, used for animation detection.
 */
public class MessageRequestEntityAnimation implements IMessage {

	private int entityID;

	public MessageRequestEntityAnimation() {}

	public MessageRequestEntityAnimation(EntityLivingBase entity) {
		this.entityID = entity.getEntityId();
		System.out.println("Creating animation request");
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
	}

	public static class MessageRequestEntityAnimationHandler implements IMessageHandler<MessageRequestEntityAnimation, IMessage> {

		@Override
		public IMessage onMessage(MessageRequestEntityAnimation message, MessageContext ctx) {
			Entity entity = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.entityID);
			if(ObsidianAPIUtil.isAnimatedEntity(entity)) {
				EntityAnimationProperties animationProps = EntityAnimationProperties.get(entity);
				if(animationProps != null)
					return new MessageAnimationStart(entity, animationProps.getActiveAnimation(), animationProps.getAnimationStartTime(), animationProps.getLoopAnim(), 0.0F);
			}
			return null;
		}

	}

}
