package obsidianAPI.render.wavefront;

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
    private boolean invertNormal;

    public TexturedTri(PositionTextureVertex[] vertices)
    {
        this.vertexPositions = vertices;
    }

    public void flipFace()
    {
        PositionTextureVertex[] apositiontexturevertex = new PositionTextureVertex[this.vertexPositions.length];

        for (int i = 0; i < this.vertexPositions.length; ++i)
        {
            apositiontexturevertex[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
        }

        this.vertexPositions = apositiontexturevertex;
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

        if (this.invertNormal)
        {
            f = -f;
            f1 = -f1;
            f2 = -f2;
        }

        renderer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

        for (int i = 0; i < 3; ++i)
        {
            PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
            renderer.pos(positiontexturevertex.vector3D.x * (double)scale, positiontexturevertex.vector3D.y * (double)scale, positiontexturevertex.vector3D.z * (double)scale).tex((double)positiontexturevertex.texturePositionX, (double)positiontexturevertex.texturePositionY).normal(f, f1, f2).endVertex();
        }

        Tessellator.getInstance().draw();
    }
}