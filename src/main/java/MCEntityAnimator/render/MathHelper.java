package MCEntityAnimator.render;

import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.obj.Face;

public class MathHelper 
{

	/**
	 * Calculate intersection between ray and face.
	 * @param p0 - Point on ray.
	 * @param p1 - Another point on the ray.
	 * @param f - Face to check against (3 vertices).
	 * @return The distance from p0 to the face if an intersect exists, null otherwise.
	 */
	public static Double rayIntersectsFace(Vec3 p0, Vec3 p1, Face f)
	{
		Vec3 v0 = Vec3.createVectorHelper(f.vertices[0].x, f.vertices[0].y, f.vertices[0].z);
		Vec3 v1 = Vec3.createVectorHelper(f.vertices[1].x, f.vertices[1].y, f.vertices[1].z);
		Vec3 v2 = Vec3.createVectorHelper(f.vertices[2].x, f.vertices[2].y, f.vertices[2].z);
		Vec3 n = v0.subtract(v1).crossProduct(v0.subtract(v2));
		Vec3 pI = getRayPlaneIntersection(p0,p1,v0,n);
		if(pI == null)
			return null;
		Vec3 u = v0.subtract(v1);
		Vec3 v = v0.subtract(v2);
		Vec3 w = v0.subtract(pI);
		double dn = u.dotProduct(v)*u.dotProduct(v)-u.dotProduct(u)*v.dotProduct(v);
		double s = (u.dotProduct(v)*w.dotProduct(v)-v.dotProduct(v)*w.dotProduct(u))/dn;
		double t = (u.dotProduct(v)*w.dotProduct(u)-u.dotProduct(u)*w.dotProduct(v))/dn;
		if(s>=0 && t>=0 && s+t<=1)
			return p0.distanceTo(pI);
		return null;
	}
	
	/**
	 * Calcualte the point of intersection between a ray and a plane.
	 * @param p0 - A point on the ray.
	 * @param p1 - Another point on the ray.
	 * @param v0 - A point in the plane.
	 * @param n - The normal to the plane.
	 * @return Point of intersection as a Vec3. Null if no intersection. 
	 */
	public static Vec3 getRayPlaneIntersection(Vec3 p0, Vec3 p1, Vec3 v0, Vec3 n)
	{
		double rd = n.dotProduct(p0.subtract(p1));
		if(rd == 0)
			return null;
		double r = n.dotProduct(p0.subtract(v0))/rd;
		return addVector(p0, scale(p0.subtract(p1),r));
	}

	public static Vec3 addVector(Vec3 v, Vec3 w)
	{
		return Vec3.createVectorHelper(v.xCoord + w.xCoord, v.yCoord + w.yCoord, v.zCoord + w.zCoord);
	}
	
	public static Vec3 scale(Vec3 v, double scale)
	{
		return Vec3.createVectorHelper(v.xCoord*scale, v.yCoord*scale, v.zCoord*scale);
	}
	
}
