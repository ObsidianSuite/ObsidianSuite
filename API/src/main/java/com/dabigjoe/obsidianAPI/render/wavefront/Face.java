package com.dabigjoe.obsidianAPI.render.wavefront;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Face
{
    public Vertex[] vertices;
    public Vertex[] vertexNormals;
    public Vertex faceNormal;
    public TextureCoordinate[] textureCoordinates;
    
    @SideOnly(Side.CLIENT)
    public void render(BufferBuilder renderer) {
        int numVertices = vertices.length;
        if(faceNormal == null)
        	faceNormal = calculateFaceNormal();
        
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int i = 0; i < numVertices; ++i) {
    		Vertex v = vertices[i];
    		TextureCoordinate tc = textureCoordinates[i];
            renderer.pos(v.x, v.y, v.z).tex(tc.u, tc.v).normal(faceNormal.x, faceNormal.y, faceNormal.z).endVertex();
        }
        //Renderer works with four vertices so add start point again if face is only three vertices.
        if(numVertices == 3)
        	renderer.pos(vertices[0].x, vertices[0].y, vertices[0].z).tex(textureCoordinates[0].u, textureCoordinates[0].v).normal(faceNormal.x, faceNormal.y, faceNormal.z).endVertex();
        
        Tessellator.getInstance().draw();
    }

    public Vertex calculateFaceNormal() {
        Vec3d v1 = new Vec3d(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
        Vec3d v2 = new Vec3d(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);
        Vec3d normalVector = null;

        normalVector = v1.crossProduct(v2).normalize();

        return new Vertex((float) normalVector.x, (float) normalVector.y, (float) normalVector.z);
    }
}