package com.dabigjoe.obsidianAPI.file.importer;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.io.IOUtils;

import com.dabigjoe.obsidianAPI.file.ObsidianFile;
import com.dabigjoe.obsidianAPI.render.ModelObj;
import com.dabigjoe.obsidianAPI.render.part.PartObj;

import ru.gloomyfolken.tcn2obj.QblConverter;
import ru.gloomyfolken.tcn2obj.obj.ObjModel;
import ru.gloomyfolken.tcn2obj.qubble.QubbleCuboid;
import ru.gloomyfolken.tcn2obj.qubble.QubbleModel;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ImporterQubble implements ModelImporter
{
	public static final ImporterQubble instance = new ImporterQubble();
	private static final File defaultTexture = new File(ImporterQubble.class.getClassLoader().getResource("model_textures/grey.png").getPath());
	private static final QblConverter qblConverter = new QblConverter();

	@Override
	public ObsidianFile toObsidianFile(File file)
	{
		String error = "Failed to import from Qubble file: " + file.getName();

		try
		{
			String entityName = file.getName().split("\\.")[0];

			//Model
			QubbleModel qubbleModel = load(file);
			String duplicatePartName;
			if((duplicatePartName = containsDuplicateParts(qubbleModel)) != null) {
				error += ". The model contains the duplicate part " + duplicatePartName;
				throw new RuntimeException(error);
			}

			ObjModel objModel = qblConverter.qbl2obj(qubbleModel, 0.0625F);

			//Texture
			ZipFile zipFile = new ZipFile(file);
			ZipEntry textureEntry = zipFile.getEntry("base.png");

			byte[] textureBytes;

			if (textureEntry != null)
			{
				textureBytes = IOUtils.toByteArray(zipFile.getInputStream(textureEntry));
			}
			else
			{
				textureBytes = IOUtils.toByteArray(new FileInputStream(defaultTexture));
			}

			zipFile.close();

			byte[] modelBytes = createModelBytes(objModel.toStringList());
			return new ObsidianFile(entityName, modelBytes, textureBytes);
		}
		catch (Exception e1)
		{
			final JOptionPane pane = new JOptionPane(error);
			final JDialog d = pane.createDialog(null, "Import Error");
			d.setAlwaysOnTop(true);
			d.setVisible(true);
			e1.printStackTrace();
			return null;
		}
	}

	private QubbleModel load(File file) throws IOException
	{
		try (ZipFile zipFile = new ZipFile(file))
		{
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements())
			{
				ZipEntry entry = entries.nextElement();

				if (entry.getName().equals("model.nbt"))
				{
					NBTTagCompound compound = CompressedStreamTools.read(new DataInputStream(zipFile.getInputStream(entry)));
					return QubbleModel.deserialize(compound);
				}
			}
		}
		catch (ZipException zipException)
		{
			return this.loadLegacy(file);
		}

		return null;
	}

	private QubbleModel loadLegacy(File file) throws IOException
	{
		try (FileInputStream in = new FileInputStream(file))
		{
			NBTTagCompound compound = CompressedStreamTools.readCompressed(in);
			return QubbleModel.deserialize(compound);
		}
	}

	@Override
	public <T extends ModelObj> T fromFile(File file, Class<T> clazz)
	{
		ObsidianFile obsidianFile = toObsidianFile(file);

		T model = FileLoader.fromFile(obsidianFile, clazz);

		try
		{
			QubbleModel qubbleModel = load(file);
			QubbleModel unparented = qubbleModel.unparent();

			//Parenting
			for (QubbleCuboid cuboid : qubbleModel.getAllCuboids())
			{
				if (cuboid.getChildren().size() > 0)
				{
					PartObj parent = model.getPartObjFromName(cuboid.getName());

					for (QubbleCuboid cube : cuboid.getChildren())
					{
						PartObj child = model.getPartObjFromName(cube.getName());
						model.setParent(child, parent, false);
					}
				}
			}

			//Rotation points
			for (QubbleCuboid cuboid : qubbleModel.getAllCuboids())
			{
				PartObj part = model.getPartObjFromName(cuboid.getName());
				QubbleCuboid unparentedCuboid = unparented.getCuboid(cuboid.getName());
				float[] rotationPoint = new float[3];
				rotationPoint[0] = -unparentedCuboid.getPositionX() / 16F;
				rotationPoint[1] = unparentedCuboid.getPositionY() / 16F - 1.5F;
				rotationPoint[2] = unparentedCuboid.getPositionZ() / 16F;
				part.setRotationPoint(rotationPoint);
			}

			//Merging
			for (PartObj part : model.getPartObjs())
			{
				if (part.getName().endsWith("_m") && part.hasParent())
				{
					model.addMerge(part.getParent(), part);
				}
			}

			model.runMerge();
		}
		catch (IOException e)
		{
			final JOptionPane pane = new JOptionPane(e.getMessage());
			final JDialog d = pane.createDialog(null, "Import Error");
			d.setAlwaysOnTop(true);
			d.setVisible(true);
			e.printStackTrace();
		}

		return model;
	}

	private static byte[] createModelBytes(List<String> objLines)
	{
		StringBuilder builder = new StringBuilder();

		for (String line : objLines)
		{
			builder.append(line).append("\n");
		}

		return builder.toString().getBytes(StandardCharsets.UTF_8);
	}

	private static String containsDuplicateParts(QubbleModel model)
	{
		List<String> names = new ArrayList<String>();

		for (QubbleCuboid box : model.getCuboids())
		{
			if (names.contains(box.getName()))
			{
				return box.getName();
			}

			names.add(box.getName());

			String duplicate = containsDuplicateParts(names, box);

			if (duplicate != null)
			{
				return duplicate;
			}
		}

		return null;
	}

	private static String containsDuplicateParts(List<String> names, QubbleCuboid cuboid)
	{
		for (QubbleCuboid child : cuboid.getChildren())
		{
			if (names.contains(child.getName()))
			{
				return child.getName();
			}

			names.add(child.getName());

			String duplicate = containsDuplicateParts(names, child);

			if (duplicate != null)
			{
				return duplicate;
			}
		}

		return null;
	}
}
