package ru.gloomyfolken.tcn2obj;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ru.gloomyfolken.tcn2obj.tbl.components.Animation;
import ru.gloomyfolken.tcn2obj.tbl.components.AnimationComponent;
import ru.gloomyfolken.tcn2obj.tbl.components.CubeInfo;

public class TabulaMetadataExporter
{
    private TblConverter      converter;
    private ArrayList<String> lines = Lists.newArrayList();

    public TabulaMetadataExporter(TblConverter converter)
    {
        this.converter = converter;
    }

    public ArrayList<String> getXMLLines()
    {
        lines.clear();
        addHeader();
        addMetadata();
        addAnimations();

        addEnd();
        return lines;
    }

    private void addHeader()
    {
        lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        lines.add("<ModelAnimator>");
        lines.add("    <model>");
        lines.add("        <phase name=\"global\" rotation=\"1,0,0,0\" offset=\"0,0,0\" scale=\"1.0\"/>");
    }

    private void addEnd()
    {
        lines.add("    </model>");
        lines.add("</ModelAnimator>");
    }

    private void addAnimations()
    {
        if (converter != null) for (Animation anim : converter.model.model.getAnimations())
        {
            addAnimation(anim);
        }
    }

    private void addAnimation(Animation anim)
    {
        int dir = -1;// TODO configuration for this.
        String name = anim.name;
        lines.add("        <phase type=\"" + name + "\">");
        for (String set : anim.sets.keySet())
        {
            String part = identToName(set);
            lines.add("            <part name=\"" + part + "\">");
            ArrayList<AnimationComponent> components = anim.getComponents(set);
            for (AnimationComponent comp : components)
            {
                String xmlStr = "                <component name=\"" + comp.name + "\"";

                xmlStr += " startKey=\"" + comp.startKey + "\"";
                xmlStr += " length=\"" + comp.length + "\"";

                if (!isEmpty(comp.rotOffset)) xmlStr += " rotOffset=\"" + dir * comp.rotOffset[0] + ","
                        + dir * comp.rotOffset[1] + "," + dir * comp.rotOffset[2] + ",\"";
                if (!isEmpty(comp.rotChange)) xmlStr += " rotChange=\"" + comp.rotChange[0] + "," + comp.rotChange[2]
                        + "," + comp.rotChange[1] + ",\"";
                if (!isEmpty(comp.posOffset)) xmlStr += " posOffset=\"" + comp.posOffset[0] + "," + comp.posOffset[1]
                        + "," + comp.posOffset[2] + ",\"";
                if (!isEmpty(comp.posChange)) xmlStr += " posChange=\"" + comp.posChange[0] + "," + comp.posChange[1]
                        + "," + comp.posChange[2] + ",\"";
                if (!isEmpty(comp.scaleOffset)) xmlStr += " scaleOffset=\"" + comp.scaleOffset[0] + ","
                        + comp.scaleOffset[1] + "," + comp.scaleOffset[2] + ",\"";
                if (!isEmpty(comp.scaleChange)) xmlStr += " scaleChange=\"" + comp.scaleChange[0] + ","
                        + comp.scaleChange[1] + "," + comp.scaleChange[2] + ",\"";

                if (comp.opacityChange != 0) xmlStr += " opacityChange=\"" + comp.length + "\"";
                if (comp.opacityOffset != 0) xmlStr += " opacityOffset=\"" + comp.length + "\"";
                if (comp.hidden) xmlStr += " hidden=\"" + comp.hidden + "\"";

                xmlStr += "/>";
                lines.add(xmlStr);
            }

            lines.add("            </part>");
        }
        lines.add("        </phase>");
    }

    private boolean isEmpty(double[] arr)
    {
        return arr[0] == 0 && arr[1] == 0 && arr[2] == 0;
    }

    private String identToName(String ident)
    {
        for (CubeInfo cube : converter.cubes)
        {
            if (cube.identifier.equals(ident)) return cube.name;
        }
        return ident;
    }

    private void addMetadata()
    {
        HashMap<String, ArrayList<String>> metaMap = Maps.newHashMap();
        if (converter != null) for (CubeInfo cube : converter.cubes)
        {
            for (String key : cube.metadata)
            {
                addToMeta(key, cube, metaMap);
            }
        }
        String xmlStr = "        <metadata headCap=\"-60,60\" headAxis=\"1\" headAxis2=\"0\" headDir=\"1\"";
        for (String s : metaMap.keySet())
        {
            ArrayList<String> parts = metaMap.get(s);
            String component = s + "=\"";
            for (int i = 0; i < parts.size(); i++)
            {
                component += parts.get(i);
                if (i < parts.size() - 1)
                {
                    component += ",";
                }
            }
            component += "\"";
            xmlStr += " " + component;
        }
        xmlStr += "/>";
        lines.add(xmlStr);
    }

    private void addToMeta(String key, CubeInfo cube, HashMap<String, ArrayList<String>> metaMap)
    {
        ArrayList<String> meta = metaMap.get(key);
        if (meta == null)
        {
            meta = Lists.newArrayList();
            metaMap.put(key, meta);
        }
        meta.add(cube.name);
    }
}
