package com.dabigjoe.obsidianAnimator.render;

import com.dabigjoe.obsidianAPI.render.ModelObj;
import com.dabigjoe.obsidianAPI.render.bend.BendPart;
import com.dabigjoe.obsidianAPI.render.bend.PartUVMap;
import com.dabigjoe.obsidianAPI.render.wavefront.Face;
import com.dabigjoe.obsidianAPI.render.wavefront.TextureCoordinate;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;
import com.dabigjoe.obsidianAnimator.render.entity.ModelObj_Animator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class BendPart_Animator extends BendPart
{
    public BendPart_Animator(Vertex[] topVertices, Vertex[] bottomVertices, PartUVMap uvMap, boolean inverted)
    {
        super(topVertices, bottomVertices, uvMap, inverted);
    }

    /**
     * Change the texture coordinates and texture if the part is highlighted.
     */
    @Override
    public void updateTextureCoordinates(Entity entity, boolean mainHighlight, boolean otherHighlight, ModelObj modelObj)
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
            texture = modelObj.getTexture(entity);
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
