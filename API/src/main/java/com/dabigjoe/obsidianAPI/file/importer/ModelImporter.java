package com.dabigjoe.obsidianAPI.file.importer;

import java.io.File;

import com.dabigjoe.obsidianAPI.file.ObsidianFile;
import com.dabigjoe.obsidianAPI.render.ModelObj;

public interface ModelImporter {
	
	/**
	 * Convert a file from the importer type to the Obsidian Model type.
	 * @param file File to convert.
	 * @return Obsidian File.
	 */
	public ObsidianFile toObsidianFile(File file);

	/**
	 * Create a model instance of a specific class from this importer's file type.
	 * @param file File to create from
	 * @param clazz Class to create, must extend ModelObj. 
	 * 	Must also have constructor String.class, WavefrontObject.class, ResourceLocation.class
	 * @return Model instance of class clazz.
	 */
	public <T extends ModelObj> T fromFile(File file, Class<T> clazz);
	
}
