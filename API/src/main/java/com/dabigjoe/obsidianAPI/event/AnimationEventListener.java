package com.dabigjoe.obsidianAPI.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dabigjoe.obsidianAPI.event.AnimationEvent.AnimationEventType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnimationEventListener {	
    AnimationEventType type();
	String entityName() default ""; 
    String animationName() default "";    
    int frame() default -1; //-1 means match all frames
    String actionName() default "";
}
