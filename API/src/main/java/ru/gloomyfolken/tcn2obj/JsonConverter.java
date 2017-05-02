package ru.gloomyfolken.tcn2obj;

import java.util.ArrayList;
import java.util.HashSet;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Sets;

import ru.gloomyfolken.tcn2obj.json.JsonModel;
import ru.gloomyfolken.tcn2obj.json.components.Box;
import ru.gloomyfolken.tcn2obj.json.components.Faces;
import ru.gloomyfolken.tcn2obj.json.components.Faces.FaceComponent;
import ru.gloomyfolken.tcn2obj.json.components.Rotation;
import ru.gloomyfolken.tcn2obj.obj.Face;
import ru.gloomyfolken.tcn2obj.obj.ObjModel;
import ru.gloomyfolken.tcn2obj.obj.Shape;
import ru.gloomyfolken.tcn2obj.obj.TextureCoords;
import ru.gloomyfolken.tcn2obj.obj.Vertex;

public class JsonConverter
{
    public JsonModel model;

    int              same = 0;

    public ObjModel tcn2obj(JsonModel model, float scale)
    {
        ObjModel obj = new ObjModel();
        this.model = model;
        ArrayList<Box> boxes = model.model.getElements();
        if (!Main.jsonTexture) preProcess();
        for (Box box : boxes)
        {
            Shape shape = convertBoxToShape(obj, box, scale);
            if (shape != null) obj.shapes.add(shape);
        }
        return obj;
    }

    private void preProcess()
    {
        ArrayList<Box> boxes = model.model.getElements();
        HashSet<Box> subBoxes = Sets.newHashSet();
        for (Box box : boxes)
        {
            if (subBoxes.contains(box)) continue;
            Rotation rotation = box.getRotation();
            for (Box box1 : boxes)
            {
                if (box == box1 || subBoxes.contains(box1)) continue;
                Rotation rotation1 = box.getRotation();

                boolean sameRot = rotation == null && rotation1 == null;
                if (!sameRot) sameRot = rotation != null && rotation.equals(rotation1);
                if (sameRot && isSubBox(box, box1))
                {
                    subBoxes.add(box1);
                }
            }
        }
        boxes.removeAll(subBoxes);
    }

    private boolean isSubBox(Box box, Box toCheck)
    {
        float[] from = box.getFrom();
        float[] to = box.getTo();
        float[] from1 = toCheck.getFrom();
        float[] to1 = toCheck.getTo();

        boolean sameFromZX = from[0] == from1[0] && from[2] == from1[2];
        boolean sameToZX = to[0] == to1[0] && to[2] == to1[2];
        boolean intersectZX = to[1] >= from1[1] && from[1] <= to1[1];

        if (sameFromZX && sameToZX && intersectZX) { return true; }

        boolean sameFromZY = from[1] == from1[1] && from[2] == from1[2];
        boolean sameToZY = to[1] == to1[1] && to[2] == to1[2];
        boolean intersectZY = to[0] >= from1[0] && from[0] <= to1[0];

        if (sameFromZY && sameToZY && intersectZY) { return true; }
        boolean sameFromYX = from[0] == from1[0] && from[1] == from1[1];
        boolean sameToYX = to[0] == to1[0] && to[1] == to1[1];
        boolean intersectYX = to[2] >= from1[2] && from[2] <= to1[2];

        if (sameFromYX && sameToYX && intersectYX) { return true; }

        return false;
    }

    int                boxNames = 0;
    HashSet<String>    names    = Sets.newHashSet();
    HashSet<float[][]> cubes    = Sets.newHashSet();

    private Shape convertBoxToShape(ObjModel model, Box box, float scale)
    {
        if (box.getName() == null)
        {
            box.setName("box_" + boxNames++);
        }
        if (names.contains(box.getName()))
        {
            box.setName(box.getName() + boxNames++);
        }
        names.add(box.getName());
        box.setName("Cube_");

        float[] from = box.getFrom();
        float[] to = box.getTo();

        for (float[][] arr : cubes)
        {
            if (isSame(from, arr[0]) && isSame(to, arr[1])) return null;
        }
        float[][] arr = new float[2][];
        arr[0] = from;
        arr[1] = to;
        cubes.add(arr);

        Faces faces = box.getFaces();
        faces.init();
        String name = box.getName();
        for (FaceComponent face : faces.components)
        {
            String temp;
            if (face != null && (temp = this.model.model.getTexture(face.getTexture().replace("#", ""))) != null)
            {
                name = temp;
            }
        }
        Shape shape = new Shape(model, name);

        from = from.clone();
        to = to.clone();
        for (int i = 0; i < 3; i++)
        {
            from[i] *= scale;
            to[i] *= scale;
        }

        Vertex frontTopLeft = new Vertex(from[0], from[1], from[2]);
        Vertex frontTopRight = new Vertex(to[0], from[1], from[2]);
        Vertex frontBottomRight = new Vertex(to[0], to[1], from[2]);
        Vertex frontBottomLeft = new Vertex(from[0], to[1], from[2]);
        Vertex backTopLeft = new Vertex(from[0], from[1], to[2]);
        Vertex backTopRight = new Vertex(to[0], from[1], to[2]);
        Vertex backBottomRight = new Vertex(to[0], to[1], to[2]);
        Vertex backBottomLeft = new Vertex(from[0], to[1], to[2]);

        if (faces.faces[0])
        {
            shape.faces.add(new Face(shape).append(frontBottomLeft, createUV(0, 0, faces.components[0]))
                    .append(frontBottomRight, createUV(1, 0, faces.components[0]))
                    .append(frontTopRight, createUV(2, 0, faces.components[0]))
                    .append(frontTopLeft, createUV(3, 0, faces.components[0])));
        }
        if (faces.faces[1])
        {
            shape.faces.add(new Face(shape).append(backBottomLeft, createUV(0, 1, faces.components[1]))
                    .append(backBottomRight, createUV(1, 1, faces.components[1]))
                    .append(backTopRight, createUV(2, 1, faces.components[1]))
                    .append(backTopLeft, createUV(3, 1, faces.components[1])));
        }
        if (faces.faces[4])
        {
            shape.faces.add(new Face(shape).append(frontTopLeft, createUV(0, 4, faces.components[4]))
                    .append(frontTopRight, createUV(1, 4, faces.components[4]))
                    .append(backTopRight, createUV(2, 4, faces.components[4]))
                    .append(backTopLeft, createUV(3, 4, faces.components[4])));
        }
        if (faces.faces[5])
        {
            shape.faces.add(new Face(shape).append(backBottomLeft, createUV(0, 5, faces.components[5]))
                    .append(backBottomRight, createUV(1, 5, faces.components[5]))
                    .append(frontBottomRight, createUV(2, 5, faces.components[5]))
                    .append(frontBottomLeft, createUV(3, 5, faces.components[5])));
        }
        if (faces.faces[2])
        {
            shape.faces.add(new Face(shape).append(backBottomLeft, createUV(0, 2, faces.components[2]))
                    .append(frontBottomLeft, createUV(1, 2, faces.components[2]))
                    .append(frontTopLeft, createUV(2, 2, faces.components[2]))
                    .append(backTopLeft, createUV(3, 2, faces.components[2])));
        }
        if (faces.faces[3])
        {
            shape.faces.add(new Face(shape).append(frontBottomRight, createUV(0, 3, faces.components[3]))
                    .append(backBottomRight, createUV(1, 3, faces.components[3]))
                    .append(backTopRight, createUV(2, 3, faces.components[3]))
                    .append(frontTopRight, createUV(3, 3, faces.components[3])));
        }

        Rotation rotation = box.getRotation();

        if (rotation != null)
        {
            float[] offset = rotation.getOrigin();
            offset = offset.clone();
            for (int i = 0; i < 3; i++)
            {
                offset[i] *= scale;
            }
            shape.translate(new Vector3f(-offset[0], -offset[1], -offset[2]));
            if (rotation.getAxis().equals("x")) shape.rotate((float) -rotation.getAngle(), 1, 0, 0);
            if (rotation.getAxis().equals("y")) shape.rotate((float) -rotation.getAngle(), 0, 1, 0);
            if (rotation.getAxis().equals("z")) shape.rotate((float) -rotation.getAngle(), 0, 0, 1);

            shape.translate(new Vector3f(offset[0], offset[1], offset[2]));
        }
        shape.translate(new Vector3f(-0.5f, 0, 0));
        shape.rotate(180, 0, 1, 0);

        return shape;
    }

    private boolean isSame(float[] a, float[] b)
    {
        return a[0] == b[0] && a[1] == b[1] && a[2] == b[2];
    }

    private TextureCoords createUV(int index, int face, FaceComponent component)
    {
        float[] uvs = getUVs(face, component.getUv(), index);
        return new TextureCoords(uvs[0] / 16f, uvs[1] / 16f);
    }

    int north = 0;
    int east  = 1;
    int south = 2;
    int west  = 3;
    int up    = 4;
    int down  = 5;

    private float[] getUVs(int face, float[] uvsIn, int index)
    {
        float[] uvs = { 0, 0 };
        if (!Main.jsonTexture)
        {
            uvsIn = new float[] { 0, 8, 8, 0 };
        }

        if (index == 0)
        {
            uvs[0] = uvsIn[1];
            uvs[1] = uvsIn[0];
        }
        if (index == 1)
        {
            uvs[0] = uvsIn[1];
            uvs[1] = uvsIn[2];
        }
        if (index == 2)
        {
            uvs[0] = uvsIn[3];
            uvs[1] = uvsIn[2];
        }
        if (index == 3)
        {
            uvs[0] = uvsIn[3];
            uvs[1] = uvsIn[0];
        }
        return uvs;
    }
}
