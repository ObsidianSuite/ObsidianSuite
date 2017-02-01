package obsidianAnimator.render.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;

public class ModelObj_Animator extends ModelObj
{

	private PartObj mainHighlight = null;
	private List<PartObj> hightlightedParts;

	public static final ResourceLocation pinkResLoc = new ResourceLocation("mod_obsidian_animator:defaultModelTextures/pink.png");
	public static final ResourceLocation whiteResLoc = new ResourceLocation("mod_obsidian_animator:defaultModelTextures/white.png");

	public ModelObj_Animator(String entityName, File modelFile, ResourceLocation texture) throws FileNotFoundException
	{			
		super(entityName, new FileInputStream(modelFile), texture);
		hightlightedParts = new ArrayList<PartObj>();
	}

	//----------------------------------------------------------------
	// 							 Selection
	//----------------------------------------------------------------

	public PartObj testRay()
	{
		PartObj closestPart = null;
		Double min = null;
		for(Part part : parts)
		{
			if(part instanceof PartObj_Animator)
			{
				PartObj_Animator p = (PartObj_Animator) part;
				Double d = p.testRay();
				if(d != null && (min == null || d < min))
				{
					closestPart = p;
					min = d;
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