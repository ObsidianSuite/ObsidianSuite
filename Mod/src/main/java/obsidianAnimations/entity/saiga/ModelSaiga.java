package obsidianAnimations.entity.saiga;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.debug.GuiDebug;
import obsidianAPI.render.ModelAnimated;

public class ModelSaiga extends ModelAnimated {

	public ModelSaiga(String entityName, WavefrontObject wavefrontObj, ResourceLocation textureLocation) {
		super(entityName, wavefrontObj, textureLocation);
	}

	@Override
	public void setRotationAngles(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5,
			Entity entity) {
		super.setRotationAngles(swingTime, swingMax, clock, lookX, lookY, f5, entity);
		
		EntityAnimationProperties animProps = (EntityAnimationProperties) entity.getExtendedProperties("Animation");
		if (animProps == null)
			GuiDebug.instance.animationText = "null";
		else
		{
			AnimationSequence seq = animProps.getActiveAnimation();
			if (seq == null)
				GuiDebug.instance.animationText = "null";
			else
				GuiDebug.instance.animationText = seq.getName();
		}		
	}

}
