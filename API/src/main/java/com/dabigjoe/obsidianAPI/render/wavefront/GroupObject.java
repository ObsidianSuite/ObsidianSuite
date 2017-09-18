package com.dabigjoe.obsidianAPI.render.wavefront;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GroupObject
{
    public String name;
    public ArrayList<Face> faces = new ArrayList<Face>();
    public int glDrawingMode;

    public GroupObject()
    {
        this("");
    }

    public GroupObject(String name)
    {
        this(name, -1);
    }

    public GroupObject(String name, int glDrawingMode)
    {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    @SideOnly(Side.CLIENT)
    public void render()
    {
        if (faces.size() > 0)
        {
            Tessellator tessellator = Tessellator.getInstance();
            render(tessellator.getBuffer());
            //tessellator.draw();
        }
    }

    @SideOnly(Side.CLIENT)
    public void render(BufferBuilder renderer)
    {
        if (faces.size() > 0)
        {
            for (Face face : faces)
            {
                face.render(renderer);
            }
        }
    }
}