package MCEntityAnimator.render.objRendering.bend;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationParenting;
import MCEntityAnimator.render.objRendering.parts.PartObj;
import net.minecraftforge.client.model.obj.Vertex;
import scala.actors.threadpool.Arrays;

public class BendNew 
{

	//Rotation point of child is the centre of the bend.
	private Vertex centreOfBend;

	//The bend is drawn between these vertices. They are the set of four vertices that is closest to the centre of the bend. 
	//parentNearVertices[0] -> childNearVertices[0] etc. These vertices are moved towards the far vertices in order to make space for the bend.
	private Vertex[] parentNearVertices, childNearVertices;

	//These are the set of four vertices that are furtherest from the centre of the bend. They will be unmoved by used in calculations.
	private Vertex[] parentFarVertices, childFarVertices;

	PartObj parent;
	PartObj child;

	//The percentage of the parent and child part which remains after the bend is made.
	//So the length of the parts is reduced to 60%
	private static final float sizeReduction = 0.1F;

	//The list of segments of the bend. These are the actual group objects that are rendered.
	private List<BendPart> bendParts;
	//The number of bend parents the bend is made up of.
	private static final int bendSplit = 50;

	public BendNew(PartObj parent, PartObj child)
	{
		this.parent = parent;
		this.child = child;

		centreOfBend = new Vertex(-child.getRotationPoint(0), -child.getRotationPoint(1), -child.getRotationPoint(2));
		bendParts = new ArrayList<BendPart>();

		//Get near and far vertices for parent and child.
		Vertex[] allParentVertices = BendHelper.getPartVertices(parent);
		allParentVertices = BendHelper.orderVerticesOnDistance(allParentVertices, centreOfBend);
		parentNearVertices = (Vertex[]) Arrays.copyOfRange(allParentVertices, 0, 4);
		parentFarVertices = (Vertex[]) Arrays.copyOfRange(allParentVertices, 4, 8);
		Vertex[] allChildVertices = BendHelper.getPartVertices(child);
		allChildVertices = BendHelper.orderVerticesOnDistance(allChildVertices, centreOfBend);
		childNearVertices = (Vertex[]) Arrays.copyOfRange(allChildVertices, 0, 4);
		childFarVertices = (Vertex[]) Arrays.copyOfRange(allChildVertices, 4, 8);

		//Match vertices, starting from parentFar, working down towards childFar.
		parentFarVertices = BendHelper.orderVerticesRelative(parentFarVertices);
		parentNearVertices = BendHelper.alignVertices(parentFarVertices, parentNearVertices);
		childNearVertices = BendHelper.alignVertices(parentNearVertices, childNearVertices);
		childFarVertices = BendHelper.alignVertices(childNearVertices, childFarVertices);

		shortenParts();

		for(int i = 0; i < bendSplit; i++)
			bendParts.add(new BendPart());
	}

	/**
	 * Reduce the length of the child and parent parts.
	 * They 'shrink' away from the centre of the bend, in order to allow the bend to be created.
	 * The amount they shorten by is due to the size reduction constant.
	 */
	private void shortenParts()
	{
		float dx,dy,dz;
		for(int i = 0; i < 4; i++)
		{
			dx = parentNearVertices[i].x - parentFarVertices[i].x;
			dy = parentNearVertices[i].y - parentFarVertices[i].y;
			dz = parentNearVertices[i].z - parentFarVertices[i].z;
			parentNearVertices[i].x = parentFarVertices[i].x + sizeReduction*dx;
			parentNearVertices[i].y = parentFarVertices[i].y + sizeReduction*dy;
			parentNearVertices[i].z = parentFarVertices[i].z + sizeReduction*dz;

			dx = childNearVertices[i].x - childFarVertices[i].x;
			dy = childNearVertices[i].y - childFarVertices[i].y;
			dz = childNearVertices[i].z - childFarVertices[i].z;
			childNearVertices[i].x = childFarVertices[i].x + sizeReduction*dx;
			childNearVertices[i].y = childFarVertices[i].y + sizeReduction*dy;
			childNearVertices[i].z = childFarVertices[i].z + sizeReduction*dz;
		}
	}


	public void render()
	{		
		//These are absolute vertex reference taking into rotation into account.
		Vertex[] topFarVertices = new Vertex[parentFarVertices.length];
		Vertex[] topNearVertices = new Vertex[parentNearVertices.length];
		Vertex[] bottomNearVertices = new Vertex[childNearVertices.length];
		Vertex[] bottomFarVertices = new Vertex[childFarVertices.length];

		//Load anipar
		AnimationParenting anipar = AnimationData.getAnipar(parent.modelObj.getEntityType());
		
		GL11.glPushMatrix();
		
		//Set top far and near vertices to rotation compensated parent far and near vertices.
		//Do all this before compensating for parent rotation.
		for(int i = 0; i < parentFarVertices.length; i++)
		{
			Vertex v = parentFarVertices[i];
			topFarVertices[i] = new Vertex(v.x, v.y, v.z);

			v = parentNearVertices[i];
			topNearVertices[i] = new Vertex(v.x, v.y, v.z);
		}
		
		//Compensate for parent rotation.
		
		//Set top far and near vertices to rotation compensated child far and near vertices.
		for(int i = 0; i < childNearVertices.length; i++)
		{	
			Vertex v = childNearVertices[i];
			bottomNearVertices[i] = new Vertex(v.x, v.y, v.z);
			BendHelper.rotateVertex(bottomNearVertices[i], child.getValues(), centreOfBend);

			v = childFarVertices[i];
			bottomFarVertices[i] = new Vertex(v.x, v.y, v.z);
			BendHelper.rotateVertex(bottomFarVertices[i], child.getValues(), centreOfBend);
		}

		//Generate curves.
		BezierCurve[] curves = generateBezierCurves(topFarVertices, topNearVertices, bottomNearVertices, bottomFarVertices);
		
		//Top of first part is topNearVertices.
		Vertex[] bendPartTop = topNearVertices;
		
		//Update bends
		for(int i = 0; i < bendSplit; i++)
		{
			if(bendParts.size() == i)
				bendParts.add(new BendPart());
			
			//Generate part bottom.
			Vertex[] bendPartBottom = generatePartBottom(curves,(float)(i+1)/bendSplit);
			//Update bend.
			bendParts.get(i).updateVertices(bendPartTop, bendPartBottom);
			//Render bend
			bendParts.get(i).render();
			//Top of next part is bottom of this part.
			bendPartTop = bendPartBottom;
		}
		
		GL11.glPopMatrix();

	}
	
	/**
	 * Adjust a glMatrix for a partObj p.
	 */
	private void compensatePartRotation(PartObj p)
	{
		GL11.glTranslatef(-p.getRotationPoint(0), -p.getRotationPoint(1), -p.getRotationPoint(2));
		GL11.glRotated((p.getValue(0) - p.getOriginalValues()[0])/Math.PI*180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotated((p.getValue(1) - p.getOriginalValues()[1])/Math.PI*180.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotated((p.getValue(2) - p.getOriginalValues()[2])/Math.PI*180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glTranslatef(p.getRotationPoint(0), p.getRotationPoint(1), p.getRotationPoint(2));
	}

	private BezierCurve[] generateBezierCurves(Vertex[] topFarVertices, Vertex[] topNearVertices, Vertex[] bottomNearVertices, Vertex[] bottomFarVertices)
	{
		BezierCurve[] curves = new BezierCurve[bottomNearVertices.length];
		for(int i = 0; i < bottomNearVertices.length; i++)
		{
			//System.out.println( bottomNearVertices[i].z + "," + bottomFarVertices[i].z);
			BezierCurve curve = new BezierCurve(topFarVertices[i], topNearVertices[i], bottomFarVertices[i], bottomNearVertices[i], child.getValues(), centreOfBend.y);
			curves[i] = curve;
		}
		return curves;
	}

	private Vertex[] generatePartBottom(BezierCurve[] curves, float t) 
	{
		Vertex[] vertices = new Vertex[curves.length];
		for(int i = 0; i < curves.length; i++)
		{
			vertices[i] = curves[i].getVertexOnCurve(t);
		}
		return vertices;
	}

	public void remove()
	{

	}


}
