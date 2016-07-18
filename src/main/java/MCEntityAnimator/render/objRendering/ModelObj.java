package MCEntityAnimator.render.objRendering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import MCEntityAnimator.MCEA_Main;
import MCEntityAnimator.Util;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationParenting;
import MCEntityAnimator.animation.PartGroupsAndNames;
import MCEntityAnimator.render.objRendering.bend.Bend;
import MCEntityAnimator.render.objRendering.parts.Part;
import MCEntityAnimator.render.objRendering.parts.PartEntityPos;
import MCEntityAnimator.render.objRendering.parts.PartObj;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;

public class ModelObj extends ModelBase
{

	private String entityType;
	public WavefrontObject model;
	public ArrayList<Part> parts;
	private ArrayList<Bend> bends;
	private AnimationParenting parenting;
	public PartGroupsAndNames groupsAndNames;
	private Map<PartObj, float[]> defaults;

	private PartObj mainHighlight = null;
	private ArrayList<PartObj> hightlightedParts;

	public static final float initRotFix = 180.0F;
	public static final float offsetFixY = -1.5F;

	private final ResourceLocation txtRL;
	private final File pxyFile;

	public final boolean renderWithTexture;

	private boolean partSetupComplete;

	public ModelObj(String par0Str)
	{	
		entityType = par0Str;
		File modelFile = new File(MCEA_Main.animationPath + "/data/shared/" + entityType + "/" + entityType + ".obj");
		pxyFile = new File(MCEA_Main.animationPath + "/data/shared/" + entityType + "/" + entityType + ".pxy");
		
		
		File textureFile = new File(MCEA_Main.animationPath + "/data/shared/" + entityType + "/" + entityType + ".png");
		renderWithTexture = textureFile.exists();
		txtRL = new ResourceLocation("animation:data/shared/" + entityType + "/" + entityType + ".png");


		try 
		{
			model = new WavefrontObject(entityType + ".obj", new FileInputStream(modelFile));
		}
		catch (ModelFormatException e) {e.printStackTrace();} 
		catch (FileNotFoundException e) {e.printStackTrace();}

		parts = createPartObjList(this, model.groupObjects);
		parts.add(new PartEntityPos(this));
		parts.add(new Part(this, "prop_rot"));
		parts.add(new Part(this, "prop_trans"));
		parenting = AnimationData.getAnipar(par0Str);
		hightlightedParts = new ArrayList<PartObj>();
		bends = new ArrayList<Bend>();
		defaults = Maps.newHashMap();
		loadFromFile();
		partSetupComplete = true;
		groupsAndNames = AnimationData.getPartGroupsAndNames(entityType, this);

		init();
	}

	public ResourceLocation getTexture()
	{
		return txtRL;
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

	public String getEntityType()
	{
		return entityType;
	}

	private void loadFromFile() 
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(pxyFile));
			String currentLine;
			PartObj currentPart = null;
			int i = 0;
			boolean readingRotation = true;

			while ((currentLine = reader.readLine()) != null)
			{
				if(currentLine.equals("PART SETUP"))
					readingRotation = false;

				if(readingRotation)
				{
					switch(i)
					{
					case 0: 
						for(Part p : parts)
						{
							if(p instanceof PartObj)
							{
								PartObj obj = (PartObj) p;
								if(obj.getName().equals(currentLine))
								{
									currentPart = obj;
									break;
								}
							}
						}
						i++;
						break;
					case 1:
						currentPart.setRotationPoint(read3Floats(currentLine));
						i++;
						break;
					case 2:
						float[] rot = read3Floats(currentLine);
						currentPart.setOriginalValues(rot);
						currentPart.setValues(rot);
						i = 0;
						break;
					}
				}
			}
			reader.close();
		}
		catch (IOException e)
		{
			throw new ModelFormatException("IO Exception reading model format", e);
		}
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
			newPartList.add(Util.getPartFromName(partName, parts));
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

	public void setParent(PartObj child, PartObj parent, boolean addBend) throws Exception
	{
		if(addBend)
		{			
			for(Part p : this.parts)
			{
				if(p instanceof PartObj)
				{
					PartObj obj = (PartObj) p;
					obj.updateTextureCoordinates(false, false);
				}
			}

			if(!child.hasBend())
			{
				Bend b = new Bend(parent, child);
				bends.add(b);
				child.setBend(b);
			}
		}
		parenting.addParenting(parent, child);
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
	// 							Highlighting
	//----------------------------------------------------------------

	/**
	 * Add a part to be highlighted
	 */
	public void hightlightPart(PartObj part)
	{
		if(part != null)
		{
			this.hightlightedParts.add(part);
		}
	}

	public void clearHighlights()
	{
		this.hightlightedParts.clear();
		this.mainHighlight = null;
	}

	public boolean isPartHighlighted(PartObj partObj) 
	{
		return mainHighlight == partObj || hightlightedParts.contains(partObj);
	}


	public boolean isMainHighlight(PartObj partObj) 
	{
		return mainHighlight == partObj;
	}

	//----------------------------------------------------------------
	//							Rendering
	//----------------------------------------------------------------

	@Override
	public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale) 
	{		
		super.render(entity, time, distance, loop, lookY, lookX, scale);

		GL11.glPushMatrix();
		GL11.glRotatef(initRotFix, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, offsetFixY, 0.0F);

		for(Part p : this.parts) 
		{
			if(p instanceof PartObj)
			{
				PartObj part = (PartObj) p;
				if(!parenting.hasParent(part))
				{
					part.render(entity, isPartHighlighted(part), isMainHighlight(part));
				}
			}
			else
				p.move(entity);
		}

		for(Bend bend : this.bends)
		{
			bend.render();
		}

		GL11.glPopMatrix();

		//TODO rendering with different textures - for highlighting parts but also for rendering with actual textures.
	}

	//----------------------------------------------------------------
	//							Utils
	//----------------------------------------------------------------

	private static float[] read3Floats(String str)
	{
		float[] arr = new float[3];
		for(int i = 0; i < 3; i++)
		{
			if(str.contains(","))
			{
				arr[i] = Float.parseFloat(str.substring(0, str.indexOf(",")));
				str = str.substring(str.indexOf(",") + 2);
			}
			else
			{
				arr[i] = Float.parseFloat(str);
			}
		}
		return arr;
	}

	public ArrayList<Part> createPartObjList(ModelObj model, ArrayList<GroupObject> groupObjects)
	{
		ArrayList<Part> parts = new ArrayList<Part>();
		for(GroupObject gObj : groupObjects)
		{
			parts.add(new PartObj(model, gObj));
		}
		return parts;
	}

}