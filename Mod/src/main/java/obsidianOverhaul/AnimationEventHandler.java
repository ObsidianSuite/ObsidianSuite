package obsidianOverhaul;

import obsidianAPI.event.AnimationEvent;
import obsidianAPI.event.AnimationEvent.AnimationEventType;
import obsidianOverhaul.entity.saiga.EntitySaiga;
import obsidianAPI.event.AnimationEventListener;

public class AnimationEventHandler {
	
	@AnimationEventListener(type = AnimationEventType.END, entityName = "Saiga", animationName = "Call")
	public void onSaigaCallEnd(AnimationEvent event) {
		((EntitySaiga) event.entity).setCalling(false);
	}
	
}
