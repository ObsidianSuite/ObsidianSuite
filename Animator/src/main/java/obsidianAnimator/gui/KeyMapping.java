package obsidianAnimator.gui;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.GuiScreen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.Map;

public class KeyMapping
{
    private final JFrame frame;
    private final Map<Integer, KeyAction> actionByMinecraftKey = Maps.newHashMap();

    public KeyMapping(JFrame frame) {this.frame = frame;}

    public void addKey(int frameKey, int minecraftKey, String actionMapKey, Action action)
    {
        addKey(frameKey, minecraftKey, false, false, actionMapKey, action);
    }

    public void addCtrlKey(int frameKey, int minecraftKey, String actionMapKey, Action action)
    {
        addKey(frameKey, minecraftKey, true, false, actionMapKey, action);
    }

    public void addCtrlShiftKey(int frameKey, int minecraftKey, String actionMapKey, Action action)
    {
        addKey(frameKey, minecraftKey, true, true, actionMapKey, action);
    }

    private void addKey(int frameKey, int minecraftKey, boolean ctrl, boolean shift, String actionMapKey, Action action)
    {
        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();

        int modifiers = 0;
        if (ctrl)
            modifiers |= InputEvent.CTRL_DOWN_MASK;
        if (shift)
            modifiers |= InputEvent.SHIFT_DOWN_MASK;

        KeyStroke keyStroke = ctrl || shift ? KeyStroke.getKeyStroke(frameKey, modifiers, true)
                                   : KeyStroke.getKeyStroke(frameKey, 0);

        inputMap.put(keyStroke, actionMapKey);
        actionMap.put(actionMapKey, action);

        actionByMinecraftKey.put(minecraftKey, new KeyAction(ctrl, action));
    }

    public boolean handleMinecraftKey(int key)
    {
        KeyAction action = actionByMinecraftKey.get(key);

        //TODO GuiScreen.isCtrlKeyDown() only work if the MC screen is in focus, not the swing frame.
        if (action != null && (!action.ctrl || GuiScreen.isCtrlKeyDown()))
        {
            action.action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));

            return true;
        } else
        {
            return false;
        }
    }

    private class KeyAction
    {
        private final boolean ctrl;
        private final Action action;

        private KeyAction(boolean ctrl, Action action)
        {
            this.ctrl = ctrl;
            this.action = action;
        }
    }
}
