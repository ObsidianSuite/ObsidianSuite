package obsidianAnimator.gui.sequence;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;

public class EntityAutoMove
{

	private float speed;
	private float moveX;
	private float moveY;
	private float moveZ;
	private float moveTotal;
	private int fps;
	
	public enum Direction
	{
		None (0,0,0),
		Foward (0,0,1),
		FowardLeft (1,0,1),
		FowardRight (-1,0,1),
		Left (1,0,0),
		Right (-1,0,0),
		BackwardLeft (1,0,-1),
		BackwardRight (-1,0,-1),
		Backward (0,0,-1);
		
		private final float x,y,z;
		
		Direction(float x, float y, float z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public EntityAutoMove(float speed, Direction direction, int fps)
	{
		this.speed = speed;
		this.moveX = direction.x;
		this.moveY = direction.y;
		this.moveZ = direction.z;
		this.fps = fps;
		this.moveTotal = Math.abs(moveX) + Math.abs(moveY) + Math.abs(moveZ);
	}

	public void moveEntity(float time, Entity entity)
	{
		float[] moveAmounts = calculateMoveAmounts(time);
		entity.posX += moveAmounts[0];
		entity.posY += moveAmounts[1];
		entity.posZ += moveAmounts[2];
	}

	public void matrixTranslate(float time)
	{
		float[] moveAmounts = calculateMoveAmounts(time);
		GL11.glTranslatef(moveAmounts[0], moveAmounts[1], moveAmounts[2]);
	}

	private float[] calculateMoveAmounts(float time)
	{
		float f = (time/(float)fps)*speed;
		float[] moveAmounts = new float[3];
		moveAmounts[0] = (moveX/moveTotal)*f;
		moveAmounts[1] = (moveY/moveTotal)*f;
		moveAmounts[2] = (moveZ/moveTotal)*f;
		return moveAmounts;
	}



}
