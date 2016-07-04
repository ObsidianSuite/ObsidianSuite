package MCEntityAnimator.render.objRendering;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationParenting;
import MCEntityAnimator.render.objRendering.parts.PartObj;

public class Bend extends GroupObject
{
	private PartObj child;
	private PartObj parent;
	private List<Vertex> childMainVertices;
	private List<Vertex> parentMainVertices;
	private HashMap<Face, float[]> originalVertices;
	private float width;
	private ArrayList<UVMap> parentUVMaps;
	private ArrayList<UVMap> childUVMaps;

	private ArrayList<Vertex[]> bendPartVertices;
	private HashMap<Face, TextureCoordinate[]> preBendTextureCoords;
	private HashMap<Face, TextureCoordinate[]> postBendTextureCoords;
	//A list of all the faces that make up the bend, grouped into sets of eight (for each part of the bend).
	private ArrayList<Face[]> bendFaces;

	private static final int curveSplit = 11;


	private Bend()
	{

	}

	public Bend(PartObj par0Child, PartObj par1Parent) throws Exception
	{
		super("", par0Child.groupObj.glDrawingMode);
		child = par0Child;
		parent = par1Parent;
		originalVertices = new HashMap<Face, float[]>();
		preBendTextureCoords = new HashMap<Face, TextureCoordinate[]>();
		postBendTextureCoords = new HashMap<Face, TextureCoordinate[]>();
		parentUVMaps = new ArrayList<UVMap>();
		childUVMaps = new ArrayList<UVMap>();
		bendPartVertices = new ArrayList<Vertex[]>();
		bendFaces = new ArrayList<Face[]>();

		storeDefaultTextureCoordinates(child.groupObj.faces);
		storeDefaultTextureCoordinates(parent.groupObj.faces);
		init();
		create();
	}

	public void init() throws Exception
	{
		float[] rotationPoint = child.getRotationPoint();

		childMainVertices = getClosestVertices(child, rotationPoint);
		parentMainVertices = getClosestVertices(parent, rotationPoint);

		try 
		{
			width = calculateWidth(parentMainVertices);
		} 
		catch (Exception e)
		{
			System.out.println(e.getMessage()); 
		}


		for(Face f : getSideTopFaces(parent.groupObj))
		{
			parentUVMaps.add(new UVMap(f));
		}

		for(Face f : getSideTopFaces(child.groupObj))
		{
			childUVMaps.add(new UVMap(f));
		}

		for(Vertex v : parentMainVertices)
		{
			v.y += width;
		}

		for(Vertex v : childMainVertices)
		{
			v.y -= width;
		}

		setupParentTextures();
		setupChildTextures();

	}

	private void setupParentTextures()
	{
		ArrayList<Face> sideFaces = new ArrayList<Face>();
		List<Vertex> topPartTopVertices = getClosestVertices(parent, parent.getRotationPoint());

		for(Face f : parent.groupObj.faces)
		{
			int topVerticesIncluded = 0;
			for(Vertex v : f.vertices)
			{
				for(Vertex w : topPartTopVertices)
				{
					if(v.x == w.x && v.y == w.y && v.z == w.z)
					{
						topVerticesIncluded++;
						break;
					}
				}
			}
			if(topVerticesIncluded == 1 || topVerticesIncluded == 2)
			{
				sideFaces.add(f);
			}
		}

		for(Face f : sideFaces)
		{
			try 
			{
				getUVMapForFace(f, true).setTextureCoordinatesForFace(f);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		parent.setDefaultTCsToCurrentTCs();

	}

	private void setupChildTextures()
	{
		ArrayList<Face> sideFaces = new ArrayList<Face>();
		List<Vertex> lowerPartTopVertices = getClosestVertices(child, child.getRotationPoint());

		for(Face f : child.groupObj.faces)
		{
			int topVerticesIncluded = 0;
			for(Vertex v : f.vertices)
			{
				for(Vertex w : lowerPartTopVertices)
				{
					if(v.x == w.x && v.y == w.y && v.z == w.z)
					{
						topVerticesIncluded++;
						break;
					}
				}
			}
			if(topVerticesIncluded == 1 || topVerticesIncluded == 2)
			{
				sideFaces.add(f);
			}
		}

		for(Face f : sideFaces)
		{
			try 
			{
				getUVMapForFace(f, false).setTextureCoordinatesForFace(f);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		child.setDefaultTCsToCurrentTCs();

	}

	private void storeDefaultTextureCoordinates(ArrayList<Face> faces)
	{
		for(Face f : faces)
		{
			if(f.textureCoordinates == null)
			{
				f.textureCoordinates = new TextureCoordinate[3];
				for(int i = 0; i < 3; i++)
				{
					f.textureCoordinates[i] = new TextureCoordinate(0, 0);
				}
			}	
			
			TextureCoordinate[] coordsToStore = new TextureCoordinate[3];
			for(int i = 0; i < 3; i++)
			{
				coordsToStore[i] = new TextureCoordinate(f.textureCoordinates[i].u, f.textureCoordinates[i].v);
			}
				
			preBendTextureCoords.put(f, coordsToStore);
		}
	}

	public void restoreDefaultTextureCoordinates(PartObj part)
	{
		for(Face f : part.groupObj.faces)
		{
			f.textureCoordinates = preBendTextureCoords.get(f);
		}
		part.setDefaultTCsToCurrentTCs();
	}


	private float calculateWidth(List<Vertex> mainVertices) throws Exception
	{
		Vertex w = mainVertices.get(0);
		for(Vertex v : mainVertices)
		{
			if(!v.equals(w) && v.y == w.y && v.z == w.z)
			{
				return Math.abs(v.x - w.x);
			}
		}		
		throw new Exception("Unable to calculate width for bend for parts " + child.getName() + " and " + parent.getName() + ". You should probably report this to Joe.");
	}

	public void create()
	{
		this.faces.clear();
		createBend();
	}

	@Override
	public void render()
	{	
		GL11.glPushMatrix();
		PartObj topParent = parent;
		ArrayList<PartObj> allParents = new ArrayList<PartObj>();
		allParents.add(topParent);
		AnimationParenting anipar = AnimationData.getAnipar(parent.modelObj.getEntityType());
		while(anipar.hasParent(topParent))
		{
			topParent = anipar.getParent(topParent);
			allParents.add(0, topParent);
		}

		for(PartObj p : allParents)
		{
			GL11.glTranslatef(-p.getRotationPoint(0), -p.getRotationPoint(1), -p.getRotationPoint(2));
			GL11.glRotated((p.getValue(0) - p.getOriginalValues()[0])/Math.PI*180.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotated((p.getValue(1) - p.getOriginalValues()[1])/Math.PI*180.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotated((p.getValue(2) - p.getOriginalValues()[2])/Math.PI*180.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(p.getRotationPoint(0), p.getRotationPoint(1), p.getRotationPoint(2));
		}

		updateBend();   
		super.render();
		GL11.glPopMatrix();
	}

	private void rotateVertex(Vertex v, float[] rotation, float[] rotationPoint)
	{
		float[] vector = new float[]{v.x - rotationPoint[0], v.y - rotationPoint[1], v.z - rotationPoint[2]};

		vector = zMatrix(vector, rotation[2]);
		vector = yMatrix(vector, rotation[1]);
		vector = xMatrix(vector, rotation[0]);

		v.x = vector[0] + rotationPoint[0];
		v.y = vector[1] + rotationPoint[1];
		v.z = vector[2] + rotationPoint[2];
	}

	private float[] xMatrix(float[] vector, double angle)
	{
		float x = vector[0], y = vector[1], z = vector[2];
		float rx = x;
		float ry = (float) (y*Math.cos(angle) - z*Math.sin(angle));
		float rz = (float) (y*Math.sin(angle) + z*Math.cos(angle));
		return new float[]{rx, ry, rz};
	}

	private float[] yMatrix(float[] vector, double angle)
	{
		float x = vector[0], y = vector[1], z = vector[2];
		float rx = (float) (x*Math.cos(angle) + z*Math.sin(angle));
		float ry = y;
		float rz = (float) (z*Math.cos(angle) - x*Math.sin(angle));
		return new float[]{rx, ry, rz};
	}

	private float[] zMatrix(float[] vector, double angle)
	{
		float x = vector[0], y = vector[1], z = vector[2];
		float rx = (float) (x*Math.cos(angle) - y*Math.sin(angle));
		float ry = (float) (x*Math.sin(angle) + y*Math.cos(angle));
		float rz = z;
		return new float[]{rx, ry, rz};
	}

	private void createBend()
	{
		float dT = 1/(float) (curveSplit);

		Vertex[] topVertices = new Vertex[4];
		Vertex[] bottomVertices = new Vertex[4];
		float[] baseX = new float[4];
		float[] baseZ = new float[4];

		for(int i = 0; i < 4; i++)
		{
			topVertices[i] = parentMainVertices.get(i);
			baseX[i] = topVertices[i].x;
			baseZ[i] = topVertices[i].z;			
		}

		float baseY = topVertices[0].y;

		for(double t=0.0; t<=1; t+=dT)
		{
			for(int i = 0; i < 4; i++)
			{
				Vertex endPoint = new Vertex(baseX[i], childMainVertices.get(0).y, baseZ[i]);
				rotateVertex(endPoint, child.getValues(), new float[]{-child.getRotationPoint(0), -child.getRotationPoint(1), -child.getRotationPoint(2)});

				float bezierX = baseX[i];
				float bezierY = -child.getRotationPoint(1);
				float bezierZ = baseZ[i];


				if(child.getValue(0) != 0.0F || child.getValue(2) != 0.0F)
				{
					//end point = b2.
					//additional point = b1.
					//base x and z = a2.x and a2.z
					//child.getRotationPoint(1) = default y.
					
					Vertex additionalPoint = new Vertex(baseX[i], childMainVertices.get(0).y - 1, baseZ[i]);
					rotateVertex(additionalPoint, child.getValues(), new float[]{-child.getRotationPoint(0), -child.getRotationPoint(1), -child.getRotationPoint(2)});
					float p1 = endPoint.z;
					float p2 = additionalPoint.z;
					float p3 = bezierZ;
					if(Math.abs(child.getValue(0)) < Math.abs(child.getValue(2)))
					{
						p1 = endPoint.x;
						p2 = additionalPoint.x;
						p3 = bezierX;
					}
					float newBY = getIntersection(p1, endPoint.y, p2, additionalPoint.y, p3);
					if(newBY < baseY+1 && newBY > childMainVertices.get(0).y-1)
					{
						bezierY = newBY;
					}
				}

				float x = (float) ((1-t)*(1-t)*baseX[i] + 2*(1-t)*t*bezierX+t*t*endPoint.x);
				float y = (float) ((1-t)*(1-t)*baseY + 2*(1-t)*t*bezierY+t*t*endPoint.y);
				float z = (float) ((1-t)*(1-t)*baseZ[i] + 2*(1-t)*t*bezierZ+t*t*endPoint.z);

				bottomVertices[i] = new Vertex(x, y, z);
			}

			Vertex[] allVertices = new Vertex[]{topVertices[0], topVertices[1], topVertices[2], topVertices[3], bottomVertices[0], bottomVertices[1], bottomVertices[2], bottomVertices[3]};

			bendPartVertices.add(allVertices);

			generateFaces(allVertices, t <= 0.5);

			for(int i = 0; i < 4; i++)
			{
				topVertices[i] = bottomVertices[i];
			}
		}

		for(int i = 0; i < 4; i++)
		{	
			Vertex endPoint = new Vertex(baseX[i], childMainVertices.get(0).y, baseZ[i]);
			rotateVertex(endPoint, child.getValues(), new float[]{-child.getRotationPoint(0), -child.getRotationPoint(1), -child.getRotationPoint(2)});
			bottomVertices[i] = endPoint;
		}

		Vertex[] allVertices = new Vertex[]{topVertices[0], topVertices[1], topVertices[2], topVertices[3], bottomVertices[0], bottomVertices[1], bottomVertices[2], bottomVertices[3]};

		bendPartVertices.add(allVertices);

		generateFaces(allVertices, false);
	}

	private void updateBend()
	{
		float dT = 1/(float) (curveSplit);

		Vertex[] topVertices = new Vertex[4];
		Vertex[] bottomVertices = new Vertex[4];
		float[] baseX = new float[4];
		float[] baseZ = new float[4];

		for(int i = 0; i < 4; i++)
		{
			topVertices[i] = parentMainVertices.get(i);
			baseX[i] = topVertices[i].x;
			baseZ[i] = topVertices[i].z;			
		}

		float baseY = topVertices[0].y;

		int index = 0;

		for(double t=0.0; t<=1; t+=dT)
		{
			for(int i = 0; i < 4; i++)
			{
				Vertex endPoint = new Vertex(baseX[i], childMainVertices.get(0).y, baseZ[i]);
				rotateVertex(endPoint, child.getValues(), new float[]{-child.getRotationPoint(0), -child.getRotationPoint(1), -child.getRotationPoint(2)});

				float bezierX = baseX[i];
				float bezierY = -child.getRotationPoint(1);
				float bezierZ = baseZ[i];


				if(child.getValue(0) != 0.0F || child.getValue(2) != 0.0F)
				{
					Vertex additionalPoint = new Vertex(baseX[i], childMainVertices.get(0).y - 1, baseZ[i]);
					rotateVertex(additionalPoint, child.getValues(), new float[]{-child.getRotationPoint(0), -child.getRotationPoint(1), -child.getRotationPoint(2)});
					float p1 = endPoint.z;
					float p2 = additionalPoint.z;
					float p3 = bezierZ;
					if(Math.abs(child.getValue(0)) < Math.abs(child.getValue(2)))
					{
						p1 = endPoint.x;
						p2 = additionalPoint.x;
						p3 = bezierX;
					}
					float newBY = getIntersection(p1, endPoint.y, p2, additionalPoint.y, p3);
					if(newBY < baseY+1 && newBY > childMainVertices.get(0).y-1)
					{
						bezierY = newBY;
					}
				}

				float x = (float) ((1-t)*(1-t)*baseX[i] + 2*(1-t)*t*bezierX+t*t*endPoint.x);
				float y = (float) ((1-t)*(1-t)*baseY + 2*(1-t)*t*bezierY+t*t*endPoint.y);
				float z = (float) ((1-t)*(1-t)*baseZ[i] + 2*(1-t)*t*bezierZ+t*t*endPoint.z);

				bottomVertices[i] = new Vertex(x, y, z);
			}

			Vertex[] allVertices = new Vertex[]{topVertices[0], topVertices[1], topVertices[2], topVertices[3], bottomVertices[0], bottomVertices[1], bottomVertices[2], bottomVertices[3]};

			for(int i = 0; i < 8; i++)
			{
				Vertex v = bendPartVertices.get(index)[i];
				v.x = allVertices[i].x;
				v.y = allVertices[i].y;
				v.z = allVertices[i].z;
			}

			index++;

			for(int i = 0; i < 4; i++)
			{
				topVertices[i] = bottomVertices[i];
			}
		}

		for(int i = 0; i < 4; i++)
		{	
			Vertex endPoint = new Vertex(baseX[i], childMainVertices.get(0).y, baseZ[i]);
			rotateVertex(endPoint, child.getValues(), new float[]{-child.getRotationPoint(0), -child.getRotationPoint(1), -child.getRotationPoint(2)});
			bottomVertices[i] = endPoint;
		}

		Vertex[] allVertices = new Vertex[]{topVertices[0], topVertices[1], topVertices[2], topVertices[3], bottomVertices[0], bottomVertices[1], bottomVertices[2], bottomVertices[3]};

		for(int i = 0; i < 8; i++)
		{
			Vertex v = bendPartVertices.get(index)[i];
			v.x = allVertices[i].x;
			v.y = allVertices[i].y;
			v.z = allVertices[i].z;
		}
		
		//Change coords of bend faces if the model is untextured
		for(int i = 0; i < bendFaces.size(); i++)
		{
			Face[] faces = bendFaces.get(i);
			
			boolean useLowerPart = i > (int) (curveSplit/2);
			
			PartObj part = useLowerPart ? child : parent;
			
			boolean highlight = part.modelObj.isPartHighlighted(part);
			boolean main = part.modelObj.isMainHighlight(part);
			
			
			float u = 0.0F;
			float v = 0.0F;
			if(highlight)
			{
				if(main)
				{
					u = 0.0F;
					v = 0.75F;
				}
				else
				{
					u = 0.75F;
					v = 0.0F;
				}
			}
			
			
			if(!child.modelObj.renderWithTexture)
			{
				for(Face f : faces)
				{
					TextureCoordinate texCo = new TextureCoordinate(u, v);
					f.textureCoordinates = new TextureCoordinate[]{texCo, texCo, texCo};
				}
			}
			else
			{
				if(highlight)
				{
					for(Face f : faces)
					{
						TextureCoordinate texCo = new TextureCoordinate(0.0F, 0.0F);
						f.textureCoordinates = new TextureCoordinate[]{texCo, texCo, texCo};
					}
				}
				else
				{
					for(Face f : faces)
					{
						f.textureCoordinates = postBendTextureCoords.get(f);
					}
				}			
			}
		}
		
	}


	/**
	 * Get the intersection between the vertical line of the parent(fixed) and the line of the child.
	 */
	private float getIntersection(float x1, float y1, float x2, float y2, float x)
	{
		//Gradient
		float m = (y1 - y2)/(x1 - x2); 
		//Intercept
		float c = y1 - m*x1;
		//Calculate intersection
		return m*x + c;
	}

	/**
	 * Create the eight faces for a part of the bend from 8 vertices
	 */
	private void generateFaces(Vertex[] vertices, boolean parentHalf)
	{
		Face f1 = null;
		Face f2 = null;
		
		Face[] faceArray = new Face[8];

		for(int i = 0; i < 4; i++)
		{
			switch(i)
			{
			case 0:
				f1 = generateFace(vertices[1], vertices[2], vertices[6], parentHalf);
				f2 = generateFace(vertices[1], vertices[6], vertices[5], parentHalf);
				break;
			case 1:
				f1 = generateFace(vertices[1], vertices[0], vertices[5], parentHalf);
				f2 = generateFace(vertices[0], vertices[5], vertices[4], parentHalf);
				break;
			case 2:
				f1 = generateFace(vertices[0], vertices[3], vertices[4], parentHalf);
				f2 = generateFace(vertices[3], vertices[4], vertices[7], parentHalf);
				break;
			case 3:
				f1 = generateFace(vertices[3], vertices[2], vertices[7], parentHalf);
				f2 = generateFace(vertices[2], vertices[7], vertices[6], parentHalf);
				break;
			}

			f1.calculateFaceNormal();
			f2.faceNormal = f1.faceNormal;
			
			faces.add(f1);
			faces.add(f2);
			
			faceArray[i*2] = f1;
			faceArray[i*2 + 1] = f2;
		}
		
		bendFaces.add(faceArray);

	}

	/**
	 * Create a single face from 3 vertices
	 */
	private Face generateFace(Vertex v1, Vertex v2, Vertex v3, boolean parentHalf)
	{	
		Face f = new Face();

		f.vertices = TextureUtil.instance.orderVertices(new Vertex[]{v1, v2, v3});

		try 
		{
			getUVMapForFace(f, parentHalf).setTextureCoordinatesForFace(f);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		postBendTextureCoords.put(f, f.textureCoordinates);
		
		return f;
	}

	private double getDistanceBetween2DPoints(float[] point1, float[] point2)
	{
		float x = point1[0] - point2[0];
		float y = point1[1] - point2[1];

		return (float) Math.sqrt(x*x + y*y);
	}

	private double getDistanceBetween3DPoints(float[] point1, float[] point2)
	{
		float x = point1[0] - point2[0];
		float y = point1[1] - point2[1];
		float z = point1[2] - point2[2];

		return (float) Math.sqrt(x*x + y*y + z*z);
	}

	private ArrayList<Vertex> getUniqueVerticesOrderedByDistance(PartObj part, float[] point)
	{
		ArrayList<Vertex> uniqueVertices = new ArrayList<Vertex>();
		for(Face f : part.groupObj.faces)
		{
			for(Vertex v : f.vertices)
			{
				boolean unique = true;
				for(Vertex vu : uniqueVertices)
				{
					if(vu.x == v.x && vu.y == v.y && vu.z == v.z)
					{
						unique = false;
					}
				}
				if(unique)
				{
					uniqueVertices.add(v);
				}
			}
		}

		List<VertexWithDistance> verticesWithDistance = new ArrayList<VertexWithDistance>();


		for(Vertex v : uniqueVertices)
		{
			verticesWithDistance.add(new VertexWithDistance(v, getDistanceBetween3DPoints(new float[]{v.x, v.y, v.z}, new float[]{-point[0], -point[1], -point[2]})));
		}

		Collections.sort(verticesWithDistance);

		ArrayList<Vertex> toReturn = new ArrayList<Vertex>();

		for(VertexWithDistance v : verticesWithDistance)
		{
			toReturn.add(v.vertex);
		}

		return toReturn;
	}

	private List<Vertex> getClosestVertices(PartObj part, float[] point)
	{
		return getUniqueVerticesOrderedByDistance(part, point).subList(0, 4);
	}

	private List<Vertex> getFurtherestVertices(PartObj part, float[] point)
	{
		return getUniqueVerticesOrderedByDistance(part, point).subList(4, 8);
	}

	public void remove() 
	{
		for(Vertex v : parentMainVertices)
		{
			v.y -= width;
		}

		for(Vertex v : childMainVertices)
		{
			v.y += width;
		}

		restoreDefaultTextureCoordinates(parent);
		restoreDefaultTextureCoordinates(child);
	}

	public static boolean canCreateBend(PartObj child, PartObj parent)
	{
		Bend bend = new Bend();
		float[] rotationPoint = child.getRotationPoint();
		List<Vertex> childMainVertices = bend.getClosestVertices(child, rotationPoint);
		List<Vertex> parentMainVertices = bend.getClosestVertices(parent, rotationPoint);
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		for(Vertex v : childMainVertices)
		{
			boolean matchFound = false;
			for(Vertex w : parentMainVertices)
			{
				if(df.format(v.x).equals(df.format(w.x)) && df.format(v.y).equals(df.format(w.y)) && df.format(v.z).equals(df.format(w.z)))
				{
					matchFound = true;
				}
			}
			if(!matchFound)
			{
				return false;
			}	
		} 
		return true;
	}

	private class VertexWithDistance implements Comparable<VertexWithDistance>
	{
		private Vertex vertex;
		private double distance;


		public VertexWithDistance(Vertex v, Double d)
		{
			vertex = v;
			distance = d;
		}

		@Override
		public int compareTo(VertexWithDistance v)
		{
			if(v.distance < distance)
			{
				return 1;
			}
			else if(v.distance > distance)
			{
				return -1;
			}
			return 0;
		}
	}

	/**
	 * Returns the four faces of a part that are on the side and are the 'upper' faces.
	 */
	private Face[] getSideTopFaces(GroupObject obj)
	{
		Face[] mainFaces = new Face[4];
		int index = 0;
		
		for(Face f : obj.faces)
		{
			boolean add = true;

			Vertex[] orderedVertices = TextureUtil.instance.orderVertices(f.vertices);

			if(orderedVertices[0].y == orderedVertices[1].y && orderedVertices[0].y == orderedVertices[2].y)
			{
				add = false;
			}

			if(orderedVertices[2].y > orderedVertices[1].y)
			{
				add = false;
			}
			
			if(add)
			{
				if(index < 4)
				{
					mainFaces[index] = f;
				}
				else
				{
					//TODO throw error
					System.out.println("Extra vertex!");
				}
				index++;
			}
		}
		

		return mainFaces;

	}

	private UVMap getUVMapForFace(Face f, boolean parent) throws Exception
	{
		ArrayList<UVMap> maps = parent ? parentUVMaps : childUVMaps;

		float minD = -10.0F;

		UVMap correctMap = null;

		Vertex cornerVertex = TextureUtil.instance.orderVertices(f.vertices)[1];

		for(UVMap map : maps)
		{
			if(map.isMapInCorrectPlaneForFace(f))
			{
				float d = map.getDistanceFromPlane(cornerVertex);
				if(d < minD || minD == -10.0F)
				{
					correctMap = map;
					minD = d;
				}
			}
		}

		if(correctMap == null)
		{
			throw new Exception("No UVMap found for face.");
		}

		return correctMap;

	}


}
