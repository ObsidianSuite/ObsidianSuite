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
	private float[] rotation;
	private float defaultY;
	
	public BezierCurve(Vertex a1, Vertex a2, Vertex b1, Vertex b2, float[] rotation, float defaultY)
	{
		this.a1 = a1;
		this.a2 = a2;
		this.b1 = b1;
		this.b2 = b2;

		this.rotation = rotation;
		this.defaultY = defaultY;
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
			float x = (1 - t)*(1 - t)*a2.x + 2*(1-t)*t*c.x + t*t*b2.x;
			float y = (1 - t)*(1 - t)*a2.y + 2*(1-t)*t*c.y + t*t*b2.y;
			float z = (1 - t)*(1 - t)*a2.z + 2*(1-t)*t*c.z + t*t*b2.z;
			
			groupObj.render();
			return new Vertex(x,y,z);
		}
		throw new RuntimeException("Cannot get point on bezier curve for t value " + t + ". Outside of valid range (0 to 1).");
	}
	
	/**
	 * Generate the CVertex from a1,a2,b1 and b2.
	 */
	private void setupCVertex()
	{
		Point p;
		if(Math.abs(rotation[0]) > Math.abs(rotation[2]))
		{
			Point p1 = new Point(a1.z, a1.y);
			Point q1 = new Point(a2.z, a2.y);
			Point p2 = new Point(b1.z, b1.y);
			Point q2 = new Point(b2.z, b2.y);
			p = getIntersection(p1, q1, p2, q2);
		}
		else if(Math.abs(rotation[2]) > Math.abs(rotation[0]))
		{
			Point p1 = new Point(a1.x, a1.y);
			Point q1 = new Point(a2.x, a2.y);
			Point p2 = new Point(b1.x, b1.y);
			Point q2 = new Point(b2.x, b2.y);
			p = getIntersection(p1, q1, p2, q2);
		}
		else
		{
			p = new Point(a2.x, (a2.y + b2.y)/2);
		}
		
		if(p.y > a1.y)
		{
			p.y = a1.y;
//			System.out.println("A: " + a1.y);
//
		}
		
//		System.out.println(p.y);

		
		c = new Vertex(a2.x, p.y, a2.z);
		
		groupObj = new BezierGroupObj(new Vertex[]{a2,b2,c});
	}
	
	/**
	 * Return the Point at which line l (line containing p1 and q1)
	 * and line m (line containing p2 and q2 meet).
	 * @return
	 */
	private Point getIntersection(Point p1, Point q1, Point p2, Point q2)
	{
		float l_grad = getGradient(p1, q1);
		float m_grad = getGradient(p2, q2);
		
		float l_int = getIntersect(p1, l_grad);
		float m_int = getIntersect(p2, m_grad);
		
		float x,y;
		
		//System.out.println(m_grad);
		if(Double.isInfinite(l_grad))
			x = p1.x;
		else
			x = (m_int - l_int)/(l_grad - m_grad);
		y = m_grad*x + m_int;

		return new Point(x,y);
	}
		
	/**
	 * Calculate gradient using m=deltay/deltax. 
	 */
	private float getGradient(Point p, Point q)
	{
		return (p.y - q.y)/(p.x - q.x);
	}
	
	/**
	 * Calculate intersect using c=y-mx
	 */
	private float getIntersect(Point p, float gradient)
	{
		return p.y - gradient*p.x;
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
	
	private class Point
	{
		float x,y;
		
		private Point(float x, float y)
		{
			this.x = x;
			this.y = y;
		}
	}


}
