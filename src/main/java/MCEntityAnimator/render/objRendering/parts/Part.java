package MCEntityAnimator.render.objRendering.parts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.render.objRendering.ModelObj;
import net.minecraft.entity.Entity;

/**
 * An abstract object for tracking information about a part (limb, position of model etc).
 */
public class Part
{
	protected float valueX, valueY, valueZ;
	protected float[] originalValues;
	private String name;
	public ModelObj modelObj;
	private FloatBuffer rotationMatrix;


	public Part(ModelObj mObj, String name) 
	{
		modelObj = mObj;
		this.name = name.toLowerCase();
		valueX = 0.0F;
		valueY = 0.0F;
		valueZ = 0.0F;
		originalValues = new float[]{0.0F, 0.0F, 0.0F};
		setupRotationMatrix();
	}

	//------------------------------------------
	//  			Basics
	//------------------------------------------

	public String getName()
	{
		return name;
	}
	
	public String getDisplayName()
	{
		return getName();
	}

	public void setValues(float[] values)
	{
		valueX = values[0];
		valueY = values[1];
		valueZ = values[2];
	}

	public void setValue(int i, float f) 
	{
		switch(i)
		{
		case 0: valueX = f; break;
		case 1: valueY = f; break;
		case 2: valueZ = f; break;
		}
	}

	public float getValue(int i) 
	{
		switch(i)
		{
		case 0: return valueX;
		case 1: return valueY;
		case 2: return valueZ;
		}
		return 0.0F;
	}
	
	public float[] getValues()
	{
		return new float[]{valueX, valueY, valueZ};
	}
	
	public void setOriginalValues(float[] rot) 
	{
		originalValues = rot;
	}

	public float[] getOriginalValues() 
	{
		return originalValues;
	}
	
	public void setToOriginalValues() 
	{
		setValues(originalValues);
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
