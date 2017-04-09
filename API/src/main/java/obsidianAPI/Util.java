package obsidianAPI;

import obsidianAPI.animation.AnimationPart;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.ModelObj;

import java.util.Map;

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

	public static float getAnimationFrameTime(long now, long startTimeNano, float startTimeFrame, int fps, float multiplier)
	{
		return (float) ((now - startTimeNano) / 1000000000D * fps * multiplier + startTimeFrame);
	}

	public static AnimationSequence createTransition(ModelObj model, String animName, Map<String, float[]> from, Map<String, float[]> to, float duration)
    {
        AnimationSequence seq = new AnimationSequence(model.entityName, "transition_"+ animName);
        seq.setFPS(10);

        for (Map.Entry<String, float[]> entry : from.entrySet())
        {
            float[] fromPart = entry.getKey().equals("entitypos") ? new float[] {0f, entry.getValue()[1], 0f} : entry.getValue();
            float[] toPart = to.get(entry.getKey());

            AnimationPart part = new AnimationPart(0, (int) (duration * 10f), fromPart, toPart, model.getPartFromName(entry.getKey()));
            seq.addAnimation(part);
        }

        return seq;
    }
}
