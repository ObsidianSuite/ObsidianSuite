package com.dabigjoe.obsidianAPI.file;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ObsidianFile {

	private String entityName;
	private byte[] modelBytes;
	private byte[] textureBytes;
	
	public ObsidianFile(String entityName, byte[] modelBytes, byte[] textureBytes) {
		this.entityName = entityName;
		this.modelBytes = modelBytes;
		this.textureBytes = textureBytes;
	}

	public String getEntityName() {
		return entityName;
	}

	public InputStream getModelStream() {
		return new ByteArrayInputStream(modelBytes);
	}
	
	public InputStream getTextureStream() {
		return new ByteArrayInputStream(textureBytes);
	}
	
	
}
