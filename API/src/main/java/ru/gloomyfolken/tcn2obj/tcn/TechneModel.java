package ru.gloomyfolken.tcn2obj.tcn;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.gloomyfolken.tcn2obj.ModelFormatException;
import ru.gloomyfolken.tcn2obj.json.helpers.JsonHelper;
import ru.gloomyfolken.tcn2obj.tcn.components.Models;
import ru.gloomyfolken.tcn2obj.tcn.components.Models.Model;
import ru.gloomyfolken.tcn2obj.tcn.components.Shape;

/** Techne model loader, copy-pasted from Minecraft Forge */
public class TechneModel
{

    public List<TechneBox> boxes        = new ArrayList<TechneBox>();

    // GlScale attributes
    public float           scaleX       = 1;
    public float           scaleY       = 1;
    public float           scaleZ       = 1;

    public int             textureSizeX = 64;
    public int             textureSizeY = 32;

    private String         filename;

    public TechneModel(File file) throws ModelFormatException
    {
        this.filename = file.getName();
        loadTechneModel(file);
    }

    private void loadTechneModel(File file) throws ModelFormatException
    {
        try
        {
            ZipFile zipFile = new ZipFile(file);
            ZipEntry modelEntry = null;
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().equals("model.xml"))
                {
                    modelEntry = entry;
                    break;
                }
            }
            byte[] modelXml;
            if (modelEntry == null)
            {
                entries = zipFile.entries();
                while (entries.hasMoreElements())
                {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().equals("model.json"))
                    {
                        modelEntry = entry;
                        break;
                    }
                }
                try
                {
                    parseFromJson(modelEntry, zipFile);
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                zipFile.close();
                return;
                // modelXml =
                // IOUtils.toByteArray(zipFile.getInputStream(modelEntry));
                // zipFile.close();
            }
            else
            {
                modelXml = IOUtils.toByteArray(zipFile.getInputStream(modelEntry));
                zipFile.close();
            }

            if (modelXml == null) { throw new ModelFormatException(
                    "Model " + filename + " contains no model.xml file"); }

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new ByteArrayInputStream(modelXml));

            NodeList nodeListTechne = document.getElementsByTagName("Techne");
            if (nodeListTechne.getLength() < 1) { throw new ModelFormatException(
                    "Model " + filename + " contains no Techne tag"); }

            NodeList nodeListModel = document.getElementsByTagName("Model");
            if (nodeListModel.getLength() < 1) { throw new ModelFormatException(
                    "Model " + filename + " contains no Model tag"); }

            NamedNodeMap modelAttributes = nodeListModel.item(0).getAttributes();

            if (modelAttributes == null) { throw new ModelFormatException(
                    "Model " + filename + " contains a Model tag with no attributes"); }

            NodeList nodes = nodeListModel.item(0).getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node node = nodes.item(i);

                if (node.getNodeName().equals("GlScale"))
                {
                    String[] scale = node.getTextContent().split(",");
                    this.scaleX = Float.parseFloat(scale[0]);
                    this.scaleY = Float.parseFloat(scale[1]);
                    this.scaleZ = Float.parseFloat(scale[2]);
                }

                if (node.getNodeName().equals("TextureSize"))
                {
                    String[] textureSize = node.getTextContent().split(",");
                    this.textureSizeX = Integer.parseInt(textureSize[0]);
                    this.textureSizeY = Integer.parseInt(textureSize[1]);
                }
            }

            NodeList shapes = document.getElementsByTagName("Shape");
            for (int i = 0; i < shapes.getLength(); i++)
            {
                Node shape = shapes.item(i);
                NamedNodeMap shapeAttributes = shape.getAttributes();
                if (shapeAttributes == null) { throw new ModelFormatException(
                        "Shape #" + (i + 1) + " in " + filename + " has no attributes"); }

                Node name = shapeAttributes.getNamedItem("name");
                String shapeName = null;
                if (name != null)
                {
                    shapeName = name.getNodeValue();
                }
                if (shapeName == null)
                {
                    shapeName = "Shape #" + (i + 1);
                }

                try
                {
                    boolean mirrored = false;
                    String[] offset = new String[3];
                    String[] position = new String[3];
                    String[] rotation = new String[3];
                    String[] size = new String[3];
                    String[] textureOffset = new String[2];

                    NodeList shapeChildren = shape.getChildNodes();
                    for (int j = 0; j < shapeChildren.getLength(); j++)
                    {
                        Node shapeChild = shapeChildren.item(j);

                        String shapeChildName = shapeChild.getNodeName();
                        String shapeChildValue = shapeChild.getTextContent();
                        if (shapeChildValue != null)
                        {
                            shapeChildValue = shapeChildValue.trim();

                            if (shapeChildName.equals("IsMirrored"))
                            {
                                mirrored = !shapeChildValue.equals("False");
                            }
                            else if (shapeChildName.equals("Offset"))
                            {
                                offset = shapeChildValue.split(",");
                            }
                            else if (shapeChildName.equals("Position"))
                            {
                                position = shapeChildValue.split(",");
                            }
                            else if (shapeChildName.equals("Rotation"))
                            {
                                rotation = shapeChildValue.split(",");
                            }
                            else if (shapeChildName.equals("Size"))
                            {
                                size = shapeChildValue.split(",");
                            }
                            else if (shapeChildName.equals("TextureOffset"))
                            {
                                textureOffset = shapeChildValue.split(",");
                            }
                        }
                    }

                    TechneBox box = new TechneBox(this, shapeName);
                    box.setTextureOffset(Integer.parseInt(textureOffset[0]), Integer.parseInt(textureOffset[1]));
                    box.mirror = mirrored;
                    box.setOffset(Float.parseFloat(offset[0]), Float.parseFloat(offset[1]),
                            Float.parseFloat(offset[2]));
                    box.setDimensions(Integer.parseInt(size[0]), Integer.parseInt(size[1]), Integer.parseInt(size[2]));
                    box.setRotationPoint(Float.parseFloat(position[0]), Float.parseFloat(position[1]) - 23.4f,
                            Float.parseFloat(position[2]));
                    box.setRotateAngles(Float.parseFloat(rotation[0]), Float.parseFloat(rotation[1]),
                            Float.parseFloat(rotation[2]));
                    box.setTextureSize(textureSizeX, textureSizeY);
                    boxes.add(box);
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }

            }

        }
        catch (ZipException e)
        {
            throw new ModelFormatException("Model " + filename + " is not a valid zip file");
        }
        catch (IOException e)
        {
            throw new ModelFormatException("Model " + filename + " could not be read", e);
        }
        catch (ParserConfigurationException e)
        {
            // hush
        }
        catch (SAXException e)
        {
            throw new ModelFormatException("Model " + filename + " contains invalid XML", e);
        }
    }

    private void parseFromJson(ZipEntry modelEntry, ZipFile zipFile) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(modelEntry)));
        String json = reader.readLine();
        json = json.replaceAll("@", "").replaceAll(json.substring(1, 2), "");
        JsonTechneModel model = JsonHelper.parseTechneModel(new ByteArrayInputStream(json.getBytes()));

        for (Models models : model.Techne.Models)
        {
            Model tblmodel = models.Model;

            for (Shape shape : tblmodel.Geometry.Shape)
            {
                boolean mirrored = shape.IsMirrored;
                String[] offset = shape.Offset.split(",");
                String[] position = shape.Position.split(",");
                String[] rotation = shape.Rotation.split(",");
                String[] size = shape.Size.split(",");
                String[] textureOffset = shape.TextureOffset.split(",");
                TechneBox box = new TechneBox(this, shape.name);
                box.setTextureOffset(Integer.parseInt(textureOffset[0]), Integer.parseInt(textureOffset[1]));
                box.mirror = mirrored;
                box.setOffset(Float.parseFloat(offset[0]), Float.parseFloat(offset[1]), Float.parseFloat(offset[2]));
                box.setDimensions(Integer.parseInt(size[0]), Integer.parseInt(size[1]), Integer.parseInt(size[2]));
                box.setRotationPoint(Float.parseFloat(position[0]), Float.parseFloat(position[1]) - 23.4f,
                        Float.parseFloat(position[2]));
                box.setRotateAngles(Float.parseFloat(rotation[0]), Float.parseFloat(rotation[1]),
                        Float.parseFloat(rotation[2]));
                box.setTextureSize(textureSizeX, textureSizeY);
                boxes.add(box);
            }
        }

    }
}
