package com.dabigjoe.obsidianAPI.render.bend;

import com.dabigjoe.obsidianAPI.render.wavefront.Face;
import com.dabigjoe.obsidianAPI.render.wavefront.GroupObject;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

import net.minecraft.util.math.Vec3d;

/**
 * Used for calculations involving cubic bezier curves.
 * The curve is generated using six points: a1,a2,b1,b2,c1 and c2.
 * Diagram: http://imgur.com/C5JOrRB
 * a1 to a2 and b1 to b2 are straight lines respectively.
 * c1 and c2 are control points. c1 lies on the same line as
 * a1 and a2, c2, on the line containing b1 and b2.
 * The curve is actually drawn between a2 and b2.
 * c1 and c2 can be calculated from a1,a2,b1 and b2.
 * <p>
 * c1 and c2 are always the same distance from a2 (1.2*difference in a2.y and defaulty)
 * <p>
 * This also has a debugging feature, which renders a mesh between a,b2 and c1/c2.
 * Very useful to see where the control points are.
 */

public class BezierCurve
{

    private Vertex a1, a2, b1, b2, c1, c2;
    //Y value of the rotation point.
    private float defaultY;
    //Group obj used for rendering the bezier point (debugging).
    private BezierGroupObj groupObj;

    private boolean inverted;

    public BezierCurve(Vertex a1, Vertex a2, Vertex b1, Vertex b2, float defaultY)
    {
        this.a1 = a1;
        this.a2 = a2;
        this.b1 = b1;
        this.b2 = b2;
        this.defaultY = defaultY;
        this.inverted = a1.y < defaultY;
        setupControlVertices();
    }

    /**
     * Setup c1 and c2.
     */
    private void setupControlVertices()
    {
        //Line l1: v1 = p1 + t*d1.
        Vec3d p1 = new Vec3d(a1.x, a1.y, a1.z);
        Vec3d d1 = getDirectionVector(a1, a2);
        //Line l2: v2 = p2 + u*d2.
        Vec3d p2 = new Vec3d(b1.x, b1.y, b1.z);
        Vec3d d2 = getDirectionVector(b1, b2);

        //S is the scalar value that will produce the vertex (a2.x, defaultY, a2.z).
        //Control points work best if they are slightly further away, so multiply scalar by 1.2.
        double s = (defaultY - a1.y) / d1.y;
        double s1 = s * 1.3F;
        double s2 = s * 1.3F;

        //Point on line1 = p1 + scalar*d1
        //Point on line2 = p2 + scalar*d2
        Vec3d cl1 = p1.addVector(d1.x * s1, d1.y * s1, d1.z * s1);
        Vec3d cl2 = p2.addVector(d2.x * s2, d2.y * s2, d2.z * s2);

        //Convert to vertex.
        c1 = new Vertex((float) cl1.x, (float) cl1.y, (float) cl1.z);
        c2 = new Vertex((float) cl2.x, (float) cl2.y, (float) cl2.z);

        groupObj = new BezierGroupObj();
    }

    /**
     * Return a point on the curve based on the parameter t.
     *
     * @param t - A value between 0 and 1. 0 will give the point a, 1 gives the point b2, and anywhere inbetween gives a point of the curve.
     */
    public Vertex getVertexOnCurve(float t)
    {
        if (t >= 0 && t <= 1)
        {
            //(1-t)^3*a + 3(1-t)^2*t*c1 + 3(1-t)*t^2*c2 + t^3*b2
            float x = cube(1 - t) * a2.x + 3 * square(1 - t) * t * c1.x + 3 * (1 - t) * square(t) * c2.x + cube(t) * b2.x;
            float y = cube(1 - t) * a2.y + 3 * square(1 - t) * t * c1.y + 3 * (1 - t) * square(t) * c2.y + cube(t) * b2.y;
            float z = cube(1 - t) * a2.z + 3 * square(1 - t) * t * c1.z + 3 * (1 - t) * square(t) * c2.z + cube(t) * b2.z;

            return new Vertex(x, y, z);
        }
        throw new RuntimeException("Cannot get point on bezier curve for t value " + t + ". Outside of valid range (0 to 1).");
    }

    private float square(float f)
    {
        return f * f;
    }

    private float cube(float f)
    {
        return f * f * f;
    }


    /**
     * Renders a mesh between a,b2 and c.
     */
    public void render()
    {
//		if(groupObj != null)
//			groupObj.render();
    }

    /**
     * Get the direction vector from p to q.
     */
    private Vec3d getDirectionVector(Vertex p, Vertex q)
    {
        Vec3d pVec = new Vec3d(p.x, p.y, p.z);
        Vec3d qVec = new Vec3d(q.x, q.y, q.z);
        return pVec.subtract(qVec);
    }

    /**
     * The group obj used for debug rendering, just a single empty face.
     */
    private class BezierGroupObj extends GroupObject
    {

        private BezierGroupObj()
        {
            super("", 2);
            Face f = new Face();
            f.vertices = new Vertex[] {a2, b2, c1};
            this.faces.add(f);
            Face g = new Face();
            g.vertices = new Vertex[] {a2, b2, c2};
            this.faces.add(g);
        }
    }
}

