package com.dabigjoe.obsidianAnimator.gui.timeline.changes;

import com.dabigjoe.obsidianAPI.Quaternion;
import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.render.part.Part;
import com.dabigjoe.obsidianAPI.render.part.PartRotation;
import com.dabigjoe.obsidianAnimator.gui.timeline.Keyframe;
import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;
import com.dabigjoe.obsidianAnimator.render.entity.ModelObj_Animator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChangeMirror implements AnimationChange
{
    @Override
    public void apply(TimelineController controller, AnimationSequence animation)
    {
        Map<Part, List<Keyframe>> frames = swapKeyframes(controller);
        for (List<Keyframe> keyframes : frames.values())
        {
            for (Keyframe frame : keyframes)
            {
                if (frame.part instanceof PartRotation)
                {
                    Quaternion quat = Quaternion.fromEuler(frame.values[0], -frame.values[1], -frame.values[2]);
                    float[] euler = quat.toEuler();
                    for (int i = 0; i < 3; i++)
                    {
                        frame.values[i] = euler[i];
                    }
                } else
                {
                    frame.values[0] *= -1;
                }
            }
        }

        controller.keyframeController.getKeyframes().clear();
        controller.keyframeController.getKeyframes().putAll(frames);
    }

    @Override
    public void undo(TimelineController controller, AnimationSequence animation)
    {
        apply(controller, animation);
    }

    /**
     * Swaps the keyframes for parts that have a left and right variant, like arms or legs.
     * All other parts are unaffected.
     *
     * @return A new map containing the changes.
     */
    private Map<Part, List<Keyframe>> swapKeyframes(TimelineController controller)
    {
        Map<Part, List<Keyframe>> frames = controller.keyframeController.getKeyframes();
        Map<Part, List<Keyframe>> result = Maps.newHashMap();

        for (Map.Entry<Part, List<Keyframe>> entry : frames.entrySet())
        {
            Part part = entry.getKey();
            Part mirrorPart = getMirrorPart(part, controller.timelineGui.entityModel);
            List<Keyframe> newFrames = Lists.newArrayList();
            for (Keyframe keyframe : entry.getValue())
            {
                newFrames.add(new Keyframe(keyframe.frameTime, mirrorPart, Arrays.copyOf(keyframe.values, keyframe.values.length)));
            }
            result.put(mirrorPart, newFrames);
        }

        return result;
    }

    private Part getMirrorPart(Part part, ModelObj_Animator model)
    {
        String name = part.getDisplayName();
        if (!name.endsWith("L") && !name.endsWith("R"))
            return part;

        String prefix = name.substring(0, name.length() - 1);
        String side = name.substring(prefix.length());
        String mirrorSide = side.equals("L") ? "R" : "L";
        String mirrorName = prefix + mirrorSide;

        for (Part p : model.parts)
        {
            if (p.getName().equals(mirrorName) || p.getDisplayName().equals(mirrorName))
                return p;
        }

        return part;
    }
}
