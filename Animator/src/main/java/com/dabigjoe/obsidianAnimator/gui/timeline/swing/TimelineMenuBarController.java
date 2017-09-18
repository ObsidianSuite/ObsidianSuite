package com.dabigjoe.obsidianAnimator.gui.timeline.swing;

import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;

public class TimelineMenuBarController {

	private TimelineController mainController;
	
	public TimelineMenuBarController(TimelineController mainController) {
		this.mainController = mainController;
	}
	
	public boolean isSaveLocationSet() {
		return mainController.isSaveLocationSet();
	}
	
	public final Runnable newPressed = new Runnable() {
		@Override
		public void run() {
			mainController.openAnimationNewFrame();
		}
	};
	
	public final Runnable openPressed = new Runnable() {
		@Override
		public void run() {
			mainController.openAnimationChooser();
		}
	};
	
	public final Runnable savePressed = new Runnable() {
		@Override
		public void run() {
			mainController.save();
		}
	};
	
	public final Runnable saveAsPressed = new Runnable() {
		@Override
		public void run() {
			mainController.trySaveAs();
		}
	};
	
	public final Runnable exitPressed = new Runnable() {
		@Override
		public void run() {
			mainController.close();
		}
	};
	
}
