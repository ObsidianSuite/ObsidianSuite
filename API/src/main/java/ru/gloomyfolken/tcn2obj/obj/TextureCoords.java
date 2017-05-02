package ru.gloomyfolken.tcn2obj.obj;

import java.util.Locale;

import org.lwjgl.util.vector.Vector2f;

public class TextureCoords {

	public Vector2f uvCoords;	
	public int index;

    public TextureCoords(float u, float v) {
        this(new Vector2f(u, v));
    }
	
	public TextureCoords(Vector2f uvCoords){
		this.uvCoords = uvCoords;
	}
	
	void register(ObjModel model){
		index = model.nextTexIndex++;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("vt ");
		sb.append(String.format(Locale.US, "%.6f", uvCoords.x)).append(" ");
		sb.append(String.format(Locale.US, "%.6f", uvCoords.y));
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof TextureCoords){
			TextureCoords uv = (TextureCoords)obj;
			return uv.uvCoords.x == uvCoords.x &&
					uv.uvCoords.y == uvCoords.y;
		}
		return false;
	}
	
}
