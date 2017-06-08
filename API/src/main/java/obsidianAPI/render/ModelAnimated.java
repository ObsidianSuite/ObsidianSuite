package obsidianAPI.render;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;

public abstract class ModelAnimated extends ModelObj
{
	
	private float previousSwingTime = 0.0F;
	
	public ModelAnimated(String entityName, WavefrontObject wavefrontObj, ResourceLocation textureLocation)
	{
		super(entityName, wavefrontObj, textureLocation);
	}

	@Override
	public void setRotationAngles(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, Entity entity)
	{
		super.setRotationAngles(swingTime, swingMax, clock, lookX, lookY, f5, entity);

		EntityAnimationProperties animProps = (EntityAnimationProperties) entity.getExtendedProperties("Animation");
		if (animProps == null)
		{
			doDefaultAnimations(swingTime, swingMax, clock, lookX, lookY, f5, entity);
		}
		else
		{
			animProps.updateFrameTime();
			animProps.updateActiveAnimation(swingTime, swingMax, clock, lookX, lookY, f5, this, entity);
			
			AnimationSequence seq = animProps.getActiveAnimation();

			if(seq != null) {
				float time = animProps.getAnimationFrameTime();
				animateToPartValues(animProps, seq.getPartValuesAtTime(this, time));
				animProps.updateAnimation(this, time);
			}
			else {
				doDefaultAnimations(swingTime, swingMax, clock, lookX, lookY, f5, entity);
			}

			//Translate for vertical y pos
			Part entityPos = getPartFromName("entitypos");
			GL11.glTranslatef(0, -entityPos.getValue(1), 0);
			
			previousSwingTime = swingTime;
		}
	}

	protected abstract void updateAnimation(float swingTime, Entity entity, EntityAnimationProperties animProps);

	protected void doDefaultAnimations(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, Entity entity)
	{
		parts.forEach(Part::setToOriginalValues);
	}
	
	public boolean isMoving(float swingTime) {
		return swingTime - previousSwingTime > 0.05F;
	}

	protected void animateToPartValues(EntityAnimationProperties animProps, Map<String, float[]> partValues)
	{
		parts.forEach(p -> p.setValues(partValues.get(p.getName())));
	}
}
