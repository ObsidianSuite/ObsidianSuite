package com.dabigjoe.obsidianAPI.event;

import net.minecraft.entity.Entity;

public class AnimationEvent {

	public enum AnimationEventType {
		START,
		FRAME,
		ACTION,
		END,
		ALL; //All is only for listening, never posting
	}
	
	public final AnimationEventType eventType;
	public final String entityName;
	public final String animationName;
	public final Entity entity;
	public final Integer frame;
	public final String actionName;
	
	/**
	 * Constructor for start, end and all event
	 */
	public AnimationEvent(AnimationEventType eventType, String entityName, String animationName, Entity entity) {
		this.eventType = eventType;
		this.entityName = entityName;
		this.animationName = animationName;
		this.entity = entity;
		this.frame = null;
		this.actionName = "";
	}
	
	/**
	 * Constructor for frame event
	 */
	public AnimationEvent(Integer frame, String entityName, String animationName, Entity entity) {
		this.eventType = AnimationEventType.FRAME;
		this.entityName = entityName;
		this.animationName = animationName;
		this.entity = entity;
		this.frame = frame;
		this.actionName = "";
	}
	
	/**
	 * Constructor for action event
	 */ 
	public AnimationEvent(String actionName, String entityName, String animationName, Entity entity) {
		this.eventType = AnimationEventType.ACTION;
		this.entityName = entityName;
		this.animationName = animationName;
		this.entity = entity;
		this.frame = null;
		this.actionName = actionName;
	}

	@Override
	public String toString() {
		return "AnimationEvent [eventType=" + eventType + ", entityName=" + entityName + ", animationName="
				+ animationName + ", entity=" + entity + ", frame=" + frame + ", actionName=" + actionName + "]";
	}

}
