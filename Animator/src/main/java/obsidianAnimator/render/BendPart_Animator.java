package obsidianAnimator.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.bend.BendPart;
import obsidianAPI.render.bend.PartUVMap;
import obsidianAnimator.render.entity.ModelObj_Animator;

public class BendPart_Animator extends BendPart
{
    public BendPart_Animator(Vertex[] topVertices, Vertex[] bottomVertices, PartUVMap uvMap, boolean inverted)
    {
        super(topVertices, bottomVertices, uvMap, inverted);
    }

    /**
     * Change the texture coordinates and texture if the part is highlighted.
     */
    public void updateTextureCoordinates(boolean mainHighlight, boolean otherHighlight, ModelObj modelObj)
    {
        boolean useHighlightCoords = true;
        ResourceLocation texture;
        TextureCoordinate[] highlightCoords = new TextureCoordinate[] {
                new TextureCoordinate(0.0F, 0.0F),
                new TextureCoordinate(0.5F, 0.0F),
                new TextureCoordinate(0.0F, 0.5F)};
        if (mainHighlight)
            texture = ModelObj_Animator.pinkResLoc;
        else if (otherHighlight)
            texture = ModelObj_Animator.whiteResLoc;
        else
        {
            texture = modelObj.getTexture();
            useHighlightCoords = false;
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        for (int i = 0; i < 8; i++)
        {
            Face f = faces.get(i);
            if (useHighlightCoords)
                f.textureCoordinates = highlightCoords;
            else
                f.textureCoordinates = faceTextureCoords.get(i);
        }
    }
}
