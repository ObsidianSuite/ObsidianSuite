package ru.gloomyfolken.tcn2obj;

import org.lwjgl.util.vector.Vector3f;
import ru.gloomyfolken.tcn2obj.obj.Face;
import ru.gloomyfolken.tcn2obj.obj.ObjModel;
import ru.gloomyfolken.tcn2obj.obj.Shape;
import ru.gloomyfolken.tcn2obj.obj.TextureCoords;
import ru.gloomyfolken.tcn2obj.obj.Vertex;
import ru.gloomyfolken.tcn2obj.qubble.QubbleCuboid;
import ru.gloomyfolken.tcn2obj.qubble.QubbleModel;

public class QblConverter
{
    public QubbleModel model;

    public ObjModel qbl2obj(QubbleModel model, float scale)
    {
        ObjModel obj = new ObjModel();
        this.model = model.unparent();

        for (QubbleCuboid cuboid : this.model.getCuboids())
        {
            obj.shapes.add(convertBoxToShape(obj, cuboid, scale));
        }

        return obj;
    }

    private Shape convertBoxToShape(ObjModel model, QubbleCuboid cuboid, float scale)
    {
        Shape shape = new Shape(model, cuboid.getName());

        Vertex frontTopLeft = new Vertex(cuboid.getOffsetX(), cuboid.getOffsetY(), cuboid.getOffsetZ());
        Vertex frontTopRight = new Vertex(cuboid.getOffsetX() + cuboid.getDimensionX(), cuboid.getOffsetY(), cuboid.getOffsetZ());
        Vertex frontBottomRight = new Vertex(cuboid.getOffsetX() + cuboid.getDimensionX(), cuboid.getOffsetY() + cuboid.getDimensionY(), cuboid.getOffsetZ());
        Vertex frontBottomLeft = new Vertex(cuboid.getOffsetX(), cuboid.getOffsetY() + cuboid.getDimensionY(), cuboid.getOffsetZ());
        Vertex backTopLeft = new Vertex(cuboid.getOffsetX(), cuboid.getOffsetY(), cuboid.getOffsetZ() + cuboid.getDimensionZ());
        Vertex backTopRight = new Vertex(cuboid.getOffsetX() + cuboid.getDimensionX(), cuboid.getOffsetY(), cuboid.getOffsetZ() + cuboid.getDimensionZ());
        Vertex backBottomRight = new Vertex(cuboid.getOffsetX() + cuboid.getDimensionX(), cuboid.getOffsetY() + cuboid.getDimensionY(), cuboid.getOffsetZ() + cuboid.getDimensionZ());
        Vertex backBottomLeft = new Vertex(cuboid.getOffsetX(), cuboid.getOffsetY() + cuboid.getDimensionY(), cuboid.getOffsetZ() + cuboid.getDimensionZ());

        if (cuboid.getDimensionX() > 0 && cuboid.getDimensionY() > 0)
        {
            // front
            shape.faces.add(new Face(shape)
                    .append(frontBottomLeft, createUV(cuboid, cuboid.getDimensionZ(), cuboid.getDimensionZ() + cuboid.getDimensionY(), cuboid.getDimensionX(), false))
                    .append(frontBottomRight,
                            createUV(cuboid, cuboid.getDimensionZ() + cuboid.getDimensionX(), cuboid.getDimensionZ() + cuboid.getDimensionY(), -cuboid.getDimensionX(), false))
                    .append(frontTopRight, createUV(cuboid, cuboid.getDimensionZ() + cuboid.getDimensionX(), cuboid.getDimensionZ(), -cuboid.getDimensionX(), false))
                    .append(frontTopLeft, createUV(cuboid, cuboid.getDimensionZ(), cuboid.getDimensionZ(), cuboid.getDimensionX(), false)));
            // back
            shape.faces
                    .add(new Face(shape)
                            .append(backBottomRight,
                                    createUV(cuboid, cuboid.getDimensionZ() * 2 + cuboid.getDimensionX(), cuboid.getDimensionZ() + cuboid.getDimensionY(), cuboid.getDimensionX(), false))
                            .append(backBottomLeft,
                                    createUV(cuboid, cuboid.getDimensionZ() * 2 + cuboid.getDimensionX() * 2, cuboid.getDimensionZ() + cuboid.getDimensionY(), -cuboid.getDimensionX(),
                                            false))
                            .append(backTopLeft, createUV(cuboid, cuboid.getDimensionZ() * 2 + cuboid.getDimensionX() * 2, cuboid.getDimensionZ(), -cuboid.getDimensionX(), false))
                            .append(backTopRight, createUV(cuboid, cuboid.getDimensionZ() * 2 + cuboid.getDimensionX(), cuboid.getDimensionZ(), cuboid.getDimensionX(), false)));
        }

        if (cuboid.getDimensionX() > 0 && cuboid.getDimensionZ() > 0)
        {
            // top
            shape.faces.add(new Face(shape).append(frontTopLeft, createUV(cuboid, cuboid.getDimensionZ(), cuboid.getDimensionZ(), cuboid.getDimensionX(), false))
                    .append(frontTopRight, createUV(cuboid, cuboid.getDimensionZ() + cuboid.getDimensionX(), cuboid.getDimensionZ(), -cuboid.getDimensionX(), false))
                    .append(backTopRight, createUV(cuboid, cuboid.getDimensionZ() + cuboid.getDimensionX(), 0, -cuboid.getDimensionX(), false))
                    .append(backTopLeft, createUV(cuboid, cuboid.getDimensionZ(), 0, cuboid.getDimensionX(), false)));

            // bottom
            shape.faces
                    .add(new Face(shape)
                            .append(backBottomLeft, createUV(cuboid, cuboid.getDimensionZ() + cuboid.getDimensionX(), cuboid.getDimensionZ(), cuboid.getDimensionX(), false))
                            .append(backBottomRight,
                                    createUV(cuboid, cuboid.getDimensionZ() + cuboid.getDimensionX() * 2, cuboid.getDimensionZ(), -cuboid.getDimensionX(), false))
                            .append(frontBottomRight, createUV(cuboid, cuboid.getDimensionZ() + cuboid.getDimensionX() * 2, 0, -cuboid.getDimensionX(), false))
                            .append(frontBottomLeft, createUV(cuboid, cuboid.getDimensionZ() + cuboid.getDimensionX(), 0, cuboid.getDimensionX(), false)));
        }

        if (cuboid.getDimensionY() > 0 && cuboid.getDimensionZ() > 0)
        {
            // left
            shape.faces
                    .add(new Face(shape)
                            .append(backBottomLeft,
                                    createUV(cuboid, 0, cuboid.getDimensionZ() + cuboid.getDimensionY(), cuboid.getDimensionX() + cuboid.getDimensionZ() * 2, true))
                            .append(frontBottomLeft, createUV(cuboid, cuboid.getDimensionZ(), cuboid.getDimensionZ() + cuboid.getDimensionY(), cuboid.getDimensionX(), true))
                            .append(frontTopLeft, createUV(cuboid, cuboid.getDimensionZ(), cuboid.getDimensionZ(), cuboid.getDimensionX(), true))
                            .append(backTopLeft, createUV(cuboid, 0, cuboid.getDimensionZ(), cuboid.getDimensionX() + cuboid.getDimensionZ() * 2, true)));

            // right
            shape.faces.add(new Face(shape)
                    .append(frontBottomRight,
                            createUV(cuboid, cuboid.getDimensionZ() + cuboid.getDimensionX(), cuboid.getDimensionZ() + cuboid.getDimensionY(), -cuboid.getDimensionX(), true))
                    .append(backBottomRight,
                            createUV(cuboid, cuboid.getDimensionZ() * 2 + cuboid.getDimensionX(), cuboid.getDimensionZ() + cuboid.getDimensionY(), -cuboid.getDimensionX() - cuboid.getDimensionZ() * 2,
                                    true))
                    .append(backTopRight,
                            createUV(cuboid, cuboid.getDimensionZ() * 2 + cuboid.getDimensionX(), cuboid.getDimensionZ(), -cuboid.getDimensionX() - cuboid.getDimensionZ() * 2, true))
                    .append(frontTopRight, createUV(cuboid, cuboid.getDimensionZ() + cuboid.getDimensionX(), cuboid.getDimensionZ(), -cuboid.getDimensionX(), true)));
        }

        shape.rotate(-cuboid.getRotationX(), 1, 0, 0);
        shape.rotate(-cuboid.getRotationY(), 0, 1, 0);
        shape.rotate(-cuboid.getRotationZ(), 0, 0, 1);

        shape.scale(new Vector3f(cuboid.getScaleX(), cuboid.getScaleY(), cuboid.getScaleZ()));

        shape.translate(new Vector3f(cuboid.getPositionX(), cuboid.getPositionY(), cuboid.getPositionZ()));

        // fix Y axis direction
        shape.rotate(180, 0, 0, 1);
        // fix x direction
        shape.rotate(180, 0, 1, 0);

        shape.scale(new Vector3f(scale, scale, scale));

        return shape;
    }

    /**
     * @param cuboid          The box that we are currently converting
     * @param baseU
     * @param baseV
     * @param mirrorOffset if side == false: positive face x size for minU coords and
     *                     negative one for maxU if side == true: (FRONT face size +
     *                     (is_adjacent_to_front_face ? 0 : face_size*2)) * (right_face ?
     *                     -1 : 1)
     * @return
     */
    private TextureCoords createUV(QubbleCuboid cuboid, float baseU, float baseV, float mirrorOffset, boolean side)
    {
        if (!cuboid.isTextureMirrored())
        {
            return new TextureCoords(((cuboid.getTextureX() + baseU) / model.getTextureWidth()),
                    (1 - (cuboid.getTextureY() + baseV) / model.getTextureHeight()));
        }
        else
        {
            return new TextureCoords(((cuboid.getTextureX() + baseU + mirrorOffset) / model.getTextureWidth()),
                    (1 - (cuboid.getTextureY() + baseV) / model.getTextureHeight()));
        }
    }
}
