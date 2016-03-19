package MCEntityAnimator.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiSlider extends GuiButton
{
	protected ResourceLocation texture;
	protected int initialX;
	//Maximum distance from initalX
	protected int range;
	protected int u,v;
	protected boolean bidirectional;
	
	public GuiSlider(int id, int x, int y, int range, int width, int height, ResourceLocation texture, int u, int v, boolean bidirectional) 
	{		
		super(id, x, y, width, height, "");
		initialX = x;
		this.range = range;
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.bidirectional = bidirectional;
	}
	
	public void updateXPos(int x)
	{
		if(bidirectional)
		{
			if(x > initialX + range)
				xPosition = initialX + range;
			else if(x < initialX - range)
				xPosition = initialX - range;
			else
				xPosition = x;
		}
		else
		{
			if(x > initialX + range)
				xPosition = initialX + range;
			else if(x < initialX)
				xPosition = initialX;
			else
				xPosition = x;
		}
	}
	
	public void updateXPosFromAngle(float angle, boolean entityPos){}
	
	/**
	 * Gets the distance the slider has moved from its original position.
	 * @return -1 to 1 for bidirectional sliders, 0 to 1 for unidirectional.
	 */
	public double getValue()
	{
		return (double) (xPosition - initialX)/(double) (range);
	}
	
	public int getInitialX() 
	{
		return initialX;
	}

	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);       
		par1Minecraft.getTextureManager().bindTexture(texture);
		this.drawTexturedModalRect(xPosition, yPosition, u, v, width, height); 
	}

}
