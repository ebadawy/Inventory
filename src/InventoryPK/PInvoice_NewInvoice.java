package InventoryPK;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class PInvoice_NewInvoice {

	static Connection conn;
	static boolean itemAdded;
	static JLabel  lItem, lQuantity, lVendorName;
	static JTextField  tfQuantity, total1, total2;
	static JButton addItembtm, removeItembtm, removeallbtm, finishbtm;
	static JComboBox cbItem, cbVendor;
	static String strItem = "";
	static double netTotalPrice;
	static JRadioButton payLater,cash;
	static ButtonGroup invoiceType;

	static Object[][] dataResult;
	static Object[] columns = {"","Item", "Quantity", "price/Item", "Total Price"};
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
			
			return false;
		}
	};
	
	
	public static JTable nPInvoiceTable;
	public static JScrollPane NewInvoice(JFrame frame) {
	
		lItem = new JLabel("Item");
		lQuantity = new JLabel("Quantity");
                
		addItembtm = new JButton("Add");
		removeItembtm = new JButton("Remove");
		removeallbtm = new JButton("Remove all");
		finishbtm = new JButton("Finish");
                
                addItembtm.setToolTipText("Add item");
                removeItembtm.setToolTipText("Remove selected item");
                removeallbtm.setToolTipText("Remove all items");
                finishbtm.setToolTipText("Finish the invoice");
                
		lVendorName = new JLabel("Vendor name");
		
		payLater = new JRadioButton("Pay Later");
		cash = new JRadioButton("Cash");
		invoiceType = new ButtonGroup();
		invoiceType.add(payLater);
		invoiceType.add(cash);
                
		Box box = Box.createHorizontalBox();
		box.add(cash);
		box.add(payLater);
		cash.setSelected(true);
		JPanel radiobtmPanel = new JPanel(new BorderLayout());
		radiobtmPanel.add(box, BorderLayout.WEST);
		
		cbItem = new JComboBox();
		cbItem.setEditable(true);
		new ComboBoxOption_PInvoice(cbItem);
		loadItemData();
		
		cbVendor = new JComboBox();
		cbVendor.setEditable(true);
		new ComboBoxOption_Vendors(cbVendor); 
		loadVendorData();
		
		tfQuantity = new JTextField(10);
		
		nPInvoiceTable = new JTable(dTableModel);
		nPInvoiceTable.setRowHeight(nPInvoiceTable.getRowHeight() + 16);
		JScrollPane nPInvoiceTableSP = new JScrollPane(nPInvoiceTable);
		
		TableColumn column = null;
		for(int i = 0; i < nPInvoiceTable.getColumnCount(); i++) {
			column = nPInvoiceTable.getColumnModel().getColumn(i);
			if(i == 0)
				column.setPreferredWidth(1);
			else
				column.setPreferredWidth(150);
		}
		
		
		JPanel inputPanel1 = new JPanel();
		
		inputPanel1.add(lItem);
		inputPanel1.add(cbItem);
		inputPanel1.add(lQuantity);
		inputPanel1.add(tfQuantity);
		
		JPanel btmPanel = new JPanel();
		btmPanel.add(addItembtm);
		btmPanel.add(removeItembtm);
		btmPanel.add(removeallbtm);
		
		JPanel vendorPanel = new JPanel();
		vendorPanel.add(lVendorName);
		vendorPanel.add(cbVendor);
		
		JPanel inputPanel2 = new JPanel(new BorderLayout());
		inputPanel2.add(btmPanel, BorderLayout.EAST);
		inputPanel2.add(vendorPanel, BorderLayout.WEST);
		
		JPanel inputPanel = new JPanel(new GridLayout(0,1,2,2));
		inputPanel.add(inputPanel1);
		inputPanel.add(inputPanel2);
		inputPanel.add(box);
		inputPanel.setBorder(BorderFactory.createEtchedBorder());
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(inputPanel, BorderLayout.NORTH);
		mainPanel.add(nPInvoiceTableSP, BorderLayout.CENTER);
		
		JPanel finishPanel = new JPanel(new BorderLayout());
		finishPanel.add(finishbtm, BorderLayout.EAST);
		
		mainPanel.add(finishPanel, BorderLayout.SOUTH);
		
		addItembtm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!cbItem.getSelectedItem().toString().isEmpty()) 
					addItem();
				else
					Main.infoPane.append(Main.tp, Color.RED, "Chose an Item\n");
					//JOptionPane.showMessageDialog(null, "Chose an Item", "Error", JOptionPane.ERROR_MESSAGE);
				
			}
			
		});
		
		removeItembtm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(nPInvoiceTable.getRowCount() == 0)
					Main.infoPane.append(Main.tp, Color.RED, "There is no item to delete!\n");
					//JOptionPane.showMessageDialog(null, "There is no item to delete!", "Empty invoice", JOptionPane.INFORMATION_MESSAGE);
				else
					removeItem();
			}
			
		});
		removeallbtm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(nPInvoiceTable.getRowCount() == 0)
					Main.infoPane.append(Main.tp, Color.RED, "There is no item to delete!\n");
					//JOptionPane.showMessageDialog(null, "There is no items to delete!", "Empty invoice", JOptionPane.INFORMATION_MESSAGE);
				else {
					int rest = JOptionPane.showConfirmDialog(null, "Are you sure, you want to remove all items?", "New Invoice", JOptionPane.YES_NO_OPTION);
					
					switch(rest) {
					
					case JOptionPane.YES_OPTION :
						dTableModel.setRowCount(1);
						dTableModel.removeRow(0);
						break;
					case JOptionPane.NO_OPTION:
						break;
					}
				}
			}
			
		});
		
		finishbtm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(nPInvoiceTable.getRowCount() == 0)
					Main.infoPane.append(Main.tp, Color.RED, "The invoice is empty!\n");
					//JOptionPane.showMessageDialog(null, "!", "Empty invoice", JOptionPane.INFORMATION_MESSAGE);
				else if(cbVendor.getSelectedItem().toString().equals(""))
					Main.infoPane.append(Main.tp, Color.RED, "Chose the Vendor\n");
					//JOptionPane.showMessageDialog(null, "Please chose the Vendor", "new invoice", JOptionPane.INFORMATION_MESSAGE);
				else {
					int reslt = JOptionPane.showConfirmDialog(null, "After this step the invoice will be unchangeable, do you want to continue?", "Finish", JOptionPane.YES_NO_OPTION);
					switch(reslt) {
					case JOptionPane.YES_OPTION :
						finishAction();
						break;
					case JOptionPane.NO_OPTION :
						break;
					}
				}
					
			}
			
		});
		JScrollPane mainPanelSP = new JScrollPane(mainPanel);
		return mainPanelSP;
	}
	
	
	private static void addItem(){
		
			if(nPInvoiceTable.getRowCount() != 0 && itemAdded)
				dTableModel.removeRow(nPInvoiceTable.getRowCount()-1);
			boolean validQuantityValue = true;
			int sQuantity = 0;
			try { 
				sQuantity = Integer.parseInt(tfQuantity.getText());
			} catch (NumberFormatException ne) {
				validQuantityValue = false;
				itemAdded = false;
				if(!tfQuantity.getText().isEmpty())
					Main.infoPane.append(Main.tp, Color.RED, "Bad formation for the Quantity value\n");
					//JOptionPane.showMessageDialog(null, "Bad formation for the Quantity value", "Error", JOptionPane.ERROR_MESSAGE);
				else 
					Main.infoPane.append(Main.tp, Color.RED, "Enter the Quantity value\n");
					//JOptionPane.showMessageDialog(null, "Enter the Quantity value", "Error", JOptionPane.ERROR_MESSAGE);
			}
			String sItem;
			double sDisPrice = 0, sTotalPrice;
			sItem = (String) cbItem.getSelectedItem();
			
			boolean itemExists = false;
			int itemExistsAt = 0;
			
			for(int i = 0; i < nPInvoiceTable.getRowCount(); i++) 
				if(nPInvoiceTable.getValueAt(i, 1).toString() == sItem) {
					itemExists = true;
					itemExistsAt = i;
					break;
				}
			
			if(!itemExists && sQuantity <= 0 && validQuantityValue) {
				Main.infoPane.append(Main.tp, Color.RED, "Quantity value must be 1 or more\n");
				//JOptionPane.showMessageDialog(null, "Quantity value must be 1 or more", "Error", JOptionPane.ERROR_MESSAGE);
				itemAdded = false;
			}
			else if(itemExists && sQuantity == 0 && validQuantityValue) {
				Main.infoPane.append(Main.tp, Color.RED, "Quantity value must be 1 or more\n");
				//JOptionPane.showMessageDialog(null, "Quantity value must be 1 or more", "Error", JOptionPane.ERROR_MESSAGE);
				itemAdded = false;
			}
			else if(validQuantityValue){
				try{
					
					Class.forName("com.mysql.jdbc.Driver");
					String username = LogIn.username;
					String password = LogIn.password;
					String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
					conn = DriverManager.getConnection(url, username, password);
					Statement stmt = conn.createStatement();
					
					String sql = "SELECT DisPrice FROM items WHERE itemName = '" + sItem + "';";
					stmt = conn.createStatement();
					ResultSet rset = stmt.executeQuery(sql);
					while(rset.next())
						sDisPrice = Double.parseDouble(new DecimalFormat(".##").format(rset.getDouble(1)));
					
					sTotalPrice = Double.parseDouble(new DecimalFormat(".##").format(sDisPrice * sQuantity));
					
					rset.close();
					conn.close();
					stmt.close();
					
					int num = 0;
					try {
						num = (int) nPInvoiceTable.getValueAt(nPInvoiceTable.getRowCount()-1, 0)+1;
					} catch(Exception ex) {
						num = 1;
					}
					
					if(!itemExists) {
						Object[] tempRow = new Object[]{num, sItem, sQuantity, sDisPrice, sTotalPrice};
						dTableModel.addRow(tempRow);
					} else {
						int oldQuantity;
						double  oldTotalPrice;
						oldQuantity = (int) nPInvoiceTable.getValueAt(itemExistsAt, 2);
						oldTotalPrice = (double) nPInvoiceTable.getValueAt(itemExistsAt, 4);
						
						if(oldQuantity+sQuantity > 0) {
							nPInvoiceTable.setValueAt(oldQuantity+sQuantity, itemExistsAt, 2);
							nPInvoiceTable.setValueAt((oldQuantity+sQuantity) * sDisPrice, itemExistsAt, 4);
						} else 
							Main.infoPane.append(Main.tp, Color.RED, "Quantity value must be 1 or more\n");
							//JOptionPane.showMessageDialog(null, "Quantity value must be 1 or more", "Error", JOptionPane.ERROR_MESSAGE);
					}
					netTotalPrice = 0;
					for(int i = 0; i <  nPInvoiceTable.getRowCount(); i++) 
						netTotalPrice += (double) nPInvoiceTable.getValueAt(i, 4);
					
					netTotalPrice = Double.parseDouble(new DecimalFormat(".##").format(netTotalPrice));
					
					Object[] FinalRow = new Object[]{"","","","",netTotalPrice};
					dTableModel.addRow(FinalRow);
					
					DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
					centerRenderer.setHorizontalAlignment( JLabel.CENTER );
					for(int i = 0; i < nPInvoiceTable.getColumnCount(); i++)
						nPInvoiceTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
					
					itemAdded = true;
					
				} catch(SQLException ex) {
					ex.printStackTrace();
					
				} catch(ClassNotFoundException ex) {
					ex.printStackTrace();
				}
				
			}
	}
	
	private static void removeItem(){
		try {
			boolean isEmbtyCell = nPInvoiceTable.getValueAt(nPInvoiceTable.getSelectedRow(), 1).toString().equals("");
			if(isEmbtyCell) {	
			System.out.println("can't remove it!");
			} else {
				if(nPInvoiceTable.getRowCount() == 2){
					try {
						dTableModel.removeRow(nPInvoiceTable.getSelectedRow());
						dTableModel.removeRow(0);
					} catch(IndexOutOfBoundsException ee) {
						Main.infoPane.append(Main.tp, Color.RED, "No item selected\n");
						//JOptionPane.showMessageDialog(null, "No item selected", "Error", JOptionPane.ERROR_MESSAGE);
					}
				
				}
				else {
					try {
						dTableModel.removeRow(nPInvoiceTable.getSelectedRow());
						dTableModel.removeRow(nPInvoiceTable.getRowCount()-1);
						System.out.println(nPInvoiceTable.getRowCount()-1);
						
						int num = 0;
						for(int i = 0; i <= nPInvoiceTable.getRowCount()-1; i++) 
							nPInvoiceTable.setValueAt(++num, i, 0);
						
						netTotalPrice = 0;
						for(int i = 0; i <  nPInvoiceTable.getRowCount(); i++) 
							netTotalPrice += (double) nPInvoiceTable.getValueAt(i, 4);
						
						netTotalPrice = Double.parseDouble(new DecimalFormat(".##").format(netTotalPrice));
						
						Object[] FinalRow = new Object[]{"","","","",netTotalPrice};
						dTableModel.addRow(FinalRow);
					
						
					} catch(IndexOutOfBoundsException ee) {
						Main.infoPane.append(Main.tp, Color.RED, "No item selected\n");
						//JOptionPane.showMessageDialog(null, "No item selected", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}catch(ArrayIndexOutOfBoundsException e) {
			Main.infoPane.append(Main.tp, Color.RED, "No item selected\n");
		}
		
	}
	
	
	public static void finishAction() {
		
		
		double invoiceValue =
                        (double) nPInvoiceTable.getValueAt
                        (nPInvoiceTable.getRowCount()-1,4);
                double balance = Cash.getBalance();
                if(invoiceValue > balance && cash.isSelected())
                    Main.infoPane.append(Main.tp, Color.RED,
                            "No enough cash balance for this invoice\n");
                else {
		Date date = new Date();
		SimpleDateFormat fotmatDate = new SimpleDateFormat("E yy.MM.dd 'at' hh:mm:ssa");
		String tempDate = fotmatDate.format(date);
		String invoiceDate = tempDate.replace("AM", "am").replace("PM","pm");
		
		String invoiceType = "";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String username = LogIn.username;
			String password = LogIn.password;
			String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
			conn = DriverManager.getConnection(url, username, password);
			Statement stmt = conn.createStatement();
			ResultSet rset = null;
			
			String addItem, updateItem;
			String vendorName, itemName;
			int quantity; 
			int nQuantity = 0;
			double price = 0, totalPrice, disPrice = 0;
			double nTotalPrice, nDisPrice, disPercentage = 0, nTotalDisPrice ;
			vendorName = (String) cbVendor.getSelectedItem();
			
			for(int i = 0; i < nPInvoiceTable.getRowCount(); i++) {
				if(nPInvoiceTable.getValueAt(i, 1).toString().length() == 0)
					continue;
				itemName = (String) nPInvoiceTable.getValueAt(i,1);
				quantity = (int) nPInvoiceTable.getValueAt(i,2);
				totalPrice = (double) nPInvoiceTable.getValueAt(i,4);
                                
                                for(int k = 0; k < Items.itemTable.getRowCount(); k++)
                                    if(Items.itemTable.getValueAt(k, 1).equals(itemName)) {
                                            price = (double) Items.itemTable.getValueAt(k,3);
                                         disPrice = (double) Items.itemTable.getValueAt(k,6);
                                         break;
                                    }
                        
                        
				//adding item to the invoice
				addItem = "INSERT INTO pInvoice VALUES ('" + vendorName + "', '" + itemName + "', " 
						+ quantity + ", " + disPrice + ", " + totalPrice + ", '" + invoiceDate + "');";
				System.out.println(addItem);
				stmt.executeUpdate(addItem);
				//updating the item in the database
				rset = stmt.executeQuery("SELECT quantity, disPercentage  FROM items WHERE itemName = '" + itemName + "';");
				while(rset.next()) {
					nQuantity = rset.getInt(1) + quantity;
					disPercentage = rset.getDouble(2);
				}
				
				nTotalPrice = Double.parseDouble(new DecimalFormat(".##").format(nQuantity * price));
				//nDisPrice  = Double.parseDouble(new DecimalFormat(".##").format(price * (1 - disPercentage)));
                                nTotalDisPrice = Double.parseDouble(new DecimalFormat(".##").format(nQuantity * disPrice));

				updateItem = "UPdATE items SET quantity = " + nQuantity + ", totalPrice = " + nTotalPrice 
						+ ", totalDisPrice = " + nTotalDisPrice + " WHERE itemName = '" + itemName + "';";
				stmt.executeUpdate(updateItem);
			}
                        
                        if(cash.isSelected()) {
                           String editCashBalance = 
                                   "INSERT INTO cash VALUES (0, " 
                                   + invoiceValue + ", 'cr', '" 
                                   + vendorName + "', '" + invoiceDate 
                                   + "', 'purchasing invoice');";
                           System.out.println(editCashBalance);
                           stmt.executeUpdate(editCashBalance);
                           
                           Object[] tempRow = new Object[]{invoiceDate, 0, invoiceValue};
                           Cash.dTableModel.addRow(tempRow);
                           invoiceType = "Cash";
                        } else {
                            updateVendorBalance();
                            invoiceType = "Pay Later";
                        }
	
			String sql = "INSERT INTO registered_pInvoice VALUES (NULL, '" + vendorName + "', '" + invoiceType + "', '" + invoiceDate + "');";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			
				
			int finalRowCount = PInvoice_RegisteredInvoices.rPInvoiceTable.getRowCount()+1;
			Object[] tempRow = new Object[]{finalRowCount, vendorName , invoiceType, invoiceDate};
			PInvoice_RegisteredInvoices.dTableModel.addRow(tempRow);
			Main.infoPane.append(Main.tp, new Color(10,100,10), "The invoice is successfully created, ckeck it in the registered invoices list.\n");
			rset.close();
			stmt.close();
			conn.close();
			
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			Main.infoPane.append(Main.tp, Color.RED, "Can't connect to the database\n");
			//JOptionPane.showMessageDialog(null, "Can't connect to the database", "Error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		dTableModel.setRowCount(0);
                }
				
	}
	
	public static void loadItemData() {
		cbItem.removeAllItems();
		cbItem.addItem("");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String username = LogIn.username;
			String password = LogIn.password;
			String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
			conn = DriverManager.getConnection(url, username, password);
			Statement stmt = conn.createStatement();
			String sql = "SELECT itemName FROM items;";
			ResultSet rset = stmt.executeQuery(sql);
			while(rset.next()) {
				cbItem.addItem(rset.getString(1));
			}
			rset.close();		
			conn.close();
			stmt.close();
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			Main.infoPane.append(Main.tp, Color.RED, "Can't connect to the database\n");
			//JOptionPane.showMessageDialog(null, "Can't connect to the database", "Error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void loadVendorData() {
		
		cbVendor.removeAllItems();
		cbVendor.addItem("");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String username = LogIn.username;
			String password = LogIn.password;
			String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
			conn = DriverManager.getConnection(url, username, password);
			Statement stmt = conn.createStatement();
			String sql = "SELECT vendorName FROM vendors;";
			ResultSet rset = stmt.executeQuery(sql);
			while(rset.next()) {
				cbVendor.addItem(rset.getString(1));
			}
			rset.close();		
			conn.close();
			stmt.close();
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			Main.infoPane.append(Main.tp, Color.RED, "Can't connect to the database\n");
			//JOptionPane.showMessageDialog(null, "Can't connect to the database", "Error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private static void updateVendorBalance() {
		
		String vendorName = cbVendor.getSelectedItem().toString();
		double oldBalance = 0, newBalance, olddr = 0, newdr = 0, oldcr = 0, newcr = 0;
		double tempdr , tempcr;
			
		
			try {
				
				tempcr = 0;
				tempdr = 0;
				
				for(int i = 0; i <  nPInvoiceTable.getRowCount(); i++) {
					if(nPInvoiceTable.getValueAt(i, 1).toString().equals(""))
						continue;
					tempdr += (double) nPInvoiceTable.getValueAt(i, 4);
				}
				tempdr = Double.parseDouble(new DecimalFormat(".##").format(tempdr));
				
				Class.forName("com.mysql.jdbc.Driver");
				String username = LogIn.username;
				String password = LogIn.password;
				String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
				conn = DriverManager.getConnection(url, username, password);
				Statement stmt = conn.createStatement();
				String sql = "SELECT balance, dr, cr  FROM vendors WHERE vendorName = '" + vendorName + "';";
                                System.out.println(sql);
				ResultSet rset = stmt.executeQuery(sql);
				while(rset.next()) {
					oldBalance = rset.getDouble(1);
					olddr = rset.getDouble(2);
					oldcr = rset.getDouble(3);
				}
				
				newdr = olddr + tempdr;
				newcr = oldcr + tempcr;
				
				newBalance = newdr - newcr;
				String updateBalanne = "UPDATE vendors SET balance = " + newBalance + ", dr = " + newdr + ", cr = " + newcr + " WHERE vendorName = '" + vendorName + "';";
				stmt.executeUpdate(updateBalanne);
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				Main.infoPane.append(Main.tp, Color.RED, "Can't connect to the database\n");
				//JOptionPane.showMessageDialog(null, "Can't connect to the database", "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		
		
	}
	
	public static double lastInvoicePrice() {
		
		String vendorName = (String) cbVendor.getSelectedItem();
		String invoiceDate = "";
		
		for(int i = PInvoice_RegisteredInvoices.rPInvoiceTable.getRowCount()-1; i >= 0; i--) {
			if(PInvoice_RegisteredInvoices.rPInvoiceTable.getValueAt(i, 1).toString().equals(vendorName)) {
				invoiceDate = (String) PInvoice_RegisteredInvoices.rPInvoiceTable.getValueAt(i, 3);
				break;
			}
		}
		
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			String username = LogIn.username;
			String password = LogIn.password;
			String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
			conn = DriverManager.getConnection(url, username, password);
			Statement stmt = conn.createStatement();
			
			double lastInvoicePrice = 0;
			
			String newTotalPrice = "SELECT SUM(totalPrice) AS newTotalPrice FROM pInvoice WHERE vendorName = '" + vendorName + "' AND invoiceDate = '" + invoiceDate + "';";
			ResultSet rset = stmt.executeQuery(newTotalPrice);
			while(rset.next()) 
				lastInvoicePrice = rset.getDouble(1);
			rset.close();
			stmt.close();
			conn.close();
			lastInvoicePrice = Double.parseDouble(new DecimalFormat(".##").format(lastInvoicePrice));
			return lastInvoicePrice;				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		return 0;
	}
}
