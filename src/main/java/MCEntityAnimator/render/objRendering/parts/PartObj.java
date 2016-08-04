package MCEntityAnimator.render.objRendering.parts;

import java.awt.GridLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationParenting;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.RenderObj;
import MCEntityAnimator.render.objRendering.bend.Bend;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;

/**
 * One partObj for each 'part' of the model.
 * 
 */
public class PartObj extends Part
{
	private float[] rotationPoint;
	private float axisRotX, axisRotY, axisRotZ;
	private float prevAxisRotX, prevAxisRotY, prevAxisRotZ;
	private boolean showModel;

	//XXX
	private Map<Face, TextureCoordinate[]> defaultTextureCoords;

	private boolean visible;
	private Bend bend = null;
	public GroupObject groupObj;
	private String displayName;
	private FloatBuffer rotationMatrix;

	public PartObj(ModelObj modelObject, GroupObject groupObj) 
	{
		super(modelObject, (groupObj.name.contains("_") ? groupObj.name.substring(0, groupObj.name.indexOf("_")) : groupObj.name).toLowerCase());
		this.groupObj = groupObj;
		this.displayName = getName();
		defaultTextureCoords = new HashMap<Face, TextureCoordinate[]>();
		setDefaultTCsToCurrentTCs();
		visible = true;
		setupRotationMatrix();
		axisRotX = originalValues[0];
		axisRotY = originalValues[1];
		axisRotZ = originalValues[2];
		prevAxisRotX = axisRotX;
		prevAxisRotY = axisRotY;
		prevAxisRotZ = axisRotZ;
	}

	//------------------------------------------
	//              Basics
	//------------------------------------------

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public void setShowModel(boolean show)
	{
		showModel = show;
	}

	public boolean getShowModel()
	{
		return showModel;
	}

	public void setRotationPoint(float[] rot) 
	{
		rotationPoint = rot;
	}

	public float getRotationPoint(int i) 
	{
		return rotationPoint[i];
	}

	public float[] getRotationPoint()
	{
		return rotationPoint;
	}

	public void setVisible(boolean bool)
	{
		visible = bool;
	}

	public void setBend(Bend b)
	{
		bend = b;
	}

	public boolean hasBend()
	{
		return bend != null;
	}

	public void removeBend()
	{
		modelObj.removeBend(bend);
		bend.remove();
		bend = null;
	}

	private void setupRotationMatrix()
	{
		float[] rotationMatrixF = new float[]{1,0,0,0,
				0,1,0,0,
				0,0,1,0,
				0,0,0,1};
		ByteBuffer vbb = ByteBuffer.allocateDirect(rotationMatrixF.length*4);
		vbb.order(ByteOrder.nativeOrder());
		rotationMatrix = vbb.asFloatBuffer();
		rotationMatrix.put(rotationMatrixF);
		rotationMatrix.position(0);
	}
	
	public void setAxisRotation(int axis, float rot)
	{
		switch(axis)
		{
		case 0: axisRotX = rot; break;
		case 1: axisRotY = rot; break;
		case 2: axisRotZ = rot; break;
		}
	}

	//------------------------------------------
	//         Rendering and Rotating
	//------------------------------------------

	/**
	 * Stores the current texture coordinates in default texture coords.
	 * This is required in case a bend is removed, then the texture coords can be restored.
	 * XXX
	 */
	public void setDefaultTCsToCurrentTCs()
	{
		for(Face f : groupObj.faces)
		{
			if(f.textureCoordinates == null)
			{
				f.textureCoordinates = new TextureCoordinate[3];
				for(int i = 0; i < 3; i++)
				{
					f.textureCoordinates[i] = new TextureCoordinate(0, 0);
				}
			}   

			TextureCoordinate[] coordsToStore = new TextureCoordinate[3];
			for(int i = 0; i < 3; i++)
			{
				coordsToStore[i] = new TextureCoordinate(f.textureCoordinates[i].u, f.textureCoordinates[i].v);
			}

			defaultTextureCoords.put(f, coordsToStore);
		}
	}

	/**
	 * Change the texture coordinates if the part is highlighted or the main part.
	 * TODO PartObj: Main highlight vs normal highlight.
	 * XXX
	 */
	public void updateTextureCoordinates(boolean highlight, boolean main)
	{		
		TextureCoordinate texCo1 = new TextureCoordinate(0.0F, 0.0F);
		TextureCoordinate[] coordsHighlight = new TextureCoordinate[]{texCo1, texCo1, texCo1};
		TextureCoordinate texCo2 = new TextureCoordinate(0.5F, 0.5F);
		TextureCoordinate[] coordsNormal = new TextureCoordinate[]{texCo2, texCo2, texCo2};

		for(Face f : groupObj.faces)
		{
			if(highlight)
				f.textureCoordinates = coordsHighlight;
			else if(modelObj.renderWithTexture)
				f.textureCoordinates = defaultTextureCoords.get(f);
			else
				f.textureCoordinates = coordsNormal;	
		}
	}

	public void render(Entity entity, boolean highlight, boolean main) 
	{
		updateTextureCoordinates(highlight, main);

		GL11.glPushMatrix();
		ResourceLocation texture = modelObj.getTexture();
		if(highlight)
			texture = RenderObj.defaultTexture;
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);		
		move(entity);
		if(visible)
			groupObj.render();
		GL11.glPopMatrix();
	}

	/**
	 * Setup rotation and translation for a GL11 matrix based on the rotation
	 * and rotation point of this part and all its parents.
	 * @param entityName - Name of the model. 
	 */
	@SideOnly(Side.CLIENT)
	public void postRender(String entityName)
	{
		//Generate a list of parents: {topParent, topParent - 1,..., parent, this}/
		AnimationParenting anipar = AnimationData.getAnipar(entityName);
		List<PartObj> parts = new ArrayList<PartObj>();
		PartObj child = this;
		PartObj parent;
		parts.add(0, child);
		while((parent = anipar.getParent(child)) != null)
		{
			parts.add(0, parent);
			child = parent;
		}

		//Translate and rotate all parts, starting with the top parent. 
		PartObj prevPart = null;
		for(PartObj p : parts)
		{
			//TODO Post render: Z formula, temporarily works for player though.
			//Not sure if the formulae will work for other entities.
			float[] currentRotPoint = p.getRotationPoint();
			//If prevPart is null, ie part is top parent, use 0,0,0.
			float[] prevRotPoint = prevPart != null ? prevPart.getRotationPoint() : new float[]{0.0F, 0.0F, 0.0F};
			prevPart = p;

			float[] trans = new float[3];
			float[] prevtrans = new float[3];

			for(int i = 0; i < 3; i++)
			{
				trans[i] = 0.0F;
				if(currentRotPoint[i] != 0.0f)
				{
					switch(i)
					{
					case 0: trans[0] = currentRotPoint[0]*-0.78125f; break;
					case 1: trans[1] = currentRotPoint[1]*0.77f + 1.2f; break;
					case 2: /* Z formula */  break;         
					}
				}

				prevtrans[i] = 0.0F;
				if(prevRotPoint[i] != 0.0f)
				{
					switch(i)
					{
					case 0: prevtrans[0] = prevRotPoint[0]*-0.78125f; break;
					case 1: prevtrans[1] = prevRotPoint[1]*0.77f + 1.2f; break;
					case 2: /* Z formula */  break;         
					}
				}
			}

			//Translate, compensating for the previous part translation.
			GL11.glTranslatef(trans[0] - prevtrans[0], trans[1] - prevtrans[1], trans[2] - prevtrans[2]);
			//Rotate
			p.applyRotation(); 
		}
	}

	@Override
	public void move(Entity entity)
	{
		GL11.glTranslatef(-rotationPoint[0], -rotationPoint[1], -rotationPoint[2]);
		applyRotation();
		GL11.glTranslatef(rotationPoint[0], rotationPoint[1], rotationPoint[2]);    

		//Do for children - rotation for parent compensated for!
		List<PartObj> children = AnimationData.getAnipar(modelObj.getEntityType()).getChildren(this);
		if(children != null)
		{
			for(PartObj child : children)
			{
				child.render(entity, modelObj.isPartHighlighted(child), modelObj.isMainHighlight(child));
			}   
		}
	}

	public void applyRotation()
	{
		updateRotationMatrix();

		double absY = Math.asin(rotationMatrix.get(8));
		double absX = Math.asin(-rotationMatrix.get(9)/Math.cos(absY));
		double absZ = Math.asin(-rotationMatrix.get(4)/Math.cos(absY));

		double absXA = Math.PI - absX;
		double absYA = Math.PI - absY;
		double absZA = Math.PI - absZ;


		boolean exit = false;
		double x=0.0F,y=0.0F,z=0.0F;
		for(int i = 0; i < 2; i++)
		{
			x = i == 0 ? absX : absXA;
			for(int j = 0; j < 2; j++)
			{
				y = j == 0 ? absY : absYA;
				for(int k = 0; k < 2; k++)
				{
					z = k == 0 ? absZ : absZA;
					boolean pass = true;
					//System.out.println("Try new");
					for(int l = 0; l < 16; l++)
					{
						if(l < 11 && l != 3 && l != 7)
						{
							double t = 0;
							switch(l)
							{
							case 0: t = Math.cos(y)*Math.cos(z); break;
							case 1: t = Math.cos(x)*Math.sin(z)+Math.sin(x)*Math.sin(y)*Math.cos(z); break;
							case 2: t = Math.sin(x)*Math.sin(z)-Math.cos(x)*Math.sin(y)*Math.cos(z); break;
							case 4: t = -Math.cos(y)*Math.sin(z); break;
							case 5: t = Math.cos(x)*Math.cos(z)-Math.sin(x)*Math.sin(y)*Math.sin(z); break;
							case 6: t = Math.sin(x)*Math.cos(z)+Math.cos(x)*Math.sin(y)*Math.sin(z); break;
							case 8: t = Math.sin(y); break;
							case 9: t = -Math.cos(y)*Math.sin(x); break;
							case 10: t = Math.cos(y)*Math.cos(x); break;
							}
							if(Math.abs(t - rotationMatrix.get(l)) > 0.1)
							{
								pass = false;
								break;
							}

						}
					}
					if(pass)
					{
						exit = true;
						break;
					}
				}
				if(exit)
					break;
			}
			if(exit)
				break;
		}
		if(!exit)
		{
			System.out.println("No solution found..");
			System.out.println("   " + valueX + "," + valueY + "," + valueZ);
		}

		valueX = (float) x;
		valueY = (float) y;
		valueZ = (float) z;
		
		GL11.glRotatef((float) (valueX/Math.PI*180.0F), 1.0F, 0.0F, 0.0F);
		GL11.glRotatef((float) (valueY/Math.PI*180.0F), 0.0F, 1.0F, 0.0F);
		GL11.glRotatef((float) (valueZ/Math.PI*180.0F), 0.0F, 0.0F, 1.0F);
	}

	public void updateRotationMatrix()
	{
		float rotX = (float) ((axisRotX - prevAxisRotX)/Math.PI*180.0F);
		float rotY = (float) ((axisRotY - prevAxisRotY)/Math.PI*180.0F);
		float rotZ = (float) ((axisRotZ - prevAxisRotZ)/Math.PI*180.0F);

		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glMultMatrix(rotationMatrix);
		GL11.glRotated(rotX, 1.0F, 0.0F, 0.0F);
		GL11.glRotated(rotY, 0.0F, 1.0F, 0.0F);
		GL11.glRotated(rotZ, 0.0F, 0.0F, 1.0F);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, rotationMatrix);
		GL11.glPopMatrix();

		prevAxisRotX = axisRotX;
		prevAxisRotY = axisRotY;
		prevAxisRotZ = axisRotZ;

	}

	public void renderRotationAxis()
	{
		Vec3 origin = Vec3.createVectorHelper(0.0F, 0.0F, 0.0F);

		Vec3 xVec = Vec3.createVectorHelper(1.0F, 0.0F, 0.0F);
		Vec3 yVec = Vec3.createVectorHelper(0.0F, 1.0F, 0.0F);
		Vec3 zVec = Vec3.createVectorHelper(0.0F, 0.0F, 1.0F);

		GL11.glPushMatrix();
		GL11.glTranslatef(-rotationPoint[0], -rotationPoint[1], -rotationPoint[2]);
		GL11.glMultMatrix(rotationMatrix);
		drawLine(origin, xVec, 0xFF0000);
		drawLine(origin, yVec, 0x00FF00);
		drawLine(origin, zVec, 0x0000FF);
		GL11.glPopMatrix();
	}

	//XXX
	private void drawLine(Vec3 p1, Vec3 p2, int color)
	{
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);

		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawing(1);
		tessellator.setColorOpaque_I(color);
		tessellator.addVertex(p1.xCoord,p1.yCoord,p1.zCoord);
		tessellator.addVertex(p2.xCoord,p2.yCoord,p2.zCoord);
		tessellator.draw();

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

}
