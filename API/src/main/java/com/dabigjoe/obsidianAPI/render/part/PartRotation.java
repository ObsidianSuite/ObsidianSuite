package com.dabigjoe.obsidianAPI.render.part;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.dabigjoe.obsidianAPI.render.ModelObj;

public class PartRotation extends Part
{

	private FloatBuffer rotationMatrix;
	
	public PartRotation(ModelObj modelObj, String name) 
	{
		super(modelObj, name);
		setupRotationMatrix();
	}
	
	private void setupRotationMatrix()
	{
		float[] rotationMatrixF = new float[16];
		ByteBuffer vbb = ByteBuffer.allocateDirect(rotationMatrixF.length*4);
		vbb.order(ByteOrder.nativeOrder());
		rotationMatrix = vbb.asFloatBuffer();
		rotationMatrix.put(rotationMatrixF);
		rotationMatrix.position(0);
	}

	public void rotate()
	{
		GL11.glRotated(valueX/Math.PI*180F, 1, 0, 0);
		GL11.glRotated(valueY/Math.PI*180F, 0, 1, 0);
		GL11.glRotated(valueZ/Math.PI*180F, 0, 0, 1);
	}

	public void rotateLocal(float delta, int dim)
	{
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		rotate();
		switch(dim)
		{
		case 0: GL11.glRotated(delta,1,0,0); break;
		case 1: GL11.glRotated(delta,0,1,0); break;
		case 2: GL11.glRotated(delta,0,0,1); break;
		}

		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, rotationMatrix);
		updateRotationAnglesFromMatrix();
		GL11.glPopMatrix();
	}

	private void updateRotationAnglesFromMatrix()
	{
		double x,y,z;
		float r8 = rotationMatrix.get(8);
		if(Math.abs(r8) != 1)
		{
			y = -Math.asin(r8);
			double cy = Math.cos(y);

			//Find x value
			float r9 = rotationMatrix.get(9);
			float r10 = rotationMatrix.get(10);
			x = Math.atan2(r9/cy, r10/cy);

			//Find z value
			float r0 = rotationMatrix.get(0);
			float r4 = rotationMatrix.get(4);
			z = Math.atan2(r4/cy, r0/cy);
		}
		else
		{
			//Gimbal lock case - infinite solutions, set z to zero.
			z = 0.0F;
			float r1 = rotationMatrix.get(1);
			float r2 = rotationMatrix.get(2);
			if(r8 == -1)
			{
				y = Math.PI/2;
				x = z + Math.atan2(r1, r2);
			}
			else
			{
				y = -Math.PI/2;
				x = z + Math.atan2(-r1,-r2);
			}
		}

		valueX = (float) -x;
		valueY = (float) -y;
		valueZ = (float) -z;
	}

}
