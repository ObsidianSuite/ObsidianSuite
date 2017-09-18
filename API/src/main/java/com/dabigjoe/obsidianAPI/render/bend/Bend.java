package com.dabigjoe.obsidianAPI.render.bend;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.dabigjoe.obsidianAPI.render.part.PartObj;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

import net.minecraft.entity.Entity;


public class Bend
{

    //Rotation point of child is the centre of the bend.
    private Vertex centreOfBend;

    //The bend is drawn between these vertices. They are the set of four vertices that is closest to the centre of the bend.
    //parentNearVertices[0] -> childNearVertices[0] etc. These vertices are moved towards the far vertices in order to make space for the bend.
    private Vertex[] parentNearVertices, childNearVertices;

    //These are the set of four vertices that are furtherest from the centre of the bend. They will be unmoved by used in calculations.
    private Vertex[] parentFarVertices, childFarVertices;

    public final PartObj parent;
    public final PartObj child;

    //The percentage of the parent and child part which remains after the bend is made.
    //So the length of the parts is reduced to 20%
    private static final float sizeReduction = 0.2F;

    //The list of segments of the bend. These are the actual group objects that are rendered.
    protected List<BendPart> bendParts;
    //The number of bend parents the bend is made up of.
    //Min 5, max 40
    protected static final int bendSplit = 20;

    //True if the parent is below the child.
    private boolean inverted;

    private PartUVMap parentUvMap, childUvMap;

    public Bend(PartObj parent, PartObj child)
    {
        this.parent = parent;
        this.child = child;

        centreOfBend = new Vertex(-child.getRotationPoint(0), -child.getRotationPoint(1), -child.getRotationPoint(2));
        bendParts = new ArrayList<BendPart>();
        parentUvMap = new PartUVMap(parent);
        childUvMap = new PartUVMap(child);


        //Get near and far vertices for parent and child.
        Vertex[] allParentVertices = BendHelper.getPartVertices(parent);
        allParentVertices = BendHelper.orderVerticesOnDistance(allParentVertices, centreOfBend);
        Vertex[] allChildVertices = BendHelper.getPartVertices(child);
        allChildVertices = BendHelper.orderVerticesOnDistance(allChildVertices, centreOfBend);
        parentNearVertices = new Vertex[4];
        parentFarVertices = new Vertex[4];
        childNearVertices = new Vertex[4];
        childFarVertices = new Vertex[4];
        for (int i = 0; i < 4; i++)
        {
            parentNearVertices[i] = allParentVertices[i];
            parentFarVertices[i] = allParentVertices[i + 4];
            childNearVertices[i] = allChildVertices[i];
            childFarVertices[i] = allChildVertices[i + 4];
        }

        //Match vertices, starting from parentFar, working down towards childFar.
        parentFarVertices = BendHelper.orderVerticesRelative(parentFarVertices);
        parentNearVertices = BendHelper.alignVertices(parentFarVertices, parentNearVertices);
        childNearVertices = BendHelper.alignVertices(parentNearVertices, childNearVertices);
        childFarVertices = BendHelper.alignVertices(childNearVertices, childFarVertices);

        //Setup inverted variable.
        inverted = childFarVertices[0].y > parentFarVertices[0].y;

        shortenParts();
    }

    /**
     * Reduce the length of the child and parent parts.
     * They 'shrink' away from the centre of the bend, in order to allow the bend to be created.
     * The amount they shorten by is due to the size reduction constant.
     */
    private void shortenParts()
    {
        float dx, dy, dz;
        for (int i = 0; i < 4; i++)
        {
            dx = parentNearVertices[i].x - parentFarVertices[i].x;
            dy = parentNearVertices[i].y - parentFarVertices[i].y;
            dz = parentNearVertices[i].z - parentFarVertices[i].z;
            parentNearVertices[i].x = parentFarVertices[i].x + sizeReduction * dx;
            parentNearVertices[i].y = parentFarVertices[i].y + sizeReduction * dy;
            parentNearVertices[i].z = parentFarVertices[i].z + sizeReduction * dz;

            dx = childNearVertices[i].x - childFarVertices[i].x;
            dy = childNearVertices[i].y - childFarVertices[i].y;
            dz = childNearVertices[i].z - childFarVertices[i].z;
            childNearVertices[i].x = childFarVertices[i].x + sizeReduction * dx;
            childNearVertices[i].y = childFarVertices[i].y + sizeReduction * dy;
            childNearVertices[i].z = childFarVertices[i].z + sizeReduction * dz;
        }

        parentUvMap.adjustPartTextureCoordinates();
        childUvMap.adjustPartTextureCoordinates();

        parent.setDefaultTCsToCurrentTCs();
        child.setDefaultTCsToCurrentTCs();

        init();
    }

    private void init()
    {
        //These are absolute vertex reference taking into rotation into account.
        Vertex[] topFarVertices = new Vertex[parentFarVertices.length];
        Vertex[] topNearVertices = new Vertex[parentNearVertices.length];
        Vertex[] bottomNearVertices = new Vertex[childNearVertices.length];
        Vertex[] bottomFarVertices = new Vertex[childFarVertices.length];

        //Set top far and near vertices to rotation compensated parent far and near vertices.
        for (int i = 0; i < parentFarVertices.length; i++)
        {
            Vertex v = parentFarVertices[i];
            topFarVertices[i] = new Vertex(v.x, v.y, v.z);

            v = parentNearVertices[i];
            topNearVertices[i] = new Vertex(v.x, v.y, v.z);
        }

        //Set top far and near vertices to rotation compensated child far and near vertices.
        float[] rotationMatrix = child.createRotationMatrixFromAngles();
        for (int i = 0; i < childNearVertices.length; i++)
        {
            Vertex v = childNearVertices[i];
            bottomNearVertices[i] = new Vertex(v.x, v.y, v.z);
            BendHelper.rotateVertex(bottomNearVertices[i], rotationMatrix, centreOfBend);

            v = childFarVertices[i];
            bottomFarVertices[i] = new Vertex(v.x, v.y, v.z);
            BendHelper.rotateVertex(bottomFarVertices[i], rotationMatrix, centreOfBend);
        }

        //Generate curves.
        BezierCurve[] curves = generateBezierCurves(topFarVertices, topNearVertices, bottomNearVertices, bottomFarVertices);

        //Top of first part is topNearVertices.
        Vertex[] bendPartTop = topNearVertices;

        //Setup bends
        for (int i = 0; i < bendSplit; i++)
        {

            //Generate part bottom.
            Vertex[] bendPartBottom = generatePartBottom(curves, (float) (i + 1) / bendSplit);

            if (inverted)
                bendParts.add(createBendPart(bendPartBottom, bendPartTop, i < bendSplit / 2 ? parentUvMap : childUvMap, inverted));
            else
                bendParts.add(createBendPart(bendPartTop, bendPartBottom, i < bendSplit / 2 ? parentUvMap : childUvMap, inverted));

            //Top of next part is bottom of this part.
            bendPartTop = bendPartBottom;
        }
    }

    protected BendPart createBendPart(Vertex[] topVertices, Vertex[] bottomVertices, PartUVMap uvMap, boolean inverted)
    {
        return new BendPart(topVertices, bottomVertices, uvMap, inverted);
    }

    public void move()
    {
        //These are absolute vertex reference taking into rotation into account.
        Vertex[] topFarVertices = new Vertex[parentFarVertices.length];
        Vertex[] topNearVertices = new Vertex[parentNearVertices.length];
        Vertex[] bottomNearVertices = new Vertex[childNearVertices.length];
        Vertex[] bottomFarVertices = new Vertex[childFarVertices.length];

        //Set top far and near vertices to rotation compensated parent far and near vertices.
        for (int i = 0; i < parentFarVertices.length; i++)
        {
            Vertex v = parentFarVertices[i];
            topFarVertices[i] = new Vertex(v.x, v.y, v.z);

            v = parentNearVertices[i];
            topNearVertices[i] = new Vertex(v.x, v.y, v.z);
        }

        //Set top far and near vertices to rotation compensated child far and near vertices.
        float[] rotationMatrix = child.createRotationMatrixFromAngles();
        for (int i = 0; i < childNearVertices.length; i++)
        {
            Vertex v = childNearVertices[i];
            bottomNearVertices[i] = new Vertex(v.x, v.y, v.z);
            BendHelper.rotateVertex(bottomNearVertices[i], rotationMatrix, centreOfBend);

            v = childFarVertices[i];
            bottomFarVertices[i] = new Vertex(v.x, v.y, v.z);
            BendHelper.rotateVertex(bottomFarVertices[i], rotationMatrix, centreOfBend);
        }

        //Generate curves.
        BezierCurve[] curves = generateBezierCurves(topFarVertices, topNearVertices, bottomNearVertices, bottomFarVertices);

        //Top of first part is topNearVertices.
        Vertex[] bendPartTop = topNearVertices;

        //Update bends
        for (int i = 0; i < bendSplit; i++)
        {

            //Generate part bottom.
            Vertex[] bendPartBottom = generatePartBottom(curves, (float) (i + 1) / bendSplit);
            //Update bend, swap top and bottom vertices if part is inverted.
            if (inverted)
                bendParts.get(i).updateVertices(bendPartBottom, bendPartTop);
            else
                bendParts.get(i).updateVertices(bendPartTop, bendPartBottom);
            //Top of next part is bottom of this part.
            bendPartTop = bendPartBottom;
        }

        //Get all parents that need compensating for.
        List<PartObj> parents = new ArrayList<PartObj>();
        PartObj p = child;
        while (p.hasParent())
        {
            p = p.getParent();
            parents.add(0, p);
        }

        //Compensate for all parents. TODO remove compensate Part rotation method
        for (PartObj q : parents)
            compensatePartRotation(q);
    }

    public void render(Entity entity)
    {
        GL11.glPushMatrix();

        move();

        //Actually render all the bend parts.
        for (int i = 0; i < bendSplit; i++)
        {
            BendPart part = bendParts.get(i);
            part.updateTextureCoordinates(entity, false, false, parent.modelObj);
            part.render();
        }
        GL11.glPopMatrix();
    }

    /**
     * Adjust a glMatrix for a partObj p.
     */
    private void compensatePartRotation(PartObj p)
    {
        p.move();
    }

    /**
     * Get the four curves based on the vertices.
     */
    private BezierCurve[] generateBezierCurves(Vertex[] topFarVertices, Vertex[] topNearVertices, Vertex[] bottomNearVertices, Vertex[] bottomFarVertices)
    {
        BezierCurve[] curves = new BezierCurve[bottomNearVertices.length];
        for (int i = 0; i < bottomNearVertices.length; i++)
        {
            BezierCurve curve = new BezierCurve(topFarVertices[i], topNearVertices[i], bottomFarVertices[i], bottomNearVertices[i], centreOfBend.y);
            curves[i] = curve;
        }
        return curves;
    }

    /**
     * Generate the bottom set of vertices for a part. 0 <= t <= 1.
     */
    private Vertex[] generatePartBottom(BezierCurve[] curves, float t)
    {
        Vertex[] vertices = new Vertex[curves.length];
        for (int i = 0; i < curves.length; i++)
        {
            vertices[i] = curves[i].getVertexOnCurve(t);
        }
        return vertices;
    }

    /**
     * * TODO implement bend removal.
     */
    public void remove()
    {

    }

    public static boolean canCreateBend(PartObj child, PartObj parent)
    {
        Vertex centreOfBend = new Vertex(-child.getRotationPoint(0), -child.getRotationPoint(1), -child.getRotationPoint(2));

        //Get near and far vertices for parent and child.
        Vertex[] allParentVertices = BendHelper.getPartVertices(parent);
        allParentVertices = BendHelper.orderVerticesOnDistance(allParentVertices, centreOfBend);
        Vertex[] allChildVertices = BendHelper.getPartVertices(child);
        allChildVertices = BendHelper.orderVerticesOnDistance(allChildVertices, centreOfBend);

        Vertex[] parentNearVertices = new Vertex[4];
        Vertex[] childNearVertices = new Vertex[4];
        for (int i = 0; i < 4; i++)
        {
            parentNearVertices[i] = allParentVertices[i];
            childNearVertices[i] = allChildVertices[i];
        }

        //Match vertices, starting from parentFar, working down towards childFar.

        parentNearVertices = BendHelper.orderVerticesRelative(parentNearVertices);
        childNearVertices = BendHelper.alignVertices(parentNearVertices, childNearVertices);

        for (int i = 0; i < parentNearVertices.length; i++)
        {
            if (!BendHelper.areVerticesEqual(parentNearVertices[i], childNearVertices[i]))
                return false;
        }

        return true;
    }


}
