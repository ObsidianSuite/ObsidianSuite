package obsidianAPI.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    	AnimationEventListener animationEventAnnotation = method.getAnnotation(AnimationEventListener.class);
        if (animationEventAnnotation != null) {
        	if(animationEventAnnotation.type() == event.eventType) {
                if(animationEventAnnotation.entityName().equals("") || animationEventAnnotation.entityName().equalsIgnoreCase(event.entityName)) {
                	if(animationEventAnnotation.animationName().equals("") || animationEventAnnotation.animationName().equalsIgnoreCase(event.animationName))
                		return true;
                }
        	}        	
        }
        return false;
    }
}
