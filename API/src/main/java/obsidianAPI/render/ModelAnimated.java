package obsidianAPI.render;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.EntityAnimationPropertiesClient;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;

public abstract class ModelAnimated extends ModelObj
{
		
	public ModelAnimated(String entityName, WavefrontObject wavefrontObj, ResourceLocation textureLocation)
	{
		super(entityName, wavefrontObj, textureLocation);
	}

	@Override
	public void setRotationAngles(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, Entity entity)
	{
		super.setRotationAngles(swingTime, swingMax, clock, lookX, lookY, f5, entity);

		EntityAnimationPropertiesClient animProps = EntityAnimationPropertiesClient.get(entity);
		if (animProps == null)
		{
			doDefaultAnimations(swingTime, swingMax, clock, lookX, lookY, f5, entity);
		}
		else
		{
			animProps.updateFrameTime();
			
			AnimationSequence seq = animProps.getActiveAnimation();

			if(seq != null) {
				float time = animProps.getAnimationFrameTime();
				animateToPartValues(seq.getPartValuesAtTime(this, time));
				//animProps.tickAnimation(this, time);
			}
			else {
				doDefaultAnimations(swingTime, swingMax, clock, lookX, lookY, f5, entity);
			}

			//Translate for vertical y pos
			Part entityPos = getPartFromName("entitypos");
			GL11.glTranslatef(0, -entityPos.getValue(1), 0);
		}
	}

	protected void doDefaultAnimations(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5, Entity entity)
	{
		parts.forEach(Part::setToOriginalValues);
	}

	protected void animateToPartValues(Map<String, float[]> partValues)
	{
		parts.forEach(p -> p.setValues(partValues.get(p.getName())));
	}
}
