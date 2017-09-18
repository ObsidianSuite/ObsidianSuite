package com.dabigjoe.obsidianAPI.network;

import com.dabigjoe.obsidianAPI.ObsidianAPIUtil;
import com.dabigjoe.obsidianAPI.properties.EntityAnimationProperties;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
			Entity entity = ctx.getServerHandler().player.world.getEntityByID(message.entityID);
			if(ObsidianAPIUtil.isAnimatedEntity(entity)) {
				EntityAnimationProperties animationProps = EntityAnimationProperties.get(entity);
				if(animationProps != null)
					return new MessageAnimationStart(entity, animationProps.getActiveAnimation(), animationProps.getLoopAnim(), 0.0F);
			}
			return null;
		}

	}

}
