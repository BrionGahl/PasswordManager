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

	
	private SQLite database = new SQLite();
	
	private JPanel mainPanel;
	
	private JButton addPass;
	private JButton delPass;
	private JButton seePass;
	private JButton altPass;
	private JButton altSite;
	
	private LayoutPanel lists;
	
	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	public Main() {
		//Window name
		setTitle("EasyPass");
		
		//Window on close operation
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLayout(new BorderLayout());
		
//		buttons = new buttonPanel();
		buildButtonPanel();
		lists = new LayoutPanel();
		
		add(mainPanel, BorderLayout.WEST);
		add(lists, BorderLayout.CENTER);
		
		pack();
		setSize(800,600);
		setVisible(true);
	}
	
	private void buildButtonPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(5,1));
		
		//finalize buttons
		addPass = new JButton("Add Account");
		delPass = new JButton("Delete Account");
		seePass = new JButton("View Password");
		altPass = new JButton("Change Password");
		altSite = new JButton("Change Website");
		
		addPass.addActionListener(new AddPassListener());
		delPass.addActionListener(new DelPassListener());
		seePass.addActionListener(new SeePassListener());
		
		//set border around buttons
		mainPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
		
		//adds buttons to the panel
		mainPanel.add(addPass);
		mainPanel.add(delPass);
		mainPanel.add(seePass);	
		mainPanel.add(altPass);
		mainPanel.add(altSite);
	}
	
	private class AddPassListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			
			Password password = new Password();
			password.generatePassword();
			
			String accountName = JOptionPane.showInputDialog(lists, "Enter Account Name: ");
			while (accountName.contains("'")) {
				accountName = JOptionPane.showInputDialog(lists, "Your account name contained the ' character, please enter the account name without this character: ");
			}
			String urlName = JOptionPane.showInputDialog(lists, "Enter Website Name: ");
			while (urlName.contains("'")) {
				urlName = JOptionPane.showInputDialog(lists, "Your website name contained the ' character, please enter the website name without this character: ");
			}
			
			database.addUser(accountName, urlName, password.toString());
			
			remove(lists);
			lists = new LayoutPanel();
			add(lists);
			revalidate();
			repaint();
		}
	}
	private class DelPassListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (lists.getSelection() == null) {
				JOptionPane.showMessageDialog(lists, "Warning no account was selected, retry after selecting an account.");
			} else {
				String confirmation = JOptionPane.showInputDialog(lists, "Confirm deletion by typing " + lists.getSelection());
				if (confirmation.equalsIgnoreCase(lists.getSelection())) {
					database.deleteUser(lists.getSelection());
					
					remove(lists);
					lists = new LayoutPanel();
					add(lists);
					revalidate();
					repaint();		
				} else {
					JOptionPane.showMessageDialog(lists, "Incorrect account name.");
				}
			}
		}
	}
	private class SeePassListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			JPanel display = new JPanel();
			
			JLabel website;
			JLabel password;
			
			JButton copyPassword = new JButton("Copy");
			JButton toWebsite = new JButton("Go to Site");
			
			copyPassword.addActionListener(new CopyPassListener());
			toWebsite.addActionListener(new ToWebsiteListener());
			
			
			ResultSet res;
			try {
				if (lists.getSelection() == null) {
					JOptionPane.showMessageDialog(lists, "Warning no account was selected, retry after selecting an account.");
				} else {
					
					res = database.displayInfo(lists.getSelection());
					website = new JLabel(res.getString("URL"));
					password = new JLabel(res.getString("Password"));
					
					display.add(website);
					display.add(password);
					display.add(copyPassword);
					display.add(toWebsite);
					
					JOptionPane.showMessageDialog(lists, display);	
				}
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
				if (lists.getSelection() == null) {
					JOptionPane.showMessageDialog(lists, "Warning no account was selected, retry after selecting an account.");
				} else {
					res = database.displayInfo(lists.getSelection());
					stringSelection = new StringSelection(res.getString("Password"));
					clipboard.setContents(stringSelection, null);
				}
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
				if (lists.getSelection() == null) {
					JOptionPane.showMessageDialog(lists, "Warning no account was selected, retry after selecting an account.");
				} else {
					res = database.displayInfo(lists.getSelection());
					browserView = new BrowserView(res.getString("URL"));
					browserView.gotoSite();
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		new Main();
	}
	
}
