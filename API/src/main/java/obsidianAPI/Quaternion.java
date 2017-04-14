package obsidianAPI;

import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.ReadableVector4f;

public class Quaternion extends org.lwjgl.util.vector.Quaternion
{
    public Quaternion()
    {
    }

    public Quaternion(ReadableVector4f src)
    {
        super(src);
    }

    public Quaternion(float x, float y, float z, float w)
    {
        super(x, y, z, w);
    }

    public Quaternion add(Quaternion q)
    {
        return new Quaternion(x + q.x,
                              y + q.y,
                              z + q.z,
                              w + q.w);
    }

    public Quaternion sub(Quaternion q)
    {
        return new Quaternion(x - q.x,
                              y - q.y,
                              z - q.z,
                              w - q.w);
    }

    public Quaternion negate()
    {
        return (Quaternion) negate(new Quaternion());
    }

    public Quaternion scale(float f)
    {
        return (Quaternion) scale(f, this, new Quaternion());
    }

    public Quaternion normalize()
    {
        return (Quaternion) normalise(new Quaternion());
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

    public static Quaternion slerp(Quaternion q0, Quaternion q1, float alpha)
    {
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

        dot = MathHelper.clamp_double(dot, -1, 1);
        double theta = Math.acos(dot) * alpha;

        Quaternion q2 = q1.sub(q0.scale((float) dot)).normalize();

        Quaternion scaledq0 = q0.scale((float) Math.cos(theta));
        return scaledq0.add(q2.scale((float) Math.sin(theta)));
    }

    private static Quaternion lerp(Quaternion q0, Quaternion q1, float alpha)
    {
        return new Quaternion(q0.x + (q1.x - q0.x) * alpha,
                              q0.y + (q1.y - q0.y) * alpha,
                              q0.z + (q1.z - q0.z) * alpha,
                              q0.w + (q1.w - q0.w) * alpha).normalize();
    }
}
