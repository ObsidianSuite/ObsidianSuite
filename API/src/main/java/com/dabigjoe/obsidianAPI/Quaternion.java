package com.dabigjoe.obsidianAPI;

import net.minecraft.util.math.MathHelper;

public class Quaternion {

	public float x, y, z, w;

    public Quaternion() {
    	setIdentity();
    }

    public Quaternion(Quaternion src)
    {
        this(src.x, src.y, src.z, src.w);
    }

    public Quaternion(float x, float y, float z, float w)
    {
        set(x, y, z, w);
    }
    
    public void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
    
    public Quaternion setIdentity() {
    	this.x = 0;
    	this.y = 0;
    	this.z = 0;
    	this.w = 1;
		return this;
	}
    
    public final float length() {
		return (float) Math.sqrt(lengthSquared());
	}
    
    public float lengthSquared() {
		return x * x + y * y + z * z + w * w;
	}

    public Quaternion add(Quaternion q) {
        return new Quaternion(x + q.x,
                              y + q.y,
                              z + q.z,
                              w + q.w);
    }

    public Quaternion sub(Quaternion q) {
        return new Quaternion(x - q.x,
                              y - q.y,
                              z - q.z,
                              w - q.w);
    }
    
    public Quaternion negate() {
		Quaternion dest = new Quaternion();
		dest.x = -this.x;
		dest.y = -this.y;
		dest.z = -this.z;
		dest.w = this.w;
		return dest;
	}

	public Quaternion scale(float scale) {
		Quaternion dest = new Quaternion();
		dest.x = this.x * scale;
		dest.y = this.y * scale;
		dest.z = this.z * scale;
		dest.w = this.w * scale;
		return dest;
	}
    
    public Quaternion normalise() {
		float inv_l = 1f/this.length();
		Quaternion dest = new Quaternion();
		dest.set(this.x * inv_l, this.y * inv_l, this.z * inv_l, this.w * inv_l);
		return dest;
	}
    
    /**
     * Converts this quaternion to XYZ euler angles in radians.
     */
    public float[] toEuler()
    {
        float[] eulerRotation = new float[3];
        double test = x * y + z * w;
        if (test > 0.4999)
        { // singularity at north pole
            eulerRotation[0] = (float) (2 * Math.atan2(x, w));
            eulerRotation[1] = (float) (Math.PI / 2);
            eulerRotation[2] = 0;
            return eulerRotation;
        }
        if (test < -0.4999)
        { // singularity at south pole
            eulerRotation[0] = (float) (-2 * Math.atan2(x, w));
            eulerRotation[1] = (float) (-Math.PI / 2);
            eulerRotation[2] = 0;
            return eulerRotation;
        }
        double sqx = x * x;
        double sqy = y * y;
        double sqz = z * z;
        eulerRotation[0] = (float) Math.atan2(2 * y * w - 2 * x * z, 1 - 2 * sqy - 2 * sqz);
        eulerRotation[1] = (float) Math.asin(2 * test);
        eulerRotation[2] = (float) Math.atan2(2 * x * w - 2 * y * z, 1 - 2 * sqx - 2 * sqz);

        return eulerRotation;
    }

    /**
     * Creates a Quaternion from the given euler angles in radians
     */
    public static Quaternion fromEuler(float x, float y, float z)
    {
        double c1 = Math.cos(x / 2);
        double s1 = Math.sin(x / 2);
        double c2 = Math.cos(y / 2);
        double s2 = Math.sin(y / 2);
        double c3 = Math.cos(z / 2);
        double s3 = Math.sin(z / 2);
        float qw = (float) (c1 * c2 * c3 - s1 * s2 * s3);
        float qx = (float) (c1 * c2 * s3 + s1 * s2 * c3);
        float qy = (float) (s1 * c2 * c3 + c1 * s2 * s3);
        float qz = (float) (c1 * s2 * c3 - s1 * c2 * s3);
        return new Quaternion(qx, qy, qz, qw);
    }
    
    public static float dot(Quaternion left, Quaternion right) {
		return left.x * right.x + left.y * right.y + left.z * right.z + left.w
				* right.w;
	}

    public static Quaternion slerp(Quaternion q0, Quaternion q1, float alpha) {
        q0 = new Quaternion(q0);
        q1 = new Quaternion(q1);

        double dot = Quaternion.dot(q0, q1);
        if (dot < 0)
        {
            q0.x *= -1;
            q0.y *= -1;
            q0.z *= -1;
            q0.w *= -1;
            dot = -dot;
        }

        float DOT_THRESHOLD = 0.9995f;
        if (dot > DOT_THRESHOLD)
            return lerp(q0, q1, alpha);

        dot = MathHelper.clamp(dot, -1, 1);
        double theta = Math.acos(dot) * alpha;

        Quaternion q2 = q1.sub(q0.scale((float) dot)).normalise();

        Quaternion scaledq0 = q0.scale((float) Math.cos(theta));
        return scaledq0.add(q2.scale((float) Math.sin(theta)));
    }

    private static Quaternion lerp(Quaternion q0, Quaternion q1, float alpha) {
        return new Quaternion(q0.x + (q1.x - q0.x) * alpha,
                              q0.y + (q1.y - q0.y) * alpha,
                              q0.z + (q1.z - q0.z) * alpha,
                              q0.w + (q1.w - q0.w) * alpha).normalise();
    }
}
