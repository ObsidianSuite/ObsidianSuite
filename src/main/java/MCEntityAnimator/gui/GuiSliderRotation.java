package MCEntityAnimator.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiSliderRotation extends GuiSlider
{
	public GuiSliderRotation(int id, int x, int y, int range) 
	{
		super(id, x, y, range, 5, 7, new ResourceLocation("mod_mcea:gui/controls.png"), 25, 0, true);
	}

	@Override
	public void updateXPosFromAngle(float angle, boolean entityPos)
	{
		if(entityPos)
		{
			updateXPos((int) (initialX + angle*range));
		}
		else
		{
			updateXPos((int) (initialX + angle/Math.PI*range));
		}
	}

}
