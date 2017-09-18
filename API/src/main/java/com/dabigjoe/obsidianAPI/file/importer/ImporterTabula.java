package com.dabigjoe.obsidianAPI.file.importer;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;

import com.dabigjoe.obsidianAPI.MathHelper;
import com.dabigjoe.obsidianAPI.file.ObsidianFile;
import com.dabigjoe.obsidianAPI.render.ModelObj;
import com.dabigjoe.obsidianAPI.render.part.PartObj;

import ru.gloomyfolken.tcn2obj.TblConverter;
import ru.gloomyfolken.tcn2obj.obj.ObjModel;
import ru.gloomyfolken.tcn2obj.tbl.TabulaBox;
import ru.gloomyfolken.tcn2obj.tbl.TabulaModel;
import ru.gloomyfolken.tcn2obj.tbl.components.CubeInfo;

public class ImporterTabula implements ModelImporter {

	public static final ImporterTabula instance = new ImporterTabula();
	private static final TblConverter tblConverter = new TblConverter();
	private static final String TBL_TEXTURE_NAME = "texture.png";
	
	@Override
	public ObsidianFile toObsidianFile(File file) {
		String error = "Failed to import from Tabula file: " + file.getName();
		try
		{
			String entityName = file.getName().substring(0, file.getName().indexOf("."));

			//Model
			TabulaModel tblModel = new TabulaModel(file);
			String duplicatePartName;
			if((duplicatePartName = containsDuplicateParts(tblModel)) != null) {
				error += ". The model contains the duplicate part " + duplicatePartName;
				throw new RuntimeException(error);
			}
			
			ObjModel objModel = tblConverter.tcn2obj(tblModel, 0.0625f);			
			byte[] modelBytes = createModelBytes(objModel.toStringList());
			
			//Texture
			ZipFile zipFile = new ZipFile(file);
			ZipEntry textureEntry = zipFile.getEntry(TBL_TEXTURE_NAME);
			byte[] textureBytes;
			if(textureEntry != null) 
				textureBytes = IOUtils.toByteArray(zipFile.getInputStream(textureEntry));
			else {
				File defaultTexture = new File(getClass().getClassLoader().getResource("model_textures/grey.png").getPath());
				textureBytes = IOUtils.toByteArray(new FileInputStream(defaultTexture));
			}
			zipFile.close();
			
			return new ObsidianFile(entityName, modelBytes, textureBytes);	
		}
		catch (Exception e1)
		{
		    final JOptionPane pane = new JOptionPane(error);
		    final JDialog d = pane.createDialog((JFrame)null, "Import Error");
		    d.setAlwaysOnTop(true);
		    d.setVisible(true);
			e1.printStackTrace();
			return null;
		}
	}


	@Override
	public <T extends ModelObj> T fromFile(File file, Class<T> clazz) {
		ObsidianFile obsidianFile = toObsidianFile(file);
		
		T model = FileLoader.fromFile(obsidianFile, clazz);
		
		TabulaModel tblModel = new TabulaModel(file);

		//Parenting
		for(TabulaBox box : tblModel.boxes) {
			if(box.cube.children != null && box.cube.children.size() > 0) {
				PartObj parent = model.getPartObjFromName(box.name);
				for(CubeInfo cube : box.cube.children) {
					PartObj child = model.getPartObjFromName(cube.name);
					model.setParent(child, parent, false);
				}
			}
		}
		
		//Rotation points
		for(TabulaBox box : tblModel.boxes) {
			PartObj part = model.getPartObjFromName(box.name);
			float[] boxRotationPoint = getAbsoluteBoxRotationPoint(tblModel.boxes, box, model);
			float[] rotationPoint = new float[3];
			rotationPoint[0] = -boxRotationPoint[0]/16F;
			rotationPoint[1] = boxRotationPoint[1]/16F - 1.5F;
			rotationPoint[2] = boxRotationPoint[2]/16F;
			part.setRotationPoint(rotationPoint);
		}
		
		//Merging
		for(PartObj part : model.getPartObjs()) {
			if(part.getName().endsWith("_m") && part.hasParent())
				model.addMerge(part.getParent(), part);
		}
		model.runMerge();
		
		return model;
	}
	
	private static byte[] createModelBytes(List<String> objLines) {
		String s = "";
		for(String line : objLines) {
			s += line + "\n";
		}
		return s.getBytes(StandardCharsets.UTF_8);
	}

	private static float[] getAbsoluteBoxRotationPoint(List<TabulaBox> boxes, TabulaBox box, ModelObj model) {
		TabulaBox parent = getTabulaBoxParent(boxes, box, model);
		if(parent == null)
			return getBoxRotationPoint(box);

		float[] absRotPoint = getBoxRotationPoint(box);
		MathHelper.rotateVertex(absRotPoint, MathHelper.createRotationMatrixFromAngles(getAbsRotation(boxes, parent, model)), new float[]{0,0,0});
		
		
		float[] parentAbsRotPoint = getAbsoluteBoxRotationPoint(boxes, parent, model);
		for(int i = 0; i < 3; i++)
			absRotPoint[i] += parentAbsRotPoint[i];
	
		return absRotPoint;
	}
	
	private static float[] getAbsRotation(List<TabulaBox> boxes, TabulaBox box, ModelObj model) {
		TabulaBox parent = getTabulaBoxParent(boxes, box, model);
		if(parent == null)
			return getBoxRotation(box);
		
		float[] absRot = getBoxRotation(box);
		
		float[] parentAbsRot = getAbsRotation(boxes, parent, model);
		for(int i = 0; i < 3; i++)
			absRot[i] += parentAbsRot[i];
	
		return absRot;
	}
	
	private static TabulaBox getTabulaBox(List<TabulaBox> boxes, PartObj obj) {
		for(TabulaBox box : boxes) {
			if(box.name.equals(obj.getName()))
				return box;
		}
		throw new RuntimeException("No box for name " + obj.getName());
	}
	
	private static TabulaBox getTabulaBoxParent(List<TabulaBox> boxes, TabulaBox box, ModelObj model) {
		PartObj child = model.getPartObjFromName(box.name);
		PartObj parent = child.getParent();
		if(parent != null)
			return getTabulaBox(boxes, parent);
		return null;
	}
	
	private static float[] getBoxRotationPoint(TabulaBox box) {
		return new float[]{box.rotationPointX, box.rotationPointY, box.rotationPointZ};
	}
	
	private static float[] getBoxRotation(TabulaBox box) {
		return new float[]{(float) (box.rotateAngleX/180F*Math.PI), (float) (box.rotateAngleY/180F*Math.PI), (float) (box.rotateAngleZ/180F*Math.PI)};
	}
	
	private static String containsDuplicateParts(TabulaModel tblModel) {
		List<String> names = new ArrayList<String>();
		for(TabulaBox box : tblModel.boxes) {
			if(names.contains(box.name))
				return box.name;
			names.add(box.name);
		}
		return null;
	}

	
}
