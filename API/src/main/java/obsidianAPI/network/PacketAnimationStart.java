package obsidianAPI.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketAnimationStart implements IMessage {

	private String message;
	
	public PacketAnimationStart() {}
	
	public PacketAnimationStart(String message) {
		this.message = message;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		message = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, message);
	}
	
	public static class PacketAnimationStartHandler implements IMessageHandler<PacketAnimationStart, IMessage> {

		@Override
		public IMessage onMessage(PacketAnimationStart message, MessageContext ctx) {
			System.out.println(message.message);
			return null;
		}
		
	}

}
