package obsidianAPI.render.bend;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.wavefront.Face;
import obsidianAPI.render.wavefront.GroupObject;
import obsidianAPI.render.wavefront.TextureCoordinate;
import obsidianAPI.render.wavefront.Vertex;

public class BendPart extends GroupObject
{

    //Sets of four vertices for the top and bottom of the sections.
    protected List<TextureCoordinate[]> faceTextureCoords;
    private boolean inverted;

    public BendPart(Vertex[] topVertices, Vertex[] bottomVertices, PartUVMap uvMap, boolean inverted)
    {
        super("", 4);
        this.inverted = inverted;
        faceTextureCoords = new ArrayList<TextureCoordinate[]>();
        setupVertices(topVertices, bottomVertices, uvMap);
    }

    private void setupVertices(Vertex[] topVertices, Vertex[] bottomVertices, PartUVMap uvMap)
    {
        this.faces.clear();

        for (int i = 0; i < 4; i++)
        {
            int j = i == 3 ? 0 : i + 1;

            Vertex vA = topVertices[j];
            Vertex vB = bottomVertices[j];
            Vertex vC = topVertices[i];
            Face f = new Face();
            f.vertices = new Vertex[] {vC, vB, vA};
            faces.add(f);

            Vertex vD = bottomVertices[i];
            Vertex vE = topVertices[i];
            Vertex vF = bottomVertices[j];
            Face g = new Face();
            g.vertices = new Vertex[] {vF, vE, vD};
            faces.add(g);

            Vertex faceNormal = inverted ? g.calculateFaceNormal() : f.calculateFaceNormal();
            f.faceNormal = faceNormal;
            g.faceNormal = faceNormal;

            uvMap.setupFaceTextureCoordinates(f);
            uvMap.setupFaceTextureCoordinates(g);

            faceTextureCoords.add(f.textureCoordinates);
            faceTextureCoords.add(g.textureCoordinates);
        }
    }

    /**
     * Update the top and bottom vertices.
     * Also generates the faces represented by these vertices.
     */
    public void updateVertices(Vertex[] topVertices, Vertex[] bottomVertices)
    {
        this.faces.clear();

        for (int i = 0; i < 4; i++)
        {
            int j = i == 3 ? 0 : i + 1;

            Vertex vA = topVertices[j];
            Vertex vB = bottomVertices[j];
            Vertex vC = topVertices[i];
            Face f = new Face();
            f.vertices = new Vertex[] {vC, vB, vA};
            faces.add(f);

            Vertex vD = bottomVertices[i];
            Vertex vE = topVertices[i];
            Vertex vF = bottomVertices[j];
            Face g = new Face();
            g.vertices = new Vertex[] {vF, vE, vD};
            faces.add(g);

            Vertex faceNormal = inverted ? g.calculateFaceNormal() : f.calculateFaceNormal();
            f.faceNormal = faceNormal;
            g.faceNormal = faceNormal;
        }
    }

    /**
     * Change the texture coordinates and texture if the part is highlighted.
     */
    public void updateTextureCoordinates(Entity entity, boolean mainHighlight, boolean otherHighlight, ModelObj modelObj)
    {
        ResourceLocation texture;

        texture = modelObj.getTexture(entity);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        for (int i = 0; i < 8; i++)
        {
            Face f = faces.get(i);
            f.textureCoordinates = faceTextureCoords.get(i);
        }
    }

}
