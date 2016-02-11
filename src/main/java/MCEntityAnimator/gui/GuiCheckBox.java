package MCEntityAnimator.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiCheckBox extends GuiButton
{
    private static final ResourceLocation textureLocation = new ResourceLocation("mod_mcea:gui/controls.png");

    public boolean isChecked = false;

    public GuiCheckBox(int id, int x, int y, boolean isChecked)
    {
        super(id, x, y, 9, 9, null);
        this.isChecked = isChecked;
    }

    @Override
    public void drawButton(Minecraft mc, int mX, int mY)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(textureLocation);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            this.field_146123_n = mX >= this.xPosition && mY >= this.yPosition && mX < this.xPosition + this.width && mY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.field_146123_n);

            int u = isChecked ? 0 : 9;
            int v = hoverState * 9;
            this.drawTexturedModalRect(this.xPosition, this.yPosition, u, v, this.width, this.height);
            this.mouseDragged(mc, mX, mY);
        }
    }
}