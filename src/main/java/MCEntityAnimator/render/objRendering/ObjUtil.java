package MCEntityAnimator.render.objRendering;

import java.util.ArrayList;

import net.minecraftforge.client.model.obj.GroupObject;

public class ObjUtil 
{
		
	public static ArrayList<PartObj> createPartObjList(ModelObj model, ArrayList<GroupObject> groupObjects)
	{
		ArrayList<PartObj> parts = new ArrayList<PartObj>();
		for(GroupObject gObj : groupObjects)
		{
			parts.add(new PartObj(model, gObj));
		}
		return parts;
	}
	
}
