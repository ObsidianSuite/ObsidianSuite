package obsidianAPI.event;

public class AnimationEvent {

	public enum AnimationEventType {
		END;
	}
	
	public final AnimationEventType eventType;
	public final String entityName;
	public final String animationName;
	
	public AnimationEvent(AnimationEventType eventType, String entityName, String animationName) {
		this.eventType = eventType;
		this.entityName = entityName;
		this.animationName = animationName;
	}
	
	@Override
	public String toString() {
		return "AnimationEvent [eventType=" + eventType + ", entityName=" + entityName + ", animationName=" + animationName + "]";
	}
	
}
