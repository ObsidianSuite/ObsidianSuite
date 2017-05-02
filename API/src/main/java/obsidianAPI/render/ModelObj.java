package obsidianAPI.render;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;
import obsidianAPI.animation.AnimationParenting;
import obsidianAPI.animation.PartGroups;
import obsidianAPI.file.PartData;
import obsidianAPI.file.importer.FileLoader;
import obsidianAPI.render.bend.Bend;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartEntityPos;
import obsidianAPI.render.part.PartObj;
import obsidianAPI.render.part.prop.PartPropRotation;
import obsidianAPI.render.part.prop.PartPropScale;
import obsidianAPI.render.part.prop.PartPropTranslation;

public class ModelObj extends ModelBase
{

	private final ResourceLocation texture;
	public final WavefrontObject obj;
	public final String entityName;
	public ArrayList<Part> parts;
	protected ArrayList<Bend> bends = new ArrayList<Bend>();
	public PartGroups partGroups;
	private Map<PartObj, float[]> defaults;

	public static final float initRotFix = 180.0F;
	public static final float offsetFixY = -1.5F;
	
	public ModelObj(String entityName, ResourceLocation objRes, ResourceLocation texture)
	{
		this(entityName, FileLoader.readObj(objRes), texture);
	}
	
	public ModelObj(String entityName, WavefrontObject obj,  ResourceLocation texture)
	{
		this.entityName = entityName;
		this.obj = obj;
		this.texture = texture;
		defaults = Maps.newHashMap();
		loadObj(obj);
		init();
	}

	public ResourceLocation getTexture()
	{
		return texture;
	}
	
	public void loadSetup(InputStream stream)
	{
		try {
			NBTTagCompound nbt = CompressedStreamTools.read(new DataInputStream(stream));
			AnimationParenting.loadData(nbt.getCompoundTag("Parenting"), this);
			partGroups.loadData(nbt.getCompoundTag("Groups"), this);
			PartData.fromNBT(nbt.getCompoundTag("Setup"), this);
		}
		catch (Exception e) {System.err.println("Unable to load model nbt for " + entityName);}
	}
	
	
	public NBTTagCompound createNBTTag()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Parenting", AnimationParenting.getSaveData(this));
		nbt.setTag("Groups", partGroups.getSaveData());
		nbt.setTag("Setup", PartData.toNBT(this));
		return nbt;
	}

	public void init() 
	{
		for(Part p : this.parts)
		{
			if(p instanceof PartObj)
			{
				PartObj obj = (PartObj) p;
				float[] arr = new float[3];
				arr[0] = obj.getRotationPoint(0);
				arr[1] = obj.getRotationPoint(1);
				arr[2] = obj.getRotationPoint(2);
				defaults.put(obj, arr);
			}
		}		
	}

	public float[] getDefaults(PartObj part)
	{
		return defaults.get(part).clone();
	}
	
	private void loadObj(WavefrontObject model)
	{
		parts = createPartObjList(model.groupObjects);
		parts.add(new PartEntityPos(this));
		if(entityName.equals("player"))
		{
			parts.add(new PartPropRotation(this));
			parts.add(new PartPropTranslation(this));
			parts.add(new PartPropScale(this));
		}
		partGroups = new PartGroups(this);
	}

	//----------------------------------------------------------------
	//				     Parts and Groups
	//----------------------------------------------------------------

	public List<PartObj> getPartObjs()
	{
		List<PartObj> partObjs = new ArrayList<PartObj>();
		for(Part part : parts)
		{
			if(part instanceof PartObj)
				partObjs.add((PartObj) part);
		}
		return partObjs;
	}

	public String getPartOrderAsString()
	{
		String s = "";
		for(PartObj p : getPartObjs())
		{
			s = s + p.getName() + ",";
		}
		s = s.substring(0, s.length() - 1);
		return s;
	}

	public void setPartOrderFromString(String order)
	{
		ArrayList<Part> newPartList = new ArrayList<Part>();
		for(String partName : order.split(","))
		{
			newPartList.add(getPartFromName(partName));
		}
		for(Part part : parts)
		{
			if(!newPartList.contains(part))
				newPartList.add(part);
		}
		parts = newPartList;
	}

	//----------------------------------------------------------------
	//						Parenting
	//----------------------------------------------------------------

	public void setParent(PartObj child, @Nullable PartObj parent, boolean addBend)
	{
		if (addBend)
		{
			for (Part p : parts)
			{
				if (p instanceof PartObj)
				{
					PartObj obj = (PartObj) p;
					obj.updateTextureCoordinates(false, false, false);
				}
			}

			if (!child.hasBend())
			{
				Bend b = createBend(parent, child);
				bends.add(b);
				child.setBend(b);
			}
		}

		if (child.hasParent())
			child.getParent().removeChild(child);

		child.setParent(parent);
		if (parent != null)
		{
			parent.addChild(child);
		} else
		{
			child.removeBend();
		}
	}

	protected Bend createBend(PartObj parent, PartObj child)
	{
		return new Bend(parent, child);
	}

	public void removeBend(Bend bend)
	{
		bends.remove(bend);
	}

	//----------------------------------------------------------------
	//							Rotation
	//----------------------------------------------------------------

	public void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) 
	{				
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

	//----------------------------------------------------------------
	//							Rendering
	//----------------------------------------------------------------

	@Override
	public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale) 
	{		
		super.render(entity, time, distance, loop, lookY, lookX, scale);

		setRotationAngles(time, distance, loop, lookY, lookX, scale, entity);
		
		GL11.glPushMatrix();
		GL11.glRotatef(initRotFix, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, offsetFixY, 0.0F);

		
		for(Part p : this.parts) 
		{
			if(p instanceof PartObj)
			{
				PartObj part = (PartObj) p;
				if(!part.hasParent())
					part.render();
			}
			//TODO entity movement via PartEntityPos
//			else if(p instanceof PartEntityPos)
//				((PartEntityPos) p).move(entity);
		}

		for (Bend bend : bends)
		{
			bend.render();
		}

		GL11.glPopMatrix();
	}

	//----------------------------------------------------------------
	//							Utils
	//----------------------------------------------------------------

	public ArrayList<Part> createPartObjList(ArrayList<GroupObject> groupObjects)
	{
		ArrayList<Part> parts = new ArrayList<Part>();
		for(GroupObject gObj : groupObjects)
			parts.add(createPart(gObj));
		return parts;
	}

	protected PartObj createPart(GroupObject group)
	{
		return new PartObj(this, group);
	}
	
	public Part getPartFromName(String name) 
	{
		for(Part part : parts)
		{
			if(part.getName().equals(name) || part.getDisplayName().equals(name))
			{
				return part;
			}
		}
		throw new RuntimeException("No part found for '" + name + "'");
	}
	
	public PartObj getPartObjFromName(String name) 
	{
		for(Part p : parts)
		{
			if(p instanceof PartObj)
			{
				PartObj part = (PartObj) p;
				if(part.getName().equals(name) || part.getDisplayName().equals(name))
					return part;
			}
		}
		throw new RuntimeException("No part obj found for " + name);
	}

}