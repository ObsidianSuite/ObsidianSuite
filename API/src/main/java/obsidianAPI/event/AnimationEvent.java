package obsidianAPI.event;

import net.minecraft.entity.Entity;

public class AnimationEvent {

	public enum AnimationEventType {
		START,
		END;
	}
	
	public final AnimationEventType eventType;
	public final String entityName;
	public final String animationName;
	public final Entity entity;
	
	public AnimationEvent(AnimationEventType eventType, String entityName, String animationName, Entity entity) {
		this.eventType = eventType;
		this.entityName = entityName;
		this.animationName = animationName;
		this.entity = entity;
	}

	@Override
	public String toString() {
		return "AnimationEvent [eventType=" + eventType + ", entityName=" + entityName + ", animationName="
				+ animationName + ", entity=" + entity + "]";
	}
	
}
