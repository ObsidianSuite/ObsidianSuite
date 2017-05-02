package ru.gloomyfolken.tcn2obj.json.components;

public class Faces
{
    FaceComponent          north;
    FaceComponent          east;
    FaceComponent          south;
    FaceComponent          west;
    FaceComponent          up;
    FaceComponent          down;

    public boolean[]       faces      = new boolean[6];
    public FaceComponent[] components = new FaceComponent[6];

    public void init()
    {
        if ((components[0] = north) != null) faces[0] = true;
        if ((components[1] = south) != null) faces[1] = true;
        if ((components[2] = east) != null) faces[2] = true;
        if ((components[3] = west) != null) faces[3] = true;
        if ((components[4] = up) != null) faces[4] = true;
        if ((components[5] = down) != null) faces[5] = true;
    }

    public String toString()
    {
        return north + " " + east + " " + south + " " + west + " " + up + " " + down;
    }

    public static class FaceComponent
    {
        String   texture;
        double[] uv;

        public float[] getUv()
        {
            float[] uv = new float[this.uv.length];
            for (int i = 0; i < uv.length; i++)
                uv[i] = (float) this.uv[i];
            return uv;
        }

        public String getTexture()
        {
            return texture;
        }
    }
}
