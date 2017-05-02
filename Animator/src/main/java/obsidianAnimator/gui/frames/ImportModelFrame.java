package obsidianAnimator.gui.frames;

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

import obsidianAPI.exceptions.MissingImporterException;
import obsidianAPI.file.FileHandler;
import obsidianAPI.file.ModelFileHandler;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.file.FileChooser;
import obsidianAnimator.file.FileNotChosenException;
import obsidianAnimator.gui.entitySetup.EntitySetupController;
import obsidianAnimator.render.entity.ModelObj_Animator;

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
				if(tPane.getSelectedIndex() == 1)
					modelFile = ((ComboBoxFileItem) tabulaModelBox.getSelectedItem()).file; 
				else
					modelFile = null;
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
		c.insets = new Insets(5, 5, 5, 5);

		tabulaPanel.add(label);

		c.gridx = 1;
		tabulaPanel.add(tabulaModelBox);
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
		File[] files = FileHandler.tabulaModelsDir.listFiles();
		ComboBoxFileItem[] fileItems = new ComboBoxFileItem[files.length];
		
		for(int i = 0; i < files.length; i++) 
			fileItems[i] = new ComboBoxFileItem(files[i]);
		
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
