package obsidianAnimator.gui.timeline.swing;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class TimelineMenuBar extends JMenuBar {

	public TimelineMenuBar() {
		setup();
	}
	
	private void setup() {
		this.add(createFileMenu());
	}
	
	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		fileMenu.add(createCustomMenuItem("New"));
		fileMenu.addSeparator();
		fileMenu.add(createCustomMenuItem("Open"));
		fileMenu.addSeparator();
		fileMenu.add(createCustomMenuItem("Save"));
		fileMenu.add(createCustomMenuItem("Save as"));
		fileMenu.addSeparator();
		fileMenu.add(createCustomMenuItem("Exit"));
		
		return fileMenu;
	}
	
	private JMenuItem createCustomMenuItem(String name) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return menuItem;
	}
	
}
