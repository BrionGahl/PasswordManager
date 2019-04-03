package password.manager;

import java.awt.BorderLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class LayoutPanel extends JPanel {

	private static final long serialVersionUID = 7081313399866160589L;

	private SQLite database = new SQLite();
	
	private JList<String> userList; //hold sql data
	private JScrollPane scrollPane; //hold userList
	
	private int amountUsers; //makes list
	private int count; //used to parse through acc's
	
	private String selectedUser;
	
	public LayoutPanel() {
		buildPanel();
	}
	private void buildPanel() {
		
		//this try/catch grabs number of accounts in the database.
		try {
			amountUsers = database.displayAmount();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
		String[] users = new String[amountUsers];
		ResultSet res;
		count = 0;
		try {
			res = database.displayUsers();
			while(res.next()) {
				users[count] = res.getString("Account");
				count++;
			}
			res.close();
			database.closeConnection();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		setLayout(new BorderLayout());
		
		userList = new JList<String>(users);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.addListSelectionListener(new ListListener());
		scrollPane = new JScrollPane(userList);
		
		setBorder(BorderFactory.createTitledBorder("Accounts"));
		
		add(scrollPane);
	}
	
	private class ListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			selectedUser = (String) userList.getSelectedValue();
		}
	}
	
	public String getSelection() {
		return selectedUser;
	}
	

}
