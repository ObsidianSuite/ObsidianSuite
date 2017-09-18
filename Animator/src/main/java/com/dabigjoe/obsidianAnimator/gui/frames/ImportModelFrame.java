package com.dabigjoe.obsidianAnimator.gui.frames;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.dabigjoe.obsidianAPI.exceptions.MissingImporterException;
import com.dabigjoe.obsidianAPI.file.FileHandler;
import com.dabigjoe.obsidianAPI.file.ModelFileHandler;
import com.dabigjoe.obsidianAnimator.data.ModelHandler;
import com.dabigjoe.obsidianAnimator.file.FileChooser;
import com.dabigjoe.obsidianAnimator.file.FileNotChosenException;
import com.dabigjoe.obsidianAnimator.gui.entitySetup.EntitySetupController;
import com.dabigjoe.obsidianAnimator.render.entity.ModelObj_Animator;

public class ImportModelFrame extends BaseFrame {

	private File modelFile;
	private JButton importButton;
	private FileSelectionPanel modelSelectionPanel;
	private JComboBox<ComboBoxFileItem> tabulaModelBox;

	public ImportModelFrame() {
		super("Import Model");
		addComponents();
	}

	@Override
	protected void addComponents() {
		importButton = new JButton("Import");
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importPressed();
			}
		});

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelPressed();
			}
		});

		importButton.setPreferredSize(cancel.getPreferredSize());

		modelSelectionPanel = new FileSelectionPanel("Model File", FileChooser.allModelFilter);

		JTabbedPane tPane = new JTabbedPane();
		tPane.add("File", modelSelectionPanel);
		tPane.add("Tabula", createTabulaPanel());
		tPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(tPane.getSelectedIndex() == 0)
					modelFile = modelSelectionPanel.targetFile;
				else if(tPane.getSelectedIndex() == 1 && tabulaModelBox.getSelectedItem() != null)
					modelFile = ((ComboBoxFileItem) tabulaModelBox.getSelectedItem()).file; 
				refresh();	

			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.gridwidth = 2;

		mainPanel.add(tPane, c);

		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 0;
		mainPanel.add(importButton, c);

		c.gridx = 1;
		mainPanel.add(cancel, c);

		refresh();
	}

	private void refresh() {
		if (modelFile == null)
			importButton.setEnabled(false);
		else
			importButton.setEnabled(true);
		frame.revalidate();
		frame.repaint();
	}

	private JPanel createTabulaPanel() {
		JPanel tabulaPanel = new JPanel();

		tabulaModelBox = new JComboBox<ComboBoxFileItem>(getComboBoxFileItems());
		tabulaModelBox.setPrototypeDisplayValue(new ComboBoxFileItem(new File("ModelSomethingOrOther.obm")));
		tabulaModelBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modelFile = ((ComboBoxFileItem) tabulaModelBox.getSelectedItem()).file; 
				refresh();				
			}
		});

		JLabel label = new JLabel("Tabula Model:");
		label.setPreferredSize(new Dimension(150, 25));

		tabulaPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.insets = new Insets(5, 5, 5, 5);

		tabulaPanel.add(label, c);

		c.gridx = 1;
		tabulaPanel.add(tabulaModelBox, c);
		return tabulaPanel;
	}

	private void importPressed() {
		try {
			ModelObj_Animator model = ModelFileHandler.instance.importModel(modelFile, ModelObj_Animator.class);
			ModelHandler.addModel(model);
			frame.dispose();
			new EntitySetupController(model.entityName).display();
		} catch (MissingImporterException e) {
			JOptionPane.showMessageDialog(frame, "There is no importer for this file type.", "Import Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void cancelPressed() {
		frame.dispose();
		new HomeFrame().display();
	}

	private ComboBoxFileItem[] getComboBoxFileItems() {
		ComboBoxFileItem[] fileItems = new ComboBoxFileItem[]{null};
		if(FileHandler.tabulaModelsDir.exists()) {
			File[] files = FileHandler.tabulaModelsDir.listFiles();
			fileItems = new ComboBoxFileItem[files.length];

			for(int i = 0; i < files.length; i++) 
				fileItems[i] = new ComboBoxFileItem(files[i]);
		}
		return fileItems;
	}

	private class FileSelectionPanel extends JPanel {
		JLabel titleLabel;
		JLabel locationLabel;
		JButton chooseFileButton;
		File targetFile;

		private FileSelectionPanel(String title, final FileNameExtensionFilter filter) {
			setLayout(new GridBagLayout());

			titleLabel = new JLabel(title);
			titleLabel.setHorizontalAlignment(JLabel.HORIZONTAL);

			locationLabel = new JLabel("No location set");
			locationLabel.setPreferredSize(new Dimension(150, 25));

			chooseFileButton = new JButton("Choose File");
			chooseFileButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						targetFile = FileChooser.loadImportFile(frame, filter);
						String path = targetFile.getAbsolutePath();
						locationLabel.setText(path);
						locationLabel.setToolTipText(path);
						modelFile = targetFile;
						refresh();
					} catch (FileNotChosenException e1) {
					}
				}
			});

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5, 5, 5, 5);

			c.gridwidth = 2;
			add(titleLabel, c);

			c.gridwidth = 1;
			c.gridy = 1;
			add(locationLabel, c);

			c.gridx = 1;
			add(chooseFileButton, c);
		}

	}

	private class ComboBoxFileItem {

		private File file;

		private ComboBoxFileItem(File file) {
			this.file = file;
		}

		@Override
		public String toString() {
			return file.getName().substring(0, file.getName().indexOf("."));
		}
	}


}
