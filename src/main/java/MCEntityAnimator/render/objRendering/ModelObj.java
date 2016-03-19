package MCEntityAnimator.render.objRendering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationParenting;
import MCEntityAnimator.gui.GuiAnimationParenting;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class ModelObj extends ModelBase
{

	private String entityType;
	public WavefrontObject model;
	public ArrayList<PartObj> parts;
	private ArrayList<Bend> bends;
	private AnimationParenting parenting;
	private Map<PartObj, float[]> defaults;
	private PartObj mainHighlight = null;
	private ArrayList<PartObj> hightlightedParts;

	public PartObj currentPart;

	public static final float initRotFix = 180.0F;
	public static final float offsetFixY = -1.5F;

	private final ResourceLocation modelRL;
	private final ResourceLocation pxyRL;
	
	public boolean renderWithTexture;
	
	public ModelObj(String par0Str)
	{	
		entityType = par0Str;
		modelRL = new ResourceLocation("mod_mcea:objModels/" + entityType + "/" + entityType + ".obj");
		pxyRL = new ResourceLocation("mod_mcea:objModels/" + entityType + "/" + entityType + ".pxy");
		model = (WavefrontObject) AdvancedModelLoader.loadModel(modelRL);
		parts = ObjUtil.createPartObjList(this, model.groupObjects);
		parenting = AnimationData.getAnipar(par0Str);
		hightlightedParts = new ArrayList<PartObj>();
		bends = new ArrayList<Bend>();
		defaults = Maps.newHashMap();
		loadFromFile();
		renderWithTexture = true;
		init();
	}
	
	public void init() 
	{
		for(PartObj obj : this.parts)
		{
			float[] arr = new float[3];
			arr[0] = obj.getRotationPoint(0);
			arr[1] = obj.getRotationPoint(1);
			arr[2] = obj.getRotationPoint(2);
			defaults.put(obj, arr);
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

			while ((currentLine = reader.readLine()) != null)
			{
				switch(i)
				{
				case 0: 
					for(PartObj obj : parts)
					{
						if(obj.getName().equals(currentLine))
						{
							currentPart = obj;
							break;
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
					currentPart.setOriginalRotation(rot);
					currentPart.setRotation(rot);
					i = 0;
					break;
				}
			}
		}
		catch (IOException e)
		{
			throw new ModelFormatException("IO Exception reading model format", e);
		}
	}
	
//----------------------------------------------------------------
//						Parenting
//----------------------------------------------------------------
	
	public void setParent(PartObj child, PartObj parent, boolean addBend)
	{
//		float[] originalParentValues = new float[6];
//		originalParentValues[0] = Float.valueOf(parent.getRotation(0));
//		originalParentValues[1] = Float.valueOf(parent.getRotation(1));
//		originalParentValues[2] = Float.valueOf(parent.getRotation(2));
//		originalParentValues[3] = Float.valueOf(parent.getRotationPoint(0));
//		originalParentValues[4] = Float.valueOf(parent.getRotationPoint(1));
//		originalParentValues[5] = Float.valueOf(parent.getRotationPoint(2));
//		if(parenting.hasParent(parent))
//		{
//			float[] defs = this.defaults.get(parent);
//			parent.setRotation(new float[]{defs[0], defs[1], defs[2]});
//			parent.setRotationPoint(new float[]{defs[3], defs[4], defs[5]});;
//		}
//		float xDif = child.getRotation(0) - parent.getRotation(0);
//		float yDif = child.getRotation(1) - parent.getRotation(1);
//		float zDif = child.getRotation(2) - parent.getRotation(2);
//		float xRotP = 0.0F;
//		float yRotP = 0.0F;
//		float zRotP = 0.0F;
//		child.setRotation(new float[]{child.getRotation(0) - parent.getRotation(0), child.getRotation(1) - parent.getRotation(1), 
//											child.getRotation(2) - parent.getRotation(2)});
//		if(parent.getRotation(0) != 0.0F)
//		{
//			yRotP = (float) (yDif*Math.cos(-parent.getRotation(0)) - zDif*Math.sin(-parent.getRotation(0)));
//			zRotP = (float) (yDif*Math.sin(-parent.getRotation(0)) + zDif*Math.cos(-parent.getRotation(0)));
//			yDif = yRotP;
//			zDif = zRotP;
//			parent.setRotation(0, 0.0F);
//		}
//		if(parent.getRotation(1) != 0.0F)
//		{
//			xRotP = (float) (xDif*Math.cos(-parent.getRotation(1)) + zDif*Math.sin(-parent.getRotation(1)));
//			zRotP = (float) (-xDif*Math.sin(-parent.getRotation(1)) + zDif*Math.cos(-parent.getRotation(1)));
//			xDif = xRotP;
//			zDif = zRotP;
//			parent.setRotation(1, 0.0F);
//		}
//		if(parent.getRotation(2) != 0.0F)
//		{
//			xRotP = (float) (xDif*Math.cos(-parent.getRotation(2)) - yDif*Math.sin(-parent.getRotation(2)));
//			yRotP = (float) (xDif*Math.sin(-parent.getRotation(2)) + yDif*Math.cos(-parent.getRotation(2)));
//			xDif = xRotP;
//			yDif = yRotP;
//			parent.setRotation(2, 0.0F);
//		}
//		if(xRotP == 0.0F){xRotP = xDif;}
//		if(yRotP == 0.0F){yRotP = yDif;}
//		if(zRotP == 0.0F){zRotP = zDif;}
//		child.setRotationPoint(new float[]{xRotP, yRotP, zRotP});
//		parent.setRotation(new float[]{originalParentValues[0], originalParentValues[1], originalParentValues[2]});
//		parent.setRotationPoint(new float[]{originalParentValues[3], originalParentValues[4], originalParentValues[5]});
//		postParentingRotations.put(child, new float[]{child.getRotation(0), child.getRotation(1), child.getRotation(2)});
		parenting.addParent(parent, child);
		if(addBend)
		{			
			boolean prevRenderWithTexture = renderWithTexture;
			renderWithTexture = true;
			
			for(PartObj part : this.parts)
			{
				part.updateTextureCoordinates(false, false);
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

		for(PartObj part : this.parts) 
		{
			if(!parenting.hasParent(part))
			{
				part.render(isPartHighlighted(part), isMainHighlight(part));
			}
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

