package ru.gloomyfolken.tcn2obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Sets;

import ru.gloomyfolken.tcn2obj.obj.Face;
import ru.gloomyfolken.tcn2obj.obj.ObjModel;
import ru.gloomyfolken.tcn2obj.obj.Shape;
import ru.gloomyfolken.tcn2obj.obj.TextureCoords;
import ru.gloomyfolken.tcn2obj.obj.Vertex;
import ru.gloomyfolken.tcn2obj.tbl.JsonTabulaModel;
import ru.gloomyfolken.tcn2obj.tbl.TabulaBox;
import ru.gloomyfolken.tcn2obj.tbl.TabulaModel;
import ru.gloomyfolken.tcn2obj.tbl.components.CubeGroup;
import ru.gloomyfolken.tcn2obj.tbl.components.CubeInfo;

public class TblConverter
{
	public HashSet<CubeInfo> cubes = Sets.newHashSet();
	public TabulaModel model;

	public ObjModel tcn2obj(TabulaModel model, float scale)
	{
		ObjModel obj = new ObjModel();
		this.model = model;
		for (CubeInfo cube : model.model.getCubes())
		{
			addFromCube(cubes, cube);
		}
		for (CubeGroup group : model.model.getCubeGroups())
		{
			addFromGroups(cubes, group);
		}

//		Map<String, List<Shape>> childrenMap = new HashMap<String, List<Shape>>();
//		for (TabulaBox box : model.boxes)
//		{
//			Shape shape = convertBoxToShape(obj, box, scale);
//			String parentName;
//			if(shape.name.endsWith("_c") && boxExists(model.boxes, (parentName = shape.name.substring(0, shape.name.length() - 2)))) {
//				List<Shape> currentChildren = childrenMap.get(parentName);
//				if(currentChildren == null) 
//					currentChildren = new ArrayList<Shape>();
//				currentChildren.add(shape);
//				childrenMap.put(parentName, currentChildren);
//				System.out.println("Child found " + shape.name);
//			} 
//			else
//				obj.shapes.add(convertBoxToShape(obj, box, scale));
//		}
//		
//		for(Shape shape : obj.shapes) {
//			if(childrenMap.containsKey(shape.name)) {
//				System.out.println("Adding child faces " + shape.name);
//				for(Shape childShape : childrenMap.get(shape.name))
//					shape.faces.addAll(childShape.faces);
//			}
//		}
		
		for (TabulaBox box : model.boxes)
		{
			obj.shapes.add(convertBoxToShape(obj, box, scale));
		}

		return obj;
	}

	private boolean boxExists(List<TabulaBox> boxes, String boxName) {
		for(TabulaBox box : boxes) {
			if(box.name.equals(boxName))
				return true;
		}
		return false;
	}

	private Shape convertBoxToShape(ObjModel model, TabulaBox box, float scale)
	{
		Shape shape = new Shape(model, box.name);

		Vertex frontTopLeft = new Vertex(box.offsetX, box.offsetY, box.offsetZ);
		Vertex frontTopRight = new Vertex(box.offsetX + box.sizeX, box.offsetY, box.offsetZ);
		Vertex frontBottomRight = new Vertex(box.offsetX + box.sizeX, box.offsetY + box.sizeY, box.offsetZ);
		Vertex frontBottomLeft = new Vertex(box.offsetX, box.offsetY + box.sizeY, box.offsetZ);
		Vertex backTopLeft = new Vertex(box.offsetX, box.offsetY, box.offsetZ + box.sizeZ);
		Vertex backTopRight = new Vertex(box.offsetX + box.sizeX, box.offsetY, box.offsetZ + box.sizeZ);
		Vertex backBottomRight = new Vertex(box.offsetX + box.sizeX, box.offsetY + box.sizeY, box.offsetZ + box.sizeZ);
		Vertex backBottomLeft = new Vertex(box.offsetX, box.offsetY + box.sizeY, box.offsetZ + box.sizeZ);

		if (box.sizeX > 0 && box.sizeY > 0)
		{
			// front
			shape.faces.add(new Face(shape)
					.append(frontBottomLeft, createUV(box, box.sizeZ, box.sizeZ + box.sizeY, box.sizeX, false))
					.append(frontBottomRight,
							createUV(box, box.sizeZ + box.sizeX, box.sizeZ + box.sizeY, -box.sizeX, false))
					.append(frontTopRight, createUV(box, box.sizeZ + box.sizeX, box.sizeZ, -box.sizeX, false))
					.append(frontTopLeft, createUV(box, box.sizeZ, box.sizeZ, box.sizeX, false)));
			// back
			shape.faces
			.add(new Face(shape)
					.append(backBottomRight,
							createUV(box, box.sizeZ * 2 + box.sizeX, box.sizeZ + box.sizeY, box.sizeX, false))
					.append(backBottomLeft,
							createUV(box, box.sizeZ * 2 + box.sizeX * 2, box.sizeZ + box.sizeY, -box.sizeX,
									false))
					.append(backTopLeft, createUV(box, box.sizeZ * 2 + box.sizeX * 2, box.sizeZ, -box.sizeX, false))
					.append(backTopRight, createUV(box, box.sizeZ * 2 + box.sizeX, box.sizeZ, box.sizeX, false)));
		}

		if (box.sizeX > 0 && box.sizeZ > 0)
		{
			// top
			shape.faces.add(new Face(shape).append(frontTopLeft, createUV(box, box.sizeZ, box.sizeZ, box.sizeX, false))
					.append(frontTopRight, createUV(box, box.sizeZ + box.sizeX, box.sizeZ, -box.sizeX, false))
					.append(backTopRight, createUV(box, box.sizeZ + box.sizeX, 0, -box.sizeX, false))
					.append(backTopLeft, createUV(box, box.sizeZ, 0, box.sizeX, false)));

			// bottom
			shape.faces
			.add(new Face(shape)
					.append(backBottomLeft, createUV(box, box.sizeZ + box.sizeX, box.sizeZ, box.sizeX, false))
					.append(backBottomRight,
							createUV(box, box.sizeZ + box.sizeX * 2, box.sizeZ, -box.sizeX, false))
					.append(frontBottomRight, createUV(box, box.sizeZ + box.sizeX * 2, 0, -box.sizeX, false))
					.append(frontBottomLeft, createUV(box, box.sizeZ + box.sizeX, 0, box.sizeX, false)));
		}

		if (box.sizeY > 0 && box.sizeZ > 0)
		{
			// left
			shape.faces
			.add(new Face(shape)
					.append(backBottomLeft,
							createUV(box, 0, box.sizeZ + box.sizeY, box.sizeX + box.sizeZ * 2, true))
					.append(frontBottomLeft, createUV(box, box.sizeZ, box.sizeZ + box.sizeY, box.sizeX, true))
					.append(frontTopLeft, createUV(box, box.sizeZ, box.sizeZ, box.sizeX, true))
					.append(backTopLeft, createUV(box, 0, box.sizeZ, box.sizeX + box.sizeZ * 2, true)));

			// right
			shape.faces.add(new Face(shape)
					.append(frontBottomRight,
							createUV(box, box.sizeZ + box.sizeX, box.sizeZ + box.sizeY, -box.sizeX, true))
					.append(backBottomRight,
							createUV(box, box.sizeZ * 2 + box.sizeX, box.sizeZ + box.sizeY, -box.sizeX - box.sizeZ * 2,
									true))
					.append(backTopRight,
							createUV(box, box.sizeZ * 2 + box.sizeX, box.sizeZ, -box.sizeX - box.sizeZ * 2, true))
					.append(frontTopRight, createUV(box, box.sizeZ + box.sizeX, box.sizeZ, -box.sizeX, true)));
		}

		shape.rotate(-box.rotateAngleX, 1, 0, 0);
		shape.rotate(-box.rotateAngleY, 0, 1, 0);
		shape.rotate(-box.rotateAngleZ, 0, 0, 1);

		shape.scale(new Vector3f(box.scaleX, box.scaleY, box.scaleZ));

		shape.translate(new Vector3f(box.rotationPointX, box.rotationPointY, box.rotationPointZ));

		offsetBoxes(box.model, box.cube, shape);
		// fix Y axis direction
		shape.rotate(180, 0, 0, 1);
		// fix x direction
		shape.rotate(180, 0, 1, 0);

		shape.scale(new Vector3f(box.parentModel.scaleX * scale, box.parentModel.scaleY * scale,
				box.parentModel.scaleZ * scale));

		return shape;
	}

	private void offsetBoxes(JsonTabulaModel model, CubeInfo start, Shape shape)
	{
		for (CubeInfo cube : cubes)
		{
			if (cube == start)
			{
				CubeInfo parent = getParent(cubes, cube);
				if (parent != null)
				{
					offsetToParents(cube, parent, shape);
				}
				return;
			}
		}
	}

	private CubeInfo getParent(HashSet<CubeInfo> cubes, CubeInfo cube)
	{
		for (CubeInfo cube2 : cubes)
		{
			if (cube2.identifier.equals(cube.parentIdentifier)) { return cube2; }
		}
		return null;
	}

	private void addFromCube(HashSet<CubeInfo> cubes, CubeInfo cube)
	{
		cubes.add(cube);
		for (CubeInfo sub : cube.children)
		{
			addFromCube(cubes, sub);
		}
	}

	private void addFromGroups(HashSet<CubeInfo> cubes, CubeGroup group)
	{
		cubes.addAll(group.cubes);
		for (CubeGroup sub : group.cubeGroups)
		{
			addFromGroups(cubes, sub);
		}
	}

	private void offsetToParents(CubeInfo cube, CubeInfo parent, Shape shape)
	{
		if (parent != null)
		{
			applyParentTransforms(parent, shape);
		}
	}

	private Vector3f getFromDoubleArr(double[] arr)
	{
		return new Vector3f((float) arr[0], (float) arr[1], (float) arr[2]);
	}

	private void applyParentTransforms(CubeInfo parent, Shape shape)
	{
		Vector3f parentRotation = getFromDoubleArr(parent.rotation);
		Vector3f parentPosition = getFromDoubleArr(parent.position);
		shape.rotate(-parentRotation.x, 1, 0, 0);
		shape.rotate(-parentRotation.y, 0, 1, 0);
		shape.rotate(-parentRotation.z, 0, 0, 1);
		shape.translate(parentPosition);
		CubeInfo parentparent = getParent(cubes, parent);
		if (parentparent != null)
		{
			applyParentTransforms(parentparent, shape);
		}
	}

	/** @param box
	 *            The box that we are currently converting
	 * @param baseU
	 * @param baseV
	 * @param mirrorOffset
	 *            if side == false: positive face x size for minU coords and
	 *            negative one for maxU if side == true: (FRONT face size +
	 *            (is_adjacent_to_front_face ? 0 : face_size*2)) * (right_face ?
	 *            -1 : 1)
	 * @return */
	private TextureCoords createUV(TabulaBox box, float baseU, float baseV, float mirrorOffset, boolean side)
	{
		if (!box.mirror)
		{
			return new TextureCoords(((box.textureOffsetX + baseU) / box.textureWidth),
					(1 - (box.textureOffsetY + baseV) / box.textureHeight));
		}
		else
		{
			return new TextureCoords(((box.textureOffsetX + baseU + mirrorOffset) / box.textureWidth),
					(1 - (box.textureOffsetY + baseV) / box.textureHeight));
		}
	}
}
