package obsidianAnimator.render;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.client.resources.FolderResourcePack;
import obsidianAPI.file.FileHandler;

public class AnimationResourcePack extends FolderResourcePack
{

	public AnimationResourcePack() 
	{
		super(new File(FileHandler.homePath + "/mods"));
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
