package com.nthrootsoftware.mcea.gui.sequence;

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

	public EntityAutoMove(float speed, float moveX, float moveZ, int fps)
	{
		this.speed = speed;
		this.moveX = moveX;
		this.moveY = 0.0F;
		this.moveZ = moveZ;
		this.fps = fps;
		this.moveTotal = moveX + moveY + moveZ;
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
