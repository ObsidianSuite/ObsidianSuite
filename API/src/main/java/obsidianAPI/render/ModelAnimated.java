package obsidianAPI.render;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAPI.render.part.Part;

public abstract class ModelAnimated extends ModelObj
{
	public ModelAnimated(String entityName, WavefrontObject wavefrontObj, ResourceLocation textureLocation)
	{
		super(entityName, wavefrontObj, textureLocation);
	}

	@Override
	public void setRotationAngles(float swingTime, float swingMax, float f2, float lookX, float lookY, float f5, Entity entity)
	{
		super.setRotationAngles(swingTime, swingMax, f2, lookX, lookY, f5, entity);

		EntityAnimationProperties animProps = (EntityAnimationProperties) entity.getExtendedProperties("Animation");
		if (animProps == null)
		{
			doDefaultAnimations(swingTime, swingMax, f2, lookX, lookY, f5, entity);
		}
		else
		{
			animProps.updateFrameTime();

			updateAnimation(swingTime, entity, animProps);
			AnimationSequence seq = animProps.getActiveAnimation();
			//			if (seq == null && AnimationRegistry.getAnimation(entityName, "Idle") != null)
			//			{
			//				animProps.setActiveAnimation(this,"Idle",true);
			//				seq = animProps.getActiveAnimation();
			//			}

			if(seq != null) {
				float time = animProps.getAnimationFrameTime();
				animateToPartValues(animProps, seq.getPartValuesAtTime(this, time));
				animProps.updateAnimation(this, time);
			}
			else {
				doDefaultAnimations(swingTime, swingMax, f2, lookX, lookY, f5, entity);
			}

			//Translate for vertical y pos
			Part entityPos = getPartFromName("entitypos");
			GL11.glTranslatef(0, -entityPos.getValue(1), 0);
		}
	}

	protected abstract void updateAnimation(float swingTime, Entity entity, EntityAnimationProperties animProps);

	protected void doDefaultAnimations(float swingTime, float swingMax, float f2, float lookX, float lookY, float f5, Entity entity)
	{
		parts.forEach(Part::setToOriginalValues);
	}

	protected boolean isIdle(EntityAnimationProperties animProps)
	{
		return animProps.getActiveAnimation() == null || animProps.getActiveAnimation().getName().equals("Idle") 
				|| animProps.getActiveAnimation().getName().equals("transition_idle");
	}

	protected boolean isAnimationActive(EntityAnimationProperties animProps, String animationName)
	{		
		if(animProps.getActiveAnimation() == null)
			return false;
		return animProps.getActiveAnimation().getName().equals(animationName) || animProps.getActiveAnimation().getName().equals("transition_" + animationName);
	}

	protected void animateToPartValues(EntityAnimationProperties animProps, Map<String, float[]> partValues)
	{
		parts.forEach(p -> p.setValues(partValues.get(p.getName())));
	}
}
