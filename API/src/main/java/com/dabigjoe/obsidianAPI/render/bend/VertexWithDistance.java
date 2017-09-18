package com.dabigjoe.obsidianAPI.render.bend;

import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

/**
 * More complex vertex class, includes a distance component.
 * Used for sorting vertices - closest to point etc.
 */
public class VertexWithDistance implements Comparable<VertexWithDistance>
{
    private Vertex vertex;
    private double distance;

    public VertexWithDistance(Vertex vertex, Float distance)
    {
        this.vertex = vertex;
        this.distance = distance;
    }

    @Override
    public int compareTo(VertexWithDistance v)
    {
        if (v.distance < distance)
        {
            return 1;
        } else if (v.distance > distance)
        {
            return -1;
        }
        return 0;
    }

    public Vertex getVertex()
    {
        return vertex;
    }
}
