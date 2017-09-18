package com.dabigjoe.obsidianAPI.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.dabigjoe.obsidianAPI.event.AnimationEvent.AnimationEventType;

public class ObsidianEventBus {
	
	private List<Object> handlers = new ArrayList<Object>();

    public void register(Object handler) {
        this.handlers.add(handler);
    }
 
    public void dispatchAnimationEvent(AnimationEvent event) {
        for (Object handler : handlers)
            dispatchEventTo(handler, event);
    }
     
    protected void dispatchEventTo(Object handler, AnimationEvent event) {
        List<Method> methods = findMatchingEventHandlerMethods(handler, event);
        for (Method method : methods) {
            try {
                method.setAccessible(true);
                
                if (method.getParameterTypes().length == 0)
                    method.invoke(handler);
                if (method.getParameterTypes().length == 1)
                    method.invoke(handler, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
 
    private List<Method> findMatchingEventHandlerMethods(Object handler, AnimationEvent event) {
        Method[] methods = handler.getClass().getDeclaredMethods();
        List<Method> result = new ArrayList<Method>();
        for (Method method : methods) {
            if (shouldHandleEvent(method, event))
                result.add(method);
        }
        return result;
    }
     
    private boolean shouldHandleEvent(Method method, AnimationEvent event) {
    	AnimationEventListener annotation = method.getAnnotation(AnimationEventListener.class);
        if (annotation != null) {
        	if(annotation.type() == AnimationEventType.ALL || annotation.type() == event.eventType) {
                if(annotation.entityName().equals("") || annotation.entityName().equalsIgnoreCase(event.entityName)) {
                	if(annotation.animationName().equals("") || annotation.animationName().equalsIgnoreCase(event.animationName)) {
                		switch(annotation.type()) {
						case END:
							return true;
						case FRAME:
							return annotation.frame() == -1 || event.frame == annotation.frame();
						case START:
							return true;
						case ACTION:
							return annotation.actionName().equals("") || annotation.actionName().equalsIgnoreCase(event.actionName);
						case ALL:
							return true;
						default:
							return false;
                		}
                	}
                }
        	}        	
        }
        return false;
    }
}
