package ru.gloomyfolken.tcn2obj.tcn;

public class TechneBox {

	public TechneModel parentModel;
	
	 /** The size of the texture file's width in pixels. */
    public float textureWidth = 64;

    /** The size of the texture file's height in pixels. */
    public float textureHeight = 32;

    /** The X offset into the texture used for displaying this model */
    public int textureOffsetX;

    /** The Y offset into the texture used for displaying this model */
    public int textureOffsetY;
    
    
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    
    
    //in degrees
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    
    public boolean mirror;

    public final String name;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    
    public float sizeX;
    public float sizeY;
    public float sizeZ;
    
    public TechneBox(TechneModel parentModel, String name){
    	this.parentModel = parentModel;
    	this.name = name;
    }
    
    public void setTextureOffset(int x, int y)
    {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
    }
    
    public void setRotationPoint(float x, float y, float z)
    {
        this.rotationPointX = x;
        this.rotationPointY = y;
        this.rotationPointZ = z;
    }
    
    public void setTextureSize(int x, int y)
    {
    	if (x == 0 || y == 0) return;
        this.textureWidth = (float)x;
        this.textureHeight = (float)y;
    }
    
    public void setOffset(float x, float y, float z){
    	this.offsetX = x;
    	this.offsetY = y;
    	this.offsetZ = z;
    }
    
    public void setDimensions(float x, float y, float z){
    	this.sizeX = x;
    	this.sizeY = y;
    	this.sizeZ = z;
    }
    
    public void setRotateAngles(float x, float y, float z){
    	this.rotateAngleX = x;
    	this.rotateAngleY = y;
    	this.rotateAngleZ = z;
    }
	
}
