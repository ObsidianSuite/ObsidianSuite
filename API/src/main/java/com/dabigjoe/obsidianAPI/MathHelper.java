package com.dabigjoe.obsidianAPI;

public class MathHelper {
	
    /**
     * Rotate a vertex by a given rotation around a given rotation point.
     */
    public static void rotateVertex(float[] v, float[] rotationMatrix, float[] rotationPoint)
    {
        float[] vector = new float[] {v[0] - rotationPoint[0], v[1] - rotationPoint[1], v[2] - rotationPoint[2]};

        v[0] = vector[0] * rotationMatrix[0] + vector[1] * rotationMatrix[3] + vector[2] * rotationMatrix[6] + rotationPoint[0];
        v[1] = vector[0] * rotationMatrix[1] + vector[1] * rotationMatrix[4] + vector[2] * rotationMatrix[7] + rotationPoint[1];
        v[2] = vector[0] * rotationMatrix[2] + vector[1] * rotationMatrix[5] + vector[2] * rotationMatrix[8] + rotationPoint[2];
    }
    
	public static float[] createRotationMatrixFromAngles(float[] rotation)
	{
		return createRotationMatrixFromAngles(rotation[0], rotation[1], rotation[2]);
	}
    
	public static float[] createRotationMatrixFromAngles(float rotX, float rotY, float rotZ)
	{
		double sx = Math.sin(-rotX);
		double sy = Math.sin(-rotY);
		double sz = Math.sin(-rotZ);
		double cx = Math.cos(-rotX);
		double cy = Math.cos(-rotY);
		double cz = Math.cos(-rotZ);

		float m0 = (float) (cy * cz);
		float m1 = (float) (sx * sy * cz - cx * sz);
		float m2 = (float) (cx * sy * cz + sx * sz);
		float m3 = (float) (cy * sz);
		float m4 = (float) (sx * sy * sz + cx * cz);
		float m5 = (float) (cx * sy * sz - sx * cz);
		float m6 = (float) -sy;
		float m7 = (float) (sx * cy);
		float m8 = (float) (cx * cy);

		return new float[] {m0, m1, m2, m3, m4, m5, m6, m7, m8};
	}

}
