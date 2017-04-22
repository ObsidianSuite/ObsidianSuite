package ru.gloomyfolken.tcn2obj.obj;

import java.util.ArrayList;
import java.util.List;

public class ObjModel {

	public List<Shape> shapes = new ArrayList<Shape>();
	public int nextVertexIndex = 1;
	public int nextTexIndex = 1;
	
	public List<String> toStringList(){
		List<String> lines = new ArrayList<String>();
		lines.add("# Converted with GloomyFolken's tcn2obj Converter");
		lines.add("#");
		for (Shape shape : shapes)
			lines.addAll(shape.toStringList());
		return lines;
	}
	
}
