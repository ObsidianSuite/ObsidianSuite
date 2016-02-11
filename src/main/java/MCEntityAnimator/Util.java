package MCEntityAnimator;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import MCEntityAnimator.render.objRendering.PartObj;

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

	public static PartObj getPartFromName(String name, ArrayList<PartObj> parts) 
	{
		for(PartObj part : parts)
		{
			if(part.getName().equals(name))
			{
				return part;
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
	
//	public static EntityPrototype getEntityFromName(String entityName)
//	{
//		EntityPrototype entity = null;
//		try 
//		{
//			entity = (EntityPrototype) Class.forName("MCEntityAnimator.entity.EntityPrototype").getConstructor(World.class).newInstance(Minecraft.getMinecraft().theWorld);
//		} 
//		catch (InstantiationException e) {e.printStackTrace();} 
//		catch (IllegalAccessException e) {e.printStackTrace();}
//		catch (IllegalArgumentException e) {e.printStackTrace();} 
//		catch (InvocationTargetException e) {e.printStackTrace();} 
//		catch (NoSuchMethodException e) {e.printStackTrace();} 
//		catch (SecurityException e) {e.printStackTrace();} 
//		catch (ClassNotFoundException e) {e.printStackTrace();}
//		return entity;
//	}
	
}
