package MCEntityAnimator.gui.animation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.lwjgl.opengl.Display;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.distribution.DataHandler;
import MCEntityAnimator.distribution.FileInfo;
import MCEntityAnimator.distribution.FileInfo.Status;
import MCEntityAnimator.distribution.FileInfo.StatusAction;
import MCEntityAnimator.distribution.ServerAccess;
import MCEntityAnimator.distribution.job.Job;
import MCEntityAnimator.distribution.job.JobPull;
import MCEntityAnimator.distribution.job.JobPush;
import MCEntityAnimator.gui.GuiBlack;
import MCEntityAnimator.gui.GuiHandler;
import MCEntityAnimator.gui.animation.table.ButtonColumn;
import MCEntityAnimator.gui.sequence.GuiAnimationTimeline;
import net.minecraft.client.Minecraft;

public class MainGUI extends JFrame
{

	private static final long serialVersionUID = -3402393679860402540L;

	JButton editButton;
	JScrollPane overviewView;
	JPanel mainPanel;
	public JobPanel jobPanel;

	private String entityToEdit;
	private String animationToEdit;

	public MainGUI()
	{
		super("Animation Files");

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());

		createGUI(mainPanel, "");
	}

	private void createGUI(final JPanel mainPanel, String outputText)
	{		
		mainPanel.removeAll();

		GridBagConstraints c = new GridBagConstraints();

		overviewView = new JScrollPane(createTable());
		overviewView.setPreferredSize(new Dimension(overviewView.getPreferredSize().width, 200));
		overviewView.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		overviewView.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);	

		jobPanel = new JobPanel();

		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(new JLabel("File Overview"),c);
		c.gridy = 1;
		mainPanel.add(overviewView,c);
		c.gridy = 2;
		mainPanel.add(jobPanel, c);
		c.gridy = 3;
		mainPanel.add(createButtonPanel(),c);

		setContentPane(mainPanel);
		pack();
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setLocation(Display.getX() + Display.getWidth()/2 - this.getWidth()/2, Display.getY() + Display.getHeight()/2 - this.getHeight()/2);

		addWindowListener(new WindowAdapter() 
		{    
			public void windowClosing(WindowEvent e) 
			{
				onClose();		
			}
		});
	}

	private void onClose()
	{
		GuiHandler.loginGUI = null;
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen instanceof GuiBlack)
			((GuiBlack) mc.currentScreen).initateClose();
	}

	private JPanel createButtonPanel()
	{
		JPanel buttonPanel = new JPanel();
		GridLayout layout = new GridLayout(0,6);
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

		JButton refresh = new JButton("Refresh");
		refresh.setToolTipText("Refresh table and get latest data from server. Does not download files.");
		refresh.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				refreshTable();
				jobPanel.refresh();
			}
		});

		JButton pushAllButton = new JButton("Push all");
		pushAllButton.setToolTipText("Push any update files to the server.");
		pushAllButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				for(FileInfo fileInfo : DataHandler.getFilesForPushAll())
				{
					String path = fileInfo.getPath();
					String localAddress, remoteAddress;
					if(path.contains("."))
					{
						localAddress = "animation/user/" + path;
						remoteAddress = "animation/" + path;
					}
					else
					{
						localAddress = "animation/shared/" + path + ".data";
						remoteAddress = "/home/shared/animation/" + path + ".data";
					}
					JobPush job = new JobPush(fileInfo.getFileHRF(), localAddress, remoteAddress);
					DataHandler.jobHandler.queueJob(job);
				}		
			}
		});

		JButton pullAllButton = new JButton("Pull all");
		pullAllButton.setToolTipText("Pull any updated files from the server.");
		pullAllButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				for(FileInfo fileInfo : DataHandler.getFilesForPullAll())
				{
					String path = fileInfo.getPath();
					String localAddress, remoteAddress;
					if(path.contains("."))
					{
						localAddress = "animation/user/" + path;
						remoteAddress = "animation/" + path;
						DataHandler.jobHandler.queueJob(new JobPull(fileInfo.getFileHRF(), localAddress, remoteAddress));
					}
					else
					{
						String[] exts = new String[]{"data", "pxy", "png", "obj"};
						for(String ext : exts)
						{
							localAddress = "animation/shared/" + path + "." + ext;
							remoteAddress = "/home/shared/animation/" + path + "."  + ext;
							DataHandler.jobHandler.queueJob(new JobPull(fileInfo.getFileHRF(), localAddress, remoteAddress));
						}
					}
				}
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
		buttonPanel.add(editButton);
		buttonPanel.add(refresh);
		buttonPanel.add(pushAllButton);
		buttonPanel.add(pullAllButton);
		buttonPanel.add(close);

		return buttonPanel;
	}

	public class JobPanel extends JPanel
	{
		private JLabel jobNumLabel;
		private JLabel curJobLabel;
		private JLabel curJobStatusLabel;

		private JobPanel()
		{				
			jobNumLabel = new JLabel("0");
			curJobLabel = new JLabel("-");
			curJobStatusLabel = new JLabel("-");

			setLayout(new GridBagLayout());
			setBorder(BorderFactory.createTitledBorder("Job queue"));
			GridBagConstraints c = new GridBagConstraints();

			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.EAST;
			c.insets = new Insets(2,2,2,2);
			c.weightx = 0;
			add(new JLabel("Jobs in queue:"),c);
			c.gridy = 1;
			add(new JLabel("Current job:"),c);
			c.gridy = 2;
			add(new JLabel("Current job status:"),c);

			c.gridx = 1;
			c.gridy = 0;
			c.anchor = GridBagConstraints.WEST;
			c.weightx = 1;
			add(jobNumLabel,c);
			c.gridy = 1;
			add(curJobLabel,c);
			c.gridy = 2;
			add(curJobStatusLabel,c);
		}

		private void refresh()
		{
			//			mainPanel.remove(this);
			//			jobPanel = new JobPanel();
			//			GridBagConstraints c = new GridBagConstraints();
			//			c.fill = GridBagConstraints.BOTH;
			//			c.insets = new Insets(5,5,5,5);
			//			c.gridy = 2;
			//			mainPanel.add(jobPanel, c);
			//			mainPanel.revalidate();
			//			mainPanel.repaint();

			revalidate();
			repaint();
		}

		public void updateJobNumberLabel(int number)
		{
			jobNumLabel.setText(Integer.toHexString(number));
			refresh();
		}

		public void updateCurrentJobLabel(String currentJob)
		{
			curJobLabel.setText(currentJob);
			refresh();
		}

		public void updateCurrentJobStatusLabel(String currentJobStatus)
		{
			curJobStatusLabel.setText(currentJobStatus);
			refresh();
		}
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
				FileInfo fileInfo = DataHandler.getFileList().get(row);
				String path = fileInfo.getPath();
				if(action == StatusAction.Push)
				{
					String localAddress, remoteAddress;
					if(path.contains("."))
					{
						localAddress = "animation/user/" + path;
						remoteAddress = "animation/" + path;
					}
					else
					{
						localAddress = "animation/shared/" + path + ".data";
						remoteAddress = "/home/shared/animation/" + path + ".data";
					}
					DataHandler.jobHandler.queueJob(new JobPush(fileInfo.getFileHRF(), localAddress, remoteAddress));
				}
				else if(action == StatusAction.Pull)
				{
					String localAddress, remoteAddress;
					if(path.contains("."))
					{
						localAddress = "animation/user/" + path;
						remoteAddress = "animation/" + path;
						DataHandler.jobHandler.queueJob(new JobPull(fileInfo.getFileHRF(), localAddress, remoteAddress));
					}
					else
					{
						String[] exts = new String[]{"data", "pxy", "png", "obj"};
						for(String ext : exts)
						{
							localAddress = "animation/shared/" + path + "." + ext;
							remoteAddress = "/home/shared/animation/" + path + "."  + ext;
							DataHandler.jobHandler.queueJob(new JobPull(fileInfo.getFileHRF(), localAddress, remoteAddress));
						}
					}
				}
			}
		};

		table.getColumnModel().getColumn(0).setPreferredWidth(200);

		ButtonColumn buttonColumn = new ButtonColumn(table, processAction, 2, StatusAction.None.name());

		return table;
	}

	public void refreshTable()
	{
		String username = ServerAccess.getUser();
		if(username != null)
		{
			DataHandler.generateFileList(username);
			mainPanel.remove(overviewView);
			overviewView = new JScrollPane(createTable());
			overviewView.setPreferredSize(new Dimension(overviewView.getPreferredSize().width, 200));
			overviewView.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			overviewView.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(5,5,5,5);
			c.weightx = 1;
			c.weighty = 1;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.gridx = 0;
			c.gridy = 1;
			mainPanel.add(overviewView, c);
			mainPanel.revalidate();
			mainPanel.repaint();
		}
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
