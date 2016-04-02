package MCEntityAnimator.render.objRendering;

import java.util.ArrayList;

import net.minecraftforge.client.model.obj.GroupObject;
import MCEntityAnimator.render.objRendering.parts.Part;
import MCEntityAnimator.render.objRendering.parts.PartObj;

public class ObjUtil 
{
		
	public static ArrayList<Part> createPartObjList(ModelObj model, ArrayList<GroupObject> groupObjects)
	{
		ArrayList<Part> parts = new ArrayList<Part>();
		for(GroupObject gObj : groupObjects)
		{
			parts.add(new PartObj(model, gObj));
		}
		return parts;
	}
	
}
