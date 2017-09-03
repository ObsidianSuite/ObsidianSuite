package obsidianAnimations;

import obsidianAPI.event.AnimationEvent;
import obsidianAPI.event.AnimationEvent.AnimationEventType;
import obsidianAPI.event.AnimationEventListener;
import obsidianAnimations.entity.saiga.EntitySaiga;

public class AnimationEventHandler {
	
	@AnimationEventListener(type = AnimationEventType.END, entityName = "Saiga", animationName = "Call")
	public void onSaigaCallEnd(AnimationEvent event) {
		((EntitySaiga) event.entity).setCalling(false);
	}
	
	@AnimationEventListener(type = AnimationEventType.START, entityName = "Saiga")
	public void onSaigaAnimationStart(AnimationEvent event) {
		System.out.println("Starting " + event.animationName);
	}
	
	@AnimationEventListener(type = AnimationEventType.FRAME, entityName = "Saiga", animationName = "Call", frame = 10)
	public void onSaigaAnimationFrame(AnimationEvent event) {
		System.out.println(event.animationName + " frame " + event.frame);
	}
	
}
