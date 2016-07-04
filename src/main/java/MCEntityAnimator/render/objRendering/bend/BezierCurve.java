package MCEntityAnimator.render.objRendering.bend;

import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.Vertex;

/**
 * Used for calculations involving quadratic bezier curves.
 * The curve is generated using five points: a1,a2,b1,b2 and c.
 * Diagram: http://imgur.com/a12AkGs
 * a1 to a2 and b1 to b2 are straight lines respectively.
 * c is the control point, which resides on the intersection of the
 * lines a1,a2 and b1,b2. 
 * The curve is actually drawn between a2 and b2.
 * Since c is the intersection, we can calculate it, so only need
 * the points a1,a2,b1 and b2 to form the curve. 
 * 
 * This is the concept in 2D space, however it gets more complex in 3D space 
 * as you aren't guaranteed an intercept.
 * Currently, this is a simplified version that works for 3D space.
 * It makes the assumption that the line a1 to a2 has direction vector (0,1,0)
 *  - ie the part is vertical. This works for the player model, however, 
 *  	more complex models where the parts are not vertically aligned will require more work.
 *  As a1 and a2 are vertically aligned, we currently only need to use the single point a
 *  in our calculations (a=a2).
 *  
 *  This also has a debugging feature, which renders a mesh between a,b2 and c.
 *  Very useful to see where the bezier point is.
 */

public class BezierCurve 
{

	private Vertex a, b1, b2, c;
	//Rotation of the child part
	private float[] rotation;
	//Y value of the rotation point.
	private float defaultY;
	//Group obj used for rendering the bezier point (debugging).
	private BezierGroupObj groupObj;

	public BezierCurve(Vertex a, Vertex b1, Vertex b2, float[] rotation, float defaultY)
	{
		this.a = a;
		this.b1 = b1;
		this.b2 = b2;
		this.rotation = rotation;
		this.defaultY = defaultY;
		setupCVertex();
	}

	/**
	 * Generate the cVertex based on other variables.
	 */
	private void setupCVertex()
	{
		//Bezier x,y and z are the values of c.x,c.y and c.z.
		//Bezier x and z are always the same as a.x and a.z - only because the parts are aligned vertically.
		//Initially, bezierY is set to defaultY and will remain at that value unless 
		//  the x and/or z rotation aren't equal to zero.
		float bezierX = a.x;
		float bezierY = defaultY;
		float bezierZ = a.z;

		//If child part is rotated in x and or z, calculate new bezierY.
		if(rotation[0] != 0.0F || rotation[2] != 0.0F)
		{
			float newBezierY = getBezierY();

			//For extreme rotations, newBezierY will produce weird and wonderful bends.
			//Therefore these checks are used to keep it within bounds:
			//Child below parent.
			if(defaultY < a.y)
			{
				if(newBezierY > a.y)
					newBezierY = defaultY;
				if(newBezierY < a.y + 2*(defaultY - a.y))
					newBezierY = a.y + 2*(defaultY - a.y);
			}
			//Child above parent
			else
			{
				if(newBezierY < a.y)
					newBezierY = defaultY;
				if(newBezierY > a.y + 2*(defaultY - a.y))
					newBezierY = a.y + 2*(defaultY - a.y);
			}

			bezierY = newBezierY;
		}


		//Setup c and group obj.
		c = new Vertex(bezierX, bezierY, bezierZ);
		groupObj = new BezierGroupObj(new Vertex[]{a,b2,c});
	}

	/**
	 * Return a point on the curve based on the parameter t.
	 * @param t - A value between 0 and 1. 0 will give the point a, 1 gives the point b2, and anywhere inbetween gives a point of the curve.
	 */
	public Vertex getVertexOnCurve(float t)
	{
		if(t >= 0 && t <= 1)
		{
			//(1-t)^2 * a + 2(1-t)t * c + t^2 * b2 
			float x = (1 - t)*(1 - t)*a.x + 2*(1-t)*t*c.x + t*t*b2.x;
			float y = (1 - t)*(1 - t)*a.y + 2*(1-t)*t*c.y + t*t*b2.y;
			float z = (1 - t)*(1 - t)*a.z + 2*(1-t)*t*c.z + t*t*b2.z;

			return new Vertex(x,y,z);
		}
		throw new RuntimeException("Cannot get point on bezier curve for t value " + t + ". Outside of valid range (0 to 1).");
	}

	/**
	 * Renders a mesh between a,b2 and c.
	 */
	public void render()
	{
		if(groupObj != null)
			groupObj.render();
	}

	/**
	 * Calculate the bezier y value based on points a,b1 and b2.
	 * Uses two lines - l1: v1 = a + t*d1 (d1 = (0,1,0) - assuming vertical alignment: line of parent)
	 * 				  - l2: v2 = b1 + s*d2 (d2 = direction vector between b2 and b1 : line of child)
	 * The value returned is the y value of the point on l1 that is closest to the point on l2.
	 * If only rotated in x or z, this will be an intercept.
	 * If rotated in both, it will not be.
	 * Formula used is here: https://en.wikipedia.org/wiki/Skew_lines#Nearest_Points
	 */
	private float getBezierY()
	{
		//Line l1: v1 = p1 + t*d1.
		Vec3 p1 = Vec3.createVectorHelper(a.x, a.y, a.z);
		Vec3 d1 = Vec3.createVectorHelper(0,1,0);
		//Line l2: v2 = p2 + s*d2.
		Vec3 p2 = Vec3.createVectorHelper(b1.x, b1.y, b1.z);
		Vec3 d2 = getDirectionVector(b1, b2);

		//n = perpendicular to l1 and l2.
		Vec3 n = d1.crossProduct(d2);

		//n2 = d2 cross n - translations of line 2 along n.
		Vec3 n2 = d2.crossProduct(n);

		//Dif = p2 - p1
		Vec3 dif = p1.subtract(p2);

		//Scalar = dif.n2/d1.n2
		double scalar = dif.dotProduct(n2)/d1.dotProduct(n2);

		//Closest point on line 1 = p1 + scalar*d1
		Vec3 closestLine1 = p1.addVector(d1.xCoord*scalar, d1.yCoord*scalar, d1.zCoord*scalar);

		return (float) closestLine1.yCoord;
	}

	/**
	 * Get the direction vector from p to q.
	 */
	private Vec3 getDirectionVector(Vertex p, Vertex q)
	{
		Vec3 pVec = Vec3.createVectorHelper(p.x, p.y, p.z);
		Vec3 qVec = Vec3.createVectorHelper(q.x, q.y, q.z);
		return pVec.subtract(qVec);
	}

	/**
	 * The group obj used for debug rendering, just a single empty face.
	 */
	private class BezierGroupObj extends GroupObject
	{

		private BezierGroupObj(Vertex[] vertices)
		{
			super("", 2);
			Face f = new Face();
			f.vertices = vertices;
			this.faces.add(f);
		}
	}


}
