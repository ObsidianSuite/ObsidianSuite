package MCEntityAnimator.animation;

import java.text.DecimalFormat;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import MCEntityAnimator.Util;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.PartObj;
import MCEntityAnimator.render.objRendering.RenderObj;

public class AnimationPart 
{
	public float xBase;
	public float xSwing;
	public float yBase;
	public float ySwing;
	public float zBase;
	public float zSwing;
	private float startTime;
	private float finishTime;
	
	private PartObj part;
	
	DecimalFormat df = new DecimalFormat("#.##");

	public AnimationPart() 
	{
		this(0.0F, 0.0F, 0.0F, null);
	}
	
	public AnimationPart(PartObj par0Part) 
	{
		this(0.0F, 0.0F, 0.0F, par0Part);
	}
	
	public AnimationPart(float par0D, float par1D, float par2D, PartObj par3Part)
	{
		xBase = par0D;
		yBase = par1D;
		zBase = par2D;
		part = par3Part;
	}

	public void update(float par0, float par1, float par2, float par3, float par4, float par5) 
	{
		xBase = par0;
		xSwing = par1;
		yBase = par2;
		ySwing = par3;
		zBase = par4;
		zSwing = par5;
	}
	
	public float[] getFinalRotation() 
	{
		float[] fs = new float[3];
		fs[0] = (finishTime - startTime)*xSwing + xBase;
		fs[1] = (finishTime - startTime)*ySwing + yBase;
		fs[2] = (finishTime - startTime)*zSwing + zBase;
		return fs;
	}
	
	public void setStartTime(float par0f)
	{
		startTime = par0f;
	}
	
	public void setFinishTime(float par0f)
	{
		finishTime = par0f;
	}
	
	public float getStartTime() 
	{
		return startTime;
	}
	

	public float getFinishTime() 
	{
		return finishTime;
	}
	
	public PartObj getPart()
	{
		return part;
	}
	
	public String getPartName()
	{
		return part.getName();
	}

	public void animatePart(float f) 
	{	
		float x = f*xSwing + xBase;
		float y = f*ySwing + yBase;
		float z = f*zSwing + zBase;
		for(int i = 0; i < 3; i++)
		{
			float f1 = 0.0F;
			switch(i)
			{
			case 0: f1 = x; break;
			case 1: f1 = y; break;
			case 2: f1 = z; break;
			}
			if(f1 > Math.PI)
			{
				f1 -= Math.PI*2;
			}
			else if(f1 < -Math.PI)
			{
				f1 += Math.PI*2;
			}
			switch(i)
			{
			case 0: x = f1; break;
			case 1: y = f1; break;
			case 2: z = f1; break;
			}
		}
		
		part.setRotation(new float[]{x, y, z});
	}

	public NBTBase getSaveData() 
	{
		NBTTagCompound animationData = new NBTTagCompound();		
		animationData.setFloat("XBase", Float.parseFloat(df.format(this.xBase)));
		animationData.setFloat("XSwing", Float.parseFloat(df.format(this.xSwing)));
		animationData.setFloat("YBase", Float.parseFloat(df.format(this.yBase)));
		animationData.setFloat("YSwing", Float.parseFloat(df.format(this.ySwing)));
		animationData.setFloat("ZBase", Float.parseFloat(df.format(this.zBase)));
		animationData.setFloat("ZSwing", Float.parseFloat(df.format(this.zSwing)));
		animationData.setFloat("StartTime", Float.parseFloat(df.format(this.startTime)));
		animationData.setFloat("FinishTime", Float.parseFloat(df.format(this.finishTime)));
		animationData.setString("Part", this.part.getName());
		return animationData;
	}

	public void loadData(String entityName, NBTTagCompound compound) 
	{
		EntityObj entity = new EntityObj(Minecraft.getMinecraft().theWorld, entityName);
		ArrayList<PartObj> parts = ((RenderObj) RenderManager.instance.getEntityRenderObject(entity)).getModel(entityName).parts;
				
		this.xBase = compound.getFloat("XBase");
		this.xSwing = compound.getFloat("XSwing");
		this.yBase = compound.getFloat("YBase");
		this.ySwing = compound.getFloat("YSwing");
		this.zBase = compound.getFloat("ZBase");
		this.zSwing = compound.getFloat("ZSwing");
		this.startTime = compound.getFloat("StartTime");
		this.finishTime = compound.getFloat("FinishTime");
		this.part = Util.getPartFromName(compound.getString("Part"), parts);
	}

//	public void loadDataForModel(ModelObj model, NBTTagCompound compound) 
//	{
//		List boxList = model.boxList;
//				
//		this.xBase = compound.getFloat("XBase");
//		this.xSwing = compound.getFloat("XSwing");
//		this.yBase = compound.getFloat("YBase");
//		this.ySwing = compound.getFloat("YSwing");
//		this.zBase = compound.getFloat("ZBase");
//		this.zSwing = compound.getFloat("ZSwing");
//		this.startTime = compound.getFloat("StartTime");
//		this.finishTime = compound.getFloat("FinishTime");
//		this.part = Util.getModelRenderFromName(compound.getString("Part"), boxList);
//	}
	
}
