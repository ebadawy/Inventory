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

public class Customers {

	
    static Connection conn;
    static JTextField tfCustomerName, tfPhone, tfAddress;
    static JLabel lCustomerName, lPhone, lAddress;
    static JButton btmAddCustomer, btmRemoveCustomer,btmRefresh;
    static Object[][] dataResult;
    static Object[] columns = 
    {"", "Customer Name","Phone", "Address",
        "Balance", "Account Receivable", "Cash Payments"};
   
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
            
            if(column == 2 || column == 3)
                return true;
            
            return false;
        }
    };
    
    public static JTable customerTable;
    
    public static JScrollPane customers() {
        
        tfCustomerName = new JTextField(16);
        tfPhone = new JTextField(16);
        tfAddress = new JTextField(16);
        lCustomerName = new JLabel("Customer Name");
        lPhone = new JLabel("Phone");
        lAddress = new JLabel("Address");
        btmAddCustomer= new JButton("Add");
        btmRemoveCustomer = new JButton("Remove");
        btmRefresh = new JButton("Refresh");        
        btmAddCustomer.setToolTipText("Add a new customer");
        btmRemoveCustomer.setToolTipText("Remove selected customer");
        btmRefresh.setToolTipText("Refresh customers database");
	
        JPanel inputPanel1 = new JPanel();
        inputPanel1.add(lCustomerName);
        inputPanel1.add(tfCustomerName);
        inputPanel1.add(lPhone);
        inputPanel1.add(tfPhone);
        inputPanel1.add(lAddress);
        inputPanel1.add(tfAddress);
        
        JPanel btmPanel1 = new JPanel();
        btmPanel1.add(btmAddCustomer);
        btmPanel1.add(btmRemoveCustomer);
        btmPanel1.add(btmRefresh);
        
        JPanel btmPanel = new JPanel(new BorderLayout());
        btmPanel.add(btmPanel1, BorderLayout.EAST);
	
        JPanel inputPanel = new JPanel(new GridLayout(0,1,2,2));
        inputPanel.add(inputPanel1);
        inputPanel.add(btmPanel);
        
        customerTable = new JTable(dTableModel);
        JScrollPane customerTableSP = new JScrollPane(customerTable);
        customerTable.setAutoCreateRowSorter(true);
        customerTable.setRowHeight(customerTable.getRowHeight() + 16);
        loadData();
	
        TableColumn column = null;
        for(int i = 0; i < customerTable.getColumnCount(); i++) {
            column = customerTable.getColumnModel().getColumn(i);
            if(i == 0)
                column.setPreferredWidth(1);
            else
                column.setPreferredWidth(150);
        }
	
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(customerTableSP, BorderLayout.CENTER);
        JScrollPane mainPanelSP = new JScrollPane(mainPanel);
	
        btmAddCustomer.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                addAction();
            }
        });
        
        btmRefresh.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                Main.infoPane.append
                        (Main.tp, Color.BLACK,
                        "Loading data...\n");
                
                double ts1 = System.currentTimeMillis();
                dTableModel.setRowCount(1);
                dTableModel.removeRow(0);
                loadData();
                
                double ts2 = System.currentTimeMillis();
                double T = 
                        Double.parseDouble
                        (new DecimalFormat(".###").
                        format((ts2 - ts1) / 1000));
                Main.infoPane.append(Main.tp, new Color(10, 100, 10),
                        "Data has been successfully loaded ("
                        + T + " sec)\n");
            }
        });
	
        btmRemoveCustomer.addActionListener(new ActionListener() {
	
            @Override
            public void actionPerformed(ActionEvent arg0) {
                removeAction();
            }
        });
	
        customerTable.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
			
                switch(e.getKeyCode()) {
                    case 10 :
                        String customerName = 
                                (String) customerTable.
                                getValueAt(customerTable.getSelectedRow(), 1);
                        String phone = 
                                (String) customerTable.
                                getValueAt(customerTable.getSelectedRow(), 2);
                        String address = 
                                (String) customerTable.
                                getValueAt(customerTable.getSelectedRow(), 3);
			
                        try {                            
			
                            Class.forName("com.mysql.jdbc.Driver");
                            String username = LogIn.username;			
                            String password = LogIn.password;
                            String url = 
                                    "jdbc:mysql://" + LogIn.ip + "/inventory";
                            conn = DriverManager.getConnection
                                    (url, username, password);
                            Statement stmt = conn.createStatement();
                            
                            //update customer in DB
                            String updateCusotmer = 
                                    "UPDATE customers SET phone = '" 
                                    + phone + "', address = '" + address 
                                    + "' WHERE customerName = '" 
                                    + customerName + "'; ";
                            
                            stmt.executeUpdate(updateCusotmer);
                            Main.infoPane.append
                                    (Main.tp, new Color(10, 100, 10),
                                    "new " + customerName 
                                    + "'s data has been updated\n");
			
                            //update customer in gui
                            customerTable.
                                    setValueAt
                                    (phone,
                                    customerTable.getSelectedRow(),
                                    2);
                            
                            customerTable.
                                    setValueAt
                                    (address,
                                    customerTable.getSelectedRow(),
                                    3);
                            
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
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });
        
        return mainPanelSP;
    }
    
    private static void addAction() {
        
        if(tfCustomerName.getText().equals(""))
            Main.infoPane.append(Main.tp, Color.RED, "Enter customer Name\n");
       
        else {
            String customerName = tfCustomerName.getText();
            String phone = tfPhone.getText();
            String address = tfAddress.getText();		
	
            try {
                
                Class.forName("com.mysql.jdbc.Driver");
                String username = LogIn.username;
                String password = LogIn.password;
                String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                conn = DriverManager.getConnection(url, username, password);
                Statement stmt = conn.createStatement();
                
                //add new customer to DB
                String addCusotmer = 
                        "INSERT INTO customers VALUES('" 
                        + customerName + "', '" + phone 
                        + "', '" + address + "', 0, 0, 0);";
                
                stmt.executeUpdate(addCusotmer);
                Main.infoPane.append
                        (Main.tp, new Color(10,100,10),
                        "new customer added to the Database (" 
                        + customerName + ")\n");
		
                //add new Customer to cusotmerTable
                int num = 0;
                try {
                    num = 
                            (int) customerTable.
                            getValueAt(customerTable.getRowCount()-1, 0)+1;
                    
                } catch(Exception ex) {
                    num = 1;
                }
                
                Object[] tempRow = 
                        new Object[]
                        {num, customerName, phone, address, 0, 0, 0};
                
                dTableModel.addRow(tempRow);		
                if(Cash.rbDebit.isSelected())
                    Cash.loadCustomersData();
                
                SInvoice_NewInvoice.loadCustomerData();
                
            }catch(SQLException ex) {
                ex.printStackTrace();
                
                Main.infoPane.append(Main.tp, Color.RED,
                        "Multiple cusotmer defined\n");
            
            } catch(ClassNotFoundException ex) {
                ex.printStackTrace();
            } 
        }
    }
    
    private static void removeAction() {
        
        try {
            String customerName = 
                    (String) customerTable.getValueAt
                    (customerTable.getSelectedRow(), 1);
            
            int result = 
                    JOptionPane.showConfirmDialog
                    (null, "Are you sure you want to "
                    + "permanently delete this item?",
                    "Delete Item",JOptionPane.YES_OPTION);
            
            switch(result) {
                case JOptionPane.YES_OPTION :
                    try {
                        Statement stmt;
                        Class.forName("com.mysql.jdbc.Driver");
                        String username = LogIn.username;
                        String password = LogIn.password;
                        String url = "jdbc:mysql://" + LogIn.ip + "/inventory";
                        conn = DriverManager.getConnection
                                (url, username, password);
                        stmt = conn.createStatement();
                        String deleteCustomer = 
                                "DELETE FROM customers WHERE customerName = '" 
                                + customerName + "';";
                        
                        stmt.executeUpdate(deleteCustomer);
                        Main.infoPane.append
                                (Main.tp, new Color(10,100,10),
                                "customer \"" + customerName +
                                "\" removed\n");
                        
                        dTableModel.removeRow(customerTable.getSelectedRow());
                        
                        int count = 0;
                        for(int i = 0; i < dTableModel.getRowCount(); i++)
                            customerTable.setValueAt(++count, i, 0);
                        
                        SInvoice_NewInvoice.loadCustomerData();
			
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case JOptionPane.NO_OPTION : 
                    break;	
            } 
	
        }catch(IndexOutOfBoundsException ee) {
            Main.infoPane.append(Main.tp, Color.RED, "No customer selected\n");
	
        }
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
            String selectStuff = "SELECT * FROM customers;";
            ResultSet rset = stmt.executeQuery(selectStuff);
            Object[] tempRow;
            int count = 0;
            while(rset.next()) {
                
                String customerName = rset.getString(1);
                String phone = rset.getString(2);
                String address = rset.getString(3);
                double balance =  rset.getDouble(4);
                double accountReceivable =  rset.getDouble(5);
                double cashPayments = rset.getDouble(6);
		
                tempRow = 
                        new Object[]{ ++count,
                            customerName,
                            phone, address,
                            balance, accountReceivable,
                            cashPayments};
                
                dTableModel.addRow(tempRow);
            }
	
            rset.close();
            conn.close();
            stmt.close();
            
            customerTable.getColumnModel().getColumn(0).setPreferredWidth(1);
            
            //Center the data in the table
            DefaultTableCellRenderer centerRenderer = 
                    new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( JLabel.CENTER );
           
            for(int i = 0; i < customerTable.getColumnCount(); i++)
                customerTable.
                        getColumnModel().getColumn(i).setCellRenderer
                        ( centerRenderer );
            
        } catch (SQLException | ClassNotFoundException e ) {
            e.printStackTrace();
        }catch (NullPointerException e2){
        }
    }
}
