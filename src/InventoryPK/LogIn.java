package InventoryPK;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


public class LogIn extends JFrame {
	
	static Connection conn;
	static String username = "root", password = "123456";
	static String ip = "";		
	static JLabel lUserName, lPassword, lIP;
	static JTextField tfUserName, tfIP;
	static JPasswordField pfPassword;
	static JButton bEnter;
	static JRadioButton rbLocalHost, rbEnterIP; 
	static ButtonGroup bgIP;
	
		
	public LogIn() {
		this.setTitle("Log in");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		int x = 250;
		int y = 240;
		this.setSize(x, y);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xPox = (dim.width/2) - x/2;
		int yPox = (dim.height/2) - y/2;
		this.setLocation(xPox, yPox);
		
		JPanel mainPanel = new JPanel();
		
		lUserName = new JLabel("User Name");
		tfUserName = new JTextField(16);
		actionListener lForTextField = new actionListener();
		tfUserName.addActionListener(lForTextField);
		
		lPassword = new JLabel("Password");
		actionListener lForPasswordField = new actionListener();
		pfPassword = new JPasswordField(16);
		pfPassword.addActionListener(lForPasswordField);
		
		lIP = new JLabel("IP Address");
		tfIP = new JTextField(16);
		tfIP.setEditable(false);
		tfIP.addActionListener(lForTextField);
		
		
		Box box = Box.createHorizontalBox();
		rbLocalHost = new JRadioButton("Local Host");
		rbEnterIP = new JRadioButton("Remote Host");
                
                
		box.add(rbLocalHost);
		box.add(rbEnterIP);
		box.setBorder(BorderFactory.createTitledBorder(""));
                
		bgIP = new ButtonGroup();
		bgIP.add(rbLocalHost);
		bgIP.add(rbEnterIP);
		rbLocalHost.setSelected(true);
		radioButtonListener ilforRadioButton = new radioButtonListener();
		rbLocalHost.addItemListener(ilforRadioButton);
		rbEnterIP.addItemListener(ilforRadioButton);
		actionListener alforRadioButton = new actionListener();
		rbLocalHost.addActionListener(alforRadioButton);
		rbEnterIP.addActionListener(alforRadioButton);
		keyListener klForrbLocalHost = new keyListener();
		keyListener klForrbrbEnterIP = new keyListener();
		rbLocalHost.addKeyListener(klForrbLocalHost);
		rbEnterIP.addKeyListener(klForrbrbEnterIP);
		
		bEnter = new JButton("Enter");
		keyListener klForButton = new keyListener();
		bEnter.addKeyListener(klForButton);
				
		mainPanel.add(lUserName);
		mainPanel.add(tfUserName);
		
		mainPanel.add(lPassword);
		mainPanel.add(pfPassword);
		
		mainPanel.add(lIP);
		mainPanel.add(box);
		mainPanel.add(tfIP);
		
		
		mainPanel.add(bEnter);
                
                
		
		actionListener lForButton = new actionListener();
		bEnter.addActionListener(lForButton);

		this.add(mainPanel);
		this.setVisible(true);
	}
	private class keyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if(e.getKeyCode() == 10)
				checkConeection();
			}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
		}
	}
	
	private class radioButtonListener implements ItemListener  {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource() == rbEnterIP) {
				tfIP.setEditable(true);
				}
			if(e.getSource() == rbLocalHost) {
				tfIP.setEditable(false);	
			}
			
		}
	}
		
	private class actionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource() == bEnter) {
				checkConeection();
			}
			if(e.getSource() == tfIP) {
				checkConeection();
			}
			if(e.getSource() == pfPassword) {
				checkConeection();
			}
			if(e.getSource() == tfUserName) {
				checkConeection();

			}
		}
		
	}

	private void checkConeection(){
		/*ip = "localhost";
		password = "123456";
		username = "root";
		
		this.dispose();
              
		createDB();
		new Main();
		
		*/
		if(rbEnterIP.isSelected()) {
			ip = "";
			ip = tfIP.getText(); 
		}
		if(rbLocalHost.isSelected()) {
			ip = "";
			ip ="localhost";	
		}
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			username = tfUserName.getText();
			password = "";
			if(rbEnterIP.isSelected() && tfIP.getText().length() == 0) 
				JOptionPane.showMessageDialog(LogIn.this, "Enter"
                                        + " target IP", "Error",
                                        JOptionPane.ERROR_MESSAGE);
			else {
				for(char c : pfPassword.getPassword()) 
					password += c ;
				String url = "jdbc:mysql://" + ip;
				Connection conn = 
                                        DriverManager.getConnection(url,
                                        username, password);
                                Statement stmt = conn.createStatement();
				String inventoryDB = "CREATE DATABASE IF NOT"
                                        + " EXISTS inventory;";
				stmt.executeUpdate(inventoryDB);
                                stmt.close();
                                this.dispose();
				createDB();
				new Main();
				conn.close();
			}
		} catch(SQLException sqlex) {
			if(tfUserName.getText().length() == 0) 
				JOptionPane.showMessageDialog(LogIn.this,
                                        "Enter user name", "Error",
                                        JOptionPane.ERROR_MESSAGE);
			else 
				JOptionPane.showMessageDialog(LogIn.this, 
                                        "Access denied for user '"
                                        + tfUserName.getText() +"' @ '"+ ip +"'"
                                        , "Error", JOptionPane.ERROR_MESSAGE);
			
			sqlex.printStackTrace();
		} catch (ClassNotFoundException cex) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(LogIn.this, "Can't access"
                                + " to the database!", "Error",
                                JOptionPane.ERROR_MESSAGE);
			cex.printStackTrace();
		}
		
	}
	
	 private static void createDB() {
		 try {
			 
				Class.forName("com.mysql.jdbc.Driver");
				String username = LogIn.username;
				String password = LogIn.password;
				String url = "jdbc:mysql://" + LogIn.ip;
				conn = DriverManager.getConnection(url, username
                                        , password);
				Statement stmt = conn.createStatement();
				String inventoryDB = "CREATE DATABASE IF NOT "
                                        + "EXISTS inventory;";
				stmt.executeUpdate(inventoryDB);
				stmt.close();
				conn.close();
				
				
				url = "jdbc:mysql://" + LogIn.ip + "/inventory";
				conn = DriverManager.getConnection(url, username
                                        , password);
				stmt = conn.createStatement();
				String items = 
						"CREATE TABLE IF NOT EXISTS items ("+
						"itemName VARCHAR(64) NOT NULL,"+
						"quantity INT NOT NULL,"+
						"price DOUBLE NOT NULL,"+
						"totalPrice DOUBLE NOT NULL,"+
						"disPercentage DOUBLE NOT NULL,"+
						"disPrice DOUBLE NOT NULL,"+
                                                "totalDisPrice DOUBLE NOT NULL,"+
						"PRIMARY KEY (itemName) "+
						");";
				stmt.executeUpdate(items);
				String pInvoiceTable = 
						"CREATE TABLE IF NOT EXISTS pInvoice ( "+
						"vendorName VARCHAR(64) NOT NULL,"+
						"itemName VARCHAR(64) NOT NULL,"+
						"quantity INT NOT NULL,"+
						"price DOUBLE NOT NULL,"+
						"totalPrice DOUBLE NOT NULL,"+
						"invoiceDate VARCHAR(64) Not NULL"+
						");";
				stmt.executeUpdate(pInvoiceTable);
				String registered_pInvoice =
						"CREATE TABLE IF NOT EXISTS registered_pInvoice ("+
						"invoiceID INT Not Null AUTO_INCREMENT,"+
						"vendorName VARCHAR(64) NOT NULL,"+
						"invoiceType varchar(64) NOT NULL,"+
						"invoiceDate VARCHAR(64) Not NULL,"+
						"PRIMARY KEY(invoiceID, invoiceDate)"+
						");";
				stmt.executeUpdate(registered_pInvoice);
				String sInvoice =
						"CREATE TABLE IF NOT EXISTS sInvoice ( "+
						"customerName VARCHAR(64) NOT NULL,"+
						"itemName VARCHAR(64) NOT NULL,"+
						"quantity INT NOT NULL,"+
						"price DOUBLE NOT NULL,"+
						"totalPrice DOUBLE NOT NULL,"+
						"invoiceDate VARCHAR(64) Not NULL"+
						");";
				stmt.executeUpdate(sInvoice);
				String registered_sInvoice =
						"CREATE TABLE IF NOT EXISTS registered_sInvoice ("+
						"invoiceID INT Not Null AUTO_INCREMENT,"+
						"customerName VARCHAR(64) NOT NULL,"+
						"invoiceType varchar(64) NOT NULL,"+
						"invoiceDate VARCHAR(64) Not NULL,"+
						"PRIMARY KEY (invoiceID, invoiceDate)"+
						");";
				stmt.executeUpdate(registered_sInvoice);
				String customers =
						"CREATE TABLE IF NOT EXISTS customers("+
						"customerName VARCHAR(64) NOT NULL PRIMARY KEY,"+
						"phone VARCHAR(64) NOT NULL,"+
						"address VARCHAR(64) NOT NULL,"+
						"balance DOUBLE NOT NULL,"+
						"accountReceivable DOUBLE NOT NULL,"+
						"cashPayments DOUBLE NOT NULL"+
						");";
				stmt.executeUpdate(customers);
				String vendors = 
						"CREATE TABLE IF NOT EXISTS vendors("+
						"vendorName VARCHAR(64) NOT NULL PRIMARY KEY,"+
						"phone VARCHAR(64) NOT NULL,"+
						"address VARCHAR(64) NOT NULL,"+
						"balance DOUBLE NOT NULL,"+
						"dr DOUBLE NOT NULL,"+
						"cr DOUBLE NOT NULL"+
						");";
				stmt.executeUpdate(vendors);
                                String cash = "CREATE TABLE IF NOT EXISTS cash("+
                                        "dr DOUBLE NOT NULL,"+
                                        "cr DOUBLE NOT NULL,"+
                                        "statusType VARCHAR(64) NOT NULL,"+
                                        "name VARCHAR(64) NOT NULL,"+
                                        "date VARCHAR(64) NOT NULL,"+
                                        "details VARCHAR(64) NOT NULL);";
                                stmt.executeUpdate(cash);
				stmt.close();
				conn.close();
				
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Can't "
                                        + "connect to the database", "Error",
                                        JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	 }
}
