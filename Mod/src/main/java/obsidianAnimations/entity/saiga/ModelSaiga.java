package obsidianAnimations.entity.saiga;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.EntityAnimationProperties;
import obsidianAPI.render.ModelAnimated;

public class ModelSaiga extends ModelAnimated {

	public ModelSaiga(String entityName, WavefrontObject wavefrontObj, ResourceLocation textureLocation) {
		super(entityName, wavefrontObj, textureLocation);
	}

	@Override
	protected void updateAnimation(float swingTime, Entity entity, EntityAnimationProperties animProps) {
		// TODO Auto-generated method stub

	}

}
