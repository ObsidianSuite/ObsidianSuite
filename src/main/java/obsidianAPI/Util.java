package obsidianAPI;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;
import org.lwjgl.util.vector.Quaternion;

public class Util 
{


	
	
	/**
	 * Calculate which frame an animation is on based on the time that it started at, which frame it started at, and its FPS.
	 * @param startTimeNano - Nano time the aniamtion starting being played on.
	 * @param startTimeFrame - Frame the animation started being played on.
	 * @param fps - FPS the animation is running at. 
	 * @param multiplier - Speed multiplier so the animation is rendered slower or faster
	 * @return Frame time.
	 */
	public static float getAnimationFrameTime(long startTimeNano, float startTimeFrame, int fps, float multiplier)
	{
		return (System.nanoTime() - startTimeNano)/1000000000F*fps*multiplier + startTimeFrame;
	}

	public static Quaternion eulerToQuarternion(double x, double y, double z)
	{
		double c1 = Math.cos(x/2);
		double s1 = Math.sin(x/2);
		double c2 = Math.cos(y/2);
		double s2 = Math.sin(y/2);
		double c3 = Math.cos(z/2);
		double s3 = Math.sin(z/2);
		float qw =(float) (c1*c2*c3 - s1*s2*s3);
		float qx =(float) (c1*c2*s3 + s1*s2*c3);
		float qy =(float) (s1*c2*c3 + c1*s2*s3);
		float qz =(float) (c1*s2*c3 - s1*c2*s3);
		return new Quaternion(qx,qy,qz,qw);
	}

	public static float[] quarternionToEuler(Quaternion q)
	{
		float[] eulerRotation = new float[3];
		double test = q.x*q.y + q.z*q.w;
		if (test > 0.499) { // singularity at north pole
			eulerRotation[0] = (float) (2 * Math.atan2(q.x,q.w));
			eulerRotation[1] = (float) (Math.PI/2);
			eulerRotation[2] = 0;
			return eulerRotation;
		}
		if (test < -0.499) { // singularity at south pole
			eulerRotation[0] = (float) (-2 * Math.atan2(q.x,q.w));
			eulerRotation[1] = (float) (- Math.PI/2);
			eulerRotation[2] = 0;
			return eulerRotation;
		}
		double sqx = q.x*q.x;
		double sqy = q.y*q.y;
		double sqz = q.z*q.z;
		eulerRotation[0] = (float) Math.atan2(2*q.y*q.w-2*q.x*q.z , 1 - 2*sqy - 2*sqz);
		eulerRotation[1] = (float) Math.asin(2*test);
		eulerRotation[2] = (float) Math.atan2(2*q.x*q.w-2*q.y*q.z , 1 - 2*sqx - 2*sqz);
		return eulerRotation;
	}

	public static Quaternion slerp(Quaternion qa, Quaternion qb, float t)
	{
		Quaternion qm = new Quaternion();
		//Normalise p and q.
		float pLen = qa.length();
		if(pLen != 1 && pLen != 0)
			qa.normalise();
		float qLen = qb.length();
		if(qLen != 1 && qLen != 0)
			qb.normalise();

		//Calculate cos half angle between
		double cosHalfTheta = Quaternion.dot(qa, qb);
		//p=q or p=-q
		if(Math.abs(cosHalfTheta) >= 1.0)
		{
			qm.w = qa.w;
			qm.x = qa.x;
			qm.y = qa.y;
			qm.z = qa.z;
			return qm;
		}
		double halfTheta = Math.acos(cosHalfTheta);
		double sinHalfTheta = Math.sqrt(1.0 - cosHalfTheta*cosHalfTheta);

		// if theta = 180 degrees then result is not fully defined
	    // we could rotate around any axis normal to qa or qb
	    if (Math.abs(sinHalfTheta) < 0.001){ // fabs is floating point absolute
	        qm.w = (float) (qa.w * 0.5 + qb.w * 0.5);
	        qm.x = (float) (qa.x * 0.5 + qb.x * 0.5);
	        qm.y = (float) (qa.y * 0.5 + qb.y * 0.5);
	        qm.z = (float) (qa.z * 0.5 + qb.z * 0.5);
	        return qm;
	    }

	    double ratioA = Math.sin((1 - t) * halfTheta) / sinHalfTheta;
	    double ratioB = Math.sin(t * halfTheta) / sinHalfTheta;
	    //calculate Quaternion.
	    qm.w = (float) (qa.w * ratioA + qb.w * ratioB);
	    qm.x = (float) (qa.x * ratioA + qb.x * ratioB);
	    qm.y = (float) (qa.y * ratioA + qb.y * ratioB);
	    qm.z = (float) (qa.z * ratioA + qb.z * ratioB);
	    return qm;
	}
}
