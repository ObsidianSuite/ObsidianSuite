package MCEntityAnimator.render.objRendering.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationParenting;
import MCEntityAnimator.render.objRendering.Bend;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.bend.BendNew;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
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
    private boolean showModel;

    //XXX
    private Map<Face, TextureCoordinate[]> defaultTextureCoords;

    private boolean visible;
    private BendNew bend = null;
    public GroupObject groupObj;
    private String displayName;

    public PartObj(ModelObj modelObject, GroupObject groupObj) 
    {
        super(modelObject, (groupObj.name.contains("_") ? groupObj.name.substring(0, groupObj.name.indexOf("_")) : groupObj.name).toLowerCase());
        this.groupObj = groupObj;
        this.displayName = getName();
        defaultTextureCoords = new HashMap<Face, TextureCoordinate[]>();
        setDefaultTCsToCurrentTCs();
        visible = true;
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

    public void setBend(BendNew b)
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
        float u = 0.0F;
        float v = 0.0F;
        if(highlight)
        {
            if(main)
            {
                u = 0.0F;
                v = 0.75F;
            }
            else
            {
                u = 0.75F;
                v = 0.0F;
            }
        }


        if(!modelObj.renderWithTexture)
        {
            for(Face f : groupObj.faces)
            {
                TextureCoordinate texCo = new TextureCoordinate(u, v);
                f.textureCoordinates = new TextureCoordinate[]{texCo, texCo, texCo};
            }
        }
        else
        {
            if(highlight)
            {
                for(Face f : groupObj.faces)
                {
                    TextureCoordinate texCo = new TextureCoordinate(0.0F, 0.0F);
                    f.textureCoordinates = new TextureCoordinate[]{texCo, texCo, texCo};
                }
            }
            else
            {
                for(Face f : groupObj.faces)
                {
                    f.textureCoordinates = defaultTextureCoords.get(f);
                }
            }           
        }
    }

    public void render(Entity entity, boolean highlight, boolean main) 
    {
    	groupObj.glDrawingMode = 4;

    	
        updateTextureCoordinates(highlight, main);

        TextureCoordinate tc = new TextureCoordinate(0.0F, 0.0F); 
        for(Face f : groupObj.faces)
        {
        	f.textureCoordinates = new TextureCoordinate[]{tc, tc, tc};
        }
        
        GL11.glPushMatrix();
        move(entity);
        if(visible)
        {
            groupObj.render();
        }
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
            if (p.valueX != 0.0F)
                GL11.glRotatef(p.valueX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            if (p.valueY != 0.0F)
                GL11.glRotatef(-p.valueY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);            
            if (p.valueZ != 0.0F)
                GL11.glRotatef(-p.valueZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);   
    	}
    }


    @Override
    public void move(Entity entity)
    {
        //Translate part to centre
        GL11.glTranslatef(-rotationPoint[0], -rotationPoint[1], -rotationPoint[2]);

        //Rotate
        GL11.glRotated((valueX - originalValues[0])/Math.PI*180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotated((valueY - originalValues[1])/Math.PI*180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotated((valueZ - originalValues[2])/Math.PI*180.0F, 0.0F, 0.0F, 1.0F);

        //Translate to original position
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


}
