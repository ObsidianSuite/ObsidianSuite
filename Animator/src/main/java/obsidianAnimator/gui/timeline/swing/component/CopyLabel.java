package obsidianAnimator.gui.timeline.swing.component;

import javax.swing.*;
import java.awt.*;

public class CopyLabel extends JComponent
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
        	System.out.println("Drawing");
            String s = String.valueOf(time);
            g.setColor(Color.red);
            g.drawString(s, x, y);
        }
    }
}
