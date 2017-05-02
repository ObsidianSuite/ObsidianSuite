package ru.gloomyfolken.tcn2obj.tcn.components;

import java.util.ArrayList;

import com.google.common.collect.Lists;

public class Models
{
    public Model Model;

    public static class Model
    {
        public String   texture;
        public String   BaseClass;
        public Geometry Geometry;
        public String   GlScale;
        public String   Name;
        public String   TextureSize;
    }

    public static class Geometry
    {
        public ArrayList<Shape> Shape = Lists.newArrayList();
    }
}
