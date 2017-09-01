package obsidianAnimations;

import obsidianAPI.event.AnimationEvent;
import obsidianAPI.event.AnimationEvent.AnimationEventType;
import obsidianAPI.event.AnimationEventListener;

public class AnimationEventHandler {
	
	@AnimationEventListener(type = AnimationEventType.END, entityName = "Saiga", animationName = "Walk")
	public void onSaigaCallEnd(AnimationEvent event) {

	}
	
}
