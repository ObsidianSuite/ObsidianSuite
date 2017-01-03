package com.nthrootsoftware.mcea.render;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.nthrootsoftware.mcea.MCEA_Main;

import net.minecraft.client.resources.FolderResourcePack;

public class MCEAResourcePack extends FolderResourcePack
{

	public MCEAResourcePack() 
	{
		super(new File(MCEA_Main.homePath));
	}
	
	@Override
    protected InputStream getInputStreamByName(String resourceName) throws IOException
    {
        return new BufferedInputStream(new FileInputStream(generateFile(resourceName)));
    }

	@Override
    protected boolean hasResourceName(String resourceName)
    {
        return generateFile(resourceName).isFile();
    }
	
	private File generateFile(String resourceName)
	{
		File file = new File(this.resourcePackFile, resourceName.substring(resourceName.indexOf("/") + 1));
		System.out.println(file.getAbsolutePath());
		return file;
	}
	
	@Override
	public Set getResourceDomains()
    {
        HashSet hashset = Sets.newHashSet();
        hashset.add("animation");
        return hashset;
    }

	

}
