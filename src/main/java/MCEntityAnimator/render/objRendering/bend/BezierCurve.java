package MCEntityAnimator.render.objRendering.bend;

import net.minecraftforge.client.model.obj.Vertex;

/**
 * Used for calculations involving quadratic bezier curves.
 * The curve is generated using five points: a1,a2,b1,b2 and c.
 * Diagram: http://imgur.com/a12AkGs
 * a1 to a2 and b1 to b2 are straight lines respectively.
 * c is the control point, which resides on the intersection of the
 * lines a1,a2 and b1,b2. 
 * Since c is the intersection, we can calculate it, so only need
 * the points a1,a2,b1 and b2 to form the curve. 
 */

public class BezierCurve 
{
	
	private Point a1, a2, b1, b2, c;
	
	public BezierCurve(Point a1, Point a2, Point b1, Point b2)
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
	public Point getPointOnCurve(float t)
	{
		if(t >= 0 && t <= 1)
		{
			//(1-t)^2 * a2 + 2(1-t)t * c + t^2 * b2 
			float x = (1 - t)*(1 - t)*a2.x + 2*(1-t)*t*c.x + t*t*b2.x;
			float y = (1 - t)*(1 - t)*a2.y + 2*(1-t)*t*c.y + t*t*b2.y;
			return new Point(x,y);
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
		float ca = getIntersect(a1, ma);
		float cb = getIntersect(b1, mb);
		float x = (cb - ca)/(ma - mb);
		float y = ma*x+ca;
		c = new Point(x, y);
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
	private float getIntersect(Point p, float m)
	{
		return p.y - m*p.x;
	}
	
	
	public class Point
	{
		
		public float x,y;
		
		public Point(float x, float y)
		{
			this.x = x;
			this.y = y;
		}
		
	}


}
