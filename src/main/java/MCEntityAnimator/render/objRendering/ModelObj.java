package MCEntityAnimator.render.objRendering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import MCEntityAnimator.MCEA_Main;
import MCEntityAnimator.Util;
import MCEntityAnimator.ZipUtils;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationParenting;
import MCEntityAnimator.animation.PartGroupsAndNames;
import MCEntityAnimator.render.objRendering.parts.Part;
import MCEntityAnimator.render.objRendering.parts.PartEntityPos;
import MCEntityAnimator.render.objRendering.parts.PartObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.ModelFormatException;
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
	//private Map<String, List<PartObj>> groups;

	private PartObj mainHighlight = null;
	private ArrayList<PartObj> hightlightedParts;

	public static final float initRotFix = 180.0F;
	public static final float offsetFixY = -1.5F;

	private final ResourceLocation modelRL, txtRL, pxyRL;

	public boolean renderWithTexture;

	private boolean partSetupComplete;

	public ModelObj(String par0Str)
	{	
		entityType = par0Str;
		modelRL = new ResourceLocation("mod_mcea:objModels/" + entityType + "/" + entityType + ".obj");
		txtRL = new ResourceLocation("mod_mcea:objModels/" + entityType + "/" + entityType + ".png");
		pxyRL = new ResourceLocation("mod_mcea:objModels/" + entityType + "/" + entityType + ".pxy");
		model = (WavefrontObject) AdvancedModelLoader.loadModel(modelRL);
		parts = ObjUtil.createPartObjList(this, model.groupObjects);
		parts.add(new PartEntityPos(this));
		parenting = AnimationData.getAnipar(par0Str);
		hightlightedParts = new ArrayList<PartObj>();
		bends = new ArrayList<Bend>();
		defaults = Maps.newHashMap();
		loadFromFile();
		renderWithTexture = true;
		partSetupComplete = true;
		groupsAndNames = AnimationData.getPartGroupsAndNames(entityType, this);

		init();
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
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(pxyRL);			
			BufferedReader reader = new BufferedReader(new InputStreamReader(res.getInputStream()));
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
				else
				{

				}
			}
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

	public void exportSource()
	{
		try
		{
			for(int i = 0; i < 3; i++)
			{
				ResourceLocation resLoc = null;
				String fileExtension = "";
				switch(i)
				{
				case 0: 
					resLoc = modelRL; 
					fileExtension = ".obj";
					break;
				case 1: 
					resLoc = txtRL;
					fileExtension = ".png";
					break;
				case 2: 
					resLoc = pxyRL;
					fileExtension = ".pxy";
					break;
				}

				IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resLoc);
				InputStream input = res.getInputStream();

				File file = new File(MCEA_Main.getEntityAnimationFolder(entityType), entityType + fileExtension);
				file.createNewFile();
				FileOutputStream output = new FileOutputStream(file, false);

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = input.read(bytes)) != -1) 
				{
					output.write(bytes, 0, read);
				}

				output.close();
			}  	
		}
		catch(Exception e)
		{
			System.out.println(e.getStackTrace());
		}
	}

	public void appendGroups()
	{
		try
		{
			//Get existing text that is correct.
			List<String> lines = new ArrayList<String>();
			File file = new File(MCEA_Main.getEntityAnimationFolder(entityType), entityType + ".pxy");
			FileReader reader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String currentLine;
			boolean flag = false;
			while(!flag && (currentLine = bufferedReader.readLine()) != null)
			{
				if(currentLine.equals("Part Setup"))
					flag = true;
				lines.add(currentLine);
			}
			bufferedReader.close();

			//Write correct existing text and append groups.
			FileWriter writer = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			for(String line : lines)
			{
				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}	
			for(PartObj p : this.getPartObjs())
			{
				bufferedWriter.write(p.getDisplayName() + ":" + groupsAndNames.getPartGroup(p));
				bufferedWriter.newLine();
			}
			bufferedWriter.close();	
		}
		catch(Exception e)
		{
			System.out.println(e.getStackTrace());
		}
	}

	public void packageEntityFilesToZip()
	{
		ZipUtils.zipFolder(MCEA_Main.getEntityAnimationFolder(entityType).getAbsolutePath(), new File(MCEA_Main.getEntityAnimationFolder(entityType), entityType + ".zip").getAbsolutePath());
	}

	//----------------------------------------------------------------
	//						Parenting
	//----------------------------------------------------------------

	public void setParent(PartObj child, PartObj parent, boolean addBend)
	{
		parenting.addParent(parent, child);
		if(addBend)
		{			
			boolean prevRenderWithTexture = renderWithTexture;
			renderWithTexture = true;

			for(Part p : this.parts)
			{
				if(p instanceof PartObj)
				{
					PartObj obj = (PartObj) p;
					obj.updateTextureCoordinates(false, false);
				}
			}

			Bend b = new Bend(child, parent);
			bends.add(b);
			child.setBend(b);

			renderWithTexture = prevRenderWithTexture;
		}
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




}

