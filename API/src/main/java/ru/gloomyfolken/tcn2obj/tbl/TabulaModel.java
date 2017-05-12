package ru.gloomyfolken.tcn2obj.tbl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import ru.gloomyfolken.tcn2obj.ModelFormatException;
import ru.gloomyfolken.tcn2obj.json.helpers.JsonHelper;
import ru.gloomyfolken.tcn2obj.tbl.components.CubeGroup;
import ru.gloomyfolken.tcn2obj.tbl.components.CubeInfo;

/** Techne model loader, copy-pasted from Minecraft Forge */
public class TabulaModel
{

    public List<TabulaBox> boxes = new ArrayList<TabulaBox>();
    public JsonTabulaModel model;

    // GlScale attributes
    public float scaleX = 1;
    public float scaleY = 1;
    public float scaleZ = 1;

    public int textureSizeX = 64;
    public int textureSizeY = 32;

    private String filename;

    public TabulaModel(File file) throws ModelFormatException
    {
        this.filename = file.getName();
        loadTabulaModel(file);
    }

    private void loadTabulaModel(File file) throws ModelFormatException
    {
        try
        {
            ZipFile zipFile = new ZipFile(file);
            ZipEntry modelEntry = null;
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().equals("model.json"))
                {
                    modelEntry = entry;
                    break;
                }
            }
            model = JsonHelper.parseTabulaModel(zipFile.getInputStream(modelEntry));
            textureSizeX = model.getTextureWidth();
            textureSizeY = model.getTextureHeight();
            toBoxes(model);

            zipFile.close();

        }
        catch (ZipException e)
        {
            throw new ModelFormatException("Model " + filename + " is not a valid zip file");
        }
        catch (IOException e)
        {
            throw new ModelFormatException("Model " + filename + " could not be read", e);
        }
        catch (Exception e)
        {
            throw new ModelFormatException("Model " + filename + " contains invalid XML", e);
        }
    }

    private void toBoxes(JsonTabulaModel model)
    {
        for (CubeInfo cube : model.getCubes())
        {
            makeBox(cube);
        }
        for(CubeGroup group: model.getCubeGroups())
        {
            makeBoxes(group);
        }
    }

    private void makeBoxes(CubeGroup group)
    {
        for(CubeInfo info: group.cubes)
        {
            makeBox(info);
        }
        for(CubeGroup sub: group.cubeGroups)
        {
            makeBoxes(sub);
        }
    }
    
    private void makeBox(CubeInfo cube)
    {
    	cube.name = formatName(cube.name);
        TabulaBox box = new TabulaBox(this, cube.name);
        new TabulaBox(this, cube.name);
        box.mirror = cube.txMirror;
        box.cube = cube;
        box.model = model;
        setOffsets(box, cube);
        setRotations(box, cube);
        setPositions(box, cube);
        setDimensions(box, cube);
        setScales(box, cube);
        box.setTextureOffset(cube.txOffset[0], cube.txOffset[1]);
        box.setTextureSize(textureSizeX, textureSizeY);
        boxes.add(box);
        for (CubeInfo child : cube.children)
        {
            makeBox(child);
        }
    }

	private String formatName(String name) {
		name = name.replaceAll("[^a-zA-Z0-9_]", "");
		return name;
	}
    
    private void setOffsets(TabulaBox box, CubeInfo cube)
    {
        box.offsetX = (float) cube.offset[0];
        box.offsetY = (float) cube.offset[1];
        box.offsetZ = (float) cube.offset[2];
    }

    private void setRotations(TabulaBox box, CubeInfo cube)
    {
        box.rotateAngleX = (float) cube.rotation[0];
        box.rotateAngleY = (float) cube.rotation[1];
        box.rotateAngleZ = (float) cube.rotation[2];
    }

    private void setPositions(TabulaBox box, CubeInfo cube)
    {
        box.rotationPointX = (float) cube.position[0];
        box.rotationPointY = (float) cube.position[1];
        box.rotationPointZ = (float) cube.position[2];
    }

    private void setDimensions(TabulaBox box, CubeInfo cube)
    {
        box.sizeX = (float) cube.dimensions[0];
        box.sizeY = (float) cube.dimensions[1];
        box.sizeZ = (float) cube.dimensions[2];
    }

    private void setScales(TabulaBox box, CubeInfo cube)
    {
        box.scaleX = (float) cube.scale[0];
        box.scaleY = (float) cube.scale[1];
        box.scaleZ = (float) cube.scale[2];
    }
}
