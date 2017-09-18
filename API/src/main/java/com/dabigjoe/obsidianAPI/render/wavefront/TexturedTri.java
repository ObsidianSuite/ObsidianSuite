package com.dabigjoe.obsidianAPI.render.wavefront;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TexturedTri
{
    public PositionTextureVertex[] vertexPositions;

    public TexturedTri(PositionTextureVertex[] vertices)
    {
        this.vertexPositions = vertices;
    }

    /**
     * Draw this primitve. This is typically called only once as the generated drawing instructions are saved by the
     * renderer and reused later.
     */
    @SideOnly(Side.CLIENT)
    public void draw(BufferBuilder renderer, float scale)
    {	
        Vec3d vec3d = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
        Vec3d vec3d1 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D);
        Vec3d vec3d2 = vec3d1.crossProduct(vec3d).normalize();
        float f = (float)vec3d2.x;
        float f1 = (float)vec3d2.y;
        float f2 = (float)vec3d2.z;
        
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        for (int i = 0; i < 3; ++i)
        {
            PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
            renderer.pos(positiontexturevertex.vector3D.x * (double)scale, positiontexturevertex.vector3D.y * (double)scale, positiontexturevertex.vector3D.z * (double)scale).tex((double)positiontexturevertex.texturePositionX, (double)positiontexturevertex.texturePositionY).endVertex();
        }
        renderer.pos(this.vertexPositions[0].vector3D.x * (double)scale, this.vertexPositions[0].vector3D.y * (double)scale, this.vertexPositions[0].vector3D.z * (double)scale).endVertex();
        
        Tessellator.getInstance().draw();
    }
}