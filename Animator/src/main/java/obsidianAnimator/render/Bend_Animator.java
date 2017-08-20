package obsidianAnimator.render;

import net.minecraft.entity.Entity;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.Vertex;
import obsidianAPI.render.bend.Bend;
import obsidianAPI.render.bend.BendPart;
import obsidianAPI.render.bend.PartUVMap;
import obsidianAPI.render.part.PartObj;
import obsidianAnimator.render.entity.ModelObj_Animator;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class Bend_Animator extends Bend
{
    public Bend_Animator(PartObj parent, PartObj child)
    {
        super(parent, child);
    }

    @Override
    protected BendPart createBendPart(Vertex[] topVertices, Vertex[] bottomVertices, PartUVMap uvMap, boolean inverted)
    {
        return new BendPart_Animator(topVertices, bottomVertices, uvMap, inverted);
    }

    private List<BendPart> getParentBendParts()
    {
        List<BendPart> parentBendParts = new ArrayList<BendPart>();
        for (int i = 0; i < bendSplit / 2; i++)
            parentBendParts.add(bendParts.get(i));
        return parentBendParts;
    }

    private List<BendPart> getChildBendParts()
    {
        List<BendPart> childBendParts = new ArrayList<BendPart>();
        for (int i = bendSplit / 2; i < bendSplit; i++)
            childBendParts.add(bendParts.get(i));
        return childBendParts;
    }

    /**
     * Test to see if a ray insects with the parent part of the bend.
     */
    public Double testRayParent()
    {
        GL11.glPushMatrix();
        move();
        Double t = testRay(RayTrace.getRayTrace(), getParentBendParts());
        GL11.glPopMatrix();
        return t;
    }

    /**
     * Test to see if a ray insects with the child part of the bend.
     */
    public Double testRayChild()
    {
        GL11.glPushMatrix();
        move();
        Double t = testRay(RayTrace.getRayTrace(), getChildBendParts());
        GL11.glPopMatrix();
        return t;
    }

    /**
     * Test to see if a ray insects with the parts of the bend.
     *
     * @return - Minimum distance from p0 to part, null if no intersect exists.
     */
    private Double testRay(RayTrace ray, List<BendPart> bendParts)
    {
        Double min = null;
        for (BendPart bendPart : bendParts)
        {
            for (Face f : bendPart.faces)
            {
                Double d = MathHelper.rayIntersectsFace(ray, f);
                if (d != null && (min == null || d < min))
                    min = d;
            }
        }
        return min;
    }

    @Override
    public void render(Entity entity)
    {
        GL11.glPushMatrix();

        move();

        //Actually render all the bend parts.
        for (int i = 0; i < bendSplit; i++)
        {
            BendPart part = bendParts.get(i);
            boolean mainHighlight = false;
            boolean otherHighlight = false;
            if (parent.modelObj instanceof ModelObj_Animator)
            {
                ModelObj_Animator parentModel = (ModelObj_Animator) parent.modelObj;
                ModelObj_Animator childModel = (ModelObj_Animator) child.modelObj;
                mainHighlight = i < bendSplit / 2 ? parentModel.isMainHighlight(parent) : childModel.isMainHighlight(child);
                otherHighlight = i < bendSplit / 2 ? parentModel.isPartHighlighted(parent) : childModel.isPartHighlighted(child);
            }
            part.updateTextureCoordinates(entity, mainHighlight, otherHighlight, parent.modelObj);
            part.render();
        }
        GL11.glPopMatrix();
    }
}
