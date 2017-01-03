package obsidianAnimator.render.objRendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.util.Vec3;
import obsidianAnimator.render.objRendering.parts.PartObj;

public class RayTrace 
{
	
	public final Vec3 p0;
	public final Vec3 p1;

	public RayTrace(Vec3 p0, Vec3 p1)
	{
		this.p0 = p0;
		this.p1 = p1;
	}
	
	public static RayTrace getRayTrace() 
	{
		FloatBuffer model = BufferUtils.createFloatBuffer(16);
		FloatBuffer projection = BufferUtils.createFloatBuffer(16);
		IntBuffer viewport = BufferUtils.createIntBuffer(16);

		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, model);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
		
		FloatBuffer posNearBuffer = BufferUtils.createFloatBuffer(3);
		FloatBuffer posFarBuffer = BufferUtils.createFloatBuffer(3);

		GLU.gluUnProject(Mouse.getX(), Mouse.getY(), 0.0F, model, projection, viewport, posNearBuffer);
		GLU.gluUnProject(Mouse.getX(), Mouse.getY(), 1.0F, model, projection, viewport, posFarBuffer);

		float[] posNear = new float[3];
		float[] posFar = new float[3];
		
		for(int i = 0; i < 3; i++)
		{
			posNear[i] = posNearBuffer.get(i);
			posFar[i] = posFarBuffer.get(i);
		}

		Vec3 v = Vec3.createVectorHelper(posNear[0], posNear[1], posNear[2]);
		Vec3 w = Vec3.createVectorHelper(posFar[0], posFar[1], posFar[2]);
		
		return new RayTrace(v,w);
		
//		entityModel.clearHighlights();
//		PartObj p = entityModel.testRay(new RayTrace(v,w));
//		if(p != null)
//			additionalHighlightPartName = p.getName();
//		else
//			additionalHighlightPartName = null;
	}

	
}
