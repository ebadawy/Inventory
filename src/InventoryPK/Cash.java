
package InventoryPK;

import java.sql.Connection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Cash{
    
    static Connection conn;
    static JComboBox cbName;
    static JRadioButton rbDebit, rbCredit;
    static ButtonGroup rbGroup;
    static JLabel lName, lDate, lAmount,  lDetails;
    static JTextField tfDate, tfAmount,  tfDetails;
    static JButton btmSubmit, btmRefresh;
    
	static Object[][] dataResult;
	static Object[] columns = {"Date", "Debit", "Credit"};
	static DefaultTableModel dTableModel =
                new DefaultTableModel(dataResult, columns) {
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
        
        static JTable cashTable;
	
        public static JScrollPane cash() {
            
            rbDebit = new JRadioButton("Debit");
            rbCredit = new JRadioButton("Credit");
            rbGroup = new ButtonGroup();
            rbGroup.add(rbDebit);
            rbGroup.add(rbCredit);
            Box box = Box.createHorizontalBox();
            box.add(rbDebit);
            box.add(rbCredit);
            rbDebit.setSelected(true);
            
            JPanel rbPanel = new JPanel(new BorderLayout());
            rbPanel.add(box, BorderLayout.WEST);
            
            lName = new JLabel("Name");
            lDate = new JLabel("Date");
            lAmount = new JLabel("Amount");
            
            tfDate = new JTextField(16);
            tfAmount = new JTextField(6);
            
            cbName = new JComboBox();
            cbName.setEditable(true);
            new ComboBoxOption_Name(cbName);
            loadCustomersData();
                        
            JPanel inputPanel1  = new JPanel();
            inputPanel1.add(lName);
            inputPanel1.add(cbName);
            inputPanel1.add(lDate);
            inputPanel1.add(tfDate);
            inputPanel1.add(lAmount);
            inputPanel1.add(tfAmount);
            
            lDetails = new JLabel("Details");
            tfDetails = new JTextField(24);
                        
            JPanel detailsPanel = new JPanel();
            detailsPanel.add(lDetails);
            detailsPanel.add(tfDetails);
            
            btmSubmit = new JButton("Submit");
            btmRefresh = new JButton("Refresh");
 
            JPanel btmPanel1 = new JPanel();
            btmPanel1.add(btmSubmit);
            btmPanel1.add(btmRefresh);
            
            JPanel btmPanel = new JPanel(new BorderLayout());
            btmPanel.add(btmPanel1, BorderLayout.EAST);
            
            cashTable = new JTable(dTableModel);
            cashTable.setRowHeight(cashTable.getRowHeight() + 16);
            JScrollPane cashTableSP = new JScrollPane(cashTable);
            
            loadData();
            
            JPanel inputPanel = new JPanel(new GridLayout(0,1,2,2));
            inputPanel.add(rbPanel);
            inputPanel.add(inputPanel1);
            inputPanel.add(detailsPanel);
            inputPanel.add(btmPanel);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(inputPanel, BorderLayout.NORTH);
            mainPanel.add(cashTableSP, BorderLayout.CENTER);
            
            JScrollPane mainPanelSP = new JScrollPane(mainPanel);
            
            
            

            
            btmSubmit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                submitAction();
            }
                
            });
            
            btmRefresh.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Main.infoPane.append(Main.tp, Color.BLACK, "Loading data...\n");
                double ts1= System.currentTimeMillis();
                dTableModel.setRowCount(1);
                dTableModel.removeRow(0);
                loadData();
                double ts2 = System.currentTimeMillis();
                String tempTiem = 
                        new DecimalFormat(".###").format((ts2 - ts1) / 1000);
                double T = Double.parseDouble(tempTiem);
                Main.infoPane.append
                        (Main.tp, new Color(10, 100, 10),
                        "Data has been successfully loaded (" + T + " sec)\n");
						
            }
            
            });
            
            cashTable.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
               if(e.getClickCount() == 2)
                   moreInfoPanel();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
                
            });
            
            rbDebit.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                loadCustomersData();
            }

           
            });
            
            rbCredit.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                loadVendorsData();            
            }

            });
            

		
            return mainPanelSP;
            
        }
        
        
        private static void submitAction() {
            try {
            String name, date, details;
            double amount;
            
            name = cbName.getSelectedItem().toString();
            date = tfDate.getText();
            int count = 1;
              for(int i = 0; i < cashTable.getRowCount(); i++) {
                  if(count == 1){
                      if(cashTable.getValueAt(i, 0).
                              equals(tfDate.getText())) 
                          count++;
                  } else
                      if(cashTable.getValueAt(i, 0).
                              equals(tfDate.getText() + "(" + count +")"))
                          count++;
              }
              
              
              if(count != 1)
                  date = tfDate.getText() +"("+count+")";
              else 
                  date = tfDate.getText();
                        
            details = tfDetails.getText();
            
            amount = Double.parseDouble(tfAmount.getText());
            
            amount = Double.parseDouble
                    (new DecimalFormat(".##").
                    format(amount));
            
            String inputType = "";
            
            if(rbCredit.isSelected())
                inputType = "cr";
            else
                inputType = "dr";
            
            if(name.equals("") || date.equals("") ||
                    details.equals("")){
                Main.infoPane.append(Main.tp, Color.RED, "Missing data\n");
            } else {
                try {
                    Class.forName("com.mysql.jdbc.Driver");

                    String username = LogIn.username;
                    String password = LogIn.password;
                    String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                    conn = DriverManager.getConnection(url, username, password);
                    Statement stmt = conn.createStatement();
                    String sql ="";
                    
                    if(rbDebit.isSelected()) {
                        sql = "INSERT INTO cash VALUES"
                            + "(" + amount + ",0,'dr','" + name + "','"
                            + date + " ','" + details + "');";
                        
                        Object[] tempRow = new Object[]{date, amount, 0}; 
                        dTableModel.addRow(tempRow);
                        
                    }
                    else {
                        sql = "INSERT INTO cash VALUES"
                            + "(0," + amount + ", 'cr','" + name + "','"
                            + date + "','" + details + "');";
                        
                        Object[] tempRow = new Object[]{date, 0, amount};
                        dTableModel.addRow(tempRow);
                        
                    }
                    
                    System.out.println(sql);
                    stmt.executeUpdate(sql);
                    
                   
                    if(rbDebit.isSelected()) {
                        double cashPayments = 0;
                        double accountReceivable = 0;
                        String getCashPayment = 
                                "SELECT cashPayments, accountReceivable"
                                + " FROM customers"
                                + " WHERE customerName = '" +
                                cbName.getSelectedItem() + "';";
                        System.out.println(getCashPayment);
                        ResultSet rset = stmt.executeQuery(getCashPayment);
                        while(rset.next()) {
                            cashPayments = rset.getDouble(1) + amount;
                            accountReceivable = rset.getDouble(2);
                        }
                        
                        String updateCashPayments = 
                                "UPDATE customers SET cashPayments = " 
                                + cashPayments + " WHERE customerName = '"
                                + cbName.getSelectedItem() + "';";
                        stmt.execute(updateCashPayments);
                        
                        double balance = cashPayments - accountReceivable;
                        String updateCutoemer = 
                                "UPDATE customers SET balance = " + balance 
                                + " WHERE customerName = '" 
                                + cbName.getSelectedItem() + "';";
                        System.out.println(updateCutoemer);
                        stmt.executeUpdate(updateCutoemer);
                        
                        
                        rset.close();
                    } else if(rbCredit.isSelected()){
                        
                         double credit = 0;
                         double debit = 0;
                         double balance = 0;
                         
                        String getStuff = 
                                "SELECT cr, dr, balance FROM vendors"
                                + " WHERE vendorName = '" +
                                cbName.getSelectedItem() + "';";
                        System.out.println(getStuff);
                        ResultSet rset = stmt.executeQuery(getStuff);
                        while(rset.next()) {
                            credit = rset.getDouble(1) + amount;
                            debit = rset.getDouble(2);
                        }
                        
                        String updateCredit = "UPDATE vendors SET cr = " 
                                + credit + " WHERE vendorName = '"
                                + cbName.getSelectedItem() + "';";
                        stmt.execute(updateCredit);
                        
                        balance = credit - debit;
                        String updateCutoemer = 
                                "UPDATE vendors SET balance = " + balance 
                                + " WHERE vendorName = '" 
                                + cbName.getSelectedItem() + "';";
                        System.out.println(updateCutoemer);
                        stmt.executeUpdate(updateCutoemer);
                        
                        rset.close();
                    }
                    
                    
                    stmt.close();
                    conn.close();
                    
                   
                } catch (SQLException ex) {
                   ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                   ex.printStackTrace();
                }

            }
            } catch (NumberFormatException ne) {
                String name = cbName.getSelectedItem().toString();
                String date = tfDate.getText();
                String details = tfDetails.getText();
                String amount = tfAmount.getText();
                
                if(name.equals("") || date.equals("") ||
                        details.equals("") || amount.equals(""))
                    Main.infoPane.append(Main.tp, Color.RED, "Missing data\n");
               else
                    Main.infoPane.append(Main.tp, Color.RED,
                            "Bad formation for the Amount value\n");
            }  
            
             updateBalance();
        }
        
        public static void loadData() {
             try {
                    Class.forName("com.mysql.jdbc.Driver");

                    String username = LogIn.username;
                    String password = LogIn.password;
                    String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                    conn = DriverManager.getConnection(url, username, password);
                    String selectStuff ="SELECT date, dr, cr FROM cash; ";
                    Statement stmt = conn.createStatement();
                    ResultSet rset = stmt.executeQuery(selectStuff);
                    Object[] tempRow;
                    while(rset.next()){
                        tempRow = new Object[]{
                            rset.getString(1),
                            rset.getDouble(2),
                            rset.getDouble(3)};
                        
                        dTableModel.addRow(tempRow);
                    }
                    
                    rset.close();
                    stmt.close();
                    conn.close();
           
            
             DefaultTableCellRenderer centerRenderer = 
                     new DefaultTableCellRenderer();
             centerRenderer.setHorizontalAlignment(JLabel.CENTER );
             for(int i = 0; i < cashTable.getColumnCount(); i++)
                 cashTable.getColumnModel().getColumn(i).
                         setCellRenderer(centerRenderer);

		} catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
		}catch(NullPointerException e2) {
                    e2.printStackTrace();
		}
        }
        
        private static void moreInfoPanel() {
            
            int selectedRow = cashTable.getSelectedRow();
            int selectedColumn = cashTable.getSelectedColumn();
            String statusType = "";
            
            JTextField sName, sDate, sAmount;
            JTextArea sDetails;
            
            sName = new JTextField(16);
            sDate = new JTextField(9);
            sDate.setText(cashTable.getValueAt(selectedRow,0).toString());
            sAmount = new JTextField(6);
            sDetails = new JTextArea(7, 14);
            sDetails.setLineWrap(true);
            sDetails.setWrapStyleWord(true);
            //sDetails.append(tfDetails.getText());
            
            double test = 
                    Double.parseDouble
                    (cashTable.getValueAt(selectedRow, 1).toString());
            
            if(test == 0) {
                sAmount.setText(cashTable.getValueAt(selectedRow,2).toString());
                statusType = "cr";
            } else {
                sAmount.setText(cashTable.getValueAt(selectedRow,1).toString());
                statusType = "dr";
            }
            
            try {
                    Class.forName("com.mysql.jdbc.Driver");

                    String username = LogIn.username;
                    String password = LogIn.password;
                    String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                    conn = DriverManager.getConnection(url, username, password);
                    String selectStuff = "";
                    if( statusType == "dr") 
                        selectStuff = "SELECT name, details FROM cash "
                                + "WHERE statusType = 'dr' AND dr = " 
                                + sAmount.getText() 
                                + " AND date = '" + sDate.getText() + "'; ";
                     else 
                        selectStuff = "SELECT name, details FROM cash "
                                + "WHERE statusType = 'cr' AND cr = " 
                                + sAmount.getText()
                                + " AND date = '" + sDate.getText() + "'; ";
                    
                    
                    Statement stmt = conn.createStatement();
                    ResultSet rset = stmt.executeQuery(selectStuff);
                    
                    while(rset.next()){
                       sName.setText(rset.getString(1));
                       sDetails.append(rset.getString(2));
                    }
                    

                    rset.close();
                    stmt.close();
                    conn.close();
                    
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
		
            sName.setEditable(false);
            sDate.setEditable(false);
            sAmount.setEditable(false);
            sDetails.setEditable(false);
            
            JScrollPane scroll = 
                    new JScrollPane(sDetails,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            JPanel topComponent = new JPanel();
            
            JLabel name = new JLabel("Name");
            JLabel date = new JLabel("Date");
            JLabel amount = new JLabel("Amount");
            
            topComponent.add(name);
            topComponent.add(sName);
            topComponent.add(date);
            topComponent.add(sDate);
            topComponent.add(amount);
            topComponent.add(sAmount);
            
            JOptionPane pane = 
                    new JOptionPane (topComponent,
                    JOptionPane.INFORMATION_MESSAGE);
            
            JDialog dialog = pane.createDialog(null, "More Info");
            Container grabbedContenet = dialog.getContentPane();
            
            MoreInfoPanel mip = new MoreInfoPanel(grabbedContenet, scroll);
            
            dialog.setContentPane(mip);
            dialog.pack();
            dialog.setVisible(true);
            
        }
        
        public static void loadVendorsData() {
            cbName.removeAllItems();
            cbName.addItem("");
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String username = LogIn.username;
                String password = LogIn.password;
                String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                conn = DriverManager.getConnection(url, username, password);
                Statement stmt = conn.createStatement();
                
                String sql = "SELECT vendorName FROM vendors;";
                ResultSet rset = stmt.executeQuery(sql);
                while(rset.next()) 
                    cbName.addItem(rset.getString(1));
                
                rset.close();
                conn.close();
                stmt.close();
                
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            
        }
        
        public static void loadCustomersData() {
            cbName.removeAllItems();
            cbName.addItem("");
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String username = LogIn.username;
                String password = LogIn.password;
                String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                conn = DriverManager.getConnection(url, username, password);
                Statement stmt = conn.createStatement();
                String sql = "SELECT customerName FROM customers;";
                ResultSet rset = stmt.executeQuery(sql);
                while(rset.next()) 
                    cbName.addItem(rset.getString(1));
                
                rset.close();
                conn.close();
                stmt.close();
                
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        
        public static void updateBalance() {
             
             double balance = 0;
             try {
                    Class.forName("com.mysql.jdbc.Driver");

                    String username = LogIn.username;
                    String password = LogIn.password;
                    String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                    conn = DriverManager.getConnection(url, username, password);
                    String selectStuff ="SELECT (SUM(dr)-SUM(cr))"
                            + " AS BALANCE FROM cash;";
                    Statement stmt = conn.createStatement();
                    ResultSet rset = stmt.executeQuery(selectStuff);
                    
                    while(rset.next())
                        balance = rset.getDouble(1);
                    
                    balance = Double.parseDouble(                        
                            new DecimalFormat(".##").
                            format(balance));
                    
                        
                    }catch (SQLException ex) {
                   ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                   ex.printStackTrace();
                }
        
             Main.statusBar.setText("Cash", "", "Balance: " + balance);
         }
        
        public static double getBalance() {
             
             double balance = 0;
             try {
                    Class.forName("com.mysql.jdbc.Driver");

                    String username = LogIn.username;
                    String password = LogIn.password;
                    String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                    conn = DriverManager.getConnection(url, username, password);
                    String selectStuff ="SELECT (SUM(dr)-SUM(cr))"
                            + " AS BALANCE FROM cash;";
                    Statement stmt = conn.createStatement();
                    ResultSet rset = stmt.executeQuery(selectStuff);
                    
                    while(rset.next())
                        balance = rset.getDouble(1);
                        
                    }catch (SQLException ex) {
                   ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                   ex.printStackTrace();
                }
        
             return balance;
        }
        
        public static void setBalance(double balance) {
            Main.statusBar.setText("Cash", "", "Balance: " + balance);
        }
}
