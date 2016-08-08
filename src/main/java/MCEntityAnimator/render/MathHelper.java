package MCEntityAnimator.render;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.render.objRendering.RayTrace;
import MCEntityAnimator.render.objRendering.parts.PartObj;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.obj.Face;

public class MathHelper 
{

	private static final float rotationWheelWidth = 0.1F;
	public static final float rotationWheelRadius = 0.5F;
	
	/**
	 * Calculate intersection between ray and face.
	 * @param p0 - Point on ray.
	 * @param p1 - Another point on the ray.
	 * @param f - Face to check against (3 vertices).
	 * @return The distance from p0 to the face if an intersect exists, null otherwise.
	 */
	public static Double rayIntersectsFace(RayTrace ray, Face f)
	{
		Vec3 v0 = Vec3.createVectorHelper(f.vertices[0].x, f.vertices[0].y, f.vertices[0].z);
		Vec3 v1 = Vec3.createVectorHelper(f.vertices[1].x, f.vertices[1].y, f.vertices[1].z);
		Vec3 v2 = Vec3.createVectorHelper(f.vertices[2].x, f.vertices[2].y, f.vertices[2].z);
		Vec3 n = v0.subtract(v1).crossProduct(v0.subtract(v2));
		Vec3 pI = getRayPlaneIntersection(ray,v0,n);
		if(pI == null)
			return null;
		Vec3 u = v0.subtract(v1);
		Vec3 v = v0.subtract(v2);
		Vec3 w = v0.subtract(pI);
		double dn = u.dotProduct(v)*u.dotProduct(v)-u.dotProduct(u)*v.dotProduct(v);
		double s = (u.dotProduct(v)*w.dotProduct(v)-v.dotProduct(v)*w.dotProduct(u))/dn;
		double t = (u.dotProduct(v)*w.dotProduct(u)-u.dotProduct(u)*w.dotProduct(v))/dn;
		if(s>=0 && t>=0 && s+t<=1)
			return ray.p0.distanceTo(pI);
		return null;
	}
	
	/**
	 * Calculate the distance between the start of the ray and the rotation wheel.
	 * @param ray - Ray to test.
	 * @param p - Centre of rotation wheel.
	 * @param n - Normal to rotation wheel.
	 * @return - Distance or null if not intersection.
	 */
	public static Double rayIntersectsRotationWheel(RayTrace ray, Vec3 p, Vec3 n)
	{
		Vec3 pI = getRayPlaneIntersection(ray,p,n);
		if(pI == null)
			return null;
		double d = pI.distanceTo(p);
		if(d > rotationWheelRadius - rotationWheelWidth && d < rotationWheelRadius + rotationWheelWidth)
			return ray.p0.distanceTo(pI);
		return null;
	}
	
	/**
	 * Calculate the point of intersection between a ray and a plane.
	 * @param ray - Ray to test.
	 * @param v0 - Point on plane.
	 * @param n - Normal to plane.
	 * @return - Point on plane where ray intersects, null if no interception. 
	 */
	public static Vec3 getRayPlaneIntersection(RayTrace ray, Vec3 p, Vec3 n)
	{
		//System.out.println(ray.p1);
		double rd = n.dotProduct(ray.p0.subtract(ray.p1));
		if(rd == 0)
			return null;
		double r = n.dotProduct(ray.p0.subtract(p))/rd;
		return addVector(ray.p0, scale(ray.p0.subtract(ray.p1),r));
	}
	
	public static double getAngleBetweenVectors(Vec3 v, Vec3 w, Vec3 n)
	{
		double angleDot =  Math.acos(v.dotProduct(w)/(v.lengthVector()*w.lengthVector()));
		Vec3 crossProduct = v.crossProduct(w);
		if(n.dotProduct(crossProduct) < 0)
			angleDot *= -1;
		return angleDot;
	}

	public static Vec3 addVector(Vec3 v, Vec3 w)
	{
		return Vec3.createVectorHelper(v.xCoord + w.xCoord, v.yCoord + w.yCoord, v.zCoord + w.zCoord);
	}
	
	public static Vec3 scale(Vec3 v, double scale)
	{
		return Vec3.createVectorHelper(v.xCoord*scale, v.yCoord*scale, v.zCoord*scale);
	}
	
	public static float[] intToRGB(int color)
	{
		float[] rgb = new float[3];
		long blue = color%256;
		color = color/256;
		long green = color%256;
		color = color/256;
		long red = color%256;
				
		rgb[0] = red/255F;
		rgb[1] = green/255F;
		rgb[2] = blue/255F;
		
		return rgb;
	}
	
}
