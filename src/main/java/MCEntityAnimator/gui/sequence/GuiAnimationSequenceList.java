package MCEntityAnimator.gui.sequence;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.Util;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.gui.sequence.timeline.GuiAnimationTimelineMain;


public class GuiAnimationSequenceList extends GuiScreen 
{
	int posX;
	int posY;
	private String entityName;
	private ArrayList<AnimationSequence> sequences;
	private int dimHeight;

	private static final ResourceLocation texture = new ResourceLocation("mod_MCEA:gui/animation_home.png");

	public GuiAnimationSequenceList(String par0Str, ArrayList<AnimationSequence> par1Sqs)
	{
		this.mc = Minecraft.getMinecraft();
		this.entityName = par0Str;
		this.sequences = par1Sqs;
		this.dimHeight = 45 + sequences.size()*22;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		super.initGui();
		this.posX = (this.width - 80)/2;
		this.posY = (this.height - dimHeight)/2;
		this.updateButtons();
	}

	public void updateButtons()
	{
		this.buttonList.clear();
		for(int i = 0; i < sequences.size(); i++)
		{
			this.buttonList.add(new GuiButton(i, posX + 5, posY + 19 + 22*i, 70, 20, sequences.get(i).getName()));
		}
		this.buttonList.add(new GuiButton(sequences.size(), posX + 5, posY + 19 + 22*sequences.size(), 70, 20, "Back"));
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	public void actionPerformed(GuiButton button)
	{	
		if(button.id == sequences.size())
		{
			//AnimationData.addNewSequence(entity.getCommandSenderName(), sequence); 
			mc.displayGuiScreen(new GuiAnimationSequenceMain(entityName));
		}
		else
		{
			mc.displayGuiScreen(new GuiAnimationTimelineMain(entityName, sequences.get(button.id), null, null, 0));
		}
	}

	public boolean doesGuiPauseGame()
	{
		return false;
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(texture);		
		Util.drawCustomGui(posX, posY, 80, this.dimHeight, 0);

		this.drawCenteredString(this.fontRendererObj, "Load Sequence", posX + 40, posY + 6, 0xffff0000);
		
		super.drawScreen(par1, par2, par3);
	}

}


