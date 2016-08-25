package MCEntityAnimator.gui.animation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.lwjgl.opengl.Display;

import com.jcraft.jsch.JSchException;

import MCEntityAnimator.distribution.DataHandler;
import MCEntityAnimator.distribution.ServerAccess;
import MCEntityAnimator.gui.GuiBlack;
import MCEntityAnimator.gui.GuiHandler;
import net.minecraft.client.Minecraft;

public class LoginGUI extends JFrame
{

	private static final long serialVersionUID = 6032906317630465138L;

	//	private static final String[] users = new String[]{"joe", "kurt", "root"};
	//	private static final Map<String, char[]> passwords = new HashMap<String, char[]>();
	//	private static final char[] joePassword = "dabigjoe".toCharArray();
	//	private static final char[] kurtPassword = "projectxykurt".toCharArray();
	//	private static final char[] rootPassword = "iamroot".toCharArray();
	//	
	private JPasswordField passwordField;

	public LoginGUI()
	{
		super("Login");

		//		passwords.put("joe", joePassword);
		//		passwords.put("kurt", kurtPassword);
		//		passwords.put("root", rootPassword);

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setPreferredSize(new Dimension(400, 250));
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1;
		c.insets = new Insets(10,10,10,10);

		c.gridy = 0;
		c.gridwidth = 1;
		mainPanel.add(new JLabel("Login as: "),c);
		c.gridx = 1;
		final JTextField usernameField = new JTextField();
		mainPanel.add(usernameField, c);

		c.gridy = 1;
		c.gridx = 0;
		mainPanel.add(new JLabel("Password: "),c);
		c.gridx = 1;
		passwordField = new JPasswordField();
		mainPanel.add(passwordField, c);

		c.gridy = 2;
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String username = usernameField.getText();
				String password = new String(passwordField.getPassword());

				if(ServerAccess.testConnection())
				{
					try 
					{
						ServerAccess.login(username, password);
						DataHandler.downloadFileList();
						//dispose();
						//new MainGUI();
					} 
					catch (JSchException exeception) 
					{
						System.out.println("Incorrect username/password combination.");
					}	
				}
				else
					System.out.println("Offline");
				


				//JOptionPane.showConfirmDialog(mainPanel, "Unable to connect to server. Run in offline mode? (Changes will not be saved).", "Connection Error", JOptionPane.YES_NO_OPTION) == 0)
				//JOptionPane.showMessageDialog(mainPanel, "Incorrect password.");
			}
		});
		mainPanel.add(loginButton,c);

		c.gridy = 3;
		JButton registerButton = new JButton("New User? Register here");
		registerButton.setEnabled(false);
		registerButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{

			}
		});
		mainPanel.add(registerButton,c);
		
		c.gridy = 4;
		JButton closeButton = new JButton("Quit");
		closeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				onClose();
				dispose();
			}
		});
		mainPanel.add(closeButton,c);
		
		addWindowListener(new WindowAdapter()
		{
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				onClose();
			}
			
		});

		setContentPane(mainPanel);
		pack();
		setResizable(false);
		setVisible(true);
		setAlwaysOnTop(true);
		setLocation(Display.getX() + Display.getWidth()/2 - this.getWidth()/2, Display.getY() + Display.getHeight()/2 - this.getHeight()/2);

	}


	private void onClose()
	{
		GuiHandler.loginGUI = null;
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen instanceof GuiBlack)
			((GuiBlack) mc.currentScreen).initateClose();
	}
	
}

