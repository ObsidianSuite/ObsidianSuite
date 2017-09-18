package com.dabigjoe.obsidianAPI.render.bend;

import com.dabigjoe.obsidianAPI.render.wavefront.Face;
import com.dabigjoe.obsidianAPI.render.wavefront.TextureCoordinate;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

/**
 * Maps a vertices to texturecoordinates.
 */
public class UVMap
{

    //p1 corresponds to tc1 etc.
    private Point p1, p2;
    private TextureCoordinate tc1, tc2;

    //True if the x coords are the ones that change along with y, z if not.
    private boolean xDominant;

    public UVMap(Vertex v1, Vertex v2, TextureCoordinate tc1, TextureCoordinate tc2, boolean xDominant)
    {
        this.tc1 = tc1;
        this.tc2 = tc2;
        this.xDominant = xDominant;

        //Setup points based on xDominant.
        p1 = new Point(xDominant ? v1.x : v1.z, v1.y);
        p2 = new Point(xDominant ? v2.x : v2.z, v2.y);
    }

    public void setupFaceTextureCoordinates(Face f)
    {
        f.textureCoordinates = new TextureCoordinate[3];
        for (int i = 0; i < 3; i++)
        {
            Vertex v = f.vertices[i];
            f.textureCoordinates[i] = getTextureCoordinateForVertex(v);
        }
    }

    /**
     * Get the texture coordinate for this vertex.
     * Calculates the proportional difference between this vertex and
     * v1, then uses that proportion to get the
     * texture u and v relative to tc1.
     */
    private TextureCoordinate getTextureCoordinateForVertex(Vertex v)
    {
        //Take xDominant into account.
        float x = xDominant ? v.x : v.z;
        float y = v.y;

        //Calculate difference between v and control point.
        float dX = x - p1.x;
        float dY = y - p1.y;

        //Convert differences to proportion. Should be between 0 and 1.
        float pX = dX / (p2.x - p1.x);
        float pY = dY / (p2.y - p1.y);

        //Calculate texture coords. Top left coord + proportion*total texture difference.
        float texU = tc1.u + pX * (tc2.u - tc1.u);
        float texV = tc1.v + pY * (tc2.v - tc1.v);

        return new TextureCoordinate(texU, texV);
    }

    private class Point
    {
        private float x, y;

        private Point(float x, float y)
        {
            this.x = x;
            this.y = y;
        }

    }

}

