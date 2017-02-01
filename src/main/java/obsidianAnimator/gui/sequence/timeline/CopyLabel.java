package obsidianAnimator.gui.sequence.timeline;

import javax.swing.*;
import java.awt.*;

class CopyLabel extends JComponent
{
    public int x;
    public int y;
    public int time;
    public boolean draw;

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(draw)
        {
            String s = String.valueOf(time);
            g.setColor(Color.red);
            g.drawString(s, x, y);
        }
    }
}
