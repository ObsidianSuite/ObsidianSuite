package ru.gloomyfolken.tcn2obj.qubble;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Credit: LLibrary
 *
 * @author iLexiconn
 */
public class QubbleCuboid
{
	private String name;
	private List<QubbleCuboid> children = new ArrayList<QubbleCuboid>();
	private int dimensionX = 1;
	private int dimensionY = 1;
	private int dimensionZ = 1;
	private float positionX;
	private float positionY;
	private float positionZ;
	private float offsetX;
	private float offsetY;
	private float offsetZ;
	private float rotationX;
	private float rotationY;
	private float rotationZ;
	private float scaleX = 1.0F;
	private float scaleY = 1.0F;
	private float scaleZ = 1.0F;
	private int textureX;
	private int textureY;
	private boolean textureMirrored;
	private String identifier;

	private QubbleCuboid()
	{
	}

	public static QubbleCuboid create(String name)
	{
		QubbleCuboid cuboid = new QubbleCuboid();
		cuboid.setName(name);
		return cuboid;
	}

	public static QubbleCuboid deserialize(NBTTagCompound compound)
	{
		QubbleCuboid cuboid = new QubbleCuboid();
		cuboid.deserializeNBT(compound);
		return cuboid;
	}

	public void deserializeNBT(NBTTagCompound compound)
	{
		this.name = compound.getString("name").replaceAll("[^a-zA-Z0-9_]", "");
		this.children.clear();

		NBTTagList childrenTag = compound.getTagList("children", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < childrenTag.tagCount(); i++)
		{
			QubbleCuboid cuboid = new QubbleCuboid();
			cuboid.deserializeNBT(childrenTag.getCompoundTagAt(i));
			this.children.add(cuboid);
		}

		if (compound.hasKey("dimension"))
		{
			NBTTagCompound dimensionTag = compound.getCompoundTag("dimension");
			this.dimensionX = dimensionTag.getInteger("x");
			this.dimensionY = dimensionTag.getInteger("y");
			this.dimensionZ = dimensionTag.getInteger("z");
		}

		if (compound.hasKey("position"))
		{
			NBTTagCompound positionTag = compound.getCompoundTag("position");
			this.positionX = positionTag.getFloat("x");
			this.positionY = positionTag.getFloat("y");
			this.positionZ = positionTag.getFloat("z");
		}

		if (compound.hasKey("offset"))
		{
			NBTTagCompound offsetTag = compound.getCompoundTag("offset");
			this.offsetX = offsetTag.getFloat("x");
			this.offsetY = offsetTag.getFloat("y");
			this.offsetZ = offsetTag.getFloat("z");
		}

		if (compound.hasKey("rotation"))
		{
			NBTTagCompound rotationTag = compound.getCompoundTag("rotation");
			this.rotationX = rotationTag.getFloat("x");
			this.rotationY = rotationTag.getFloat("y");
			this.rotationZ = rotationTag.getFloat("z");
		}

		if (compound.hasKey("scale"))
		{
			NBTTagCompound scaleTag = compound.getCompoundTag("scale");
			this.scaleX = scaleTag.getFloat("x");
			this.scaleY = scaleTag.getFloat("y");
			this.scaleZ = scaleTag.getFloat("z");
		}

		if (compound.hasKey("texture"))
		{
			NBTTagCompound textureTag = compound.getCompoundTag("texture");
			this.textureX = textureTag.getInteger("x");
			this.textureY = textureTag.getInteger("y");
			this.textureMirrored = textureTag.getBoolean("mirrored");
		}

		if (compound.hasKey("identifier"))
		{
			this.identifier = compound.getString("identifier");
		}
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<QubbleCuboid> getChildren()
	{
		return this.children;
	}

	public int getDimensionX()
	{
		return this.dimensionX;
	}

	public int getDimensionY()
	{
		return this.dimensionY;
	}

	public int getDimensionZ()
	{
		return this.dimensionZ;
	}

	public float getPositionX()
	{
		return this.positionX;
	}

	public float getPositionY()
	{
		return this.positionY;
	}

	public float getPositionZ()
	{
		return this.positionZ;
	}

	public float getOffsetX()
	{
		return this.offsetX;
	}

	public float getOffsetY()
	{
		return this.offsetY;
	}

	public float getOffsetZ()
	{
		return this.offsetZ;
	}

	public float getRotationX()
	{
		return this.rotationX;
	}

	public float getRotationY()
	{
		return this.rotationY;
	}

	public float getRotationZ()
	{
		return this.rotationZ;
	}

	public float getScaleX()
	{
		return this.scaleX;
	}

	public float getScaleY()
	{
		return this.scaleY;
	}

	public float getScaleZ()
	{
		return this.scaleZ;
	}

	public int getTextureX()
	{
		return this.textureX;
	}

	public int getTextureY()
	{
		return this.textureY;
	}

	public boolean isTextureMirrored()
	{
		return this.textureMirrored;
	}

	public void setTextureMirrored(boolean textureMirrored)
	{
		this.textureMirrored = textureMirrored;
	}

	public String getIdentifier()
	{
		return this.identifier;
	}

	public void setTexture(int x, int y)
	{
		this.textureX = x;
		this.textureY = y;
	}

	public void setPosition(float x, float y, float z)
	{
		this.positionX = x;
		this.positionY = y;
		this.positionZ = z;
	}

	public void setOffset(float x, float y, float z)
	{
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
	}

	public void setDimensions(int x, int y, int z)
	{
		this.dimensionX = x;
		this.dimensionY = y;
		this.dimensionZ = z;
	}

	public void setRotation(float x, float y, float z)
	{
		this.rotationX = x;
		this.rotationY = y;
		this.rotationZ = z;
	}

	public void setScale(float x, float y, float z)
	{
		this.scaleX = x;
		this.scaleY = y;
		this.scaleZ = z;
	}

	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	public QubbleCuboid copy()
	{
		QubbleCuboid cuboid = QubbleCuboid.create(this.getName());

		for (QubbleCuboid child : this.getChildren())
		{
			cuboid.getChildren().add(child.copy());
		}

		cuboid.setDimensions(this.getDimensionX(), this.getDimensionY(), this.getDimensionZ());
		cuboid.setPosition(this.getPositionX(), this.getPositionY(), this.getPositionZ());
		cuboid.setOffset(this.getOffsetX(), this.getOffsetY(), this.getOffsetZ());
		cuboid.setRotation(this.getRotationX(), this.getRotationY(), this.getRotationZ());
		cuboid.setScale(this.getScaleX(), this.getScaleY(), this.getScaleZ());
		cuboid.setTexture(this.getTextureX(), this.getTextureY());
		cuboid.setTextureMirrored(this.isTextureMirrored());
		cuboid.setIdentifier(this.getIdentifier());

		return cuboid;
	}

	public QubbleCuboid getCuboid(String name)
	{
		for (QubbleCuboid cuboid : this.children)
		{
			if (cuboid.getName().equals(name))
			{
				return cuboid;
			}
			else
			{
				QubbleCuboid c = cuboid.getCuboid(name);

				if (c != null)
				{
					return c;
				}
			}
		}

		return null;
	}

	public List<QubbleCuboid> getAllChildren()
	{
		List<QubbleCuboid> children = new ArrayList<>();

		for (QubbleCuboid cuboid : this.children)
		{
			children.add(cuboid);
			children.addAll(cuboid.getAllChildren());
		}

		return children;
	}
}
