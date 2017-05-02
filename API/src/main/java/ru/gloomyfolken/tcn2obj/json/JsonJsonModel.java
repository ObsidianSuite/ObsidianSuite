package ru.gloomyfolken.tcn2obj.json;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ru.gloomyfolken.tcn2obj.json.components.Box;

public class JsonJsonModel
{
    ArrayList<Box>     elements = Lists.newArrayList();
    HashMap<String, String> texMap = Maps.newHashMap();

    public ArrayList<Box> getElements()
    {
        return elements;
    }

    public String getTexture(String name)
    {
        return texMap.get(name);
    }
}
