package obsidianAnimator.gui.timeline;

import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.KeyMapping;
import obsidianAnimator.gui.timeline.changes.*;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class TimelineKeyMappings
{

    private KeyMapping keyMappings;
    private TimelineController controller;

    public TimelineKeyMappings(TimelineController controller)
    {
        this.controller = controller;

        keyMappings = new KeyMapping(controller.timelineFrame);

        keyMappings.addKey(KeyEvent.VK_SPACE, Keyboard.KEY_SPACE, "spacePressed", new SpaceAction());
        keyMappings.addKey(KeyEvent.VK_W, Keyboard.KEY_W, "wPressed", new WAction());
        keyMappings.addKey(KeyEvent.VK_S, Keyboard.KEY_S, "sPressed", new SAction());
        keyMappings.addKey(KeyEvent.VK_A, Keyboard.KEY_A, "aPressed", new AAction());
        keyMappings.addKey(KeyEvent.VK_D, Keyboard.KEY_D, "dPressed", new DAction());
        keyMappings.addCtrlKey(KeyEvent.VK_Z, Keyboard.KEY_Z, "undoReleased", new UndoAction());
        keyMappings.addCtrlKey(KeyEvent.VK_Y, Keyboard.KEY_Y, "redoReleased", new RedoAction());
        keyMappings.addKey(KeyEvent.VK_ESCAPE, Keyboard.KEY_ESCAPE, "escPressed", new EscAction());
        keyMappings.addKey(KeyEvent.VK_DELETE, Keyboard.KEY_DELETE, "deletePressed", new DeleteAction());
        keyMappings.addCtrlKey(KeyEvent.VK_R, Keyboard.KEY_R, "rPressed", new ResetAction());
        keyMappings.addCtrlKey(KeyEvent.VK_C, Keyboard.KEY_C, "cPressed", new CopyAction());
        keyMappings.addCtrlKey(KeyEvent.VK_V, Keyboard.KEY_V, "vPressed", new PasteAction());
        keyMappings.addCtrlShiftKey(KeyEvent.VK_ADD, Keyboard.KEY_ADD, "addAllPressed", new AddAction(true));
        keyMappings.addCtrlShiftKey(KeyEvent.VK_SUBTRACT, Keyboard.KEY_SUBTRACT, "removeAllPressed", new RemoveAction(true));
        keyMappings.addCtrlKey(KeyEvent.VK_ADD, Keyboard.KEY_ADD, "addPressed", new AddAction(false));
        keyMappings.addCtrlKey(KeyEvent.VK_SUBTRACT, Keyboard.KEY_SUBTRACT, "removePressed", new RemoveAction(false));

        int[] numpadKey = new int[] {
                Keyboard.KEY_NUMPAD0, Keyboard.KEY_NUMPAD1, Keyboard.KEY_NUMPAD2, Keyboard.KEY_NUMPAD3,
                Keyboard.KEY_NUMPAD4, Keyboard.KEY_NUMPAD5, Keyboard.KEY_NUMPAD6, Keyboard.KEY_NUMPAD7,
                Keyboard.KEY_NUMPAD8, Keyboard.KEY_NUMPAD9};

        for (int j = 0; j <= 9; j++)
        {
            keyMappings.addKey(KeyEvent.VK_NUMPAD0 + j, numpadKey[j], "numpad" + j, new ChangeViewAction(j));
        }
    }

    public void handleMinecraftKey(int par2)
    {
        keyMappings.handleMinecraftKey(par2);
    }

	/* ---------------------------------------------------- *
     * 				   		Actions							*
	 * ---------------------------------------------------- */

    private class SpaceAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            int time = (int) controller.getTime();
            Part part = controller.getSelectedPart();
            Keyframe keyframe = controller.keyframeController.getKeyframe(part, time);

            if (keyframe == null && part != null)
            {
                controller.versionController.applyChange(new ChangeCreateKeyframe(part.getName(), time, part.getValues()));
            }
        }
    }

    private class WAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            for (int i = 0; i < controller.timelineGui.entityModel.parts.size(); i++)
            {
                if (controller.timelineGui.entityModel.parts.get(i).equals(controller.timelineGui.selectedPart))
                {
                    if (i > 0)
                        controller.timelineGui.updatePart(controller.timelineGui.entityModel.parts.get(i - 1));
                    else
                        controller.timelineGui.updatePart(controller.timelineGui.entityModel.parts.get(controller.timelineGui.entityModel.parts.size() - 1));
                    break;
                }
            }
        }
    }

    private class SAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            for (int i = 0; i < controller.timelineGui.entityModel.parts.size(); i++)
            {
                if (controller.timelineGui.entityModel.parts.get(i).equals(controller.timelineGui.selectedPart))
                {
                    if (i < controller.timelineGui.entityModel.parts.size() - 1)
                        controller.timelineGui.updatePart(controller.timelineGui.entityModel.parts.get(i + 1));
                    else
                        controller.timelineGui.updatePart(controller.timelineGui.entityModel.parts.get(0));
                    break;
                }
            }
        }
    }

    private class AAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            controller.setExceptionPart(null);
            controller.setTime(controller.getTime() > 0 ? controller.getTime() - 1 : controller.getTime());
            controller.keyframeController.refreshSliderAndTextBox();
            controller.timelineFrame.repaint();
        }
    }

    private class DAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            controller.setExceptionPart(null);
            controller.setTime(controller.getTime() < controller.keyframeController.panel.getTimelineLength() ? controller.getTime() + 1 : controller.getTime());
            controller.keyframeController.refreshSliderAndTextBox();
            controller.timelineFrame.repaint();
        }
    }

    private class UndoAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            controller.versionController.undo();
        }
    }

    private class RedoAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            controller.versionController.redo();
        }
    }

    private class DeleteAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            int time = (int) controller.getTime();
            Part part = controller.getSelectedPart();
            Keyframe keyframe = controller.keyframeController.getKeyframe(part, time);
            if (keyframe != null)
            {
                controller.versionController.applyChange(new ChangeDeleteKeyFrame(part.getName(), time));
            }
        }
    }

    private class EscAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            controller.close();
        }
    }

    private class ResetAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Part part = controller.timelineGui.selectedPart;
            if (part != null)
            {
                Keyframe toReset = controller.keyframeController.getKeyframe(part, (int) controller.getTime());
                if (toReset != null)
                {
                    controller.versionController.applyChange(new ChangeSetValues(toReset.values, part.getOriginalValues(), part.getName(), (int) controller.getTime()));
                } else
                {
                    controller.setExceptionPart(null);
                    controller.getSelectedPart().setToOriginalValues();
                }
            }

        }
    }

    private class CopyAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (controller.timelineGui.selectedPart != null)
            {
                Keyframe frame = controller.keyframeController.getKeyframe(controller.timelineGui.selectedPart, (int) controller.getTime());
                if (frame != null)
                    controller.setCopiedValues(frame.values);
                else
                    controller.setCopiedValues(controller.timelineGui.selectedPart.getValues());
            }
        }
    }

    private class PasteAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Part part = controller.timelineGui.selectedPart;
            if (part != null && controller.getCopiedValues() != null)
            {
                if (!pasteValues())
                {
                    controller.versionController.applyChange(new ChangeCreateKeyframe(part.getName(), (int) controller.getTime(), controller.getCopiedValues()));
                }
            }
        }

        private boolean pasteValues()
        {
            Keyframe frame = controller.keyframeController.getKeyframe(controller.timelineGui.selectedPart, (int) controller.getTime());
            if (frame != null)
            {
                controller.versionController.applyChange(new ChangeSetValues(frame.values, controller.getCopiedValues(), frame.part.getName(), (int) controller.getTime()));

                return true;
            }

            return false;
        }
    }

    private class ChangeViewAction extends AbstractAction
    {

        private int numpadNumber;

        private ChangeViewAction(int numpadNumber)
        {
            this.numpadNumber = numpadNumber;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            controller.timelineGui.changeView(numpadNumber);
        }
    }

    private class AddAction extends AbstractAction
    {
        private final boolean allParts;

        private AddAction(boolean allParts) {this.allParts = allParts;}

        @Override
        public void actionPerformed(ActionEvent e)
        {
            int time = (int) controller.getTime();
            if (allParts)
            {
                controller.versionController.applyChange(new ChangeAddFrame(null, time));

            } else if (controller.getSelectedPart() != null)
            {
                controller.versionController.applyChange(new ChangeAddFrame(controller.getSelectedPart().getName(), time));
            }
        }
    }

    private class RemoveAction extends AbstractAction
    {
        private final boolean allParts;

        private RemoveAction(boolean allParts) {this.allParts = allParts;}

        @Override
        public void actionPerformed(ActionEvent e)
        {
            int time = (int) controller.getTime();
            if (allParts)
            {
                controller.versionController.applyChange(new ChangeRemoveFrame(null, time));
            } else if (controller.getSelectedPart() != null)
            {
                controller.versionController.applyChange(new ChangeRemoveFrame(controller.getSelectedPart().getName(), time));
            }
        }
    }
}