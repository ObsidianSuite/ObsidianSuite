package obsidianAPI.render;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
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

public class ModelObj extends ModelBase {

	public final String entityName;
	public final WavefrontObject obj;
	private ResourceLocation texture;
	public List<Part> parts;
	protected List<Bend> bends = new ArrayList<Bend>();
	public PartGroups partGroups;
	private Map<PartObj, float[]> defaults;

	public static final float initRotFix = 180.0F;
	public static final float offsetFixY = -1.5F;

	public ModelObj(String entityName, WavefrontObject obj, ResourceLocation texture) {
		this.entityName = entityName;
		this.obj = obj;
		this.texture = texture;
		defaults = Maps.newHashMap();
		loadObj(obj);
		init();
	}

	public ResourceLocation getTexture(Entity entity) {
		return texture;
	}
	
	public void setTexture(ResourceLocation texture) {
		this.texture = texture;
	}

	public void loadSetup(InputStream stream) {
		try {
			NBTTagCompound nbt = CompressedStreamTools.read(new DataInputStream(stream));
			AnimationParenting.loadData(nbt.getCompoundTag("Parenting"), this);
			partGroups.loadData(nbt.getCompoundTag("Groups"), this);
			PartData.fromNBT(nbt.getCompoundTag("Setup"), this);
			runMerge();
		} catch (Exception e) {
			System.err.println("Unable to load model nbt for " + entityName);
		}
	}

	public NBTTagCompound createNBTTag() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Parenting", AnimationParenting.getSaveData(this));
		nbt.setTag("Groups", partGroups.getSaveData());
		nbt.setTag("Setup", PartData.toNBT(this));
		return nbt;
	}

	public void init() {
		for (Part p : this.parts) {
			if (p instanceof PartObj) {
				PartObj obj = (PartObj) p;
				float[] arr = new float[3];
				arr[0] = obj.getRotationPoint(0);
				arr[1] = obj.getRotationPoint(1);
				arr[2] = obj.getRotationPoint(2);
				defaults.put(obj, arr);
			}
		}
	}

	public float[] getDefaults(PartObj part) {
		return defaults.get(part).clone();
	}

	private void loadObj(WavefrontObject model) {
		parts = createPartObjList(model.groupObjects);
		parts.add(new PartEntityPos(this));
		if (entityName.equals("ObsidianPlayer")) {
			parts.add(new PartPropRotation(this));
			parts.add(new PartPropTranslation(this));
			parts.add(new PartPropScale(this));

			parts.add(new PartPropRotation(this,"prop_rot_l"));
			parts.add(new PartPropTranslation(this, "prop_trans_l"));
			parts.add(new PartPropScale(this, "prop_scale_l"));
		}
		partGroups = new PartGroups(this);
	}

	// ----------------------------------------------------------------
	// Parts and Groups
	// ----------------------------------------------------------------

	public List<PartObj> getPartObjs() {
		List<PartObj> partObjs = new ArrayList<PartObj>();
		for (Part part : parts) {
			if (part instanceof PartObj)
				partObjs.add((PartObj) part);
		}
		return partObjs;
	}

	public NBTTagList getPartOrderAsList()
	{
		NBTTagList list = new NBTTagList();
		for(PartObj p : getPartObjs())
		{
			list.appendTag(new NBTTagString(p.getName()));
		}
		return list;
	}

	public void setPartOrderFromList(NBTTagList order)
	{
		ArrayList<Part> newPartList = new ArrayList<Part>();
		for (int i = 0; i < order.tagCount(); i++)
		{
			newPartList.add(getPartFromName(order.getStringTagAt(i)));
		}
		for(Part part : parts)
		{
			if(!newPartList.contains(part))
				newPartList.add(part);
		}
		parts = newPartList;
	}

	// ----------------------------------------------------------------
	// Parenting
	// ----------------------------------------------------------------

	public void setParent(PartObj child, @Nullable PartObj parent, boolean addBend) {
		
		if (addBend) {
			for (Part p : parts) {
				if (p instanceof PartObj) {
					PartObj obj = (PartObj) p;
					obj.updateTextureCoordinates(null, false, false, false);
				}
			}

			if (!child.hasBend()) {
				Bend b = createBend(parent, child);
				bends.add(b);
				child.setBend(b);
			}
		}

		if (child.hasParent())
			child.getParent().removeChild(child);

		child.setParent(parent);
		if (parent != null) {
			parent.addChild(child);
		} else {
			child.removeBend();
		}
	}
	
	private void removeParenting(PartObj child) {
		PartObj parent = child.getParent();
		if(parent != null) {
			parent.getChildren().remove(child);
			setParent(child, null, false);
		}
	}

	protected Bend createBend(PartObj parent, PartObj child) {
		return new Bend(parent, child);
	}

	public void removeBend(Bend bend) {
		bends.remove(bend);
	}

	public void runMerge() {
		fixMergeParenting();
		for (PartObj topParent : getTopParents())
			merge(topParent);
	}

	private void merge(PartObj part) {
		//Run on children first.
		for(PartObj child : part.getChildren()) {
			if (!child.getChildren().isEmpty())
				merge(child);
		}
		
		/** Merge any unmerged parts **/
		//Caching part obj list
		List<PartObj> currentPartObjs = getPartObjs(); 
		//List of parts merged into parts that are going to be merge. 
		//They need copying onto the parent.
		List<PartObj> mergedPartsToCopy = new ArrayList<PartObj>(); //
		for (PartObj mergedPart : part.getMergedParts()) {
			//Part that is still in part list hasn't actually been merged yet
			// so merge it here. 
			if(currentPartObjs.contains(mergedPart)) {
				merge(part, mergedPart);
				mergedPartsToCopy.addAll(mergedPart.getMergedParts());
			}
		}
		//Copy merged parts over to parent
		for(PartObj mergedPart : mergedPartsToCopy)
			part.addMergedPart(mergedPart);
	}

	/**
	 * Adjust part parenting so parts that are parented to parts that will be
	 * merged are parented to the parts their old parents will merge into.
	 */
	private void fixMergeParenting() {
		for (PartObj part : getPartObjs()) {
			if (part.isMerged() || !part.hasParent())
				continue;
			PartObj parent = part.getParent();
			while (parent.isMerged()) {
				parent = parent.getParent();
			}
			setParent(part, parent, false);
		}
	}

	private List<PartObj> getTopParents() {
		List<PartObj> topParents = new ArrayList<PartObj>();
		for (PartObj part : getPartObjs()) {
			if (!part.hasParent())
				topParents.add(part);
		}
		return topParents;
	}

	public void addMerge(PartObj part, PartObj partToMerge) {
		if(partToMerge.getParent() != part)
			setParent(partToMerge, part, false);
		part.addMergedPart(partToMerge);
	}
	
	private void merge(PartObj part, PartObj partToMerge) {
		part.addFacesFromPart(partToMerge);
		removeParenting(partToMerge);
		parts.remove(partToMerge);
	}

	// ----------------------------------------------------------------
	// Rotation
	// ----------------------------------------------------------------

	public void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

	// ----------------------------------------------------------------
	// Rendering
	// ----------------------------------------------------------------

	@Override
	public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.render(entity, time, distance, loop, lookY, lookX, scale);

		setRotationAngles(time, distance, loop, lookY, lookX, scale, entity);

		GL11.glPushMatrix();
		GL11.glRotatef(initRotFix, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, offsetFixY, 0.0F);

		for (Part p : this.parts) {
			if (p instanceof PartObj) {
				PartObj part = (PartObj) p;
				if (!part.hasParent())
					part.render(entity);
			}
			// TODO entity movement via PartEntityPos
			// else if(p instanceof PartEntityPos)
			// ((PartEntityPos) p).move(entity);
		}

		for (Bend bend : bends) {
			bend.render(entity);
		}

		GL11.glPopMatrix();
	}

	// ----------------------------------------------------------------
	// Utils
	// ----------------------------------------------------------------

	public ArrayList<Part> createPartObjList(ArrayList<GroupObject> groupObjects) {
		ArrayList<Part> parts = new ArrayList<Part>();
		for (GroupObject gObj : groupObjects)
			parts.add(createPart(gObj));
		return parts;
	}

	protected PartObj createPart(GroupObject group) {
		return new PartObj(this, group);
	}

	public Part getPartFromName(String name) {
		for (Part part : parts) {
			if (part.getName().equals(name) || part.getDisplayName().equals(name)) {
				return part;
			}
		}
		throw new RuntimeException("No part found for '" + name + "' for entity " + entityName);
	}

	public PartObj getPartObjFromName(String name) {
		for (Part p : parts) {
			if (p instanceof PartObj) {
				PartObj part = (PartObj) p;
				if (part.getName().equals(name) || part.getDisplayName().equals(name))
					return part;
			}
		}
		throw new RuntimeException("No part obj found for " + name);
	}

}