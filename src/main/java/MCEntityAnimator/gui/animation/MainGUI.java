package MCEntityAnimator.gui.animation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultCaret;

import org.lwjgl.opengl.Display;

import com.jcraft.jsch.JSchException;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.distribution.DataHandler;
import MCEntityAnimator.distribution.FileInfo;
import MCEntityAnimator.distribution.FileInfo.Status;
import MCEntityAnimator.distribution.FileInfo.StatusAction;
import MCEntityAnimator.distribution.ServerAccess;
import MCEntityAnimator.gui.GuiBlack;
import MCEntityAnimator.gui.GuiHandler;
import MCEntityAnimator.gui.animation.table.ButtonColumn;
import MCEntityAnimator.gui.sequence.GuiAnimationTimeline;
import net.minecraft.client.Minecraft;

public class MainGUI extends JFrame
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

	public MainGUI()
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

		JScrollPane overviewView = new JScrollPane(createTable());
		overviewView.setPreferredSize(new Dimension(overviewView.getPreferredSize().width, 200));
		overviewView.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		overviewView.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

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
					Minecraft.getMinecraft().displayGuiScreen(new GuiAnimationTimeline(entityToEdit,seq));
				}
				else
					JOptionPane.showMessageDialog(MainGUI.this, "Unable to load animation " + animationToEdit + " for " + entityToEdit + ".");
			}
		});

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
				if(JOptionPane.showConfirmDialog(MainGUI.this, "Refreshing will overwrite any changes you may \n have made since the last time you uploaded.\n Continue?", 
						"Refresh?", JOptionPane.YES_NO_OPTION) == 0)
				{


					updateOutput("/-------------< Beginning download >-------------\\", false);
					Runnable downloadThread = new Runnable() 
					{
						public void run() 
						{
							//SaveLoadHandler.download();
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
						List<String> failedFiles =  new ArrayList<String>();//SaveLoadHandler.upload();
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
				dispose();
				onClose();		
			}
		});

		buttonPanel.add(newButton);
		buttonPanel.add(refresh);
		buttonPanel.add(upload);
		buttonPanel.add(close);

		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridy = 0;
		mainPanel.add(new JLabel("File Overview"),c);
		c.gridy = 1;
		mainPanel.add(overviewView,c);
		c.gridy = 2;
		mainPanel.add(editButton, c);

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
			public void windowClosing(WindowEvent e) 
			{
				onClose();		
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

	private void onClose()
	{
		GuiHandler.loginGUI = null;
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen instanceof GuiBlack)
			((GuiBlack) mc.currentScreen).initateClose();
	}

	private JTable createTable()
	{
		String[] columnNames = {"File", "Status", "Action"};

		List<FileInfo> fileData = DataHandler.getFileList();
		Object[][] data = new Object[fileData.size()][4];

		for(int i = 0; i < fileData.size(); i++)
		{
			FileInfo fileInfo = fileData.get(i);
			Status status = fileInfo.getStatus();
			data[i][0] = fileInfo.getFileHRF();
			data[i][1] = status.name();
			data[i][2] = status.action.name();
		}

		JTable table = new JTable(data, columnNames)
		{
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component c = super.prepareRenderer(renderer, row, column);
				
				if(column == 1)
					c.setForeground(DataHandler.getFileList().get(row).getStatus().color);
				else
					c.setForeground(Color.black);
					
				return c;
			}
		};
		table.getTableHeader().setReorderingAllowed(false);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		table.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );

		Action processAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JTable table = (JTable)e.getSource();
				int row = Integer.valueOf(e.getActionCommand());
				StatusAction action = DataHandler.getFileList().get(row).getStatus().action;
				if(action == StatusAction.Pull)
				{
					try 
					{
						String path = DataHandler.getFileList().get(row).getPath();
						if(path.contains("."))
							ServerAccess.getFile("animation/user/" + path, "animation/" + path);
					} 
					catch (IOException e1) {e1.printStackTrace();} 
					catch (JSchException e1) {e1.printStackTrace();}
				}
				else if(action == StatusAction.Push)
				{
					try 
					{
						String path = DataHandler.getFileList().get(row).getPath();
						if(path.contains("."))
							ServerAccess.sendFile("animation/user/" + path, "animation/" + path, true);
					} 
					catch (IOException e1) {e1.printStackTrace();} 
					catch (JSchException e1) {e1.printStackTrace();}
				}
			}
		};

		ButtonColumn buttonColumn = new ButtonColumn(table, processAction, 2, StatusAction.None.name());

		return table;
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

	private void initModelSetup(String entityName)
	{
		//		if(ServerAccess.username.equals("root"))
		//			if(JOptionPane.showConfirmDialog(MainGUI.this, "Setup model for " + entityName + "?", "Setup Model", JOptionPane.YES_NO_OPTION) == 0)
		//			{
		//				Minecraft.getMinecraft().displayGuiScreen(new GuiPartSetup(entityName));
		//				MainGUI.this.dispose();
		//			}
		//			else
		//				JOptionPane.showMessageDialog(MainGUI.this, "Permission denied, must be root user.");
	}

}
