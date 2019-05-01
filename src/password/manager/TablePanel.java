package password.manager;

import java.awt.BorderLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

public class TablePanel extends JPanel {
	private static final long serialVersionUID = 1480513754465297259L;

	private SQLite database = new SQLite();
	
	private String[] columnNames = {"Account Name", "Username", "URL", "Password"};
	private String[][] data;
	
	private int amountAccounts;
	private int row;
	
	private String selectedAccount;
	private ListSelectionModel listSelectionModel;
	
	private JTable table;
	private JScrollPane scrollPane;
	
	public TablePanel() {
		buildPanel();
	}
	private void buildDataArray() {
		try {
			amountAccounts = database.displayAmount();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
		data = new String[amountAccounts][4];
		ResultSet res;
		row = 0;
		try {
			res = database.displayUsers();
			while(res.next()) {
				data[row][0] = res.getString("Account");
				data[row][1] = res.getString("Username");
				data[row][2] = res.getString("URL");
				data[row][3] = "****************";
				row++;
			}
			res.close();
			database.closeConnection();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	private void buildPanel() {
		buildDataArray();
		
		setLayout(new BorderLayout());
		
//		table = new JTable(data, columnNames);
		table = new JTable(new TableModel());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSelectionModel = table.getSelectionModel();
		listSelectionModel.addListSelectionListener(new TableListener());
		scrollPane = new JScrollPane(table);
		
		setBorder(BorderFactory.createTitledBorder("Data"));
		
		add(scrollPane);
	}
	public String getSelection() {
		return selectedAccount;
	}
	
	private class TableModel extends AbstractTableModel {
		private static final long serialVersionUID = -5216922331789207037L;
		public int getColumnCount() {
			return columnNames.length;
		}
		public int getRowCount() {
			return data.length;
		}
		
		public String getColumnName(int col) {
			return columnNames[col];
		}
		public String getValueAt(int rowNum, int colNum) {
			return data[rowNum][colNum];
		}
	}
	
	private class TableListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			selectedAccount = (String) table.getValueAt(table.getSelectedRow(), 0);
		}
	}
}
