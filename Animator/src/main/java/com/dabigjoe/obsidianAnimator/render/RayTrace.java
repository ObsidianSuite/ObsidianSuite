package com.dabigjoe.obsidianAnimator.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.util.math.Vec3d;

public class RayTrace 
{
	
	public final Vec3d p0;
	public final Vec3d p1;

	public RayTrace(Vec3d p0, Vec3d p1)
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

		Vec3d v = new Vec3d(posNear[0], posNear[1], posNear[2]);
		Vec3d w = new Vec3d(posFar[0], posFar[1], posFar[2]);
		
		return new RayTrace(v,w);
		
//		entityModel.clearHighlights();
//		PartObj p = entityModel.testRay(new RayTrace(v,w));
//		if(p != null)
//			hoveredPart = p.getName();
//		else
//			hoveredPart = null;
	}

	
}
