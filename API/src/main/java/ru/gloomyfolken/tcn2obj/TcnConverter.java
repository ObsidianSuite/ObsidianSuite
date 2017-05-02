package ru.gloomyfolken.tcn2obj;

import org.lwjgl.util.vector.Vector3f;

import ru.gloomyfolken.tcn2obj.obj.Face;
import ru.gloomyfolken.tcn2obj.obj.ObjModel;
import ru.gloomyfolken.tcn2obj.obj.Shape;
import ru.gloomyfolken.tcn2obj.obj.TextureCoords;
import ru.gloomyfolken.tcn2obj.obj.Vertex;
import ru.gloomyfolken.tcn2obj.tcn.TechneBox;
import ru.gloomyfolken.tcn2obj.tcn.TechneModel;

public class TcnConverter {

	public ObjModel tcn2obj(TechneModel tcn, float scale){
		ObjModel obj = new ObjModel();
		
		for (TechneBox box : tcn.boxes){
			obj.shapes.add(convertBoxToShape(obj, box, scale));
		}
		
		return obj;
	}
	
	private Shape convertBoxToShape(ObjModel model, TechneBox box, float scale){
		Shape shape = new Shape(model, box.name);
		
		Vertex frontTopLeft = new Vertex(box.offsetX, box.offsetY, box.offsetZ);
		Vertex frontTopRight = new Vertex(box.offsetX+box.sizeX, box.offsetY, box.offsetZ);
		Vertex frontBottomRight = new Vertex(box.offsetX+box.sizeX, box.offsetY+box.sizeY, box.offsetZ);
		Vertex frontBottomLeft = new Vertex(box.offsetX, box.offsetY+box.sizeY, box.offsetZ);
		Vertex backTopLeft = new Vertex(box.offsetX, box.offsetY, box.offsetZ+box.sizeZ);
		Vertex backTopRight = new Vertex(box.offsetX+box.sizeX, box.offsetY, box.offsetZ+box.sizeZ);
		Vertex backBottomRight = new Vertex(box.offsetX+box.sizeX, box.offsetY+box.sizeY, box.offsetZ+box.sizeZ);
		Vertex backBottomLeft = new Vertex(box.offsetX, box.offsetY+box.sizeY, box.offsetZ+box.sizeZ);
		
		if (box.sizeX > 0 && box.sizeY > 0){
			//front
			shape.faces.add(new Face(shape)
				.append(frontBottomLeft, createUV(box, box.sizeZ, box.sizeZ+box.sizeY, box.sizeX, false))
				.append(frontBottomRight, createUV(box, box.sizeZ+box.sizeX, box.sizeZ+box.sizeY, -box.sizeX, false))
				.append(frontTopRight, createUV(box, box.sizeZ+box.sizeX, box.sizeZ, -box.sizeX, false))
				.append(frontTopLeft, createUV(box, box.sizeZ, box.sizeZ, box.sizeX, false))
				);
			//back
			shape.faces.add(new Face(shape)
				.append(backBottomRight, createUV(box, box.sizeZ*2+box.sizeX, box.sizeZ+box.sizeY, box.sizeX, false))
				.append(backBottomLeft, createUV(box, box.sizeZ*2+box.sizeX*2, box.sizeZ+box.sizeY, -box.sizeX, false))
				.append(backTopLeft, createUV(box, box.sizeZ*2+box.sizeX*2, box.sizeZ, -box.sizeX, false))
				.append(backTopRight, createUV(box, box.sizeZ*2+box.sizeX, box.sizeZ, box.sizeX, false))
				);
		}
		
		if (box.sizeX > 0 && box.sizeZ > 0){
			//top
			shape.faces.add(new Face(shape)
				.append(frontTopLeft, createUV(box, box.sizeZ, box.sizeZ, box.sizeX, false))
				.append(frontTopRight, createUV(box, box.sizeZ+box.sizeX, box.sizeZ, -box.sizeX, false))
				.append(backTopRight, createUV(box, box.sizeZ+box.sizeX, 0, -box.sizeX, false))
				.append(backTopLeft, createUV(box, box.sizeZ, 0, box.sizeX, false))
				);
			
			//bottom
			shape.faces.add(new Face(shape)
				.append(backBottomLeft, createUV(box, box.sizeZ+box.sizeX, box.sizeZ, box.sizeX, false))
				.append(backBottomRight, createUV(box, box.sizeZ+box.sizeX*2, box.sizeZ, -box.sizeX, false))
				.append(frontBottomRight, createUV(box, box.sizeZ+box.sizeX*2, 0, -box.sizeX, false))
				.append(frontBottomLeft, createUV(box, box.sizeZ+box.sizeX, 0, box.sizeX, false))
				);
		}
		
		if (box.sizeY > 0 && box.sizeZ > 0){
			//left
			shape.faces.add(new Face(shape)
				.append(backBottomLeft, createUV(box, 0, box.sizeZ+box.sizeY, box.sizeX+box.sizeZ*2, true))
				.append(frontBottomLeft, createUV(box, box.sizeZ, box.sizeZ+box.sizeY, box.sizeX, true))
				.append(frontTopLeft, createUV(box, box.sizeZ, box.sizeZ, box.sizeX, true))
				.append(backTopLeft, createUV(box, 0, box.sizeZ, box.sizeX+box.sizeZ*2, true))
				);
			
			//right
			shape.faces.add(new Face(shape)
				.append(frontBottomRight, createUV(box, box.sizeZ+box.sizeX, box.sizeZ+box.sizeY, -box.sizeX, true))
				.append(backBottomRight, createUV(box, box.sizeZ*2+box.sizeX, box.sizeZ+box.sizeY, -box.sizeX-box.sizeZ*2, true))
				.append(backTopRight, createUV(box, box.sizeZ*2+box.sizeX, box.sizeZ, -box.sizeX-box.sizeZ*2, true))
				.append(frontTopRight, createUV(box, box.sizeZ+box.sizeX, box.sizeZ, -box.sizeX, true))
				);
		}
		
		shape.rotate(-box.rotateAngleX, 1, 0, 0);
		shape.rotate(-box.rotateAngleY, 0, 1, 0);
		shape.rotate(-box.rotateAngleZ, 0, 0, 1);
		
		shape.translate(new Vector3f(box.rotationPointX, box.rotationPointY, box.rotationPointZ));
		
		
		//fix Y axis direction
		shape.rotate(180, 0, 0, 1);
		
		shape.scale(new Vector3f(box.parentModel.scaleX*scale, 
				box.parentModel.scaleY*scale, 
				box.parentModel.scaleZ*scale));
		
		return shape;
	}
	
	/**
	 * 
	 * @param box The box that we are currently converting
	 * @param baseU 
	 * @param baseV
	 * @param mirrorOffset 
	 * 			if side == false: positive face x size for minU coords and negative one for maxU
	 * 			if side == true: (FRONT face size + (is_adjacent_to_front_face ? 0 : face_size*2)) * (right_face ? -1 : 1)
	 * @return
	 */
	private TextureCoords createUV(TechneBox box, float baseU, float baseV, float mirrorOffset, boolean side){
		if (!box.mirror){
			return new TextureCoords(((box.textureOffsetX + baseU) / box.textureWidth),
					(1-(box.textureOffsetY + baseV) / box.textureHeight));
		} else {
			return new TextureCoords(((box.textureOffsetX + baseU + mirrorOffset) / box.textureWidth),
					(1-(box.textureOffsetY + baseV) / box.textureHeight));
		}
	}
}
