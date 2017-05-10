package obsidianAPI.file.importer;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import obsidianAPI.file.ObsidianFile;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.PartObj;
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
		try
		{
			String entityName = file.getName().substring(0, file.getName().indexOf("."));

			//Model
			TabulaModel tblModel = new TabulaModel(file);
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
			System.err.println("Failed to import from Tabula file: " + file.getName());
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
			
			float[] rotationPoint = new float[3];
			rotationPoint[0] = -box.rotationPointX/16F;
			rotationPoint[1] = box.rotationPointY/16F - 1.5F;
			rotationPoint[2] = box.rotationPointZ/16F;
			part.setRotationPoint(rotationPoint);
		}
		
		return model;
	}

	
	private static byte[] createModelBytes(List<String> objLines) {
		String s = "";
		for(String line : objLines) {
			s += line + "\n";
		}
		return s.getBytes(StandardCharsets.UTF_8);
	}

}
