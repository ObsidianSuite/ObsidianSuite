package MCEntityAnimator.gui.animation;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.lwjgl.opengl.Display;

import MCEntityAnimator.MCEA_Main;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.distribution.SaveLoadHandler;
import MCEntityAnimator.distribution.ServerAccess;
import MCEntityAnimator.gui.GuiPartSetup;
import MCEntityAnimator.gui.sequence.GuiAnimationTimelineWithFrames;
import MCEntityAnimator.gui.sequence.GuiEntityRendererWithRotation;
import net.minecraft.client.Minecraft;

public class FileGUI extends JFrame
{

	private static final long serialVersionUID = -3402393679860402540L;

	private Border spaceBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	private Border bevelBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	private Border customBorder = BorderFactory.createCompoundBorder(bevelBorder, spaceBorder);

	private static final String logFolderPath = "logs";

	private Calendar cal = Calendar.getInstance();
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	JTextArea outputLog;
	JButton editButton;

	private String entityToEdit;
	private String animationToEdit;

	public FileGUI()
	{
		super("Animation Files");

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());

		createGUI(mainPanel, "");
	}

	private void createGUI(final JPanel mainPanel, String outputText)
	{		
		mainPanel.removeAll();

		GridBagConstraints c = new GridBagConstraints();

		final JTree tree = new JTree(getFileTree(new File(MCEA_Main.animationPath + "/data")));
		tree.setBorder(customBorder);
		tree.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) 
			{
				processTreeClick(tree, e);
			}
		});
		JScrollPane treeView = new JScrollPane(tree);
		treeView.setPreferredSize(new Dimension(200,200));
		treeView.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeView.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel todoPanel = createErrorPanel();
		JScrollPane todoView = new JScrollPane(todoPanel);
		todoView.setPreferredSize(new Dimension(200,200));
		todoView.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		todoView.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		outputLog = new JTextArea(15,30);
		DefaultCaret caret = (DefaultCaret)outputLog.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		outputLog.setEditable(false);
		outputLog.setLineWrap(true);
		outputLog.setWrapStyleWord(true);
		outputLog.setBackground(Color.black);
		outputLog.setForeground(Color.gray);

		editButton = new JButton("Edit");
		editButton.setToolTipText("Edit the selected animation.");
		editButton.setEnabled(false);
		editButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				AnimationSequence seq = null;
				for(AnimationSequence s : AnimationData.getSequences(entityToEdit))
				{
					if(s.getName().equals(animationToEdit))
					{
						seq = s;
						break;
					}
				}
				if(seq != null)
				{
					dispose();
					//Minecraft.getMinecraft().displayGuiScreen(new GuiAnimationTimelineWithFrames(entityToEdit,seq));
					Minecraft.getMinecraft().displayGuiScreen(new GuiEntityRendererWithRotation(entityToEdit));
				}
				else
					JOptionPane.showMessageDialog(FileGUI.this, "Unable to load animation " + animationToEdit + " for " + entityToEdit + ".");
			}
		});
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(new JLabel("Data"),c);
		c.gridy = 1;
		mainPanel.add(treeView,c);
		c.gridy = 2;
		mainPanel.add(editButton, c);

		c.gridx = 1;
		c.gridy = 0;
		mainPanel.add(new JLabel("Errors"),c);
		c.gridy = 1;
		c.gridheight = 2;
		mainPanel.add(todoView,c);

		JPanel buttonPanel = new JPanel();
		GridLayout layout = new GridLayout(0,4);
		layout.setHgap(5);
		buttonPanel.setLayout(layout);

		JButton newButton = new JButton("New");
		newButton.setToolTipText("Create a new animation.");
		newButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				dispose();
				new AnimationNewGUI();
			}
		});

		JButton refresh = new JButton("Refresh");
		refresh.setToolTipText("Pull all the latest data from the server. Will overwrite any local changes.");
		refresh.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				if(JOptionPane.showConfirmDialog(FileGUI.this, "Refreshing will overwrite any changes you may \n have made since the last time you uploaded.\n Continue?", 
						"Refresh?", JOptionPane.YES_NO_OPTION) == 0)
				{


					updateOutput("/-------------< Beginning download >-------------\\", false);
					Runnable downloadThread = new Runnable() 
					{
						public void run() 
						{
							SaveLoadHandler.download();
							updateOutput("\\-------------< Download complete >-------------/", false);
							createGUI(mainPanel, outputLog.getText());
						}
					};
					new Thread(downloadThread).start();
				}
			}
		});

		JButton upload = new JButton("Upload");
		upload.setToolTipText("Push all your data to the server.");
		upload.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				updateOutput("/------------< Beginning upload >-------------\\", false);
				Runnable uploadThread = new Runnable() 
				{
					public void run() 
					{
						List<String> failedFiles = SaveLoadHandler.upload();
						if(failedFiles.isEmpty())
							updateOutput("\\--------------< Upload complete >-------------/", false);
						else
						{
							updateOutput("\\--------< Upload finished, some errors >--------/", false);
							String s = "Upload failed for these files:\n";
							for(String failedFile : failedFiles)
								s += "   - " + failedFile + "\n";
							showUploadErrorPopup(s);
						}
					}
				};
				new Thread(uploadThread).start();
			}
		});

		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				ServerAccess.username = "";
				dispose();
				Minecraft.getMinecraft().displayGuiScreen(null);
			}
		});

		buttonPanel.add(newButton);
		buttonPanel.add(refresh);
		buttonPanel.add(upload);
		buttonPanel.add(close);

		c.gridwidth = 2;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 3;
		mainPanel.add(new JScrollPane(outputLog, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),c);
		c.gridy = 4;
		mainPanel.add(buttonPanel,c);

		setContentPane(mainPanel);
		pack();
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setLocation(Display.getX() + Display.getWidth()/2 - this.getWidth()/2, Display.getY() + Display.getHeight()/2 - this.getHeight()/2);

		outputLog.setText(outputText);

		addWindowListener(new WindowAdapter() 
		{    
			public void windowClosed(WindowEvent e) 
			{
				ServerAccess.gui = null;
			}

			public void windowClosing(WindowEvent e) 
			{
				ServerAccess.gui = null;			
			}
		});
	}

	public void updateOutput(String text, boolean server)
	{
		String current = outputLog.getText();
		text = "[" + (server ? "SERVER" : "CLIENT") + "] " + text;
		text = "[" + sdf.format(cal.getTime()) + "] " + text;
		outputLog.setText(current + text + "\n");
	}

	private DefaultMutableTreeNode getFileTree(File file)
	{   
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(file.getName());

		if(file.isDirectory())
		{
			File[] files = file.listFiles();
			if(files.length == 0)
				top.add(new DefaultMutableTreeNode("EMPTY!"));
			else
			{
				for(File f : files)
					top.add(getFileTree(f));
			}

		}
		return top;
	}

	private JPanel createErrorPanel()
	{
		JPanel errorPanel = new JPanel();
		errorPanel.setBorder(customBorder);
		errorPanel.setBackground(Color.white);
		errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));

		for(String s : ServerAccess.getErrors())
		{
			final JLabel label = new JLabel(s);
			if(label.getText().contains("No data file"))
			{
				final String entityName = label.getText().substring(17, label.getText().length() - 1);
				label.setForeground(Color.decode("#0000FF"));
				label.addMouseListener(new MouseListener()
				{

					@Override
					public void mouseClicked(MouseEvent arg0) {initModelSetup(entityName);}

					@Override
					public void mouseEntered(MouseEvent arg0) {label.setForeground(Color.decode("#00CCFF"));}

					@Override
					public void mouseExited(MouseEvent arg0) {label.setForeground(Color.decode("#0000FF"));}

					@Override
					public void mousePressed(MouseEvent arg0) {}

					@Override
					public void mouseReleased(MouseEvent arg0) {}

				});
			}
			errorPanel.add(label);
		}

		return errorPanel;
	}

	private void showUploadErrorPopup(String text)
	{
		//Custom button text
		Object[] options = {"Open log", "Ok"};
		int n = JOptionPane.showOptionDialog(this, text, "Upload error",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				options,
				options[1]);
		if(n == 0)
		{
			try 
			{
				Desktop.getDesktop().open(getLogLocation());
				Desktop.getDesktop().open(writeOutputLogToFile());
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	private File writeOutputLogToFile() throws IOException 
	{
		String fileName = "MCEA_Uploader_Log-" + sdf.format(cal.getTime()) + ".txt";
		fileName = fileName.replace(":", "-");
		File file = new File(getLogLocation(), fileName);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter writer = new PrintWriter(fos);
		for(String s : outputLog.getText().split("\n"))
		{
			writer.println(s);
		}
		writer.close();
		return file;
	}

	private File getLogLocation() 
	{
		File file = new File(logFolderPath);
		file.mkdirs();
		return file;
	}

	private void processTreeClick(JTree tree, MouseEvent e) 
	{
		TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
		if (tp != null)
		{
			int length = tp.getPathCount();
			boolean enabled = false;
			if(length == 4 && !tp.getPathComponent(1).toString().equals("shared"))
			{
				animationToEdit = tp.getPathComponent(3).toString();
				if(!animationToEdit.equals("EMPTY!"))
				{
					enabled = true;
					animationToEdit = animationToEdit.substring(0, animationToEdit.indexOf("."));
					entityToEdit = tp.getPathComponent(2).toString();
				}
			}
			else if(length == 3 && tp.getPathComponent(1).toString().equals("shared"))
			{
				initModelSetup(tp.getPathComponent(2).toString());
			}
			editButton.setEnabled(enabled);
		}
	}

	private void initModelSetup(String entityName)
	{
		if(ServerAccess.username.equals("root"))
			if(JOptionPane.showConfirmDialog(FileGUI.this, "Setup model for " + entityName + "?", "Setup Model", JOptionPane.YES_NO_OPTION) == 0)
			{
				Minecraft.getMinecraft().displayGuiScreen(new GuiPartSetup(entityName));
				FileGUI.this.dispose();
			}
			else
				JOptionPane.showMessageDialog(FileGUI.this, "Permission denied, must be root user.");
	}

}
