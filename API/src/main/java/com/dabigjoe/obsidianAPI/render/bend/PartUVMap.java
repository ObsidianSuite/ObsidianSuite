package com.dabigjoe.obsidianAPI.render.bend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dabigjoe.obsidianAPI.render.part.PartObj;
import com.dabigjoe.obsidianAPI.render.wavefront.Face;
import com.dabigjoe.obsidianAPI.render.wavefront.TextureCoordinate;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

public class PartUVMap
{

    //Vertex normal -> UVMap
    private Map<Vertex, UVMap> normalToUV;

    private PartObj part;


    public PartUVMap(PartObj part)
    {
        this.part = part;
        normalToUV = new HashMap<Vertex, UVMap>();

        //Setup pairings between faces.
        //Use pairedFaces to ensure there are no duplicated pairs
        // - ie (f,g) and (g,f).
        //Size of face pairs should be 4.
        List<FacePair> facePairs = new ArrayList<FacePair>();
        List<Face> pairedFaces = new ArrayList<Face>();
        for (Face f : part.groupObj.faces)
        {
            if (isVerticalFace(f) && !pairedFaces.contains(f))
            {
                Face g = getFaceWithSameNormal(f);
                FacePair pair = new FacePair(f, g);
                facePairs.add(pair);
                pairedFaces.add(f);
                pairedFaces.add(g);
            }
        }

        for (FacePair fp : facePairs)
        {
            normalToUV.put(fp.f.faceNormal, getUVMap(fp));
        }
    }

    public void adjustPartTextureCoordinates()
    {
        for (Face f : part.groupObj.faces)
        {
            if (isVerticalFace(f))
            {
                setupFaceTextureCoordinates(f);
            }
        }
    }

    public void setupFaceTextureCoordinates(Face f)
    {
        Vertex closestNormal = getClosestNormal(f.faceNormal);
        UVMap map = null;
        for (Vertex v : normalToUV.keySet())
        {
            if (BendHelper.areVerticesEqual(v, closestNormal))
            {
                map = normalToUV.get(v);
                break;
            }
        }
        if (map != null)
            map.setupFaceTextureCoordinates(f);
        else
            System.out.println("No map for normal: " + BendHelper.getVertexAsString(closestNormal));
    }

    private boolean isVerticalFace(Face f)
    {
        float deltaX = 0.0F;
        float deltaY = 0.0F;
        float deltaZ = 0.0F;

        Vertex v = f.vertices[0];
        for (Vertex w : f.vertices)
        {
            deltaX += Math.abs(w.x - v.x);
            deltaY += Math.abs(w.y - v.y);
            deltaZ += Math.abs(w.z - v.z);
        }

        if (deltaX > deltaZ)
        {
            return deltaY > deltaZ;
        }

        return deltaY > deltaX;
    }


    private UVMap getUVMap(FacePair fp)
    {
        //Create an array of all the vertices and texture coords in the face pair.
        //There will be duplicates but this doesn't matter.
        Vertex[] allVertices = new Vertex[6];
        TextureCoordinate[] allTcs = new TextureCoordinate[6];

        for (int i = 0; i < 3; i++)
        {
            allVertices[i] = fp.f.vertices[i];
            allTcs[i] = fp.f.textureCoordinates[i];
            allVertices[i + 3] = fp.g.vertices[i];
            allTcs[i + 3] = fp.g.textureCoordinates[i];
        }

        //Get minimum vertex (least x/z and y)
        int min = 0;
        Float minX = null;
        Float minY = null;
        for (int i = 0; i < 6; i++)
        {
            Vertex v = allVertices[i];
            float x = fp.xDominant ? v.x : v.z;
            float y = v.y;

            if ((minX == null || x < minX) && (minY == null || y < minY))
            {
                min = i;
                minX = x;
                minY = y;
            }
        }

        int max = 0;
        Vertex[] orderedVertices = BendHelper.orderVerticesOnDistance(allVertices, allVertices[min]);
        Vertex v = orderedVertices[orderedVertices.length - 1];
        for (int i = 0; i < 6; i++)
        {
            Vertex w = allVertices[i];
            if (BendHelper.areVerticesEqual(v, w))
            {
                max = i;
                break;
            }
        }

        return new UVMap(allVertices[min], allVertices[max], allTcs[min], allTcs[max], fp.xDominant);
    }

    private Face getFaceWithSameNormal(Face f)
    {
        Vertex closestNormal = getClosestNormal(f.faceNormal);
        for (Face g : part.groupObj.faces)
        {
            if (!g.faceNormal.equals(closestNormal) && BendHelper.areVerticesEqual(g.faceNormal, closestNormal))
                return g;
        }
        throw new RuntimeException("No face found with same normal: " + f.faceNormal);
    }

    private Vertex getClosestNormal(Vertex normal)
    {
        List<Vertex> normals = new ArrayList<Vertex>();
        for (Face f : part.groupObj.faces)
        {
            normals.add(f.faceNormal);
        }
        Float minDif = null;
        Vertex closestNormal = null;
        for (Vertex n : normals)
        {
            float f = Math.abs(n.x - normal.x) + Math.abs(n.y - normal.y) + Math.abs(n.z - normal.z);
            if (minDif == null || f < minDif)
            {
                closestNormal = n;
                minDif = f;
            }
        }
        return closestNormal;
    }

    private class FacePair
    {
        private Face f, g;
        private boolean xDominant;

        private FacePair(Face f, Face g)
        {
            this.f = f;
            this.g = g;
            this.xDominant = isXDominant();
        }

        private boolean isXDominant()
        {
            float deltaX = 0.0F;
            float deltaZ = 0.0F;
            Vertex v = f.vertices[0];
            for (Vertex w : f.vertices)
            {
                deltaX += Math.abs(w.x - v.x);
                deltaZ += Math.abs(w.z - v.z);
            }
            for (Vertex w : g.vertices)
            {
                deltaX += Math.abs(w.x - v.x);
                deltaZ += Math.abs(w.z - v.z);
            }
            return deltaX > deltaZ;
        }
    }

}

