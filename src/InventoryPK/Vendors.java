package InventoryPK;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class Vendors {
	
	static Connection conn;
	
	static JLabel lVendorName, lPhone, lAddress;
	static JTextField tfVendorName, tfPhone, tfAddress;
	static JButton btmAddVendor, btmRemoveVendor,btmRefresh;
	
	static Object[][] dataResult;
	static Object[] columns = {"", "Vendor Name","Phone",
            "Address", "Balance", "Debit", "Credit"};
	static DefaultTableModel dTableModel = new DefaultTableModel(dataResult, columns) {
		public Class getColumnClass(int column){
			Class returnValue;
			
			if((column >= 0) && (column < getColumnCount())) 
				returnValue = getValueAt(0, column).getClass();
			else 
				returnValue = Object.class;
			return returnValue;
		}
		public boolean isCellEditable(int row, int column) {

                    if(column == 2 || column == 3)
				return true;
			return false;
		}
	};
	
	public static JTable vendorTable;
	
	public static JScrollPane vendors() {
		
		vendorTable = new JTable(dTableModel);
		JScrollPane vendorTableSP = new JScrollPane(vendorTable);
		vendorTable.setAutoCreateRowSorter(true);
		vendorTable.setRowHeight(vendorTable.getRowHeight() + 16);
		loadData();
		
		TableColumn column = null;
		for(int i = 0; i < vendorTable.getColumnCount(); i++) {
			column = vendorTable.getColumnModel().getColumn(i);
			if(i == 0)
				column.setPreferredWidth(1);
			else
				column.setPreferredWidth(150);
		}
		
		lVendorName = new JLabel("Vendor name");		
		lPhone = new JLabel("Phone");
		lAddress = new JLabel("Address");
		
		tfVendorName = new JTextField(16);
		tfPhone = new JTextField(16);
		tfAddress = new JTextField(16);
		
		btmAddVendor= new JButton("Add");
		btmRemoveVendor = new JButton("Remove");
		btmRefresh = new JButton("Refresh");
                
                btmAddVendor.setToolTipText("Add a new vendor");
                btmRemoveVendor.setToolTipText("Remove selected vendor");
                btmRefresh.setToolTipText("Refresh vendors database");

                
		
		JPanel inputPanel1 = new JPanel();
		
		inputPanel1.add(lVendorName);
		inputPanel1.add(tfVendorName);
		inputPanel1.add(lPhone);
		inputPanel1.add(tfPhone);
		inputPanel1.add(lAddress);
		inputPanel1.add(tfAddress);
		
		
		JPanel btmPanel1 = new JPanel();
		btmPanel1.add(btmAddVendor);
		btmPanel1.add(btmRemoveVendor);
		btmPanel1.add(btmRefresh);
		
		JPanel btmPanel = new JPanel(new BorderLayout());
		btmPanel.add(btmPanel1, BorderLayout.EAST);
		
		JPanel inputPanel = new JPanel(new GridLayout(0,1,2,2));
		inputPanel.add(inputPanel1);
		inputPanel.add(btmPanel);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(inputPanel, BorderLayout.NORTH);
		mainPanel.add(vendorTableSP, BorderLayout.CENTER);
		JScrollPane mainPanelSP = new JScrollPane(mainPanel);
		
		
		btmAddVendor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				addAction();
			}
			
		});
		
		btmRemoveVendor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				removeAction();
			}
			
		});
		
		btmRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Main.infoPane.append(Main.tp, Color.BLACK, "Loading data...\n");
				double ts1 = System.currentTimeMillis();
				
				dTableModel.setRowCount(1);
				dTableModel.removeRow(0);
				loadData();
				
				double ts2 = System.currentTimeMillis();
				double T = Double.parseDouble(new DecimalFormat(".###").format((ts2 - ts1) / 1000));
				Main.infoPane.append(Main.tp, new Color(10, 100, 10), "Data has been successfully loaded (" + T + " sec)\n");
				
			}
			
		});
		
		vendorTable.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				switch(e.getKeyCode()) {
				case 10 :
					String vendorName = (String) vendorTable.getValueAt(vendorTable.getSelectedRow(), 1);
					String phone = (String) vendorTable.getValueAt(vendorTable.getSelectedRow(), 2);
					String address = (String) vendorTable.getValueAt(vendorTable.getSelectedRow(), 3);
					
					try {
						
						Class.forName("com.mysql.jdbc.Driver");
						String username = LogIn.username;
						String password = LogIn.password;
						String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
						conn = DriverManager.getConnection(url, username, password);
						Statement stmt = conn.createStatement();
						
						//update vendor in DB
						String updateCusotmer = "UPDATE vendors SET phone = '" + phone + "', address = '" + address + "' WHERE vendorName = '" + vendorName + "'; ";
						stmt.executeUpdate(updateCusotmer);
						Main.infoPane.append(Main.tp, new Color(10, 100, 10), "new " + vendorName + "'s data has been updated\n");
						//update vendor in gui
						vendorTable.setValueAt(phone, vendorTable.getSelectedRow(), 2);
						vendorTable.setValueAt(address, vendorTable.getSelectedRow(), 3);
					}catch(SQLException ex) {
						ex.printStackTrace();				
					} catch(ClassNotFoundException ex) {
						ex.printStackTrace();
					}
					break;
				case 127 :
					removeAction();
					break;
				}
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		return mainPanelSP;		
	}
	
	public static void loadData() {
		Statement stmt;
		try {
			
			
			Class.forName("com.mysql.jdbc.Driver");
			String username = LogIn.username;
			String password = LogIn.password;
			String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
			conn = DriverManager.getConnection(url, username, password);
			stmt = conn.createStatement();
			String selectStuff = "SELECT * FROM vendors;";
			ResultSet rset = stmt.executeQuery(selectStuff);

			Object[] tempRow;
			int count = 0;
			while(rset.next()) {
				
				String vendorName = rset.getString(1);
				String phone = rset.getString(2);
				String address = rset.getString(3);
				double balance = rset.getDouble(4);
                                double debit = rset.getDouble(5);
                                double credit = rset.getDouble(6);
                                
                                balance = Double.parseDouble
                                        (new DecimalFormat(".##").
                                        format(balance));
				
				tempRow = new Object[]{ ++count,vendorName,
                                    phone, address, balance, debit, credit};
				dTableModel.addRow(tempRow);
			}
			rset.close();
			conn.close();
			
			vendorTable.getColumnModel().getColumn(0).setPreferredWidth(1);
			
			//Center the data in the table
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment( JLabel.CENTER );
			for(int i = 0; i < vendorTable.getColumnCount(); i++)
				vendorTable.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
			

		

		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(NullPointerException e2){
			
		}
	}
	
	private static void addAction() {
		
		if(tfVendorName.getText().equals("")) {
			Main.infoPane.append(Main.tp, Color.RED, "Enter vendor Name\n");
		}
			//JOptionPane.showMessageDialog(null, "Enter vendor Name", "Vendors", JOptionPane.INFORMATION_MESSAGE);
		else {
			String vendorName = tfVendorName.getText();
			String phone = tfPhone.getText();
			String address = tfAddress.getText();		
			
			try {
				
				Class.forName("com.mysql.jdbc.Driver");
				String username = LogIn.username;
				String password = LogIn.password;
				String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
				conn = DriverManager.getConnection(url, username, password);
				Statement stmt = conn.createStatement();
				
				//add new vendor to DB
				String addCusotmer = "INSERT INTO vendors VALUES('" + vendorName + "', '" + phone + "', '" + address + "', 0, 0, 0);";
				stmt.executeUpdate(addCusotmer);
				Main.infoPane.append(Main.tp, new Color(10,100,10), "new vendor added to the Database (" + vendorName + ")\n");
				//add new vendor to vendorTable
				int num = 0;
				try {
					num = (int) vendorTable.getValueAt(vendorTable.getRowCount()-1, 0)+1;
				} catch(Exception ex) {
					num = 1;
				}
				
				Object[] tempRow = new Object[]{num, vendorName, phone, address, 0, 0, 0};
				dTableModel.addRow(tempRow);		
				if(Cash.rbCredit.isSelected())
                                    Cash.loadVendorsData();
				PInvoice_NewInvoice.loadVendorData();
			}catch(SQLException ex) {
				ex.printStackTrace();
				Main.infoPane.append(Main.tp, Color.RED, "Multiple vendor defined\n");
				//JOptionPane.showMessageDialog(null, "Multiple vendor defined", "Error", JOptionPane.ERROR_MESSAGE);
		
			} catch(ClassNotFoundException ex) {
				ex.printStackTrace();
			} 
		
		}
	}
	
	private static void removeAction() {
		try {
			String vendorName = (String) vendorTable.getValueAt(vendorTable.getSelectedRow(), 1);
			int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to permanently delete this item?", "Delete Item",JOptionPane.YES_OPTION);
			switch(result) {
			case JOptionPane.YES_OPTION :
				try {
					Statement stmt;
					Class.forName("com.mysql.jdbc.Driver");
					String username = LogIn.username;
					String password = LogIn.password;
					String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
					conn = DriverManager.getConnection(url, username, password);
					stmt = conn.createStatement();
					String deleteVendor = "DELETE FROM vendors WHERE vendorName = '" + vendorName + "';";
					stmt.executeUpdate(deleteVendor);
					Main.infoPane.append(Main.tp, new Color(10,100,10), "vendor \"" + vendorName + "\" removed\n");
					dTableModel.removeRow(vendorTable.getSelectedRow());
					
					int count = 0;
					for(int i = 0; i < dTableModel.getRowCount(); i++)
						vendorTable.setValueAt(++count, i, 0);
					
					PInvoice_NewInvoice.loadVendorData();
				} catch (SQLException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case JOptionPane.NO_OPTION : 
				break;	
			} 
		}catch(IndexOutOfBoundsException ee) {
			Main.infoPane.append(Main.tp, Color.RED, "No vendor selected\n");
			//JOptionPane.showMessageDialog(null, "No vendor selected", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		
	}
}
