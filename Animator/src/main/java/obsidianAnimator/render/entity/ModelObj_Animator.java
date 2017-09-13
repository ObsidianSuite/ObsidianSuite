package obsidianAnimator.render.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.bend.Bend;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;
import obsidianAPI.render.wavefront.GroupObject;
import obsidianAPI.render.wavefront.WavefrontObject;
import obsidianAnimator.render.Bend_Animator;

public class ModelObj_Animator extends ModelObj
{

	private PartObj mainHighlight = null;
	private List<PartObj> hightlightedParts;

	public static final ResourceLocation pinkResLoc = new ResourceLocation("mod_obsidian_animator:defaultModelTextures/pink.png");
	public static final ResourceLocation whiteResLoc = new ResourceLocation("mod_obsidian_animator:defaultModelTextures/white.png");
	
	public ModelObj_Animator(String entityName, WavefrontObject objRes, ResourceLocation texture)
	{			
		super(entityName, objRes, texture);
		hightlightedParts = new ArrayList<PartObj>();
	} 


	@Override
	protected PartObj createPart(GroupObject group)
	{
		return new PartObj_Animator(this, group);
	}

	@Override
	protected Bend createBend(PartObj parent, PartObj child)
	{
		return new Bend_Animator(parent, child);
	}

	//----------------------------------------------------------------
	// 							 Selection
	//----------------------------------------------------------------

	public PartObj testRay()
	{
		PartObj closestPart = null;
		Double min = null;
		synchronized ( parts )
		{
			for (Part part : parts)
			{
				if (part instanceof PartObj_Animator)
				{
					PartObj_Animator p = (PartObj_Animator) part;
					Double d = p.testRay();
					if (d != null && (min == null || d < min))
					{
						closestPart = p;
						min = d;
					}
				}
			}
		}
		for (Bend bend : bends)
		{
			if (bend instanceof Bend_Animator)
			{
				Bend_Animator b = (Bend_Animator) bend;

				Double d = b.testRayChild();
				if (d != null && (min == null || d < min))
				{
					closestPart = bend.child;
					min = d;
				}
				Double d2 = b.testRayParent();
				if (d2 != null && (min == null || d2 < min))
				{
					closestPart = bend.parent;
					min = d2;
				}
			}
		}

		return closestPart;
	}

	//----------------------------------------------------------------
	// 							Highlighting
	//----------------------------------------------------------------

	/**
	 * Highlight a part.
	 * @param part - Part to highlight.
	 * @param main - True if main highlight (pink).
	 */
	public void hightlightPart(PartObj part, boolean main)
	{
		if(part != null)
		{
			if(main)
				mainHighlight = part;
			else
				hightlightedParts.add(part);
		}
	}

	public void clearHighlights()
	{
		this.hightlightedParts.clear();
		this.mainHighlight = null;
	}

	public boolean isMainHighlight(PartObj partObj) 
	{
		return mainHighlight == partObj;
	}

	/**
	 * Highlighted but not main highlight (white).
	 */
	public boolean isPartHighlighted(PartObj partObj) 
	{
		return  hightlightedParts.contains(partObj);
	}
}