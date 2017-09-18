package com.dabigjoe.obsidianAPI.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client -> Server
 * Contains player limb swing amount, used for animation detection.
 */
public class MessagePlayerLimbSwing implements IMessage {

	private int entityID;
	private float limbSwingAmount;

	public MessagePlayerLimbSwing() {}

	public MessagePlayerLimbSwing(EntityLivingBase entity) {
		this.entityID = entity.getEntityId();
		this.limbSwingAmount = entity.limbSwingAmount;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = buf.readInt();
		limbSwingAmount = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeFloat(limbSwingAmount);
	}

	public static class MessagePlayerLimbSwingHandler implements IMessageHandler<MessagePlayerLimbSwing, IMessage> {

		@Override
		public IMessage onMessage(MessagePlayerLimbSwing message, MessageContext ctx) {
			ctx.getServerHandler().player.limbSwingAmount = message.limbSwingAmount;
			return null;
		}

	}

}
