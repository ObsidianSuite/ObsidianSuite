package obsidianAnimator.gui.timeline.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class TimelineMenuBar extends JMenuBar {

	private TimelineMenuBarController controller;
	private JMenuItem save;
	
	public TimelineMenuBar(TimelineMenuBarController controller) {
		this.controller = controller;
		setup();
	}
	
	private void setup() {
		this.add(createFileMenu());
		refresh();
	}

	public void refresh() {
		save.setEnabled(controller.isSaveLocationSet());
	}
	
	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
		});
		save = createCustomMenuItem("Save", controller.savePressed);
		fileMenu.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		fileMenu.add(createCustomMenuItem("New", controller.newPressed));
		fileMenu.addSeparator();
		fileMenu.add(createCustomMenuItem("Open", controller.openPressed));
		fileMenu.addSeparator();
		fileMenu.add(save);
		fileMenu.add(createCustomMenuItem("Save as", controller.saveAsPressed));
		fileMenu.addSeparator();
		fileMenu.add(createCustomMenuItem("Exit", controller.exitPressed));
		
		return fileMenu;
	}
	
	private JMenuItem createCustomMenuItem(String name, Runnable action) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				action.run();
				refresh();
			}
		});
		return menuItem;
	}
	
}
