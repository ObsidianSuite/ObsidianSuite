package obsidianAnimator.gui;

import java.awt.Dimension;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Test {

	//XXX remove static
	private static JSpinner spinner;
	
	public static void main(String[] args) {
		new Test();
		setSpinnerValue(0.5D);
	}
	
	public Test() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		
		//Useful stuff begins here
		SpinnerNumberModel model = new SpinnerNumberModel(0, -1, 1, 0.01);
		spinner = new JSpinner(model);
		spinner.setPreferredSize(new Dimension(100, 20));
		panel.add(spinner);
		
		spinner.addChangeListener(new ChangeListener(){
			DecimalFormat df = new DecimalFormat("#.##");
			@Override
			public void stateChanged(ChangeEvent e) {
				//Change event here
			}
			
		});
		
		
		
		//And ends here
		frame.setLocationRelativeTo(null);
		frame.setContentPane(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	//XXX remove static
	public static void setSpinnerValue(double value) {
		spinner.setValue(value);
	}
	
}
