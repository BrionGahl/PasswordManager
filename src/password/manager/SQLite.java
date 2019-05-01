package password.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class SQLite {

	private Connection conn;

	private boolean hasData = false;
	
	private void getConnection() {
		final String  DB_URL = "jdbc:sqlite:data.db";
		
		try
		{
			Class.forName("org.sqlite.JDBC"); //loads class with this name
			conn = DriverManager.getConnection(DB_URL);
			System.out.println("Established Connection to DB");
			initialize();
		}
		catch(ClassNotFoundException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	private void initialize() {
		if (!hasData) {
			hasData = true;
			try
			{
				Statement state = conn.createStatement();
				ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='data'");
				
				if(!res.next()) {
//					System.out.println("Building data table.");
					
					Statement state1 = conn.createStatement();
					state1.execute("CREATE TABLE data(account varchar(60), username varchar(60), url varchar(60), password varchar(60));");
					
				}	
			}
			catch (SQLException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
	
	public void closeConnection() throws SQLException {
		if (conn == null) {
			getConnection();
		}
		conn.close();
//		System.out.println("Layout panel successfully close connection.");
	}
	
	public ResultSet displayUsers() throws SQLException {
		if (conn == null) {
			getConnection();
		}
		
		Statement state = conn.createStatement();
		ResultSet res = state.executeQuery("SELECT account, username, url, password FROM data");
		
		return res;
	}
	
	public ResultSet displayInfo(String account) throws SQLException {
		if (conn == null) {
			getConnection();
		}
		
		Statement state = conn.createStatement();
		ResultSet res = state.executeQuery("SELECT username, url, password FROM data WHERE account = '" + account + "'");
		
		return res;
	}
	
	public int displayAmount() throws SQLException {
		if (conn == null) {
			getConnection();
		}
		int count = 0;
		//PreparedStatement state = conn.prepareStatement("SELECT count(*) FROM data;");
		PreparedStatement state = conn.prepareStatement("SELECT * FROM data");
		ResultSet res = state.executeQuery();
		while (res.next()) {
			count++;
		}
		return count;
	}
	
	public void addUser(String account, String username,String url, String password) {
		if (conn == null) {
			getConnection();
		}
		try
		{
			PreparedStatement prep = conn.prepareStatement("INSERT INTO data values(?,?,?,?);");
			prep.setString(1, account);
			prep.setString(2, username);
			prep.setString(3, url);
			prep.setString(4, password);
			prep.execute();	
		}
		catch (SQLException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	public void changeUser(String account, String username) {
		if (conn == null) {
			getConnection();
		}
		try
		{
			PreparedStatement prep = conn.prepareStatement("UPDATE data SET username = '" + username + "' WHERE account = '" + account + "'");
			prep.execute();
		}
		catch (SQLException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public void changePass(String account, String password) {
		if (conn == null) {
			getConnection();
		}
		try
		{
			PreparedStatement prep = conn.prepareStatement("UPDATE data SET password = '" + password + "' WHERE account = '" + account + "'");
			prep.execute();
		}
		catch (SQLException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	public void changeSite(String account, String url) {
		if (conn == null) {
			getConnection();
		}
		try
		{
			PreparedStatement prep = conn.prepareStatement("UPDATE data SET url = '" + url + "' WHERE account = '" + account + "'");
			prep.execute();
		}
		catch (SQLException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}	
	}
	
	public void deleteUser(String account) {
		if (conn == null) {
			getConnection();
		}
		try
		{
			PreparedStatement prep = conn.prepareStatement("DELETE FROM data WHERE account = '" + account + "'");
			prep.execute();	
		}
		catch (SQLException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
	}
	
	
}
