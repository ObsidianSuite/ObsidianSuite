package ru.gloomyfolken.tcn2obj.json.components;

import java.util.Arrays;

public class Box
{
    private String   name;
    private double[] from;
    private double[] to;
    private Rotation rotation;
    private Faces    faces;

    public String toString()
    {
        return getName() + " " + Arrays.toString(getFrom()) + "->" + Arrays.toString(getTo()) + " " + getRotation()
                + " " + getFaces();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public float[] getTo()
    {
        float[] to = new float[3];
        for (int i = 0; i < 3; i++)
            to[i] = (float) this.to[i];
        return to;
    }

    public void setTo(float[] to)
    {
        for (int i = 0; i < 3; i++)
            this.to[i] = to[i];
    }

    public float[] getFrom()
    {
        float[] from = new float[3];
        for (int i = 0; i < 3; i++)
            from[i] = (float) this.from[i];
        return from;
    }

    public void setFrom(float[] from)
    {
        for (int i = 0; i < 3; i++)
            this.from[i] = from[i];
    }

    public Faces getFaces()
    {
        return faces;
    }

    public Rotation getRotation()
    {
        return rotation;
    }
}
