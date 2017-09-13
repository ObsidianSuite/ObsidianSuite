package obsidianAnimations.entity.saiga;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import obsidianAPI.render.ModelAnimated;
import obsidianAPI.render.wavefront.WavefrontObject;

public class ModelSaiga extends ModelAnimated {

	public ModelSaiga(String entityName, WavefrontObject wavefrontObj, ResourceLocation textureLocation) {
		super(entityName, wavefrontObj, textureLocation);
	}
	
	@Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

	@Override
	public void setRotationAngles(float swingTime, float swingMax, float clock, float lookX, float lookY, float f5,
			Entity entity) {
		super.setRotationAngles(swingTime, swingMax, clock, lookX, lookY, f5, entity);
		
//		EntityAnimationPropertiesClient animProps = EntityAnimationPropertiesClient.get(entity);
//		if (animProps == null)
//			GuiDebug.instance.animationText = "null";
//		else
//		{
//			AnimationSequence seq = animProps.getActiveAnimation();
//			if (seq == null)
//				GuiDebug.instance.animationText = "null";
//			else
//				GuiDebug.instance.animationText = seq.getName();
//		}		
	}

}
