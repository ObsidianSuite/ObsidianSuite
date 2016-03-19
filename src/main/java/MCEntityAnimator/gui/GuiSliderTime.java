package MCEntityAnimator.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiSliderTime extends GuiSlider
{
	
	private final int offset = 2;
	
	public GuiSliderTime(int id, int x, int y, int range) 
	{
		super(id, x, y, range, 5, 5, new ResourceLocation("mod_mcea:gui/controls.png"), 30, 0, false);
	}

	/**
	 * Gets the distance the slider has moved from its original position.
	 * @return -1 to 1 for bidirectional sliders, 0 to 1 for unidirectional.
	 */
	@Override
	public double getValue()
	{
		return (double) (xPosition - initialX);
	}
	
	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
	{
		drawHorizontalLine(xPosition, xPosition + offset*2, yPosition - 1, 0xff000000);
		drawHorizontalLine(xPosition + offset/2, xPosition + 3*offset/2, yPosition, 0xff000000);
		drawHorizontalLine(xPosition + offset, xPosition + offset, yPosition + 1, 0xff000000);
		drawVerticalLine(xPosition + offset, yPosition, yPosition + 121, 0x77000000);
	}

}
