package ru.gloomyfolken.tcn2obj.json.components;

import java.util.Arrays;

public class Rotation
{
    double[] origin;
    private String   axis;
    private double   angle;
    
    public String toString()
    {
        return getAxis()+" "+getAngle()+" "+Arrays.toString(origin);
    }

    public String getAxis()
    {
        return axis;
    }

    public double getAngle()
    {
        return angle;
    }
    
    public float[] getOrigin()
    {
        float[] origin = new float[3];
        for (int i = 0; i < 3; i++)
            origin[i] = (float) this.origin[i];
        return origin;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof Rotation)
        {
            Rotation rot = (Rotation) o;
            return angle == rot.angle && rot.axis.equals(axis) && rot.origin[0] == origin[0] && rot.origin[1] == origin[1] && rot.origin[2] == origin[2];
        }
        
        return super.equals(o);
    }
}
