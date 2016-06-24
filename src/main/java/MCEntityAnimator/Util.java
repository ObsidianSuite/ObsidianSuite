package MCEntityAnimator;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import MCEntityAnimator.render.objRendering.parts.Part;
import MCEntityAnimator.render.objRendering.parts.PartObj;

public class Util 
{
	
	/**
	 * Used for rendering custom Gui sizes, args: posX, posY, width, height, zLevel
	 **/
	public static void drawCustomGui(double x, double y, double width, double height, double zLevel)
	{
		Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, 0,1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, 1,0);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, 0, 0);
        tessellator.draw();
	}
	
	//rotationChange in degrees
	public static float calculateSwing(float rotationChange, float timeChange)
	{
		return rotationChange/timeChange;
	}
	
//	public static ArrayList<ModelRenderer> convertBoxListToModelRendererArray(List boxList) 
//	{
//		ArrayList<ModelRenderer> mrs = new ArrayList<ModelRenderer>();
//		for(int i = 0; i < boxList.size(); i++)
//		{
//			mrs.add((ModelRenderer) boxList.get(i));
//		}
//		return mrs;
//	}
	
	public static String getDisplayName(String partName, ArrayList<Part> parts) 
	{
		Part p = getPartFromName(partName, parts);
		if(p != null)
			return p.getDisplayName();
		return partName;
	}

	public static Part getPartFromName(String name, ArrayList<Part> parts) 
	{
		for(Part part : parts)
		{
			if(part.getName().equals(name))
			{
				return part;
			}
		}
		return null;
	}
	
	public static PartObj getPartObjFromName(String name, ArrayList<Part> parts) 
	{
		for(Part p : parts)
		{
			if(p instanceof PartObj)
			{
				PartObj part = (PartObj) p;
				if(part.getName().equals(name) || part.getDisplayName().equals(name))
				{
					return part;
				}
			}
		}
		return null;
	}
	
	public static GuiButton getButtonFromID(int id, List buttonList)
	{
		for(Object obj : buttonList)
		{
			if(obj instanceof GuiButton)
			{
				GuiButton b = (GuiButton) obj;
				if(b.id == id)
				{
					return b;
				}
			}
		}
		return null;
	}

	public static ArrayList<PartObj> removeDuplicates(ArrayList<PartObj> mrs) 
	{
		ArrayList<PartObj> temp = new ArrayList<PartObj>();
		for(PartObj mr : mrs)
		{
			if(!temp.contains(mr))
			{
				temp.add(mr);
			}
		}
		return temp;
	}
	
	public static float[] getEntityPosition(Entity e)
	{
		float[] pos = new float[3];
		pos[0] = (float) e.posX;
		pos[1] = (float) e.posY;
		pos[2] = (float) e.posZ;
		return pos;
	}
	
	public void checkForUpdate()
	{
		
	}
	
}
