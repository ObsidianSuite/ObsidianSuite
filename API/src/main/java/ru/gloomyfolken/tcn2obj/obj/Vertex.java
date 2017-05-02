package ru.gloomyfolken.tcn2obj.obj;

import java.util.Locale;

import org.lwjgl.util.vector.Vector3f;

public class Vertex {

	public Vector3f position;
	public int index;
	
	public Vertex(float x, float y, float z){
		this(new Vector3f(x, y, z));
	}
	
	public Vertex(Vector3f position){
		this.position = position;
	}
	
	void register(ObjModel model){
		index = model.nextVertexIndex++;
	}
	
	@Override
	public String toString(){
		position.y += 1.5;
		
		StringBuilder sb = new StringBuilder();
		sb.append("v ");
		sb.append(String.format(Locale.US, "%.6f", position.x)).append(" ");
		sb.append(String.format(Locale.US, "%.6f", position.y)).append(" ");
		sb.append(String.format(Locale.US, "%.6f", position.z));
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Vertex){
			Vertex vertex = (Vertex)obj;
			return vertex.position.x == position.x &&
					vertex.position.y == position.y &&
					vertex.position.z == position.z;
		}
		return false;
	}

}
