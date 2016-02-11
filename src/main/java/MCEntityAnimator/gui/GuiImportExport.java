package MCEntityAnimator.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.Util;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.animation.AnimationStance;


public class GuiImportExport extends GuiScreen 
{
	int posX;
	int posY;
	private String entityName;
	private int init = 0;

	private boolean flashMessage = false;
	private String flashMessageText = "";
	private int flashMessageTime = 0;

	private static final ResourceLocation texture = new ResourceLocation("mod_MCEA:gui/animation_home.png");

	public GuiImportExport(String par0String)
	{
		this.mc = Minecraft.getMinecraft();
		this.entityName = par0String;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		super.initGui();
		this.posX = (this.width - 80)/2;
		this.posY = (this.height - 110)/2;
		this.updateButtons();
	}

	public void updateButtons()
	{
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, posX + 5, posY + 19, 70, 20, "Imp-Folder"));
		this.buttonList.add(new GuiButton(1, posX + 5, posY + 41, 70, 20, "Import"));
		this.buttonList.add(new GuiButton(2, posX + 5, posY + 63, 70, 20, "Export"));
		this.buttonList.add(new GuiButton(3, posX + 5, posY + 85, 70, 20, "Back"));
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	public void actionPerformed(GuiButton button)
	{	
		if(init > 4)
		{
			switch(button.id)
			{
			case 0: openImportFolder(); break;
			case 1: importAll(); break;
			case 2: exportAll(); break;
			case 3: mc.displayGuiScreen(new GuiAnimationHome(entityName)); break;
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button)
	{
		super.mouseClicked(mouseX, mouseY, button);
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
		Util.drawCustomGui(posX, posY, 80, 110, 0);

		this.drawCenteredString(this.fontRendererObj, entityName, posX + 40, posY + 6, 0xffff0000);

		if(flashMessage)
		{
			this.drawCenteredString(this.fontRendererObj, flashMessageText, posX + 40, posY - 15, 0xffff0000);
			flashMessageTime++;
			if(flashMessageTime > 200)
			{
				flashMessage = false;
				flashMessageTime = 0;
			}
		}

		super.drawScreen(par1, par2, par3);

		if(init < 5)
		{
			init += 1;
		}
	}

	private void openImportFolder()
	{
		File folder = new File(this.mc.mcDataDir.getAbsolutePath() + "/Animation/Import/" + entityName);
		folder.mkdirs();

		try 
		{
			Desktop.getDesktop().open(folder);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private void importAll()
	{
		File folder = new File(this.mc.mcDataDir.getAbsolutePath() + "/Animation/Import/" + entityName);
		folder.mkdirs();
		System.out.println(importParenting(folder));
		String parentingImportResult = importParenting(folder) ? "Parenting imported." : "Parenting file not found.";
		int numberImported = importAnimations(folder);
		initFlashMessage(parentingImportResult + " " + numberImported + " animation(s) sucessfully imported.");
	}

	/**
	 * Import animations from the import folder.
	 * @return the number of animations imported.
	 */
	private int importAnimations(File folder)
	{
		int totalImported = 0;

		for(File f : folder.listFiles())
		{
			if(f.getName().contains(".dat") && !f.getName().contains(entityName + "Parenting"))
			{
				try 
				{
					NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(f));
					AnimationSequence sequence = new AnimationSequence("");
					sequence.loadData(entityName, nbt);
					if(AnimationData.addNewSequence(entityName, sequence))
					{
						totalImported++;
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
		return totalImported;
	}

	/**
	 * Import the parenting for the current entity.
	 * @return if the import was successful (ie file exists).
	 */
	private boolean importParenting(File folder)
	{
		File parentingFile = new File(folder, entityName + "Parenting.dat");
		if(!parentingFile.exists())
		{
			return false;
		}

		try
		{
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(parentingFile));
			AnimationData.getAnipar(entityName).loadData(nbt, entityName);
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private void exportAll()
	{
		File folder = new File(this.mc.mcDataDir.getAbsolutePath() + "/Animation/Export/" + entityName);
		folder.mkdirs();

		exportAnimations(folder);
		exportParenting(folder);

		//TODO export parenting as NBT

		try 
		{
			Desktop.getDesktop().open(folder);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Export the animations and stances of an entity as a .dat files to a given folder.
	 */
	private void exportAnimations(File folder)
	{
		for(AnimationSequence animation : AnimationData.getSequences(entityName))
		{
			NBTTagCompound nbttagcompound = (NBTTagCompound) animation.getSaveData();
			File file = new File(folder, animation.getName() + ".dat");
			try 
			{
				CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file));
			} 
			catch (FileNotFoundException e) {e.printStackTrace();} 
			catch (IOException e) {e.printStackTrace();}

			//TODO info file still required?
			//			File animationInfoFile = new File(folder, animation.getName() + "Info.txt");
			//			PrintWriter infoFile = null;
			//			try 
			//			{
			//				animationInfoFile.createNewFile();
			//				infoFile = new PrintWriter(animationInfoFile);
			//			} 
			//			catch (FileNotFoundException e) {e.printStackTrace();} 
			//			catch (IOException e) {e.printStackTrace();} 
			//
			//			if(infoFile != null)
			//			{		
			//				infoFile.println("Name: " + animation.getName());
			//				infoFile.println("Length: " + animation.getTotalTime());
			//				infoFile.println("Action Point: " + animation.getActionPoint());
			//				infoFile.close();
			//			}
		}

		for(AnimationStance stance : AnimationData.getStances(entityName))
		{
			NBTTagCompound nbttagcompound = (NBTTagCompound) stance.getSaveData();
			File file = new File(folder, stance.getName() + "Stance.dat");
			try 
			{
				CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file));
			} 
			catch (FileNotFoundException e) {e.printStackTrace();} 
			catch (IOException e) {e.printStackTrace();}
		}
	}

	/**
	 * Export the parenting of an entity as a .dat file to a given folder.
	 */
	private void exportParenting(File folder)
	{
		NBTTagCompound nbttagcompound = AnimationData.getAnipar(entityName).getSaveData(entityName);
		File parentingFile = new File(folder, entityName + "Parenting.dat");
		try 
		{
			CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(parentingFile));
		} 
		catch (FileNotFoundException e) {e.printStackTrace();} 
		catch (IOException e) {e.printStackTrace();}
	}



	private void outputAnimation() 
	{
		//		File folder = new File(this.mc.mcDataDir.getAbsolutePath() + "/Animation/Output/" + entityName);
		//		folder.mkdirs();

		//TODO parenting and rendering still required??
		//		AnimationParenting anipar = AnimationData.getAnipar(entityName);
		//		File file = new File(folder, entityName + "Parenting.txt");
		//		PrintWriter parentingFile = null;
		//		try 
		//		{
		//			file.createNewFile();
		//			parentingFile = new PrintWriter(file);
		//		} 
		//		catch (FileNotFoundException e) {e.printStackTrace();} 
		//		catch (IOException e) {e.printStackTrace();} 
		//
		//		if(parentingFile != null)
		//		{		
		//			ArrayList<PartObj> mrs = new ArrayList<PartObj>();
		//			for(PartObj mr : anipar.getAllParents())
		//			{
		//				if(!mrs.contains(mr)){mrs.add(mr);}
		//			}
		//			for(PartObj parent : mrs)
		//			{
		//				for(PartObj child : anipar.getChildren(parent))
		//				{
		//					parentingFile.println(parent.getName() + ".addChild(" + child.getName() + ");");
		//					parentingFile.println(child.getName() + ".setRotationPoint(" + child.getRotation(0) + "F, " + child.getRotation(1) + "F, " + child.getRotation(2) + "F);");
		//					float[] rots = child.getOriginalRotation();
		//					parentingFile.println("setRotation(" + child.getName() + ", " + rots[0] + "F, " + rots[1] + "F, " + rots[2] + "F);");
		//				}
		//			}
		//			parentingFile.close();
		//		}
		//
		//		File file2 = new File(folder, entityName + "Rendering.txt");
		//		PrintWriter renderingFile = null;
		//		try 
		//		{
		//			file2.createNewFile();
		//			renderingFile = new PrintWriter(file2);
		//		} 
		//		catch (FileNotFoundException e) {e.printStackTrace();} 
		//		catch (IOException e) {e.printStackTrace();} 
		//
		//		//TODO change rendering output, in fact change whole output?!?!?
		////		if(renderingFile != null)
		////		{		
		////			ArrayList<PartObj> mrs = new ArrayList<PartObj>();
		////
		////			for(PartObj obj : ((RenderObj) RenderManager.instance.getEntityRenderObject(new EntityObj(mc.theWorld))).getModel().parts);
		////			{
		////				Object obj;
		////				if(!anipar.getAllChildren().contains(obj))
		////				{
		////					renderingFile.println(obj.getName() + ".render(f5);");
		////				}
		////			}
		////			renderingFile.close();
		////		}
	}

	private void initFlashMessage(String text)
	{
		flashMessageText = text;
		flashMessage = true;
	}


}



