package password.manager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;


public class Main extends JFrame {
	
	private static final long serialVersionUID = 1L;
//menu, preferences/plaintext, enter data for you on gotoSite(), Watch out for SQL Injections.
	
	private SQLite database = new SQLite();
	
	private JPanel mainPanel;
	private JPanel display;
	
	private JButton addPass;
	private JButton delPass;
	private JButton seePass;
	private JButton altUser;
	private JButton altPass;
	private JButton altSite;
	
	private TablePanel table;
	
	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	private Password password;
	
	public Main() {
		//Window name
		setTitle("EasyPass");
		
		//Window on close operation
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLayout(new BorderLayout());
//		buttons = new buttonPanel();
		buildButtonPanel();
		table = new TablePanel();
		
		add(mainPanel, BorderLayout.WEST);
		add(table, BorderLayout.CENTER);
		
		pack();
		setSize(800,600);
		setVisible(true);
	}
	
	private void buildButtonPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(6,1,0,10));
		
		//finalize buttons
		addPass = new JButton("Add Account");
		delPass = new JButton("Delete Account");
		seePass = new JButton("View Credentials");
		altUser = new JButton("Change Username");
		altPass = new JButton("Change Password");
		altSite = new JButton("Change Website");
		
		addPass.addActionListener(new AddPassListener());
		delPass.addActionListener(new DelPassListener());
		seePass.addActionListener(new SeePassListener());
		altUser.addActionListener(new AltUserListener());
		altPass.addActionListener(new AltPassListener());
		altSite.addActionListener(new AltSiteListener());
		
		//set border around buttons
		mainPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
		
		//adds buttons to the panel
		mainPanel.add(addPass);
		mainPanel.add(delPass);
		mainPanel.add(seePass);
		mainPanel.add(altUser);
		mainPanel.add(altPass);
		mainPanel.add(altSite);
	}

	private class AddPassListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			
			password = new Password();
			password.generatePassword();
			
			try {
				String accountName = JOptionPane.showInputDialog(table, "Enter Account Name: ");
				while (accountName.contains("'") || accountName.equals("")) {
					accountName = JOptionPane.showInputDialog(table, "Your account name contained the ' character or was empty, please enter the account name without this character: ");
				}
				if (accountName != null) {
					String usernameName = JOptionPane.showInputDialog(table, "Enter Account Username: ");
					while (usernameName.contains("'") || usernameName.equals("")) {
						usernameName = JOptionPane.showInputDialog(table, "Your username contained the ' character or was empty, please enter the website name without this character: ");
					}
					if (usernameName != null) {
						String urlName = JOptionPane.showInputDialog(table, "Enter Website Name: ");
						while (urlName.contains("'") || urlName.equals("")) {
							urlName = JOptionPane.showInputDialog(table, "Your website name contained the ' character or was empty, please enter the website name without this character: ");
						}
						if (urlName != null) {
							database.addUser(accountName, usernameName, urlName, password.toString());
						}
					}
				}	
			} catch (Exception e) {
				System.out.println("Cancel Pressed");
			}
			
				
				remove(table);
				table = new TablePanel();
				add(table);
				revalidate();
				repaint();
			
			password = null;
			System.gc();
			
		}
	}
	private class DelPassListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (table.getSelection() == null) {
				JOptionPane.showMessageDialog(table, "Warning no account was selected, retry after selecting an account.");
			} else {
				try {
					String confirmation = JOptionPane.showInputDialog(table, "Confirm deletion by typing " + table.getSelection());
					if (confirmation.equalsIgnoreCase(table.getSelection())) {
						database.deleteUser(table.getSelection()); //Here
						
						remove(table);
						table = new TablePanel();
						add(table);
						revalidate();
						repaint();		
					} else {
						JOptionPane.showMessageDialog(table, "Incorrect account name.");
					}	
				} catch (Exception e) {
					System.out.println("Cancel Pressed");
				}
			}
		}
	}
	private class SeePassListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			display = new JPanel();
			
			JLabel passText;
			
			JButton copyUsername = new JButton("Copy Username");
			JButton copyPassword = new JButton("Copy Password");
			JButton toWebsite = new JButton("Go to Site");
			
			copyPassword.addActionListener(new CopyPassListener());
			copyUsername.addActionListener(new CopyUserListener());
			toWebsite.addActionListener(new ToWebsiteListener());
			
			
			ResultSet res;
			try {
				if (table.getSelection() == null) {
					JOptionPane.showMessageDialog(table, "Warning no account was selected, retry after selecting an account.");
				} else {
					
					res = database.displayInfo(table.getSelection());
					passText = new JLabel(res.getString("Password"));
					
					display.add(passText);
					display.add(copyUsername);
					display.add(copyPassword);
					display.add(toWebsite);
					
					JOptionPane.showMessageDialog(table, display);	
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
	//I might be able to remove the table.getSelection() here.
	private class CopyUserListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			ResultSet res;
			StringSelection stringSelection;
			try {
				res = database.displayInfo(table.getSelection());
				stringSelection = new StringSelection(res.getString("Username"));
				clipboard.setContents(stringSelection, null);
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
	private class CopyPassListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			ResultSet res;
			StringSelection stringSelection;
			try {
				res = database.displayInfo(table.getSelection());
				stringSelection = new StringSelection(res.getString("Password"));
				clipboard.setContents(stringSelection, null);			
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
	
	private class ToWebsiteListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			BrowserView browserView;
			ResultSet res;
			try {
				res = database.displayInfo(table.getSelection());
				browserView = new BrowserView(res.getString("URL"));
				browserView.gotoSite();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
	
	private class AltUserListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				if (table.getSelection() != null) {
					String newUsername = JOptionPane.showInputDialog(table, "Enter new username.");
					if (!newUsername.equals("")) {
						database.changeUser(table.getSelection(), newUsername);	
						
						remove(table);
						table = new TablePanel();
						add(table);
						revalidate();
						repaint();
					} else {
						JOptionPane.showMessageDialog(table, "Warning no replacement was entered, retry after entering a replacement.");
					}
				} else {
					JOptionPane.showMessageDialog(table, "Warning no account was selected, retry after selecting an account.");
				}	
			} catch (Exception e) {
				System.out.println("Cancel Pressed");
			}
		}
	}
	
	private class AltPassListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			
			password = new Password();
			password.generatePassword();
			String newPassword;
			
			if (table.getSelection() == null) {
				JOptionPane.showMessageDialog(table, "Warning no account was selected, retry after selecting an account.");
			} else {
				int answer = JOptionPane.showConfirmDialog(table, "Are you sure you would like to regenerate this account's password?", "EasyPass" ,JOptionPane.YES_NO_OPTION);
				System.out.println(answer);
				if (answer == 0) {
					int answer2 = JOptionPane.showConfirmDialog(table, "Would you like to enter your own password?", "EasyPass", JOptionPane.YES_NO_OPTION);
					if (answer2 == 0) {
						newPassword = JOptionPane.showInputDialog(table, "Enter new password.");
						password = new Password(newPassword);
					}
					database.changePass(table.getSelection(), password.toString());	
					JOptionPane.showMessageDialog(table, "Password successfully changed.");
				} else {
					JOptionPane.showMessageDialog(table, "Password not changed.");
				}
			}
			password = null;
			System.gc();
		}
	}
	
	private class AltSiteListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				if (table.getSelection() != null) {
					String newSite = JOptionPane.showInputDialog(table, "Enter the URL of the new website.");
					if (!newSite.equals("")) {
						database.changeSite(table.getSelection(), newSite);	
						
						remove(table);
						table = new TablePanel();
						add(table);
						revalidate();
						repaint();
					} else {
						JOptionPane.showMessageDialog(table, "Warning no replacement was entered, retry after entering a replacement.");
					}
				} else {
					JOptionPane.showMessageDialog(table, "Warning no account was selected, retry after selecting an account.");
				}	
			} catch (Exception e) {
				System.out.println("Cancel Pressed");
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		new Main();
	}
	
}
