package com.dabigjoe.obsidianOverhaul;

import com.dabigjoe.obsidianAPI.event.AnimationEvent;
import com.dabigjoe.obsidianAPI.event.AnimationEventListener;
import com.dabigjoe.obsidianAPI.event.AnimationEvent.AnimationEventType;
import com.dabigjoe.obsidianOverhaul.entity.saiga.EntitySaiga;

public class AnimationEventHandler {
	
	@AnimationEventListener(type = AnimationEventType.END, entityName = "Saiga", animationName = "Call")
	public void onSaigaCallEnd(AnimationEvent event) {
		((EntitySaiga) event.entity).setCalling(false);
	}
	
}
