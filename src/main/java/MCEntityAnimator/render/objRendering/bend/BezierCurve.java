package MCEntityAnimator.render.objRendering.bend;

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
 */

public class BezierCurve 
{
	
	private Vertex a1, a2, b1, b2, c;
	private BezierGroupObj groupObj;
	
	public BezierCurve(Vertex a1, Vertex a2, Vertex b1, Vertex b2)
	{
		this.a1 = a1;
		this.a2 = a2;
		this.b1 = b1;
		this.b2 = b2;
		setupCVertex();
	}
	
	/**
	 * Return a point on the curve based on the parameter t.
	 * @param t - A value between 0 and 1. 0 will give the point a2, 1 gives the point b2, and anywhere inbetween gives a point of the curve.
	 */
	public Vertex getVertexOnCurve(float t)
	{
		if(t >= 0 && t <= 1)
		{
			//TODO initially just working with x rotation (so use y and z from vertex)
			//(1-t)^2 * a2 + 2(1-t)t * c + t^2 * b2 
			float x = 0.0F;
			float y = (1 - t)*(1 - t)*a2.y + 2*(1-t)*t*c.y + t*t*b2.y;
			float z = (1 - t)*(1 - t)*a2.z + 2*(1-t)*t*c.z + t*t*b2.z;
			//System.out.println(t);
			//groupObj.render();
			return new Vertex(a2.x,y,z);
		}
		throw new RuntimeException("Cannot get point on bezier curve for t value " + t + ". Outside of valid range (0 to 1).");
	}
	
	/**
	 * Generate the CVertex from a1,a2,b1 and b2.
	 */
	private void setupCVertex()
	{
		float ma = getGradient(a1, a2);
		float mb = getGradient(b1, b2);
				
		//System.out.println(ma);
		
		float ca = getIntersect(a1, ma);
		float cb = getIntersect(b1, mb);
		float z = (cb - ca)/(ma - mb);
		float y = ma*z+ca;
						
		if(Double.isNaN(y))
			y = (a2.y + b2.y)/2.0F;		
		if(Double.isNaN(z))
			z = (a2.z + b2.z)/2.0F;
		
		c = new Vertex(a2.x, y, z);
		
		groupObj = new BezierGroupObj(new Vertex[]{a2,b2,c});
	}
	
	//TODO initially just working with x rotation (so use y and z from vertex)
	/**
	 * Calculate gradient using m=deltay/deltax. 
	 */
	private float getGradient(Vertex p, Vertex q)
	{
		return (p.y - q.y)/(p.z - q.z);
	}
	
	//TODO initially just working with x rotation (so use y and z from vertex)
	/**
	 * Calculate intersect using c=y-mx
	 */
	private float getIntersect(Vertex p, float m)
	{
		return p.y - m*p.z;
	}
	
	
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
